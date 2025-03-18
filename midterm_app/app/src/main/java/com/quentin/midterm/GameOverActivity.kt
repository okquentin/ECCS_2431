package com.quentin.midterm

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)  // Make sure you have the corresponding XML layout

        // Find the TextView in the layout
        val winnerTextView: TextView = findViewById(R.id.winnerTextView) // Ensure this ID is in your layout
        
        winnerTextView.text = "Player Wins!"

        // Want to display winny the poo eating honey at the end
    }
}
