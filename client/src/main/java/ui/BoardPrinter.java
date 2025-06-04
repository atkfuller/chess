package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    public static void printBoardWhiteView(ChessBoard board) {
        System.out.println(ERASE_SCREEN);
        printBoard(board, false);
    }

    public static void printBoardBlackView(ChessBoard board) {
        System.out.println(ERASE_SCREEN);
        printBoard(board, true);
    }

    private static void printBoard(ChessBoard board, boolean flipped) {
        // Correct column and row label order
        String[] colLabels = flipped
                ? new String[]{"h", "g", "f", "e", "d", "c", "b", "a"}
                : new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        int[] rowLabels = flipped
                ? new int[]{1, 2, 3, 4, 5, 6, 7, 8}
                : new int[]{8, 7, 6, 5, 4, 3, 2, 1};

        // Print top column labels
        System.out.print("    ");
        for (String label : colLabels) {
            System.out.print(" " + label + "   ");
        }
        System.out.println();

        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            int row = rowLabels[rowIdx];
            System.out.print(" " + row + " ");
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                int col = flipped ? 8 - colIdx : colIdx + 1;

                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // Light square if (row + col) is even (standard rule)
                boolean isLightSquare = (row + col) % 2 != 0;

                String bg = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                String fg = (piece != null && piece.getTeamColor() == ChessGame.TeamColor.BLACK)
                        ? SET_TEXT_COLOR_BLACK
                        : SET_TEXT_COLOR_WHITE;

                String symbol = getPieceSymbol(piece);
                System.out.print(bg + fg + " " + symbol + " " + RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.println(" " + row);
        }

        // Print bottom column labels
        System.out.print("    ");
        for (String label : colLabels) {
            System.out.print(" " + label + "   ");
        }
        System.out.println();
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return "   ";
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}