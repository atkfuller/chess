package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard;
    private boolean isOver;
    public ChessGame() {
        gameBoard=new ChessBoard();
        gameBoard.resetBoard();
        teamTurn=TeamColor.WHITE;
        isOver=false;
    }
    public ChessGame(ChessBoard board, TeamColor color){
        gameBoard= board;
        teamTurn=color;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
      teamTurn=team;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //gives all the validMoves at the startPosition
        //null if no piece is this position
        ArrayList<ChessMove> moves=new ArrayList<>();
        Collection<ChessMove> valid=new ArrayList<>();
        ChessPiece currPiece= gameBoard.getPiece(startPosition);
        if(currPiece==null){
            return null;
        }
        moves= (ArrayList<ChessMove>) currPiece.pieceMoves(gameBoard, startPosition);
        valid= (Collection<ChessMove>) moves.clone();
        for(ChessMove move: moves){
            ChessBoard testBoard=gameBoard.clone();
            testBoard.movePiece(move);
            ChessGame simulatedGame= new ChessGame(testBoard, this.getTeamTurn());
            if(simulatedGame.isInCheck(currPiece.getTeamColor())){
                valid.remove(move);
            }
        }
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start=move.getStartPosition();
        ChessPiece piece=gameBoard.getPiece(start);
        Collection<ChessMove> moves=validMoves(start);
        if(moves==null){
            throw new InvalidMoveException("invalid move");
        }
        if(piece.getTeamColor()!=teamTurn) {
            throw new InvalidMoveException("not players turn");
        }
        if(!moves.contains(move)){
            throw new InvalidMoveException("invalid move");
        }


        else{
            gameBoard.movePiece(move);
        }
        if(teamTurn==TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //check what position the teamColor's king is in
        // then check all the validmoves of the opposing team players to see if it has a move to that kings position.
        Collection<ChessPosition> oppPieces;
        ChessPosition kingPos;
        if(teamColor==TeamColor.WHITE){
            oppPieces=gameBoard.getBlackPieces();
            kingPos=gameBoard.getWhiteKing();
        }
        else{
            oppPieces=gameBoard.getWhitePieces();
            kingPos=gameBoard.getBlackKing();
        }
        for(ChessPosition thisPosition: oppPieces){
            ChessPiece piece=gameBoard.getPiece(thisPosition);
            Collection<ChessMove> moves=piece.pieceMoves(gameBoard, thisPosition);
            for(ChessMove move: moves){
                ChessPosition end = move.getEndPosition();
                if(end.getRow()==kingPos.getRow() && end.getColumn()==kingPos.getColumn()){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //cycle through all the kings valid moves and check each position if it is still in check.
        Collection<ChessMove> kingMoves;
        ChessPosition kingPos;
        if (!isInCheck(teamColor)) {
            return false; // Can't be in checkmate if not in check
        }
        Collection<ChessPosition> pieces;
        if(teamColor==TeamColor.WHITE){
            pieces=gameBoard.getWhitePieces();
        }
        else{
            pieces=gameBoard.getBlackPieces();
        }
        for(ChessPosition pos: pieces){
            Collection<ChessMove> moves=this.validMoves(pos);
            if(!moves.isEmpty()){
                return false;
            }
        }
        isOver=true;
        return true;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //checks if all the team current pieces have no valid moves use the validMove method
        if(this.isInCheckmate(teamColor)){
            return false;
        }
        Collection<ChessPosition> pieces;
        if(teamColor==TeamColor.WHITE){
            pieces=gameBoard.getWhitePieces();
        }
        else{
            pieces=gameBoard.getBlackPieces();
        }
        for(ChessPosition pos: pieces){
            Collection<ChessMove> moves=this.validMoves(pos);
            if(!moves.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard=board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
    public boolean isGameOver(){return isOver;}
    public void setGameOver(){isOver=true;}
}
