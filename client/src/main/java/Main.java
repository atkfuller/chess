import chess.*;
import dataaccess.DataAccessException;
import server.Server;
import ui.PreLoginUI;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var server= new Server();
        var port= server.run(8080);
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        PreLoginUI ui= new PreLoginUI(serverUrl);
        ui.clear();
        ui.run();
    }
}