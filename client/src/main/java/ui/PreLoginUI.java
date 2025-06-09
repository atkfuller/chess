package ui;


import java.util.Objects;
import java.util.Scanner;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static ui.EscapeSequences.*;


public class PreLoginUI implements ReplPhase {
    private final LoginClient client;

    public PreLoginUI(String serverUrl) {
        this.client = new LoginClient(serverUrl);
    }

    @Override
    public ReplPhase run() {
        System.out.println(SET_TEXT_COLOR_WHITE + SET_TEXT_BOLD + BLACK_PAWN +
                " Welcome to THE CHESS GAME " + BLACK_PAWN + " login to play" + RESET_TEXT_COLOR);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                var nextPhase = client.eval(line);
                if (nextPhase == null) {
                    return null;
                }
                if (nextPhase != null) return nextPhase;
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
        return null;
    }

    private void printPrompt() {
        System.out.print("\n" + "CHESS GAME" + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}


//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }


