package ui;

import model.GameData;

import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ChessClient {
       private ReplPhase phase;
       private final String serverURL;

        public ChessClient(String server) {
            serverURL=server;
        }

        public void run() {
            phase= new PreLoginUI(serverURL);
            while (phase != null) {
                phase = phase.run();
            }
            System.out.println("Goodbye!");
        }
}
