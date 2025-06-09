package ui;

import chess.*;
import model.GameData;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameRepl implements ReplPhase {
    private final String serverUrl;
    private final String authToken;
    private final GameData game;
    private final String playerColor;

    public GameRepl(String serverUrl, String authToken, GameData game, String playerColor) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.game = game;
        this.playerColor = playerColor.toUpperCase();
    }

    @Override
    public ReplPhase run() {
        System.out.println(SET_BG_COLOR_LIGHT_GREY + "You're now playing game: " + game.gameName() + RESET_BG_COLOR);
        drawBoard();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            String[] tokens = line.trim().split(" ");
            String command = tokens[0].toLowerCase();

            try {
                switch (command) {
                    case "move" -> {
                        if (tokens.length != 3) {
                            System.out.println("Usage: move <start> <end>  (e.g. move e2 e4)");
                        } else {
                            makeMove(tokens[1], tokens[2]);
                        }
                    }
                    case "redraw" -> drawBoard();
                    case "leave" -> leaveGame();
                    case "exit" -> {
                        System.out.println("Exiting game.");
                        return new PostLoginUI(serverUrl, authToken, game.whiteUsername()); // back to lobby
                    }
                    case "help" -> printHelp();
                    default -> System.out.println("Unknown command. Type 'help'.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private ReplPhase leaveGame() {
        System.out.println("You resigned.");
        return new PostLoginUI(serverUrl, authToken, game.whiteUsername());

    }

    private void drawBoard() {
        if ("WHITE".equals(playerColor)) {
            BoardPrinter.printBoardWhiteView(game.game().getBoard());
        } else {
            BoardPrinter.printBoardBlackView(game.game().getBoard());
        }
    }

    private void makeMove(String from, String to) throws Exception {
        ChessPosition start = pos(from);
        ChessPosition end = pos(to);
        ChessMove move = new ChessMove(start, end, null); // Add promotion if needed
        game.game().makeMove(move);
        System.out.println("Move made: " + from + " to " + to);
        drawBoard();
    }

    private ChessPosition pos(String input) throws Exception {
        if (input.length() != 2)
            throw new Exception("Invalid position: " + input);
        int col = input.charAt(0) - 'a' + 1;
        int row = Integer.parseInt(String.valueOf(input.charAt(1)));
        return new ChessPosition(row, col);
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_CYAN + "\nGAME >>> " + SET_TEXT_COLOR_GREEN);
    }

    private void printHelp() {
        System.out.println(SET_TEXT_COLOR_YELLOW + """
                Available commands:
                - move <start> <end>       (e.g. move e2 e4)
                - redraw                   (refresh the board)
                - resign                   (resign the game)
                - exit                     (exit to game lobby)
                - help
                """ + RESET_TEXT_COLOR);
    }
}

