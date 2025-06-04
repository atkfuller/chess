package ui;

import java.util.Scanner;

import static java.awt.Color.BLUE;

public class PreLoginUI {
    private final LoginClient client;

    public PreLoginUI(String serverUrl) {
        client = new LoginClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to THE CHESS GAME. login to play");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
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

    public void notify(Notification notification) {
        System.out.println(RED + notification.message());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }

}
}
