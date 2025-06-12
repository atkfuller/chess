package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private ChessMove move;
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, String vistorName, ChessGame.TeamColor playerColor, ChessMove move) {
        super(commandType, authToken, gameID, vistorName, playerColor);
        this.move= move;
    }
    public ChessMove getMove(){return move;}
}
