package chess.moves;

import chess.ChessPiece;

public class MoveFactory {
    public MovesCalculator getMovesCalculator(ChessPiece.PieceType type) {
        switch(type){
            case PAWN:
                return new PawnMove();
            case KNIGHT:
                return new KnightMove();
            case BISHOP:
                return new BishopMove();
            case ROOK:
                return new RookMove();
            case QUEEN:
                return new QueenMove();
            case KING:
                return new KingMove();
            default:
                return new NullMove();
        }
    }
}
