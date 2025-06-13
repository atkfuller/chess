package ui;

import chess.*;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameREPL {
    private final String serverUrl;
    private final String authToken;
    private final GameData game;
    private final String playerColor;

    public GameREPL(String serverUrl, String authToken, GameData game, String playerColor) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.game = game;
        this.playerColor = playerColor.toUpperCase();
    }

    public ReplPhase eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        while (true) {
            try {
                return switch (cmd) {
                    case "move" -> {
                        if (tokens.length != 3) {
                            System.out.println("Expected: move <start> <end>  (e.g. move e2 e4)");
                        } else {
                            try {
                                makeMove(tokens[1], tokens[2]);
                            }catch(Exception e){
                                System.out.println("Error making move: " + e.getMessage());
                            }
                        }
                        yield thisPhase();
                    }
                    case "redraw" -> {
                        drawBoard(null);
                        yield thisPhase();
                    }
                    case "leave" -> leaveGame();
                    case "highlight" ->{
                        if(tokens.length!=2){
                            System.out.println("Expected: highlight <position>  (e.g. move e2 e4)");
                        }
                        else {
                            legalMoves(tokens[1]);
                        }
                        yield thisPhase();
                    }

                    case "help" ->{
                        printHelp();
                        yield thisPhase();
                    }
                    default -> {
                        System.out.println("Unknown command. Type 'help'.");
                        yield thisPhase();
                    }
                };
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private void legalMoves(String posStr) throws Exception {
        ChessPosition position= pos(posStr);
        Collection<ChessMove> moves=game.game().validMoves(position);
        drawBoard((ArrayList<ChessMove>) moves);

    }
    private ReplPhase leaveGame() throws Exception {
        System.out.println("You resigned.");
        return new PostLoginUI(serverUrl, authToken, game.whiteUsername());

    }

    private void drawBoard(ArrayList<ChessMove> moves) {
        if ("WHITE".equals(playerColor)) {
            BoardPrinter.printBoardWhiteView(game.game().getBoard(), moves);
        } else {
            BoardPrinter.printBoardBlackView(game.game().getBoard(), moves);
        }
    }

    private void makeMove(String from, String to) throws Exception {
        ChessPosition start = pos(from);
        ChessPosition end = pos(to);
        ChessMove move = new ChessMove(start, end, null);
        ChessPiece piece=game.game().getBoard().getPiece(start);
        String color;
        if(piece.getTeamColor()== ChessGame.TeamColor.WHITE) {
            color = "WHITE";
        }
        else {
            color = "BLACK";
        }
        if(!color.equals(playerColor)){
            throw new ClientException(400,"Error: not your piece");
        }
        try {// Add promotion if needed
            game.game().makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ClientException(400, String.join("Error", e.getMessage()));
        }
        System.out.println("Move made: " + from + " to " + to);
        drawBoard(null);
    }

    private ChessPosition pos(String input) throws Exception {
        if (input.length() != 2) {
            throw new ClientException(400, "Error: Invalid position: " + input);
        }
        int col = input.charAt(0) - 'a' + 1;
        int row = Integer.parseInt(String.valueOf(input.charAt(1)));
        return new ChessPosition(row, col);
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_MAGENTA + "\nGAME >>> " + SET_TEXT_COLOR_GREEN);
    }

    public void printHelp() {
        System.out.println(SET_TEXT_COLOR_YELLOW + """
                Available commands:
                - move <start> <end>       (e.g. move e2 e4)
                - redraw                   (refresh the board)
                - leave                    (leave the game)
                - highlight <position>      (highlight moves)
                - help
                """ + RESET_TEXT_COLOR);
    }
    private ReplPhase thisPhase() {
        return new GameUI(serverUrl,authToken,game,playerColor);
    }
}

