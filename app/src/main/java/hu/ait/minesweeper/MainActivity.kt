package com.ait.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ait.minesweeper.ui.theme.Screen.MatrixCell
import com.ait.minesweeper.ui.theme.Screen.createBoardWithMines
import com.ait.minesweeperr.ui.theme.Screen.MineSweeperMatrix

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinesweeperGame()
        }
    }
}

@Composable
fun MinesweeperGame() {
    val gridSize = 5
    val mineCount = 5
    var board by remember { mutableStateOf(createBoardWithMines(gridSize, mineCount)) }
    var isFirstClick by remember { mutableStateOf(value = true) }
    var isFlagMode by remember { mutableStateOf(value = false) }
    var gameOver by remember { mutableStateOf(false) }
    var gameWon  by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                gameOver && gameWon -> "You Win!"
                gameOver            -> "Game Ended"
                else                -> "Minesweeper"
            },
            fontSize = if (gameOver) 36.sp else 32.sp,
            color = when {
                gameOver && gameWon -> Color(0xFF2E7D32)
                gameOver            -> Color.Red
                else                -> Color.Black
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Flag",
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            Checkbox(
                checked = isFlagMode,
                onCheckedChange = { isChecked ->
                    isFlagMode = isChecked
                }
            )
        }

        MineSweeperMatrix(board) { row, col ->
            if (gameOver) return@MineSweeperMatrix

            if (isFlagMode) {
                board = board.mapIndexed { rIndex, rList ->
                    rList.mapIndexed { cIndex, cell ->
                        if (rIndex == row && cIndex == col) {
                            cell.copy(isFlagged = !cell.isFlagged)
                        } else {
                            cell
                        }
                    }
                }
            } else if (isFirstClick) {
                if (board[row][col].isMine) {
                    gameOver = true
                    board = showAllMines(board)
                } else {
                    board = showFirstClick(board, row, col)
                    isFirstClick = false
                }
            } else {
                if (board[row][col].isMine) {
                    gameOver = true
                    gameWon = false
                    board = showAllMines(board)
                } else {
                    board = board.mapIndexed { rIndex, rList ->
                        rList.mapIndexed { cIndex, cell ->
                            if (rIndex == row && cIndex == col) {
                                cell.copy(isVisible = true)
                            } else {
                                cell
                            }
                        }
                    }
                    if (didWin(board)) { gameOver = true; gameWon = true }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            board = createBoardWithMines(gridSize, mineCount)
            isFirstClick = true
            isFlagMode = false
            gameOver = false
            gameWon = false
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.DarkGray
        )) {
            Text(text = "Reset Game", color = Color.White)
        }
    }
}


fun showAllMines(board: List<List<MatrixCell>>): List<List<MatrixCell>> {
    return board.map { row ->
        row.map { cell ->
            if (cell.isMine) {
                cell.copy(isVisible = true)
            } else {
                cell
            }
        }
    }
}

private fun didWin(board: List<List<MatrixCell>>): Boolean =
    board.flatten().all { it.isMine || it.isVisible }

fun showFirstClick(board: List<List<MatrixCell>>, row: Int, col: Int): List<List<MatrixCell>> {
    val newBoard = board.map { it.toMutableList() }.toMutableList()
    showCells(newBoard, row, col)
    return newBoard
}

fun showCells(board: MutableList<MutableList<MatrixCell>>, row: Int, col: Int) {
    if (row !in board.indices || col !in board[row].indices || board[row][col].isVisible) return
    board[row][col] = board[row][col].copy(isVisible = true)

    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )

    for ((dr, dc) in directions) {
        val newRow = row + dr
        val newCol = col + dc
        if (newRow in board.indices && newCol in board[newRow].indices && !board[newRow][newCol].isMine) {
            board[newRow][newCol] = board[newRow][newCol].copy(isVisible = true)
        }
    }
}



fun countNeighboringMines(board: List<List<MatrixCell>>, row: Int, col: Int): Int {
    val directions = listOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, 0 to 1,
        1 to -1, 1 to 0, 1 to 1
    )
    return directions.count { (dr, dc) ->
        val newRow = row + dr
        val newCol = col + dc
        newRow in board.indices && newCol in board[newRow].indices && board[newRow][newCol].isMine
    }
}
