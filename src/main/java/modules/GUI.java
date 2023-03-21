package modules;

import dbModel.Organization;
import dbModel.Player;
import dbModel.Showable;
import gui.PlaceholderTextField;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GUI extends JFrame {
    private Organization org;
    private Document doc;
    private PlaceholderTextField txtInput;
    private JLabel lblHeader;
    private JButton btnLoadOrg;
    private JButton btnConfirm;
    private JTable tblContent;
    private JPanel contentPanel;

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
        tblContent = new JTable();

        // set layout
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(txtInput);
        inputPanel.add(btnLoadOrg);
        add(inputPanel, BorderLayout.NORTH);
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setPreferredSize(new Dimension(500, 250));
        contentPanel.add(lblHeader, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(tblContent);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(btnConfirm,BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        // add action listeners
        btnLoadOrg.addActionListener(e -> loadOrg(txtInput.getText()));
        btnConfirm.addActionListener(e -> makePrep());
        pack();
    }

    public Document getDoc() {
        return doc;
    }

    private void makePrep() {
        try {
            DataExtractor.fetchAccountsToPlayers(org);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(GUI.this, "IO error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        DefaultTableModel model = new DefaultTableModel(data.getContent(),data.getColumnNames()){
            @Override
            public boolean isCellEditable(int row, int column) {
                if(column == 0)
                    return false;
                else
                    return false;
            }
        };
        tblContent.setModel(model);
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
