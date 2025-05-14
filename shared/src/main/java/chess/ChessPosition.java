package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int positionRow;
    private final int positionCol;
    public ChessPosition(int row, int col) {
        this.positionRow= row;
        this.positionCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return positionRow;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition position = (ChessPosition) o;
        return positionRow == position.positionRow && positionCol == position.positionCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionRow, positionCol);
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return positionCol;
    }
}
