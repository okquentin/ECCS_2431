package com.quentin.midterm

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.ContextMenu
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

const val MAX_DICE = 2

class MainActivity : AppCompatActivity(),
    RollLengthDialogFragment.OnRollLengthSelectedListener {

    private lateinit var messageTextView: TextView
    private lateinit var branchTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var restartButton: Button
    private lateinit var rollsRemainingTextView: TextView

    private var winThreshold = 30
    private var currentBranch = 0
    private var skipNextRoll = false

    private var numVisibleDice = MAX_DICE
    private lateinit var diceList: MutableList<Dice>
    private lateinit var diceImageViewList: MutableList<ImageView>
    private lateinit var optionsMenu: Menu
    private var timer: CountDownTimer? = null
    private var selectedDie = 0
    private var timerLength = 2000L
    private var initTouchX = 0
    private lateinit var gestureDetector: GestureDetectorCompat

    private var rollLimit = 8
    private var currentRollCount = 0
    private var rollsRemaining = rollLimit
    private var difficulty = "User Selected"

    override fun onRollLengthClick(which: Int) {
        // Convert to milliseconds
        timerLength = 1000L * (which + 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the difficulty level from the Intent
        difficulty = intent.getStringExtra("difficulty") ?: "Normal"

        // Set roll limit based on difficulty
        rollLimit = when (difficulty) {
            "Easy" -> 10
            "Normal" -> 8
            "Hard" -> 6
            else -> 8
        }

        messageTextView = findViewById(R.id.messageText)
        branchTextView = findViewById(R.id.branchText)
        rollsRemainingTextView = findViewById(R.id.rollsRemainingText)

        // Create list of Dice
        diceList = mutableListOf()
        for (i in 0 until MAX_DICE) {
            diceList.add(Dice(i + 1))
        }

        // Create list of ImageViews
        diceImageViewList = mutableListOf(
            findViewById(R.id.dice1), findViewById(R.id.dice2)
        )

        showDice()

        // Register context menus for all dice and tag each die
        for (i in 0 until diceImageViewList.size) {
            registerForContextMenu(diceImageViewList[i])
            diceImageViewList[i].tag = i
        }

        // Moving finger left or right changes dice number
        diceImageViewList[0].setOnTouchListener { v, event ->
            var returnVal = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initTouchX = event.x.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    val x = event.x.toInt()

                    // See if movement is at least 20 pixels
                    if (abs(x - initTouchX) >= 20) {
                        if (x > initTouchX) {
                            diceList[0].number++
                        } else {
                            diceList[0].number--
                        }
                        showDice()
                        initTouchX = x
                    }
                }

                MotionEvent.ACTION_UP -> {
                    v.performClick() // Ensures proper click handling
                }

                else -> returnVal = false
            }
            returnVal
        }

        gestureDetector = GestureDetectorCompat(this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    rollDice()
                    return true
                }
            }
        )

        progressBar = findViewById(R.id.progressBar)
        restartButton = findViewById(R.id.restartButton)

        restartButton.setOnClickListener {
            resetGame()
        }

        updateRollsRemaining()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Determine which menu option was chosen
        return when (item.itemId) {
            R.id.action_stop -> {
                timer?.cancel()
                item.isVisible = false
                true
            }

            R.id.action_roll -> {
                rollDice()
                true
            }

            R.id.action_roll_length -> {
                val dialog = RollLengthDialogFragment()
                dialog.show(supportFragmentManager, "rollLengthDialog")
                true
            }

            R.id.action_return_to_menu -> {
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        optionsMenu = menu!!
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        // Save which die is selected
        selectedDie = v?.tag as Int

        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_one -> {
                diceList[selectedDie].number++
                showDice()
                true
            }

            R.id.subtract_one -> {
                diceList[selectedDie].number--
                showDice()
                true
            }

            R.id.roll -> {
                rollDice()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun showDice() {
        // Show visible dice
        for (i in 0 until numVisibleDice) {
            val diceDrawable = ContextCompat.getDrawable(this, diceList[i].imageId)
            diceImageViewList[i].setImageDrawable(diceDrawable)
            diceImageViewList[i].contentDescription = diceList[i].imageId.toString()
        }
    }

    private fun rollDice() {

        currentRollCount++
        updateRollsRemaining()

        optionsMenu.findItem(R.id.action_stop).isVisible = true
        timer?.cancel()

        // Start a timer that periodically changes each visible dice
        timer = object : CountDownTimer(timerLength, 100) {
            override fun onTick(millisUntilFinished: Long) {
                for (i in 0 until numVisibleDice) {
                    diceList[i].roll()
                }
                showDice()
            }

            override fun onFinish() {
                optionsMenu.findItem(R.id.action_stop).isVisible = false
                showDice()  // Display dice after rolling

                val diceValues = diceList.map { it.number }
                val total = diceValues.sum()

                if (skipNextRoll) {
                    messageTextView.text = "Pooh skips this roll due to bee swarm!"
                    skipNextRoll = false
                    return
                }

                if (diceValues[0] == diceValues[1]) {
                    currentBranch = maxOf(0, currentBranch - 4)
                    messageTextView.text = "Doubles Penalty! Pooh slips down to Branch $currentBranch"
                } else if (total == 4) {
                    messageTextView.text = "Windy Day Challenge! Pooh stays on Branch $currentBranch"
                } else {
                    currentBranch += total
                    if (currentBranch > winThreshold && difficulty != "Hard") {
                        currentBranch = winThreshold
                    }
                    messageTextView.text = "Pooh climbs to Branch $currentBranch"
                }

                Log.d("MainActivity", "Current Branch: $currentBranch") // Log the current branch

                if (currentBranch == 10 || currentBranch == 20) {
                    skipNextRoll = true
                    messageTextView.text = "Bee Swarm Trouble! Pooh will skip the next roll."
                }

                progressBar.progress = currentBranch
                branchTextView.text = "Branch $currentBranch"
                
                // Check for win condition
                if (currentBranch >= 30) {
                    val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                    intent.putExtra("isWinner", currentBranch >= 30)
                    startActivity(intent)
                    finish()
                }

                // Check for loss condition 1 (overshooting the branch in hard mode)
                if (currentBranch > winThreshold) {
                    val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                    Log.d("Main Activity", "Overshot branch!")
                    intent.putExtra("isOvershoot", true)
                    startActivity(intent)
                    finish()
                    return
                }

                // Check for loss condition 2 (no rolls remaining)
                if (rollsRemaining == 0) {
                    val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                    startActivity(intent)
                    finish() 
                    return
                }
            }
        }.start()
    }

    private fun updateRollsRemaining() {
        rollsRemaining = rollLimit - currentRollCount
        rollsRemainingTextView.text = "Rolls Remaining: $rollsRemaining"
    }

    private fun resetGame() {
        currentBranch = 0
        skipNextRoll = false
        progressBar.progress = 0
        branchTextView.text = "Branch 0"
        messageTextView.text = "Pooh jumped down the tree!"
        currentRollCount = 0
        updateRollsRemaining()
    }
}