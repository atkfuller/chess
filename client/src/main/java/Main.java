import chess.*;
import server.MySqlDAOsProvider;
import server.Server;
import ui.BoardPrinter;
import ui.ChessClient;
import ui.PreLoginUI;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var serverUrl = "http://localhost:8080";
        Server myServer= new Server(new MySqlDAOsProvider());
        myServer.run(8080);
        ChessClient client= new ChessClient(serverUrl);
        client.run();
    }
}