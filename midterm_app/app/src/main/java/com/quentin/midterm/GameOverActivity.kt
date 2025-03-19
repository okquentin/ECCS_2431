package com.quentin.midterm

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * The activity displayed when the game is over, showing the result.
 */
class GameOverActivity : AppCompatActivity() {

    /**
     * Initializes the activity.
     * @param savedInstanceState The last saved instance state of the Activity, or null if this is a freshly created Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)  // Ensure this layout exists

        // Find the TextView in the layout
        val winnerTextView: TextView = findViewById(R.id.winnerTextView) // Ensure this ID is in your layout
        val winnerImageView: ImageView = findViewById(R.id.winnerImageView) // Ensure this ID is in your layout
        val returnButton: Button = findViewById(R.id.returnButton) // Ensure this ID is in your layout

        val isWinner = intent.getBooleanExtra("isWinner", false)
        val isOvershoot = intent.getBooleanExtra("isOvershoot", false)
        
        // Choose the appropriate outcome message and image based on the game result
        if (isOvershoot) {
            winnerTextView.text = "Pooh overshot the branch and fell!"
            winnerImageView.setImageResource(R.drawable.pooh_fall) // Ensure this drawable exists
        } else if (isWinner) {
            winnerTextView.text = "Pooh reaches Branch 30 and gets some honey!"
            winnerImageView.setImageResource(R.drawable.pooh_honey) // Ensure this drawable exists
        } else {
            winnerTextView.text = "Pooh couldn't reach the honey this time..."
            winnerImageView.setImageResource(R.drawable.pooh_sad) // Ensure this drawable exists
        }

        returnButton.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
            finish() // Close the game over screen
        }
    }
}
