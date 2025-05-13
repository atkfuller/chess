package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board =new ChessPiece[8][8];
    private ArrayList<ChessPosition> blackPieces=new ArrayList<>();
    private ArrayList<ChessPosition> whitePieces=new ArrayList<>();
    private ChessPosition whiteKing;
    private ChessPosition blackKing;

    public ChessPosition getWhiteKing() {
        return whiteKing;
    }
    public ChessPosition getBlackKing() {
        return  blackKing;
    }

    public ChessBoard() {
        resetBoard();
    }


    public Collection<ChessPosition> getWhitePieces(){
        return whitePieces;
    }
    public Collection<ChessPosition> getBlackPieces(){
        return blackPieces;
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        if(getPiece(position)!=null){
            removePiecefromBoard(getPiece(position), position);
        }
        else{
            if(piece.getTeamColor()== ChessGame.TeamColor.WHITE){
                whitePieces.add(position);
            }
            else{
                blackPieces.add(position);
            }
        }
        if(piece.getPieceType()==ChessPiece.PieceType.KING){
            if(piece.getTeamColor()== ChessGame.TeamColor.WHITE){
                whiteKing=position;
            }
            else{
                blackKing=position;
            }
        }
        board[8-position.getRow()][position.getColumn()-1]=piece;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[8-position.getRow()][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board=new ChessPiece[8][8];
        for(ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            ChessPosition position;
            ChessPiece piece;
            switch(type) {
                case KING:
                    piece=new ChessPiece(ChessGame.TeamColor.WHITE, type);
                    position=new ChessPosition(1,5);
                    addPiece(position, piece);
                    piece=new ChessPiece(ChessGame.TeamColor.BLACK, type);
                    position=new ChessPosition(8,5);
                    addPiece(position, piece);
                    break;
                case QUEEN:
                    piece=new ChessPiece(ChessGame.TeamColor.WHITE, type);
                    position=new ChessPosition(1,4);
                    addPiece(position, piece);
                    piece=new ChessPiece(ChessGame.TeamColor.BLACK, type);
                    position=new ChessPosition(8,4);
                    addPiece(position, piece);
                    break;
                case BISHOP:
                    addDuplicatePiece(ChessGame.TeamColor.WHITE, type, 1, 2);
                    addDuplicatePiece(ChessGame.TeamColor.BLACK, type, 8, 2);
                    break;
                case KNIGHT:
                    addDuplicatePiece(ChessGame.TeamColor.WHITE, type, 1, 1);
                    addDuplicatePiece(ChessGame.TeamColor.BLACK, type, 8, 1);
                    break;
                case ROOK:
                    addDuplicatePiece(ChessGame.TeamColor.WHITE, type, 1, 0);
                    addDuplicatePiece(ChessGame.TeamColor.BLACK, type, 8, 0);
                    break;
                case PAWN:
                    piece=new ChessPiece(ChessGame.TeamColor.WHITE, type);
                    for(int p=1;p<9;p++) {
                        position=new ChessPosition(2,p);
                        addPiece(position, piece);
                    }
                    piece=new ChessPiece(ChessGame.TeamColor.BLACK, type);
                    for(int p=1;p<9;p++) {
                        position=new ChessPosition(7,p);
                        addPiece(position, piece);
                    }
                    break;
            }
        }

    }
    private void addDuplicatePiece(ChessGame.TeamColor color, ChessPiece.PieceType type, int row, int col){
        ChessPosition position = new ChessPosition(row,8-col);
        ChessPiece piece = new ChessPiece(color, type);
        addPiece(position, piece);
        position = new ChessPosition(row,col+1);
        addPiece(position, piece);
    }
    public boolean insideBoard(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
    private void removePiece(ChessPosition position){
        ChessPiece myPiece=getPiece(position);
        board[8-position.getRow()][position.getColumn()-1]=null;
        removePiecefromBoard(myPiece, position);
    }
    private void removePiecefromBoard(ChessPiece myPiece, ChessPosition position){
        if(myPiece.getTeamColor()== ChessGame.TeamColor.WHITE){
            whitePieces.remove(position);
        }
        else{
            blackPieces.remove(position);
        }
    }
    public void movePiece(ChessMove move){
        ChessPosition start=move.getStartPosition();
        ChessPiece myPiece=getPiece(start);
        ChessPosition end=move.getEndPosition();
        addPiece(end, myPiece);
        removePiece(start);
    }
}
