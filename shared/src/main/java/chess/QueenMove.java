package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMove implements MovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves=new ArrayList<ChessMove>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };
        moves.addAll(new BishopMove().pieceMoves(board, myPosition));
        moves.addAll(new RookMove().pieceMoves(board, myPosition));
        return moves;
    }
}
