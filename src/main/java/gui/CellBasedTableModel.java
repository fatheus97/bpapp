package gui;

import javax.swing.table.DefaultTableModel;
import java.util.Collection;
import java.util.HashSet;

public class CellBasedTableModel extends DefaultTableModel {
    public Collection<Cell> getEditableCells() {
        return editableCells;
    }

    private Collection<Cell> editableCells = new HashSet<>();

    public CellBasedTableModel() {
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editableCells.contains(new Cell(row, column));
    }

    public void addEditableCell(Cell cell) {
        editableCells.add(cell);
    }

    public void removeEditableCell(Cell cell) {
        editableCells.remove(cell);
    }
}