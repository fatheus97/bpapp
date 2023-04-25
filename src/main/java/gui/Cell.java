package gui;

import java.util.Arrays;

public class Cell {
    private final int row, col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell another) {
            return another.col == col && another.row == row;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[] {row, col});
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
