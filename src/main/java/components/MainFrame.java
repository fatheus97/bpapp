package components;

import com.google.gson.JsonObject;
import dbModel.Organisation;
import dbModel.Player;
import dbModel.Roster;
import errorHandling.PlayerNotFoundInLoLProsException;
import gui.JCellStyleTable;
import gui.RosterTable;
import gui.RosterTableModel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private Organisation org;
    private final JButton btnMakePrep;
    private JCellStyleTable tblContent;
    private JScrollPane tblContentScroll;
    private final JPanel contentPanel;
    private final JComboBox<String> cmbRegion;
    private final JComboBox<String> cmbTournament;
    private final JComboBox<String> cmbOrganisation;

    public MainFrame() {
        // set frame properties
        setTitle("LOLPrep");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon("src/main/resources/icon.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            DatabaseManager.closeSession();
            DatabaseManager.closeSessionFactory();
            }
        });

        // create active components
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();
        JButton btnLoadRoster = new JButton("Show organisation's roster");
        btnMakePrep = new JButton("Confirm roster to continue");
        cmbRegion = new JComboBox<>(new String[]{"EMEA"});
        cmbTournament = new JComboBox<>();
        cmbOrganisation = new JComboBox<>();


        // set layout
        wrapperPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        add(wrapperPanel,new GridBagConstraints());
        inputPanel.add(new JLabel("Region:"));
        cmbRegion.setPreferredSize(new Dimension(128, (int) cmbRegion.getPreferredSize().getHeight()));
        inputPanel.add(cmbRegion);
        inputPanel.add(new JLabel("League:"));
        cmbTournament.setPreferredSize(new Dimension(512, (int) cmbRegion.getPreferredSize().getHeight()));
        inputPanel.add(cmbTournament);
        inputPanel.add(new JLabel("Team:"));
        cmbOrganisation.setPreferredSize(new Dimension(128, (int) cmbRegion.getPreferredSize().getHeight()));
        inputPanel.add(cmbOrganisation);
        inputPanel.add(btnLoadRoster);
        wrapperPanel.add(inputPanel, BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(608, 342));
        contentPanel.add(btnMakePrep,BorderLayout.SOUTH);
        wrapperPanel.add(contentPanel, BorderLayout.CENTER);

        // add action listeners
        btnLoadRoster.addActionListener(e -> {
            JDialog waitDialog = new JDialog(this, "Loading current line-up...", Dialog.ModalityType.APPLICATION_MODAL);
            initWaitDialog(waitDialog);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                private String errorMessage = null;

                @Override
                protected Void doInBackground() {
                    try {
                        loadRoster((String) cmbOrganisation.getSelectedItem());
                        showRoster();
                    } catch (UnsupportedOperationException e) {
                        errorMessage = "The Roster of " + cmbOrganisation.getSelectedItem() + " for " + cmbTournament.getSelectedItem() + " was not announced yet.";
                        // TODO: 07.04.2023 manually add team's roster
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void done() {
                    waitDialog.dispose();
                    if (errorMessage != null) {
                        JOptionPane.showMessageDialog(MainFrame.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        btnMakePrep.addActionListener(e -> {
            JDialog waitDialog = new JDialog(this, "Loading match data...", Dialog.ModalityType.APPLICATION_MODAL);
            JLabel waitLabel = new JLabel("<html><div>" +
                    "<span style='font-size: 10px; font-weight: bold;'>Please wait 🙂</span><br>" +
                    "It can take up to several minutes according to the last time you have updated this organization.</div></html>");
            initWaitDialog(waitDialog, waitLabel, new Dimension(400,250));
            JTextArea statusArea = getStatusArea(waitDialog);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                private String errorMessage = null;

                @Override
                protected Void doInBackground() {
                    try {
                        makePrep(statusArea);
                    } catch (PlayerNotFoundInLoLProsException e) {
                        JOptionPane.showMessageDialog(MainFrame.this, "We could not find LOLPros page of this player: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        // TODO: 07.04.2023 retry or manually add player's soloQ accounts
                    } catch (Throwable e) {
                        errorMessage = e.getMessage();
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    waitDialog.dispose();
                    if (errorMessage != null) {
                        JOptionPane.showMessageDialog(MainFrame.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        });

        cmbRegion.addActionListener(e -> cmbRegionAction());

        cmbTournament.addActionListener(e -> cmbTournamentAction());

        cmbRegionAction();
        cmbTournamentAction();

        pack();
        setLocationRelativeTo(null);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void cmbTournamentAction() {
        JDialog waitDialog = new JDialog(this, "Loading teams...", Dialog.ModalityType.APPLICATION_MODAL);
        initWaitDialog(waitDialog);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try {
                    updateTeams();
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                waitDialog.dispose();
                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(MainFrame.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void initWaitDialog(JDialog waitDialog) {
        waitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        waitDialog.setSize(250, 100);
        waitDialog.setLocationRelativeTo(this);
        waitDialog.setLayout(new BoxLayout(waitDialog.getContentPane(), BoxLayout.Y_AXIS));

        JLabel waitLabel = new JLabel("<html><div>" +
                "<span style='font-size: 10px; font-weight: bold;'>Please wait 🙂</span><br>" +
                "It usually takes few seconds.</div></html>");
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        waitDialog.add(waitLabel);
        waitDialog.setVisible(true);
    }

    private void initWaitDialog(JDialog waitDialog, JLabel waitLabel, Dimension dimension) {
        waitDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        waitDialog.setSize(dimension);
        waitDialog.setLocationRelativeTo(this);
        waitDialog.setLayout(new BoxLayout(waitDialog.getContentPane(), BoxLayout.Y_AXIS));
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        waitLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        waitDialog.add(waitLabel);
        waitDialog.setVisible(true);
    }

    private JTextArea getStatusArea(JDialog waitDialog) {
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Status:"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane waitScrollPane = new JScrollPane(statusArea);
        waitDialog.add(waitScrollPane);
        return statusArea;
    }

    private void cmbRegionAction() {
        JDialog waitDialog = new JDialog(this, "Loading tournaments...", Dialog.ModalityType.APPLICATION_MODAL);
        initWaitDialog(waitDialog);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try {
                    updateTournaments();
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void done() {
                waitDialog.dispose();
                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(MainFrame.this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void updateTournaments() throws URISyntaxException, IOException, InterruptedException {
        String region = (String) cmbRegion.getSelectedItem();
        JsonObject jsonObject = DataExtractor.getTournamentsByRegion(region);

        cmbTournament.removeAllItems();
        jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
            String leagueName = jsonElement.getAsJsonObject()
                    .get("title").getAsJsonObject()
                    .get("OverviewPage").getAsString();
            cmbTournament.addItem(leagueName);
        });

        cmbTournament.validate();
        cmbTournament.repaint();
    }

    private void updateTeams() throws URISyntaxException, IOException, InterruptedException {
        String tournament = (String) cmbTournament.getSelectedItem();
        JsonObject jsonObject = DataExtractor.getTeamsByTournament(tournament);

        cmbOrganisation.removeAllItems();
        jsonObject.get("cargoquery").getAsJsonArray().forEach(jsonElement -> {
            String teamName = jsonElement.getAsJsonObject()
                    .get("title").getAsJsonObject()
                    .get("Team").getAsString();
            cmbOrganisation.addItem(teamName);
        });

        cmbOrganisation.validate();
        cmbOrganisation.repaint();
    }

    private void makePrep(JTextArea waitTextArea) throws IOException, URISyntaxException, InterruptedException, PlayerNotFoundInLoLProsException {
        setStartingRoster();
        
        SwingUtilities.invokeLater(() -> waitTextArea.append("fetching accounts data to players... "));
        DataExtractor.fetchAccountsToPlayers(org);
        SwingUtilities.invokeLater(() -> waitTextArea.append("done\n"));

        // TODO: 16.04.2023 show and edit player's accounts 
        /*List<Player> players = org.getRoster().getPlayers();
        for (Player p : players) {
            GUI.showData(p);
        }*/
        SwingUtilities.invokeLater(() -> waitTextArea.append("fetching matches data to accounts... "));
        DataExtractor.fetchMatches(org);
        SwingUtilities.invokeLater(() -> waitTextArea.append("done\n"));

        SwingUtilities.invokeLater(() -> waitTextArea.append("inserting matches data to database... "));
        DatabaseManager.insertObject(org);
        SwingUtilities.invokeLater(() -> waitTextArea.append("done\n"));

        SwingUtilities.invokeLater(() -> waitTextArea.append("making html output... "));
        Path tempFilePath = OutputMaker.makeHTMLOutput(org);
        SwingUtilities.invokeLater(() -> waitTextArea.append("done\n"));

        addLinkToPrep(tempFilePath);
    }

    private void setStartingRoster() {
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < tblContent.getRowCount(); i++) {
            playerNames.add((String) tblContent.getValueAt(i,1));
        }
        List<Player> players = org.getLastRoster().getPlayers();
        players.removeIf(player -> !playerNames.contains(player.getName()));
        org.setStartingLineUp(new Roster(org, players));
    }

    private int getRosterChangeCount(List<String> playerNames) {
        List<Player> players = org.getRoster(1).getPlayers();
        return (int) playerNames.stream()
                .filter(name -> players.stream()
                        .noneMatch(player -> player.getName().equals(name)))
                .count();
    }

    private void loadRoster(String orgName) throws URISyntaxException, IOException, InterruptedException {
        DataExtractor.setTournament((String) cmbTournament.getSelectedItem());

        org = DataExtractor.getOrganization(orgName);
    }

    public void showRoster() {

        RosterTableModel model = new RosterTableModel(org.getLastRoster().getPlayers());

        if(tblContentScroll != null)
            contentPanel.remove(tblContentScroll);

        tblContent = new RosterTable(model);
        tblContentScroll = new JScrollPane(tblContent);
        model.getEditableCells().forEach(cell -> {
            tblContent.putEditor(cell, new DefaultCellEditor(model.getJComboBox(cell)));
            tblContent.putRenderer(cell, new DefaultTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return model.getJComboBox(cell);
                }
            });
            tblContent.setRowHeight(model.getJComboBox(cell).getPreferredSize().height);
        });

        contentPanel.add(tblContentScroll, BorderLayout.CENTER);
        contentPanel.validate();
        contentPanel.repaint();
    }

    public void addLinkToPrep(Path tempFilePath) {
        JButton btnLinkTo = new JButton("Go to match preparation");
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
        contentPanel.remove(btnMakePrep);
        contentPanel.add(btnLinkTo, BorderLayout.SOUTH);

        validate();
        repaint();
    }
}
