package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class PatientManagementWindow extends JFrame {
    private Connection connection;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField nameField, ageField, phoneField;
    private JTextArea addressField;
    private JComboBox<String> genderCombo;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private int selectedPatientId = -1;

    public PatientManagementWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadPatientData();
    }

    private void initializeGUI() {
        setTitle("Patient Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main layout
        setLayout(new BorderLayout());

        // Top panel with search
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        // Right panel with form
        JPanel rightPanel = createFormPanel();
        add(rightPanel, BorderLayout.EAST);

        // Bottom panel with buttons
        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(33, 150, 243));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchPatients();
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(searchLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(searchField);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 10));

        // Table setup
        String[] columnNames = { "ID", "Name", "Age", "Gender", "Phone", "Address" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(new Font("Arial", Font.PLAIN, 14));
        patientTable.setRowHeight(25);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Table selection listener
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = patientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });

        // Customize table appearance
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        patientTable.getTableHeader().setBackground(new Color(33, 150, 243));
        patientTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Patient Information"),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(350, 0));

        // Name field
        panel.add(createFieldPanel("Name:", nameField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        // Age field
        panel.add(createFieldPanel("Age:", ageField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        // Gender combo
        genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        panel.add(createFieldPanel("Gender:", genderCombo));
        panel.add(Box.createVerticalStrut(15));

        // Phone field
        panel.add(createFieldPanel("Phone:", phoneField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        // Address field
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        addressField = new JTextArea(4, 20);
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));
        addressField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane addressScroll = new JScrollPane(addressField);

        panel.add(addressLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(addressScroll);

        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldPanel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(80, 25));

        if (field instanceof JTextField) {
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setPreferredSize(new Dimension(200, 25));
        }

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addButton = createStyledButton("Add Patient", new Color(76, 175, 80));
        updateButton = createStyledButton("Update Patient", new Color(33, 150, 243));
        deleteButton = createStyledButton("Delete Patient", new Color(244, 67, 54));
        clearButton = createStyledButton("Clear Form", new Color(158, 158, 158));

        addButton.addActionListener(e -> addPatient());
        updateButton.addActionListener(e -> updatePatient());
        deleteButton.addActionListener(e -> deletePatient());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    private void loadPatientData() {
        try {
            String query = "SELECT * FROM patients ORDER BY id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Clear existing data
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getInt("age"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patient data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPatients() {
        String searchText = searchField.getText().toLowerCase();

        try {
            String query = "SELECT * FROM patients WHERE LOWER(name) LIKE ? OR LOWER(phone) LIKE ? OR LOWER(address) LIKE ? ORDER BY id";
            PreparedStatement pstmt = connection.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getInt("age"));
                row.add(rs.getString("gender"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching patients: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int selectedRow) {
        selectedPatientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        ageField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        genderCombo.setSelectedItem((String) tableModel.getValueAt(selectedRow, 3));
        phoneField.setText((String) tableModel.getValueAt(selectedRow, 4));
        addressField.setText((String) tableModel.getValueAt(selectedRow, 5));
    }

    private void addPatient() {
        if (!validateForm())
            return;

        try {
            String query = "INSERT INTO patients (name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setInt(2, Integer.parseInt(ageField.getText().trim()));
            pstmt.setString(3, (String) genderCombo.getSelectedItem());
            pstmt.setString(4, phoneField.getText().trim());
            pstmt.setString(5, addressField.getText().trim());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatientData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding patient: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePatient() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to update!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm())
            return;

        try {
            String query = "UPDATE patients SET name=?, age=?, gender=?, phone=?, address=? WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setInt(2, Integer.parseInt(ageField.getText().trim()));
            pstmt.setString(3, (String) genderCombo.getSelectedItem());
            pstmt.setString(4, phoneField.getText().trim());
            pstmt.setString(5, addressField.getText().trim());
            pstmt.setInt(6, selectedPatientId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPatientData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating patient: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age!",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePatient() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this patient?\nThis action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM patients WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, selectedPatientId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Patient deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPatientData();
                    clearForm();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        ageField.setText("");
        genderCombo.setSelectedIndex(0);
        phoneField.setText("");
        addressField.setText("");
        selectedPatientId = -1;
        patientTable.clearSelection();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient name!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        try {
            int age = Integer.parseInt(ageField.getText().trim());
            if (age <= 0 || age > 150) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age (1-150)!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            ageField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter phone number!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        return true;
    }
}