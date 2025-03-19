package com.quentin.midterm

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)  // Ensure this layout exists

        // Find the TextView in the layout
        val winnerTextView: TextView = findViewById(R.id.winnerTextView) // Ensure this ID is in your layout
        winnerTextView.text = "Pooh reaches Branch 30 and gets some honey!"

        // Find the ImageView in the layout
        val winnerImageView: ImageView = findViewById(R.id.winnerImageView) // Ensure this ID is in your layout
        winnerImageView.setImageResource(R.drawable.pooh_honey) // Ensure this drawable exists
    }
}
