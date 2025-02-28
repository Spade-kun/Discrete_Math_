package com.final_project.math;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PropositionalLogicGUI extends JFrame implements ActionListener {
    private JLabel numVarLabel;
    private JTextField numVarTextField;
    private JButton submitButton;
    private JTable truthTable;
    private DefaultTableModel tableModel;
    private JButton clearButton;
    private JButton addConnectiveButton;
    private JButton identifyStatementButton;
    private JButton showConnectiveLawsButton;

    private ArrayList<String> variableNames = new ArrayList<>();
    private ArrayList<String> logicalStatements = new ArrayList<>();
    private Map<String, Boolean> variableValues = new LinkedHashMap<>();

    public PropositionalLogicGUI() {
        setTitle("Propositional Logic Truth Table Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the JFrame

        Font font = new Font("Arial", Font.BOLD, 15);

        JPanel inputPanel = new JPanel();

        numVarLabel = new JLabel("Enter the number of variables:");
        numVarTextField = new JTextField(6);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);

        inputPanel.add(numVarLabel);
        inputPanel.add(numVarTextField);
        inputPanel.add(submitButton);

        inputPanel.setBackground(new Color(207, 209, 207));
        submitButton.setBackground(Color.lightGray);
        numVarLabel.setFont(font);
        numVarLabel.setForeground(Color.BLACK);
        submitButton.setBackground(new Color(191, 217, 245));
        numVarTextField.setFont(font);

        tableModel = new DefaultTableModel() {
            // Make the table non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        truthTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(truthTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        truthTable.setBackground(new Color(215, 220, 224));
        truthTable.setForeground(Color.BLACK);
        truthTable.setFont(font);

        clearButton = new JButton("Clear Table");
        addConnectiveButton = new JButton("Add Connective");
        clearButton.addActionListener(this);
        addConnectiveButton.addActionListener(this);
        addConnectiveButton.setEnabled(false); // Initially disabled

        identifyStatementButton = new JButton("Identify Statement Type");
        showConnectiveLawsButton = new JButton("Show Connective Laws");
        identifyStatementButton.addActionListener(this);
        showConnectiveLawsButton.addActionListener(this);

        // Center align the text in the table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        truthTable.setDefaultRenderer(Object.class, centerRenderer);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearButton);
        buttonPanel.add(addConnectiveButton);
        buttonPanel.add(identifyStatementButton);
        buttonPanel.add(showConnectiveLawsButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        clearButton.setBackground(new Color(191, 217, 245));
        addConnectiveButton.setBackground(new Color(191, 217, 245));
        identifyStatementButton.setBackground(new Color(191, 217, 245));
        showConnectiveLawsButton.setBackground(new Color(191, 217, 245));

        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);

        setVisible(true);
        lockTable(); // Prevent the table from moving
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            handleSubmission();
        } else if (e.getSource() == clearButton) {
            clearTable();
        } else if (e.getSource() == addConnectiveButton) {
            addLogicalConnective();
        } else if (e.getSource() == identifyStatementButton) {
            identifyStatementType();
        } else if (e.getSource() == showConnectiveLawsButton) {
            showConnectiveLaws();
        }
    }

    private void handleSubmission() {
        try {
            int numVariables = Integer.parseInt(numVarTextField.getText());
            if (numVariables <= 0) {
                showErrorMessage("Please enter a positive integer for the number of variables.");
                return;
            }
            for (int i = 0; i < numVariables; i++) {
                String varName = showInputDialog("Enter variable name " + (i + 1) + ":", "Variable Name");
                if (varName == null || varName.trim().isEmpty()) {
                    showErrorMessage("Please input a variable name.");
                    return;
                }
                variableNames.add(varName);
                variableValues.put(varName, false); // Initialize all variables with false
            }

            // Enable the "Add Connective" button
            addConnectiveButton.setEnabled(true);

            // Prompt user for logical statements
            int option = showConfirmDialog("Do you want to enter logical statements?", "Input");
            if (option == JOptionPane.YES_OPTION) {
                String input = showInputDialog("Enter logical statements separated by commas (not, and, or, then, iff):",
                        "Logical Statements");
                if (input != null) {
                    String[] statements = input.split(",\\s*");
                    logicalStatements.addAll(Arrays.asList(statements));
                }
            }

            generateTruthTable();
        } catch (NumberFormatException ex) {
            showErrorMessage("Please enter a valid integer for the number of variables.");
        }
    }

    private void generateTruthTable() {
        Font font = new Font("Arial", Font.BOLD, 20);
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);

        // Add columns for variable names
        for (String varName : variableNames) {
            tableModel.addColumn(varName);
        }

        // Add columns for logical statements
        for (String statement : logicalStatements) {
            tableModel.addColumn(statement);
        }

        int numRows = (int) Math.pow(2, variableNames.size());
        int[] alternations = new int[variableNames.size()];
        for (int i = 0; i < variableNames.size(); i++) {
            alternations[i] = (int) Math.pow(2, variableNames.size() - i - 1);
        }

        for (int i = 0; i < numRows; i++) {
            // Update variable values for each row
            for (int j = 0; j < variableNames.size(); j++) {
                String varName = variableNames.get(j);
                boolean value = (i / alternations[j]) % 2 == 0;
                variableValues.put(varName, value);
            }

            Object[] rowData = new Object[variableNames.size() + logicalStatements.size()];
            // Fill rowData with variable values
            for (int j = 0; j < variableNames.size(); j++) {
                rowData[j] = variableValues.get(variableNames.get(j)) ? "T" : "F";
            }

            // Evaluate logical statements if any
            for (int j = 0; j < logicalStatements.size(); j++) {
                boolean result = evaluateStatement(logicalStatements.get(j), variableValues);
                rowData[variableNames.size() + j] = result ? "T" : "F";
            }

            tableModel.addRow(rowData);
        }
        JTableHeader tableHeader = truthTable.getTableHeader();
        tableHeader.setFont(font);
    }

    // Method to evaluate logical statements
    private boolean evaluateStatement(String statement, Map<String, Boolean> variableValues) {
        PropositionalLogicStatement ps = new PropositionalLogicStatement(statement);
        return ps.evaluate(variableValues);
    }

    // Helper method to display an error message dialog
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Helper method to display an input dialog
    private String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    // Helper method to display a confirmation dialog
    private int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }

    // Method to clear the table
    private void clearTable() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        logicalStatements.clear();
        variableNames.clear();
        variableValues.clear();

        // Disable the "Add Connective" button again when variables are cleared
        addConnectiveButton.setEnabled(false);
    }

    // Method to add logical connective
    private void addLogicalConnective() {
        String connective = JOptionPane.showInputDialog("Enter logical connective (not, and, or, then, iff):");
        logicalStatements.add(connective);
        generateTruthTable();
    }

    // Method to identify the type of logical statement
    private void identifyStatementType() {
        // Assuming that the user clicks on a column in the truth table
        int columnClicked = getClickedColumn();
        if (columnClicked != -1) {
            String columnName = truthTable.getColumnName(columnClicked);
            String statement = columnName;
            PropositionalLogicStatement ps = new PropositionalLogicStatement(statement);
            String statementType = ps.identifyStatementType(variableValues);
            JOptionPane.showMessageDialog(null, "Statement type: " + statementType);
        }
    }

    // Method to get the index of the clicked column in the truth table
    private int getClickedColumn() {
        int column = truthTable.getSelectedColumn();
        return column;
    }


    // Method to show the list of logical connective laws
    private void showConnectiveLaws() {
        String laws = "List of logical connective laws:\n" +
                "1. Commutative Laws:\n" +
                "   - p AND q = q AND p\n" +
                "   - p OR q = q OR p\n" +
                "2. Associative Laws:\n" +
                "   - (p AND q) AND r = p AND (q AND r)\n" +
                "   - (p OR q) OR r = p OR (q OR r)\n" +
                "3. Distributive Laws:\n" +
                "   - p AND (q OR r) = (p AND q) OR (p AND r)\n" +
                "   - p OR (q AND r) = (p OR q) AND (p OR r)\n" +
                "4. De Morgan's Laws:\n" +
                "   - NOT (p AND q) = (NOT p) OR (NOT q)\n" +
                "   - NOT (p OR q) = (NOT p) AND (NOT q)\n" +
                "5. Identity Laws:\n" +
                "   - p AND true = p\n" +
                "   - p OR false = p\n" +
                "6. Negation Laws:\n" +
                "   - p AND NOT p = false\n" +
                "   - p OR NOT p = true";
        JOptionPane.showMessageDialog(null, laws);
    }

    // Method to lock a table
    private void lockTable() {
        truthTable.getTableHeader().setReorderingAllowed(false);
        truthTable.getTableHeader().setResizingAllowed(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PropositionalLogicGUI());
    }
}

