package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMove implements MovesCalculator {
    @java.lang.Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves;
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
        Collection<ChessMove> moves=new ArrayList<ChessMove>();
        int row=myPosition.getRow()+rowIncr;
        int col=myPosition.getColumn();
        ChessPosition newPosition= new ChessPosition(row,col);
        if(board.insideBoard(newPosition) && board.getPiece(newPosition)==null) {
                if (newPosition.getRow() == endRow) {
                    moves.addAll(promotePawn(board, myPosition, newPosition, true));
                } else {
                    moves.addAll(promotePawn(board, myPosition, newPosition, false));
                }
                if (myPosition.getRow() == startRow) {
                    row = row + rowIncr;
                    newPosition = new ChessPosition(row, col);
                    addMove(board, myPosition, endRow, newPosition, moves);
                }
            }
        //diagonally left
        row=myPosition.getRow()+rowIncr;
        col=myPosition.getColumn()-1;
        pawnTakeCalc(board, myPosition, endRow, row, col, myPiece, moves);
        //diagonally right
        col=myPosition.getColumn()+1;
        newPosition= new ChessPosition(row,col);
        pawnTakeCalc(board, myPosition, endRow, row, col, myPiece, moves);
        return moves;
    }

    private void addMove(ChessBoard board, ChessPosition myPosition, int endRow, ChessPosition newPosition, Collection<ChessMove> moves) {
        if (board.insideBoard(newPosition) && board.getPiece(newPosition) == null) {
            if (newPosition.getRow() == endRow) {
                moves.addAll(promotePawn(board, myPosition, newPosition, true));
            } else {
                moves.addAll(promotePawn(board, myPosition, newPosition, false));
            }
        }
    }

    private void pawnTakeCalc(ChessBoard board, ChessPosition myPosition, int endRow,
                              int row, int col, ChessPiece myPiece, Collection<ChessMove> moves) {
        ChessPosition newPosition;
        newPosition= new ChessPosition(row, col);
        addMove(board, myPosition, endRow, newPosition,moves);
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
