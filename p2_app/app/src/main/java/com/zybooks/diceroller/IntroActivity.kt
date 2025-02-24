package com.zybooks.diceroller;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.zybooks.diceroller.MainActivity
import com.zybooks.diceroller.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val button2Player = findViewById<Button>(R.id.button2Player)
        val button3Player = findViewById<Button>(R.id.button3Player)

        button2Player.setOnClickListener {
            startGame(2)
        }

        button3Player.setOnClickListener {
            startGame(3)
        }
    }

    private fun startGame(numPlayers: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("numPlayers", numPlayers)
        startActivity(intent)
        finish() // Close the intro screen
    }
}
