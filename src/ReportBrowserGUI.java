import javax.swing.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import data_types.TestCase;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.List;

public class ReportBrowserGUI extends JFrame {
    // List to store selected XML file paths
    private List<String> xmlFilePaths;

    // Text field for specifying the output location
    private JTextField outputLocationField;

    // Two-dimensional ArrayList to store test case data for tabular representation
    ArrayList<ArrayList<String>> rowDataList = new ArrayList<>();

    /**
     * Retrieves test case data from an XML file.
     *
     * @param xmlFile The XMLFile object representing the XML file to extract test cases from.
     * @return An ArrayList of TestCase objects containing extracted test case data.
     */
    private ArrayList<TestCase> getTestCasesFromXML(XMLFile xmlFile) {
        ArrayList<TestCase> tcList = new ArrayList<>();
        for (Element element : xmlFile.getNodeElementsListByTagName("testcase")) {
            // Extract data and create a TestCase object
            tcList.add(
                    new TestCase(
                            element.getElementsByTagName("title").item(0).getTextContent(),
                            ((Element) element.getElementsByTagName("verdict").item(0)).getAttribute("time"),
                            ((Element) element.getElementsByTagName("verdict").item(0)).getAttribute("result")
                    )
            );
        }
        return tcList;
    }

    /**
     * Prints the details of a list of test cases to the console.
     *
     * @param testCaseList An ArrayList of TestCase objects to be printed.
     */
    private void printTestCasesList(ArrayList<TestCase> testCaseList) {
        for (TestCase testCase : testCaseList) {
            // Print test case details
            System.out.println("Title: " + testCase.getTitle()
                    + "\nTime: " + testCase.getStartTime()
                    + "\nResult: " + testCase.getResult() + "\n\n");
        }
    }

    /**
     * Converts a list of test cases into a two-dimensional ArrayList for tabular data.
     *
     * @param testCaseArrayLists An ArrayList of ArrayLists of TestCase objects.
     * @return An ArrayList of ArrayLists of String, representing test case data suitable for tabular representation.
     */
    private ArrayList<ArrayList<String>> convertTCListToRowData(ArrayList<ArrayList<TestCase>> testCaseArrayLists) {
        for (ArrayList<TestCase> tcList : testCaseArrayLists) {
            for (TestCase testCase : tcList) {
                // Convert test case data into rows for tabular representation
                rowDataList.add(new ArrayList<>() {{
                    add(testCase.getTitle());
                    add(testCase.getStartTime());
                    add(testCase.getResult());
                }});
            }
        }
        return rowDataList;
    }

    /**
     * Calculates the number of test cases with different results from a list of test case lists.
     *
     * @param allTCsLists An ArrayList of ArrayLists of TestCase objects.
     * @return An Integer array containing counts of "Pass," "Fail," and "Inconclusive" test results.
     */
    private Integer[] getTCListResults(ArrayList<ArrayList<TestCase>> allTCsLists) {
        Integer[] resultArray = {0, 0, 0};
        for (ArrayList<TestCase> tcList : allTCsLists) {
            for (TestCase testCase : tcList) {
                // Count test case results
                if (testCase.getResult().equalsIgnoreCase("Pass"))
                    resultArray[0]++;
                else if (testCase.getResult().equalsIgnoreCase("Fail"))
                    resultArray[1]++;
                else if (testCase.getResult().equalsIgnoreCase("Inconclusive"))
                    resultArray[2]++;
            }
        }
        return resultArray;
    }

    /**
     * Calculates the total number of inner lists (test cases) from a list of test case lists.
     *
     * @param allTCsLists An ArrayList of ArrayLists of TestCase objects.
     * @return The total number of test cases across all inner lists.
     */
    private int getSizeInnerLists(ArrayList<ArrayList<TestCase>> allTCsLists) {
        int innerLength = 0;
        for (ArrayList<TestCase> tcList : allTCsLists) {
            // Calculate the total number of test cases
            innerLength += tcList.size();
        }
        return innerLength;
    }

