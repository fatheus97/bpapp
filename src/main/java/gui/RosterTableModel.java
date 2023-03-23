package gui;

import dbModel.Player;
import dbModel.Role;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RosterTableModel extends AbstractTableModel {
    private List<Role> roles;
    private Map<Role, List<String>> roleToNames;

    public RosterTableModel(List<Player> players) {
        this.roles = players.stream()
                .map(Player::getRole)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        this.roleToNames = players.stream()
                .collect(Collectors.groupingBy(Player::getRole,
                        Collectors.mapping(Player::getName, Collectors.toList())));
    }

    @Override
    public int getRowCount() {
        return roles.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Role";
            case 1:
                return "Name";
            default:
                throw new IllegalArgumentException("Invalid column index");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Role.class;
            case 1:
                return String.class;
            default:
                throw new IllegalArgumentException("Invalid column index");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Role role = roles.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return role;
            case 1:
                List<String> names = roleToNames.get(role);
                return names.get(0);

            default:
                throw new IllegalArgumentException("Invalid column index");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // TODO: 23.03.2023  
        if (columnIndex == 1) {
            String name = (String) aValue;
            Role role = roles.get(rowIndex);
            List<String> names = roleToNames.get(role);
            names.add(name);
            roleToNames.put(role, names);
        }
    }
}
