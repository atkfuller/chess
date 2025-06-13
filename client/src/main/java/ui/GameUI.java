package ui;
import model.GameData;
import ui.GameREPL;
import ui.LoggedClient;
import ui.ReplPhase;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class GameUI implements ReplPhase {
    private final GameREPL client;
    private final GameData game;
    public GameUI(String serverUrl, String authToken, GameData game, String playerColor) {
        this.game=game;
        this.client = new GameREPL(serverUrl, authToken, game, playerColor);
    }

    @Override
    public ReplPhase run() {
        System.out.println(SET_BG_COLOR_LIGHT_GREY + "You're now playing game: " + game.gameName() + RESET_BG_COLOR);
        System.out.println(SET_BG_COLOR_DARK_GREEN + "THE CHESS GAME");
        client.printHelp();

        Scanner scanner = new Scanner(System.in);
        String result;

        while (true) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                ReplPhase next = client.eval(line);
                if (next != null) {return next;}
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + "CHESS GAME >>> " + SET_TEXT_COLOR_GREEN);
    }
}
