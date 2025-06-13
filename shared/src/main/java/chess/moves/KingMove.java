package chess.moves;
import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMove implements MovesCalculator {
    @java.lang.Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves=new ArrayList<ChessMove>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        return MoveChecker.oneSpaceCalc(myPosition, directions, board);
    }

}
