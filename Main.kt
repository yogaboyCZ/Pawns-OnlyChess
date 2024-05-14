package chess
import Board
import java.util.Locale

fun main() {
    val board = Board()
    println(Board.title)
    // Prompting players for their names
    println("First Player's name:")
    val player1Name = readlnOrNull() ?: ""
    println("Second Player's name:")
    val player2Name = readlnOrNull() ?: ""

    // Main game loop
    var currentPlayer = player1Name
    var move: String

    board.printBoard()
    while (true) {
        println("$currentPlayer's turn: ")
        move = readlnOrNull() ?: ""

        // Checking for the exit command
        if (move.lowercase(Locale.getDefault()) == "exit") {
            println("Bye!")
            break
        }

        if (!board.isValidMove(move, currentPlayer == player1Name)) {
            continue
        }

        board.movePawn(move)
        board.printBoard()

        if (board.checkGameOver()) break

        // Switch to the other player's turn
        currentPlayer = if (currentPlayer == player1Name) player2Name else player1Name
    }
}
