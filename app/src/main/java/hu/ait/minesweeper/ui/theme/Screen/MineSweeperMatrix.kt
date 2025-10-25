package com.ait.minesweeperr.ui.theme.Screen

import androidx.compose.foundation.Image
import com.example.mine.R
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.ait.minesweeper.ui.theme.Screen.MatrixCell


@Composable
fun MineSweeperMatrix(board: List<List<MatrixCell>>, onCellClick: (Int, Int) -> Unit) {
    Column {
        for (rowIndex in board.indices) {
            Row {
                for (colIndex in board[rowIndex].indices) {
                    MinesweeperCell(
                        cell = board[rowIndex][colIndex],
                        onClick = { onCellClick(rowIndex, colIndex) }
                    )
                }
            }
        }
    }
}

@Composable
fun MinesweeperCell(cell: MatrixCell, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable {
                onClick()
                if (cell.isFlagged) {
                    cell.isFlagged = false
                }

            },

        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.white_cell),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        when {
            cell.isVisible -> {
                if (cell.isMine) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black))
                } else if (cell.neighboringMines == 0) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Green))
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Blue.copy(alpha = 0.3f)))
                }

                Text(
                    text = if (cell.isMine) "\uD83D\uDCA3" else if (cell.neighboringMines > 0) "${cell.neighboringMines}" else "",
                    color = if (cell.isMine) Color.White else Color.Black,
                    style = TextStyle(fontSize = 18.sp)
                )
            }
            cell.isFlagged -> {
                Text(
                    text = "🚩",
                    fontSize = 20.sp,
                    color = Color.Red
                )
            }
        }
    }
}

