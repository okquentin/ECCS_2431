package com.quentin.midterm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.quentin.midterm.MainActivity
import com.quentin.midterm.R

/**
 * The Game Menu activity where the user selects the game difficulty.
 */
class IntroActivity : AppCompatActivity() {

    /**
     * Initializes the activity.
     * @param savedInstanceState The last saved instance state of the Activity, or null if this is a freshly created Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val easyButton = findViewById<Button>(R.id.easyButton)
        val normalButton = findViewById<Button>(R.id.normalButton)
        val hardButton = findViewById<Button>(R.id.hardButton)

        easyButton.setOnClickListener {
            startGame("Easy")
        }

        normalButton.setOnClickListener {
            startGame("Normal")
        }

        hardButton.setOnClickListener {
            startGame("Hard")
        }
    }

    /**
     * Starts the game with the selected difficulty.
     * @param difficulty The selected difficulty level.
     */
    private fun startGame(difficulty: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("difficulty", difficulty)
        startActivity(intent)
        finish() // Close the intro screen
    }
}
