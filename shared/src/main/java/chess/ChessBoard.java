package chess;

import java.util.*;



/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board =new ChessPiece[8][8];
    private HashSet<ChessPosition>blackPieces= new HashSet<ChessPosition>();
    private HashSet<ChessPosition> whitePieces=new HashSet<ChessPosition>();
    private ChessPosition whiteKing;
    private ChessPosition blackKing;

    public void setBlackKing(ChessPosition blackKing) {
        this.blackKing = blackKing;
    }

    public void setWhiteKing(ChessPosition whiteKing) {
        this.whiteKing = whiteKing;
    }

    public ChessPosition getWhiteKing() {
        return whiteKing;
    }
    public ChessPosition getBlackKing() {
        return  blackKing;
    }

    public ChessBoard() {
    }

    public HashSet<ChessPosition> getWhitePieces(){
        return whitePieces;
    }
    public HashSet<ChessPosition> getBlackPieces(){
        return blackPieces;
    }
    public void setWhitePieces(HashSet<ChessPosition> positions){
        whitePieces=positions;
    }
    public void setBlackPieces(HashSet<ChessPosition> positions){
        blackPieces=positions;
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
        if(piece!=null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                whitePieces.add(position);
            } else {
                blackPieces.add(position);
            }
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    whiteKing = position;
                } else {
                    blackKing = position;
                }
            }
        }
        board[8-position.getRow()][position.getColumn()-1]=piece;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board) && Objects.equals(blackPieces, that.blackPieces) && Objects.equals(whitePieces, that.whitePieces)
                && Objects.equals(whiteKing, that.whiteKing) && Objects.equals(blackKing, that.blackKing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(board), blackPieces, whitePieces, whiteKing, blackKing);
    }

    public void movePiece(ChessMove move){
        ChessPosition start=move.getStartPosition();
        ChessPiece myPiece=getPiece(start);
        if(move.getPromotionPiece()!=null){
            myPiece=new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece());
        }
        ChessPosition end=move.getEndPosition();
        addPiece(end, myPiece);
        removePiece(start);
    }

    public ChessBoard clone(){
        ChessPiece[][] newBoard = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece originalPiece = this.board[row][col];
                if (originalPiece != null) {
                    newBoard[row][col] = originalPiece; // or just copy reference if immutable
                }
            }
        }

        ChessBoard clonedBoard = new ChessBoard();
        clonedBoard.setBoard(newBoard); // you’ll need a method to do this, or set directly
        clonedBoard.setBlackPieces((HashSet<ChessPosition>) blackPieces.clone());
        clonedBoard.setWhitePieces((HashSet<ChessPosition>) whitePieces.clone());
        clonedBoard.setBlackKing(blackKing);
        clonedBoard.setWhiteKing(whiteKing);
        return clonedBoard;
}

    private void setBoard(ChessPiece[][] newBoard) {
        board=newBoard;
    }
    }
