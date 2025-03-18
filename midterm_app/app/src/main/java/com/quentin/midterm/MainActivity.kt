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
import android.widget.ImageView
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
    private lateinit var turnTextView: TextView
    private lateinit var scoreTextView: TextView
    private var winThreshold = 10
    private var playerScores = mutableListOf(0, 0)
    private var playerRolls = mutableListOf(0, 0)
    private var currentPlayer = 0 // Start w/ player 1
    private var roundNum = 1
    private var gameOver = false
    private var tie = false
    private var numPlayers = 2 // Default 2 players, will change based on intro screen


    private var numVisibleDice = MAX_DICE
    private lateinit var diceList: MutableList<Dice>
    private lateinit var diceImageViewList: MutableList<ImageView>
    private lateinit var optionsMenu: Menu
    private var timer: CountDownTimer? = null
    private var selectedDie = 0
    private var timerLength = 2000L
    private var initTouchX = 0
    private lateinit var gestureDetector: GestureDetectorCompat


    override fun onRollLengthClick(which: Int) {
        // Convert to milliseconds
        timerLength = 1000L * (which + 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the number of players passed from the IntroActivity
        numPlayers = intent.getIntExtra("numPlayers", 2)
        messageTextView = findViewById(R.id.messageText)
        turnTextView = findViewById(R.id.turnText)

        // Initialize player scores for 3 players if needed
        if (numPlayers == 3) {
            playerScores = mutableListOf(0, 0, 0)
            playerRolls = mutableListOf(0, 0, 0)
        }

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

                // Update player roll for the current player
                val diceValues = diceList.map { it.number }

                // Sort the dice values to create the highest two-digit number
                playerRolls[currentPlayer] = diceValues.sortedDescending().joinToString("").toInt()

                // Check if the current player is the last player in the round
                if (currentPlayer == numPlayers - 1) {
                    // Find the player with the highest score
                    val highestScore = playerRolls.maxOrNull()
                    val winningPlayer = playerRolls.indexOf(highestScore) + 1

                    val winningPlayers = playerRolls.withIndex()
                        .filter { it.value == highestScore }
                        .map { it.index + 1 } // Get player numbers (1-indexed)

                    if (winningPlayers.size > 1) {
                        messageTextView.text = "Round $roundNum is a tie between Players ${winningPlayers.joinToString(", ")} with score $highestScore"
                        tie = true
                    }
                    else {
                        if (tie) {
                            playerScores[winningPlayer - 1] += 2  // -1 because player index is 1-based
                            tie = false
                        }
                        else {
                            playerScores[winningPlayer - 1] += 1 // -1 because player index is 1-based
                        }
                        // Display the winner and start a new round
                        messageTextView.text = "Round $roundNum Winner: Player $winningPlayer with score $highestScore"
                        roundNum++ // Increment round after showing the winner
                    }

                    // Check if any player's score is >= winThreshold
                    val isAnyPlayerWinner = playerScores.any { it >= winThreshold}

                    if (isAnyPlayerWinner) {
                        val winner = playerScores.indexOfFirst { it >= winThreshold} + 1 // +1 to make the player number 1-based
                        // Pass the winner's player number as an extra
                        val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                        intent.putExtra("WINNING_PLAYER", winner)  // Put the winner's player number
                        startActivity(intent) // Navigate to the new activity
                    }
                    else {
                        // If no player has reached the winning score
                        currentPlayer = 0  // Reset the current player for the next round
                    }
                    // Update the scoreTextView with the current player scores
                    Log.d("RollDice", "Player scores: $playerScores")
                }
                else {
                    // Move to the next player if not the last one
                    currentPlayer++
                }

                updateTurn()
            }
        }.start()
    }

    private fun updateTurn() {
        // Update the message based on the game state
        turnTextView.text = "Player ${currentPlayer + 1}'s turn!"
    }



    // Not Currently Used
    private fun resetGame() {
        gameOver = false
        currentPlayer = 0
    }
}