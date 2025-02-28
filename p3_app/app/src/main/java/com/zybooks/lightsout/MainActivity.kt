package com.zybooks.lightsout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children

const val GAME_STATE = "gameState"
private var lightOnColorId = 0

class MainActivity : AppCompatActivity() {

    private lateinit var game: BattleshipGame
    private lateinit var lightGridLayout: GridLayout
    private lateinit var statsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lightGridLayout = findViewById(R.id.light_grid)
        statsText = findViewById(R.id.stats_text)

        game = BattleshipGame()
        game.newGame()

        // Set up grid buttons
        for (i in 0 until GRID_SIZE * GRID_SIZE) {
            val button = Button(this)
            button.setOnClickListener { onGridButtonClick(i) }
            lightGridLayout.addView(button)
        }

        updateStats()
    }

    private fun onGridButtonClick(index: Int) {
        val row = index / GRID_SIZE
        val col = index % GRID_SIZE
        val gameOver = game.makeGuess(row, col)

        // Update UI
        val button = lightGridLayout.getChildAt(index) as Button
        if (game.grid[row][col].isHit) {
            if (game.grid[row][col].hasShip) {
                button.text = "Hit"
            } else {
                button.text = "Miss"
            }
        }

        updateStats()

        // Check if the game is over
        if (gameOver) {
            Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStats() {
        statsText.text = "Guesses: ${game.guesses}  Hits: ${game.hits}  Misses: ${game.misses}"
    }

    fun onResetGameClick(view: View) {
        game.newGame()
        lightGridLayout.children.forEach {
            (it as Button).text = ""
        }
        updateStats()
    }
}

