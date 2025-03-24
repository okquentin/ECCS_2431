package com.zybooks.dotty

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zybooks.dotty.DotsView.DotsGridListener
import java.util.Locale

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.CountDownTimer
import android.content.Intent

class MainActivity : AppCompatActivity() {

   private val dotsGame = DotsGame.getInstance()
   private lateinit var dotsView: DotsView
   private lateinit var movesRemainingTextView: TextView
   private lateinit var scoreTextView: TextView
   private lateinit var soundEffects: SoundEffects
   private lateinit var timer: CountDownTimer
   private lateinit var timerTextView: TextView
   private lateinit var roundInfoTextView: TextView
   private lateinit var roundPassedTextView: TextView
   private var totalTimeInMillis: Long = 60 * 1000L

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)

      movesRemainingTextView = findViewById(R.id.moves_remaining_text_view)
      scoreTextView = findViewById(R.id.score_text_view)
      dotsView = findViewById(R.id.dots_view)
      findViewById<Button>(R.id.new_game_button).setOnClickListener { newGameClick() }
      timerTextView = findViewById(R.id.timer_text_view)
      roundInfoTextView = findViewById(R.id.round_info_text_view)
      roundPassedTextView = findViewById(R.id.round_passed_text_view)

      dotsView.setGridListener(gridListener)
      soundEffects = SoundEffects.getInstance(applicationContext)

      startNewGame()
      startTimer(totalTimeInMillis)
   }

   override fun onDestroy() {
      super.onDestroy()
      soundEffects.release()
      timer.cancel()
   }

   private val gridListener = object : DotsGridListener {
      override fun onDotSelected(dot: Dot, status: DotSelectionStatus) {
         // Ignore selections when game is over
         if (dotsGame.isGameOver) return
         
         // Play first tone when first dot is selected
         if (status == DotSelectionStatus.First) {
            soundEffects.resetTones()
         }
         
         // Select the dot and play the right tone 
         val addStatus = dotsGame.processDot(dot)

         if (addStatus == DotStatus.Added) {
            soundEffects.playTone(true)
         } else if (addStatus == DotStatus.Removed) {
            soundEffects.playTone(false)
         }

         // If done selecting dots then replace selected dots and display new moves and score
         if (status === DotSelectionStatus.Last) {
            if (dotsGame.selectedDots.size > 1) {
               dotsView.animateDots()

               //dotsGame.finishMove()
              //updateMovesAndScore()
            } else {
               dotsGame.clearSelectedDots()
            }
         }

         // Display changes to the game
         dotsView.invalidate()
      }
      override fun onAnimationFinished() {
         dotsGame.finishMove()
         dotsView.invalidate()
         updateMovesAndScore()

         if (dotsGame.isRoundComplete) {
            soundEffects.playVictory()
            showRoundPassedMessage()
            dotsGame.nextRound()
            if (dotsGame.isGameOver) {
               startActivity(Intent(this@MainActivity, VictoryActivity::class.java))
               finish()
            } else {
               startNewGame()
            }
         } else if (dotsGame.isGameOver) {
            soundEffects.playGameOver()
            startActivity(Intent(this@MainActivity, FailureActivity::class.java))
            finish()
         }
      }
   }

   private fun newGameClick() {
            // Animate down off screen
            val screenHeight = this.window.decorView.height.toFloat()
            val moveBoardOff = ObjectAnimator.ofFloat(
            dotsView, "translationY", screenHeight)
            moveBoardOff.duration = 700
            moveBoardOff.start()
      
            moveBoardOff.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
               startNewGame()
   
               // Animate from above the screen down to default location
               val moveBoardOn = ObjectAnimator.ofFloat(
                  dotsView, "translationY", -screenHeight, 0f)
               moveBoardOn.duration = 700
               moveBoardOn.start()
            }
         }
      )
   }

   private fun startNewGame() {
      dotsGame.newGame()
      dotsView.invalidate()
      updateMovesAndScore()
      updateRoundInfo()
   }

   private fun startTimer(timeInMillis: Long) {
      timer = object : CountDownTimer(timeInMillis, 1000) {
         override fun onTick(millisUntilFinished: Long) {
            totalTimeInMillis = millisUntilFinished
            val minutes = millisUntilFinished / 1000 / 60
            val seconds = millisUntilFinished / 1000 % 60
            timerTextView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
         }

         override fun onFinish() {
            if (!dotsGame.isRoundComplete) {
               soundEffects.playGameOver()
               startActivity(Intent(this@MainActivity, FailureActivity::class.java))
               finish()
            }
         }
      }
      timer.start()
   }

   private fun updateMovesAndScore() {
      movesRemainingTextView.text = String.format(Locale.getDefault(), "%d", dotsGame.movesLeft)
      scoreTextView.text = String.format(Locale.getDefault(), "%d", dotsGame.score)
      updateRoundInfo()
   }

   private fun updateRoundInfo() {
      val requiredScore = dotsGame.getRequiredScores()[dotsGame.currentRound - 1]
      val remainingScore = requiredScore - dotsGame.score
      roundInfoTextView.text = String.format(Locale.getDefault(), "Round %d: Score %d more connections", dotsGame.currentRound, remainingScore)
   }

   private fun showRoundPassedMessage() {
      roundPassedTextView.visibility = TextView.VISIBLE
      val screenWidth = this.window.decorView.width.toFloat()
      val moveMessage = ObjectAnimator.ofFloat(
         roundPassedTextView, "translationX", -screenWidth, screenWidth)
      moveMessage.duration = 2000
      moveMessage.start()
      moveMessage.addListener(object : AnimatorListenerAdapter() {
         override fun onAnimationEnd(animation: Animator) {
            roundPassedTextView.visibility = TextView.GONE
         }
      })
   }
}