    public ReportBrowserGUI() {
        // Set the title, size, close operation, and location of the JFrame
        setTitle("XML File Browser");
        setSize(650, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the list of XML file paths
        xmlFilePaths = new ArrayList<>();

        // Set the Look and Feel to FlatLaf with the dark theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            FlatLaf.updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a JPanel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label for selecting XML Files
        JLabel xmlFileLabel = new JLabel("Select XML Files:");
        panel.add(xmlFileLabel, gbc);

        // Text field for displaying the selected XML file paths
        JTextField xmlFilesField = new JTextField(30);
        xmlFilesField.setEditable(false); // Make it non-editable
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(xmlFilesField, gbc);

        // Browse button for selecting the XML files
        JButton browseXMLButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(browseXMLButton, gbc);

        browseXMLButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                // Set the file filter to show only XML files
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("XML Files", "xml");
                fileChooser.setFileFilter(xmlFilter);

                // Allow multiple file selection
                fileChooser.setMultiSelectionEnabled(true);

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Get the selected XML files and store their paths in the list
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    xmlFilePaths.clear(); // Clear the previous selections
                    for (File selectedFile : selectedFiles) {
                        xmlFilePaths.add(selectedFile.getAbsolutePath());
                    }

                    // Display the selected file paths in the text field
                    xmlFilesField.setText(String.join(", ", xmlFilePaths));
                }
            }
        });

        // Label for specifying the output location
        JLabel outputLocationLabel = new JLabel("Output Location:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(outputLocationLabel, gbc);

        // Text field for specifying the output location path
        outputLocationField = new JTextField(30); // Increased the number of columns to make it wider
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(outputLocationField, gbc);

        // Browse button for selecting the output location
        JButton browseOutputButton = new JButton("Browse");
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(browseOutputButton, gbc);

        browseOutputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    outputLocationField.setText(selectedFolder.getAbsolutePath());
                }
            }
        });

        // Button to generate reports
        JButton generateButton = new JButton("Generate Reports");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(generateButton, gbc);

        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String outputLocationPath = outputLocationField.getText();
                if (xmlFilePaths.size() > 0) {
                    ReportBrowserGUI main = new ReportBrowserGUI();
                    // You can now use xmlFilePaths (List of selected XML file paths) and outputLocationPath to perform your logic.
                    // Example:
                    System.out.println("Output Location: " + outputLocationPath);
                    ArrayList<ArrayList<TestCase>> allTCsLists = new ArrayList<>();
                    for (String xmlFilePath : xmlFilePaths) {
                        allTCsLists.add(main.getTestCasesFromXML(new XMLFile(xmlFilePath)));
                        System.out.println("XML File: " + xmlFilePath + "\n");
                    }

                    for (ArrayList<TestCase> list : allTCsLists) {
                        main.printTestCasesList(list);
                    }
                    int innerListsSize = main.getSizeInnerLists(allTCsLists);
                    ExcelFile reportExcelFile;
                    if (outputLocationPath != null)
                        reportExcelFile = new ExcelFile(outputLocationPath + "/", "Test Summary Report");
                    else
                        reportExcelFile = new ExcelFile("test_excel_reports/", "Test Summary Report");

                    XSSFSheet testCasesTableSheet = reportExcelFile.createSheet("Test Cases Table");
                    reportExcelFile.createTable(
                            testCasesTableSheet,
                            "*Test Summary Table*",
                            "A1",
                            "C" + innerListsSize,
                            3,
                            new ArrayList<>() {{
                                //Columns Data
                                add("Title");
                                add("Time");
                                add("Result");
                            }},
                            innerListsSize,
                            main.convertTCListToRowData(allTCsLists)
                    );

                    reportExcelFile.createTCsChart(
                            reportExcelFile.createSheet("Summary"),
                            main.getTCListResults(allTCsLists)
                    );
                    reportExcelFile.createFile();
                    JOptionPane.showMessageDialog(null, "File created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Set the JPanel as the content pane of the JFrame
        setContentPane(panel);
    }

    // Main method to create and display the GUI
    public static void main(String[] args) {
        // Create and display the GUI on the Swing event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ReportBrowserGUI gui = new ReportBrowserGUI();
                gui.setVisible(true);
            }
        });
    }
}
