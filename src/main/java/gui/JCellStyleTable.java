package gui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JCellStyleTable extends JTable {


    private final Map<Cell, TableCellRenderer> renderersMap = new HashMap<>();

    private final Map<Cell, TableCellEditor> editorsMap = new HashMap<>();

    public JCellStyleTable() {
        super();
    }

    public JCellStyleTable(TableModel dm) {
        super(dm);
    }

    public Map<Cell, TableCellRenderer> getRenderersMap() {
        return renderersMap;
    }

    public Map<Cell, TableCellEditor> getEditorsMap() {
        return editorsMap;
    }

    public void putRenderer(Cell cell, TableCellRenderer renderer) {
        renderersMap.put(Objects.requireNonNull(cell), renderer);
    }

    public TableCellRenderer getRenderer(Cell cell) {
        return renderersMap.get(Objects.requireNonNull(cell));
    }

    public void putEditor(Cell cell, TableCellEditor editor) {
        editorsMap.put(Objects.requireNonNull(cell), editor);
    }

    public TableCellEditor getEditor(Cell cell) {
        return editorsMap.get(Objects.requireNonNull(cell));
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer result = renderersMap.get(new Cell(row, column));
        return result == null ? super.getCellRenderer(row, column) : result;
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        return editorsMap.get(new Cell(row, column)); // when null - no editing
    }
}
