package com.quentin.midterm

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)  // Ensure this layout exists

        // Find the TextView in the layout
        val winnerTextView: TextView = findViewById(R.id.winnerTextView) // Ensure this ID is in your layout
        val winnerImageView: ImageView = findViewById(R.id.winnerImageView) // Ensure this ID is in your layout
        val returnButton: Button = findViewById(R.id.returnButton) // Ensure this ID is in your layout

        val isWinner = intent.getBooleanExtra("isWinner", false)
        if (isWinner) {
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
