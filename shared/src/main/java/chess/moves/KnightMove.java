package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMove implements MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-2, -1}, {-2, +1}, {-1, -2}, {-1, +2},
                {+1, -2}, {+1, +2}, {+2, -1}, {+2, +1}
        };
        return MoveChecker.oneSpaceCalc(myPosition, directions, board);
    }
}
