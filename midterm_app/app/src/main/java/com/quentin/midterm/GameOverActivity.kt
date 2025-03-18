package com.quentin.midterm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)  // Make sure you have the corresponding XML layout

        // Get the player number passed via Intent, default to 0 if not found
        val winningPlayer = intent.getIntExtra("WINNING_PLAYER", 0)

        // Find the TextView in the layout
        val winnerTextView: TextView = findViewById(R.id.winnerTextView) // Ensure this ID is in your layout

        // Check if the winning player is valid (not 0) and set the text to display the winning player
        if (winningPlayer > 0) {
            winnerTextView.text = "Player $winningPlayer Wins!"
        } else {
            winnerTextView.text = "Error"
        }
    }
}
