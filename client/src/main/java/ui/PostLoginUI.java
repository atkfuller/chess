package ui;

import java.util.Scanner;

import static java.awt.Color.BLUE;
import static ui.EscapeSequences.*;

public class PostLoginUI implements ReplPhase {
    private final LoggedClient client;

    public PostLoginUI(String serverUrl, String authToken, String name) throws Exception {
        this.client = new LoggedClient(serverUrl, authToken, name);
    }

    @Override
    public ReplPhase run() {
        System.out.println(SET_BG_COLOR_DARK_GREEN + "THE CHESS GAME");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result;

        while (true) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + "CHESS GAME >>> " + SET_TEXT_COLOR_GREEN);
            String line = scanner.nextLine();
            try {
                return client.eval(line);
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR);
            }
        }
    }

}
