
import kotlin.math.abs

class Board {
    private var enPassantVulnerable: Pair<Int, Int>? = null

    private val board = Array(8) { row ->
        Array(8) { _ ->
            when (row) {
                1 -> Pawn.BLACK
                6 -> Pawn.WHITE
                else -> Pawn.NONE
            }
        }
    }

    fun printBoard() {
        println(sepLine)
        for (i in board.indices) {
            print("${8 - i} | ")
            print(board[i].joinToString(" | ") { it.color.toString() })
            println(" |")
            println(sepLine)
        }
        println(colIndexString)
    }

    fun isValidMove(move: String, isWhite: Boolean): Boolean {
        if (!move.matches(Regex("[a-h][1-8][a-h][1-8]"))) {
            println("Invalid Input")
            return false
        }

        val colFrom = move[0] - 'a'
        val rowFrom = '8' - move[1]
        val colTo = move[2] - 'a'
        val rowTo = '8' - move[3]

        val pawn = board[rowFrom][colFrom]
        if (pawn == Pawn.NONE) {
            println("No ${if (isWhite) "white" else "black"} pawn at ${move.substring(0, 2)}")
            return false
        }

        if ((isWhite && pawn != Pawn.WHITE) || (!isWhite && pawn != Pawn.BLACK)) {
            println("No ${if (isWhite) "white" else "black"} pawn at ${move.substring(0, 2)}")
            return false
        }

        val direction = if (isWhite) -1 else 1
        val startRow = if (isWhite) 6 else 1

        if (colFrom == colTo) {
            if (rowTo == rowFrom + direction && board[rowTo][colTo] == Pawn.NONE) {
                return true
            }
            if (rowFrom == startRow && rowTo == rowFrom + 2 * direction &&
                board[rowFrom + direction][colFrom] == Pawn.NONE && board[rowTo][colTo] == Pawn.NONE) {
                return true
            }
        }

        // Capture logic
        if (abs(colFrom - colTo) == 1 && rowTo == rowFrom + direction) {
            if ((isWhite && board[rowTo][colTo] == Pawn.BLACK) || (!isWhite && board[rowTo][colTo] == Pawn.WHITE)) {
                return true
            }
            // En Passant capture logic
            if (enPassantVulnerable != null && enPassantVulnerable == Pair(colTo, rowFrom + direction)) {
                return true
            }
        }
        println("Invalid Input")
        return false
    }

    fun movePawn(move: String) {
        // Convert the move string to board indices
        val (colFrom, rowFrom, colTo, rowTo) = move.toBoardIndices()

        // En Passant capture logic
        val enPassantRow = if (board[rowFrom][colFrom] == Pawn.WHITE) rowTo + 1 else rowTo - 1
        if (enPassantVulnerable != null && enPassantVulnerable == Pair(colTo, rowTo)) {
            //            println("Removing captured pawn at ${'a' + colTo}${8 - enPassantRow}")
            board[enPassantRow][colTo] = Pawn.NONE // Remove the captured pawn
        }

        // Move the pawn to the new location
        board[rowTo][colTo] = board[rowFrom][colFrom]
        board[rowFrom][colFrom] = Pawn.NONE

        // Update enPassantVulnerable for two-square pawn moves
        enPassantVulnerable = if (abs(rowTo - rowFrom) == 2) Pair(colTo, (rowFrom + rowTo) / 2) else null
    }

    // Helper function to convert move string to board indices
    private fun String.toBoardIndices(): List<Int> {
        val colFrom = this[0] - 'a'
        val rowFrom = '8' - this[1]
        val colTo = this[2] - 'a'
        val rowTo = '8' - this[3]
        return listOf(colFrom, rowFrom, colTo, rowTo)
    }

    fun checkGameOver(): Boolean {
        // Flatten the 2D array to a 1D list and count the number of white and black pawns
        // The flatten() function transforms the 2D array into a single list containing all the elements in row-major order.
        val whitePawns = board.flatten().count { it == Pawn.WHITE }
        val blackPawns = board.flatten().count { it == Pawn.BLACK }

        // Check for win condition: if any white pawn reaches the first rank or all black pawns are captured
        if (board[0].contains(Pawn.WHITE) || blackPawns == 0) {
            println("White Wins!")
            println("Bye!")
            return true
        }

        // Check for win condition: if any black pawn reaches the last rank or all white pawns are captured
        if (board[7].contains(Pawn.BLACK) || whitePawns == 0) {
            println("Black Wins!")
            println("Bye!")
            return true
        }

        // Check for stalemate (draw) condition: if neither player has valid moves
        if (!hasValidMoves(true) || !hasValidMoves(false)) {
            println("Stalemate!")
            println("Bye!")
            return true
        }

        return false
    }

    private fun hasValidMoves(isWhite: Boolean): Boolean {
        // Determine the direction of movement based on the color of the pawn
        // If the pawn is white, it moves up (-1), otherwise down (1).
        val direction = if (isWhite) -1 else 1

        // Check if any pawn of the given color has a valid move
        return board.indices.any { row ->
            board[row].indices.any { col ->
                val pawn = board[row][col]
                // Check if the pawn belongs to the current player
                if ((isWhite && pawn == Pawn.WHITE) || (!isWhite && pawn == Pawn.BLACK)) {
                    // Check forward move
                    val forwardMove = row + direction in board.indices && board[row + direction][col] == Pawn.NONE
                    // Check capture moves
                    val captureMoveLeft = col - 1 in board[row].indices && row + direction in board.indices &&
                            board[row + direction][col - 1] != Pawn.NONE && board[row + direction][col - 1] != pawn
                    val captureMoveRight = col + 1 in board[row].indices && row + direction in board.indices &&
                            board[row + direction][col + 1] != Pawn.NONE && board[row + direction][col + 1] != pawn

                    // Return true if any valid move is found
                    forwardMove || captureMoveLeft || captureMoveRight
                } else {
                    false
                }
            }
        }
    }


    companion object {
        const val title = "Pawns-Only Chess"
        const val sepLine = "  +---+---+---+---+---+---+---+---+"
        const val colIndexString = "    a   b   c   d   e   f   g   h"
    }
}

enum class Pawn(val color: Char) {
    BLACK('B'), WHITE('W'), NONE(' ')
}