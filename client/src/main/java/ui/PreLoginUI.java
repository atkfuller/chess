package ui;


import java.util.Scanner;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static ui.EscapeSequences.*;


public class PreLoginUI {
    private final LoginClient client;

    public PreLoginUI(String serverUrl) {
        client = new LoginClient(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE+SET_TEXT_BOLD+BLACK_PAWN+" Welcome to THE CHESS GAME "+BLACK_PAWN+" login to play"+RESET_TEXT_COLOR);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        System.out.print("\n" + "CHESS GAME" + ">>> " + SET_TEXT_COLOR_GREEN);
    }
    public void clear() throws Exception {
        client.clear();
    }

}

