package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe {
    final static Scanner scanner = new Scanner(System.in);
    private static int xNum = 0;
    private static int oNum = 0;
    private static int _Num = 9;

    public static void main(String[] args) {
        // write your code here
        char[] board = initBoard();
        boolean gameStatus = false;
        do {
            if (fight(board)) {
                gameStatus = checkBoard(board);
            }
        } while (!gameStatus);
    }

    public static void print(char[] board) {
        System.out.println("---------");
        System.out.printf("| %c %c %c |\n", board[0], board[1], board[2]);
        System.out.printf("| %c %c %c |\n", board[3], board[4], board[5]);
        System.out.printf("| %c %c %c |\n", board[6], board[7], board[8]);
        System.out.println("---------");
    }

    public static char[] initBoard() {
        char[] board = new char[9];
        Arrays.fill(board, ' ');
        print(board);
        return board;
    }

    public static boolean fight(char[] board) {
        System.out.print("Enter the coordinates: ");
        String[] nextCoordinates = scanner.nextLine().split(" ");
        if (nextCoordinates.length == 2) {
            try {
                int y = Integer.parseInt(nextCoordinates[0]) - 1;
                int x = Integer.parseInt(nextCoordinates[1]) - 1;
                if (x < 0 || x > 2 || y < 0 || y > 2) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    return false;
                }
                x =  2 - x; // tricky method
                if (board[x * 3 + y] != ' ') {
                    System.out.println("This cell is occupied! Choose another one!");
                    return false;
                }
                if (xNum <= oNum) {
                    board[x * 3 + y] = 'X';
                    xNum++;
                } else {
                    board[x * 3 + y] = 'O';
                    oNum++;
                }
                _Num -= 1;
                print(board);
                return true;
            } catch (NumberFormatException e) {
                System.out.println("You should enter numbers!");
                return false;
            }
        } else {
            System.out.println("The coordinates should consist of 2 values!");
            return false;
        }
    }

    private static boolean checkBoard(char[] board) {
        int xWins = 0;
        int oWins = 0;
        for (int i = 0; i < 9; i += 3) {
            if (board[i] == board[i+1] && board[i] == board[i+2]) {
                if (board[i] == 'X') {
                    xWins++;
                } else if (board[i] == 'O') {
                    oWins++;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[i] == board[i+3] && board[i] == board[i+6]) {
                if (board[i] == 'X') {
                    xWins++;
                } else if (board[i] == 'O') {
                    oWins++;
                }
            }
        }
        if ((board[0] == board[4] && board[0] == board[8]) || (board[2] == board[4] && board[2] == board[6])) {
            if (board[4] == 'X') {
                xWins++;
            } else if (board[4] == 'O') {
                oWins++;
            }
        }
        if (xWins == 0 && oWins == 0 && _Num == 0) {
            System.out.println("Draw");
            return true;
        } else if (xWins > 0) {
            System.out.println("X wins");
            return true;
        } else if (oWins > 0) {
            System.out.println("O wins");
            return true;
        }
        return false;
    }
}
