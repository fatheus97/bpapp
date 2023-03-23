package modules;

import com.google.gson.JsonObject;
import dbModel.Organization;
import dbModel.Player;
import dbModel.Role;
import dbModel.Showable;
import gui.PlaceholderTextField;
import gui.RosterTableModel;
import org.jsoup.nodes.Document;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUI extends JFrame {

    private Organization org;
    private Document doc;
    private PlaceholderTextField txtInput;
    private JLabel lblHeader;
    private JButton btnLoadOrg;
    private JButton btnConfirm;
    private JTable tblContent;
    private JPanel contentPanel;
    private JComboBox cmbRegion;
    private JComboBox cmbTournament;
    private JComboBox cmbTeam;

    public GUI() {
        // set frame properties
        setTitle("LOLPrep");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseManager.insertObject(org);
                DatabaseManager.closeSession();
                DatabaseManager.closeSessionFactory();
            }
        });

        // create components
        txtInput = new PlaceholderTextField(20);
        txtInput.setPlaceholder("Enter Organization name");
        lblHeader = new JLabel();
        btnLoadOrg = new JButton("Load");
        btnConfirm = new JButton("Confirm");
        cmbRegion = new JComboBox<>(new String[]{"China", "EMEA", "International", "Korea", "North America"});
        cmbTournament = new JComboBox<>();
        cmbTeam = new JComboBox<>();

        // set layout
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Region:"));
        inputPanel.add(cmbRegion);
        inputPanel.add(new JLabel("League:"));
        inputPanel.add(cmbTournament);
        inputPanel.add(new JLabel("Team:"));
        inputPanel.add(cmbTeam);
        inputPanel.add(btnLoadOrg);
        add(inputPanel, BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(lblHeader, BorderLayout.NORTH);
        contentPanel.add(btnConfirm,BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // add action listeners
        btnLoadOrg.addActionListener(e -> loadOrg((String) cmbTeam.getSelectedItem()));
        btnConfirm.addActionListener(e -> makePrep());
        cmbRegion.addActionListener(e -> updateTournaments());
        cmbTournament.addActionListener(e -> updateTeams());

        pack();
    }

    private void updateTournaments() {
        String region = (String) cmbRegion.getSelectedItem();

        String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                "&format=json" +
                "&tables=Tournaments" +
                "&fields=OverviewPage" +
                "&where=Region='" + region + "'" +
                " AND DateStart IS NOT NULL" +
                " AND DateStart >= " + Time.getUTCString(365, DateTimeFormatter.ofPattern("yyyyMMddhhmmss")) +
                "&order_by=DateStart DESC";
        String jsonString = "";
        System.out.println(urlString);
        try {
            jsonString = Network.getJSONFromURLString(urlString);
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(GUI.this, "IO error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println(jsonString);
        JsonObject jsonObject = DataExtractor.getJsonObject(jsonString);

        cmbTournament.removeAllItems();
        jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
            String leagueName = jsonElement.getAsJsonObject()
                    .get("title").getAsJsonObject()
                    .get("OverviewPage").getAsString();
            cmbTournament.addItem(leagueName);
        });

        pack();
    }

    private void updateTeams() {
        String tournament = (String) cmbTournament.getSelectedItem();

        String urlString = "https://lol.fandom.com/api.php?action=cargoquery" +
                "&format=json" +
                "&tables=TournamentRosters" +
                "&fields=Team" +
                "&where=TournamentRosters.OverviewPage='" + tournament + "'" +
                "&order_by=Team";
        String jsonString = "";
        System.out.println(urlString);
        try {
            jsonString = Network.getJSONFromURLString(urlString);
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(GUI.this, "IO error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println(jsonString);
        JsonObject jsonObject = DataExtractor.getJsonObject(jsonString);

        cmbTeam.removeAllItems();
        jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
            String teamName = jsonElement.getAsJsonObject()
                    .get("title").getAsJsonObject()
                    .get("Team").getAsString();
            cmbTeam.addItem(teamName);
        });

        pack();
    }

    public Document getDoc() {
        return doc;
    }

    private void makePrep() {
        try {
            DataExtractor.fetchAccountsToPlayers(org);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(GUI.this, "IO error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*List<Player> players = org.getRoster().getPlayers();
        for (Player p : players) {
            GUI.showData(p);
        }*/

        DataExtractor.fetchMatchesToAccounts(org);

        doc = OutputMaker.makeHTMLOutput(org);

        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("LOLPrep", ".html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // write the HTML document to the temporary file
        try (BufferedWriter writer = Files.newBufferedWriter(tempFilePath)) {
            writer.write(doc.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.addLinkToPrep(tempFilePath);
    }

    private void loadOrg(String orgName) {
        DataExtractor.setTournament((String) cmbTournament.getSelectedItem());

        org = DatabaseManager.getObject(Organization.class, orgName);

        if (org != null){
            try {
                DataExtractor.updateOrganization(org);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(GUI.this, "IO error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                org = DataExtractor.getOrganization(orgName);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(GUI.this, "IO error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        showData(org);
    }

    public void showData(Showable data) {
        lblHeader.setText(data.getHeader());
        tblContent = new JTable() {
            private final List<Role> roles = org.getLastRoster().getPlayers().stream()
                    .map(Player::getRole)
                .distinct()
                .sorted()
                .toList();

            private final Map<Role, List<String>> roleToNames = org.getLastRoster().getPlayers().stream()
                    .collect(Collectors.groupingBy(Player::getRole,
                             Collectors.mapping(Player::getName, Collectors.toList())));
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1) {
                    Role role = roles.get(row);
                    List<String> names = roleToNames.get(role);
                    if (names.size() > 1) {
                        JComboBox<String> comboBox = new JComboBox<>(names.toArray(new String[0]));
                        comboBox.setEditable(true);
                        return new DefaultCellEditor(comboBox);
                    }
                }
                return super.getCellEditor(row, column);
            };

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 1) {
                    Role role = roles.get(row);
                    List<String> names = roleToNames.get(role);
                    if (names.size() > 1) {
                        JComboBox<String> comboBox = new JComboBox<>(names.toArray(new String[0]));
                        this.setRowHeight(comboBox.getPreferredSize().height);
                        return new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(JTable table, Object value,
                                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                                return comboBox;
                            }
                        };
                    }
                }
                return super.getCellRenderer(row, column);
            }
        };
        tblContent.setModel(new RosterTableModel(org.getLastRoster().getPlayers()));

        JScrollPane scrollPane = new JScrollPane(tblContent);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        pack();
    }

    public void addLinkToPrep(Path tempFilePath) {
        JButton btnLinkTo = new JButton("Go to prep");
        btnLinkTo.addActionListener(e -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                if (Files.exists(tempFilePath)) {
                    try {
                        Desktop.getDesktop().browse(tempFilePath.toUri());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    System.err.println("File not found: " + tempFilePath);
                }
            } else {
                System.err.println("Desktop browsing not supported");
            }
        });
        contentPanel.remove(btnConfirm);
        contentPanel.add(btnLinkTo, BorderLayout.SOUTH);
        pack();
    }
}
