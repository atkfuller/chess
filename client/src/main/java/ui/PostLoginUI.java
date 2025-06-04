package ui;

import java.util.Scanner;

import static java.awt.Color.BLUE;
import static ui.EscapeSequences.*;

public class PostLoginUI {
    private final LoggedClient client;
    private final String authToken;

    public PostLoginUI(String serverUrl, String authToken, String name) {
        this.authToken= authToken;
        client = new LoggedClient(serverUrl, authToken, name);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_DARK_GREEN+ "THE CHESS GAME");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if (result.contains("logged out")) {
                    break;
                }
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
        System.out.print("\n" + SET_TEXT_COLOR_WHITE+"CHESS GAME" + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}

