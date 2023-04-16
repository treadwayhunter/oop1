package project;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import java.io.IOException;

public class RatingStatsApp extends JFrame {
    private JTextArea textArea;
    private DatasetHandler dh;
    private boolean fileFound;
    private Set<Dataset> datasets;
    private JTable dataSetsTable;
    private DefaultTableModel model;
    private JTextField statFileField;
    private JSlider rowSlider;
    private int k;

    public static void main(String[] args) {
        RatingStatsApp app = new RatingStatsApp();
    }

    public RatingStatsApp() {
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Rating Stats App");
        fileFound = false;
  
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        rowSlider = new JSlider(JSlider.HORIZONTAL, 0, 50, 20);
        rowSlider.setMajorTickSpacing(10);
        rowSlider.setMinorTickSpacing(5);
        rowSlider.setPaintTicks(true);
        rowSlider.setPaintLabels(true);
        rowSlider.setSnapToTicks(true);
        rowSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                k = rowSlider.getValue();
            }
            
        });
        k = 20;

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());

        JScrollPane textScrollPane = new JScrollPane();
        textScrollPane.setViewportView(textArea);

        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel rightTopPanel = new JPanel(new BorderLayout());
        JPanel rightButtonPanel = new JPanel();


        String[] columnHeaders = {"id", "file", "ratings"};
        Object[][] data = {};
        dataSetsTable = new JTable();
        model = new DefaultTableModel(data, columnHeaders);
        dataSetsTable.setModel(model);

        dataSetsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                int selectedRow = dataSetsTable.getSelectedRow();
                
                try {
                    List<Dataset> datalist = new ArrayList<>(datasets);
                    String statField = datalist.get(selectedRow).getDataId();

                    statFileField.setText(statField);
                    statFileField.repaint();
                }
                catch(IndexOutOfBoundsException ie) {
                }
            }
            
        });

        JScrollPane tableScrollPane = new JScrollPane(dataSetsTable); // create scrollPane with table
        statFileField = new JTextField();
        statFileField.setEditable(false);
        statFileField.setBackground(Color.WHITE);

        JButton viewStatsButton = new JButton("View");
        viewStatsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // option 3, just view the file
                
                // get textField value
                viewStats();
            }
        });
        JButton recalStatsButton = new JButton("Recalculate");
        recalStatsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // option 3, just view the file
                
                String uuid = statFileField.getText();
                if(uuid.length() > 0) {
                    handleFile(uuid);
                }
            }
        });
        JButton fileButton = new JButton("New File");
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                handleFile();
            }
        });
        
        rightButtonPanel.add(rowSlider);
        rightButtonPanel.add(fileButton);
        rightButtonPanel.add(viewStatsButton);
        rightButtonPanel.add(recalStatsButton);
        
        rightTopPanel.add(new JLabel("Selected File ID:"), BorderLayout.NORTH);
        rightTopPanel.add(statFileField);
        rightTopPanel.add(rightButtonPanel, BorderLayout.SOUTH);

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        leftPanel.add(textScrollPane, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel);
        setVisible(true);
        setup();
    }

    /**
     * Calls the initial few methods to setup the GUI info.
     */
    public void setup() {
        try {
            dh = new DatasetHandler();
            datasets = dh.getDataSets();

            generateTable();
            repaintTable();

            String welcomeMessage = "Open a new file or view an existing file to get started.\n";
            appendText(welcomeMessage);

        }
        catch(IOException e) {

            appendText("Dataset path not found: " + e.getMessage());
            appendText("Please check the file and try again, exiting.");
        }
    }

    /**
     * Analogous to option 2 in the text-based program.
     */
    private void handleFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int result = fileChooser.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION) {

            clearText();
            String uuid = UUID.randomUUID().toString();
            
            if (!(dh.checkID(uuid))){
                final String input = fileChooser.getSelectedFile().getName();
                appendText("Selected File: " + input + "\n");
                boolean check = dh.addCollection(uuid, input);
                if(check) {
                    appendText("Collection " + uuid + " added\n");
                    fileFound = true;
                }
                else {
                    // I'd be surprised if a select file is not found
                    appendText("File not found");
                    appendText("Try again.");
                }
            }
            else {
                // this is probably unnecessary if the uuid is guaranteed unique
                appendText(uuid + " is in the current database, displaying existing statistics.\n");
            }

            if(fileFound) {
                try {
                    Dataset d = dh.populateCollection(uuid);

                    // the id is unique, soooo.....
                    int stats = d.statsExist();
                    // Absolutley asked ChatGPT for this one.
                    appendText("Computing... this may take awhile\n");
                    textArea.paintImmediately(textArea.getBounds());
                    
                    if(stats > 0) {
                        //appendText(processStats);
                    }
                    if(stats == 0) {
                        d.computeStats();
                        dh.saveStats(uuid);
                        dh.saveDB();
                    }
                    appendText("Completed processing!\n");
                }
                catch(IOException e) {
                    appendText("Dataset error." + e.getMessage());
                }

                appendText(dh.printReport(uuid, k));
                generateTable();
                repaintTable();
            }//end if found
        }
    }

    /**
     * 
     * @param uuid
     * Analogous to option 3 in the text-based program.
     * Takes a uuid, and then recalculates the data in the file associated with the uuid.
     */
    private void handleFile(String uuid) {
        clearText();
        
        try {
            Dataset d = dh.populateCollection(uuid);

            // the id is unique, soooo.....
            int stats = d.statsExist();
            // Absolutley asked ChatGPT for this one.
            appendText("Computing... this may take awhile\n");
            textArea.paintImmediately(textArea.getBounds());
            
            if(stats > 0) {
                //appendText(processStats);
            }
            if(stats == 0) {
                d.computeStats();
                dh.saveStats(uuid);
                dh.saveDB();
            }
            appendText("Completed processing!\n");
        }
        catch(IOException e) {
            appendText("Dataset error." + e.getMessage());
        }

        appendText(dh.printReport(uuid, k));
        // no changes are being made to the table.
        generateTable();
        repaintTable();
    }

    /**
     * 
     * @param text
     * 
     * Takes String text and appends it to the JTextArea.
     */
    private void appendText(String text) {
        // appends text to textarea
        textArea.append(text);
    }

    /**
     * Clears all text from the JTextArea.
     */
    private void clearText() {
        textArea.setText("");
    }

    /**
     * A method for viewing the stats of a selected file
     */
    private void viewStats() {
        String uuid = statFileField.getText();
        if(uuid.length() > 0) {
            clearText();
            try {
                dh.populateCollection(uuid);
                appendText(dh.printReport(uuid, k));
            }
            catch(IOException e) {
                appendText("Dataset error." + e.getMessage());
            }
        }
    }

    /**
     * Repaints the table component
     * The table is constantly changing, and needs to be repainted
     * to view those changes.
     */
    private void repaintTable() {
        dataSetsTable.repaint();
    }

    /**
     * Clears the table, then inserts new rows into the table.
     */
    private void generateTable() {
        model.setRowCount(0);
        for(Dataset ds : datasets) {
            String id = ds.getDataId();
            String fileName = ds.getRawFile().toString();
            long ratings = ds.getNumberOfRatings();
            addTableRow(id, fileName, ratings);
        }
    }

    /**
     * @param id
     * @param fileName
     * @param ratings
     * 
     * A helper function for generateTable. It adds a single row to the table.
     */
    private void addTableRow(String id, String fileName, long ratings) {
        model.addRow(new Object[]{id, fileName, ratings});
    }

}
