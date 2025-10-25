package com.ait.minesweeper.ui.theme.Screen

import com.ait.minesweeper.countNeighboringMines
import kotlin.random.Random


data class MatrixCell(
    val isMine: Boolean = false,
    val neighboringMines: Int = 0,
    val isVisible: Boolean = false,
    var isFlagged: Boolean = false
)

fun createBoardWithMines(size: Int, mineCount: Int): List<List<MatrixCell>> {
    val board = MutableList(size) { MutableList(size) { MatrixCell() } }
    var minesPlaced = 0

    // Randomly place mines on the board
    while (minesPlaced < mineCount) {
        val row = Random.nextInt(size)
        val col = Random.nextInt(size)
        if (!board[row][col].isMine) {
            board[row][col] = board[row][col].copy(isMine = true)
            minesPlaced++
        }
    }

    // Count neighboring mines for non-mine cells
    for (row in 0 until size) {
        for (col in 0 until size) {
            if (!board[row][col].isMine) {
                val neighboringMines = countNeighboringMines(board, row, col)
                board[row][col] = board[row][col].copy(neighboringMines = neighboringMines)
            }
        }
    }

    return board
}