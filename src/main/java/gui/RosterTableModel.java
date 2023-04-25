package gui;

import dbModel.Player;
import dbModel.Role;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class RosterTableModel extends CellBasedTableModel {
    private final List<Role> roles;
    private final Map<Role, List<String>> roleToNames;

    public RosterTableModel(List<Player> players) {
        this.roles = players.stream()
                .map(Player::getRole)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        this.roleToNames = players.stream()
                .collect(Collectors.groupingBy(Player::getRole,
                        Collectors.mapping(Player::getName, Collectors.toList())));
        roleToNames.forEach((role, strings) -> {
            if(strings.size() > 1) {
                int i = role.ordinal();
                addEditableCell(new Cell(i, 1));
            }
        });
    }

    @Override
    public int getRowCount() {
        return 5;
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
    public Object getValueAt(int row, int column) {
        Role role = roles.get(row);
        switch(column) {
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
    public void setValueAt(Object value, int row, int column) {
        if (column == 1) {
            String name = (String) value;
            Role role = roles.get(row);
            List<String> names = roleToNames.get(role);
            int i = names.indexOf(name);
            if (i > 0) {
                Collections.swap(names, 0, i);
            }
            roleToNames.put(role, names);
        }
    }

    public JComboBox<String> getJComboBox(Cell cell) {
        String[] names = getNames(cell);
        System.out.println(Arrays.toString(names));
        return new JComboBox<String>(names);
    }

    private String[] getNames(Cell cell) {
        System.out.println(roleToNames);
        Role role = roles.get(cell.getRow());
        System.out.println(role);
        return roleToNames.get(role).toArray(new String[0]);
    }
}