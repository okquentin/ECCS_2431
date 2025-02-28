package com.zybooks.lightsout

import kotlin.random.Random

const val GRID_SIZE = 3

class BattleshipGame {

    val grid = Array(GRID_SIZE) { Array(GRID_SIZE) { Cell() } }
    private var shipsSunk = 0
    var guesses = 0
    var hits = 0
    var misses = 0

    data class Cell(var hasShip: Boolean = false, var isHit: Boolean = false)

    fun newGame() {
        // Reset the grid
        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                grid[row][col] = Cell()
            }
        }

        // Place two ships randomly
        var shipsPlaced = 0
        while (shipsPlaced < 2) {
            val row = Random.nextInt(GRID_SIZE)
            val col = Random.nextInt(GRID_SIZE)
            if (!grid[row][col].hasShip) {
                grid[row][col].hasShip = true
                shipsPlaced++
            }
        }

        // Reset stats
        guesses = 0
        hits = 0
        misses = 0
        shipsSunk = 0
    }

    fun makeGuess(row: Int, col: Int): Boolean {
        val cell = grid[row][col]
        guesses++
        if (cell.isHit) return false // Already hit this cell

        cell.isHit = true
        if (cell.hasShip) {
            hits++
            if (hits == 2) {
                return true // Both ships are sunk
            }
        } else {
            misses++
        }
        return false
    }

    val gameStatus: String
        get() = if (hits == 2) "You Win!" else "Keep Guessing"
}
