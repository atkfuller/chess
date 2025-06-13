package chess;

import java.util.ArrayList;

public class MoveChecker {
    public static ArrayList<ChessMove> oneSpaceCalc(ChessPosition myPosition, int[][] directions, ChessBoard board){
        ArrayList<ChessMove> moves= new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];
            ChessPosition newPosition = new ChessPosition(r, c);
            if(board.insideBoard(newPosition)) {
                if (board.getPiece(newPosition) == null) {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    moves.add(move);
                } else {
                    ChessPiece myPiece=board.getPiece(myPosition);
                    if(board.getPiece(newPosition).getTeamColor()!=myPiece.getTeamColor()){
                        ChessMove move=new ChessMove(myPosition, newPosition, null);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }
    public static ArrayList<ChessMove> multiMoveCalc(ChessPosition position, int[][] movements, ChessBoard board){
        ArrayList<ChessMove> returnMoves= new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        for (int[] dir : movements) {
            int r = row + dir[0];
            int c = col + dir[1];
            ChessPosition newPosition = new ChessPosition(r, c);
            while(board.insideBoard(newPosition)) {
                if(board.getPiece(newPosition) == null){
                    ChessMove move=new ChessMove(position, newPosition, null);
                    returnMoves.add(move);
                }
                else{
                    ChessPiece myPiece=board.getPiece(position);
                    if(board.getPiece(newPosition).getTeamColor()!=myPiece.getTeamColor()){
                        ChessMove move=new ChessMove(position, newPosition, null);
                        returnMoves.add(move);
                    }
                    break;
                }
                r=r+dir[0];
                c=c+dir[1];
                newPosition = new ChessPosition(r, c);
            }
        }
        return returnMoves;
    }
}
