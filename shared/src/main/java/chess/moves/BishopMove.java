package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMove implements MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves=new ArrayList<ChessMove>();
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        return MoveChecker.multiMoveCalc(myPosition, directions, board);
    }

}
