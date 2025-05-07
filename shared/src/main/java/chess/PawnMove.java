package chess;

import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMove implements MovesCalculator{
    @java.lang.Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();
        int whiteRow = 1;
        int blackRow = -1;
        ChessPiece myPiece = board.getPiece(myPosition);
        //first moves
        if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            int startRow = 7;
            int endRow=1;
            moves = moveByColor(board, myPosition, startRow, endRow,blackRow);
        }
        else{
            int startRow=2;
            int endRow=8;
            moves = moveByColor(board, myPosition, startRow, endRow, whiteRow);
        }
        return moves;
    }
    private Collection<ChessMove> moveByColor(ChessBoard board, ChessPosition myPosition, int startRow,int endRow, int rowIncr) {
        ChessPiece myPiece=board.getPiece(myPosition);
        ChessPiece.PieceType type= null;
        Collection<ChessMove> moves=new ArrayList<ChessMove>();
        int row=myPosition.getRow()+rowIncr;
        int col=myPosition.getColumn();
        ChessPosition newPosition= new ChessPosition(row,col);
        if(!board.outOfBoard(newPosition)){
            if(board.getPiece(newPosition)==null) {
                if (newPosition.getRow() == endRow) {
                    moves.addAll(promotePawn(board, myPosition, newPosition, true));
                } else {
                    moves.addAll(promotePawn(board, myPosition, newPosition, false));
                }
                if (myPosition.getRow() == startRow) {
                    row = row + rowIncr;
                    ;
                    newPosition = new ChessPosition(row, col);
                    if (!board.outOfBoard(newPosition) && board.getPiece(newPosition) == null) {
                        if (newPosition.getRow() == endRow) {
                            moves.addAll(promotePawn(board, myPosition, newPosition, true));
                        } else {
                            moves.addAll(promotePawn(board, myPosition, newPosition, false));
                        }
                    }
                }
            }
        }
        //diagonally left
        row=myPosition.getRow()+rowIncr;
        col=myPosition.getColumn()-1;
        newPosition= new ChessPosition(row,col);
        if(!board.outOfBoard(newPosition)&& board.getPiece(newPosition)!=null&& board.getPiece(newPosition).getTeamColor()!=myPiece.getTeamColor()){
            if(newPosition.getRow()==endRow) {
                moves.addAll(promotePawn(board, myPosition, newPosition, true));
            }
            else{
                moves.addAll(promotePawn(board, myPosition, newPosition, false));
            }
        }
        //diagonally right
        col=myPosition.getColumn()+1;
        newPosition= new ChessPosition(row,col);
        if(!board.outOfBoard(newPosition)&& board.getPiece(newPosition)!=null&&  board.getPiece(newPosition).getTeamColor()!=myPiece.getTeamColor()){
            if(newPosition.getRow()==endRow) {
                moves.addAll(promotePawn(board, myPosition, newPosition, true));
            }
            else{
                moves.addAll(promotePawn(board, myPosition, newPosition, false));
            }
        }
        return moves;
    }
    private Collection<ChessMove> promotePawn(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, boolean promote) {
        Collection<ChessMove> moves= new ArrayList<>();
        if(promote) {
            for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                if(type!= ChessPiece.PieceType.KING && type!= ChessPiece.PieceType.PAWN) {
                    ChessMove move = new ChessMove(myPosition, newPosition, type);
                    moves.add(move);
                }
            }
        }
        else{
            ChessMove move=new ChessMove(myPosition, newPosition, null);
            moves.add(move);
        }
        return moves;
    }

}
