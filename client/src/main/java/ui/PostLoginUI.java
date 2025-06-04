package ui;

import java.util.Scanner;

import static java.awt.Color.BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class PostLoginUI {
    private final LoggedClient client;
    private final String authToken;

    public PostLoginUI(String serverUrl, String authToken) {
        this.authToken= authToken;
        client = new LoggedClient(serverUrl, authToken);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 THE CHESS GAME");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
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

}

