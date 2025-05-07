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
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            ChessPosition newPosition = new ChessPosition(r, c);
            while(!board.outOfBoard(newPosition)) {
                if(board.getPiece(newPosition) == null){
                    ChessMove move=new ChessMove(myPosition, newPosition, null);
                    moves.add(move);
                }
                else{
                    ChessPiece myPiece=board.getPiece(myPosition);
                    if(board.getPiece(newPosition).getTeamColor()!=myPiece.getTeamColor()){
                        ChessMove move=new ChessMove(myPosition, newPosition, null);
                        moves.add(move);
                    }
                    break;
                }
                r=r+dir[0];
                c=c+dir[1];
                newPosition = new ChessPosition(r, c);
            }
        }
        return moves;
    }
}
