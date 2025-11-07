package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class DoctorManagementWindow extends JFrame {
    private Connection connection;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JTextField nameField, specializationField, phoneField, emailField;
    private JButton addButton, updateButton, deleteButton, clearButton;
    private int selectedDoctorId = -1;

    public DoctorManagementWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadDoctorData();
    }

    private void initializeGUI() {
        setTitle("Doctor Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createFormPanel();
        add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = createButtonPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(33, 150, 243));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Doctor Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchDoctors();
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

        String[] columnNames = { "ID", "Name", "Specialization", "Phone", "Email" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 14));
        doctorTable.setRowHeight(25);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = doctorTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromTable(selectedRow);
                }
            }
        });

        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        doctorTable.getTableHeader().setBackground(new Color(33, 150, 243));
        doctorTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Doctor Information"),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(350, 0));

        panel.add(createFieldPanel("Name:", nameField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createFieldPanel("Specialization:", specializationField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createFieldPanel("Phone:", phoneField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createFieldPanel("Email:", emailField = new JTextField(20)));
        panel.add(Box.createVerticalStrut(15));

        return panel;
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fieldPanel.setBackground(new Color(245, 245, 245));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(100, 25));

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 25));

        fieldPanel.add(label);
        fieldPanel.add(field);

        return fieldPanel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        addButton = createStyledButton("Add Doctor", new Color(76, 175, 80));
        updateButton = createStyledButton("Update Doctor", new Color(33, 150, 243));
        deleteButton = createStyledButton("Delete Doctor", new Color(244, 67, 54));
        clearButton = createStyledButton("Clear Form", new Color(158, 158, 158));

        addButton.addActionListener(e -> addDoctor());
        updateButton.addActionListener(e -> updateDoctor());
        deleteButton.addActionListener(e -> deleteDoctor());
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

    private void loadDoctorData() {
        try {
            String query = "SELECT * FROM doctors ORDER BY id";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("specialization"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("email"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctor data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchDoctors() {
        String searchText = searchField.getText().toLowerCase();

        try {
            String query = "SELECT * FROM doctors WHERE LOWER(name) LIKE ? OR LOWER(specialization) LIKE ? OR LOWER(phone) LIKE ? ORDER BY id";
            PreparedStatement pstmt = connection.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("specialization"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("email"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching doctors: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromTable(int selectedRow) {
        selectedDoctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        specializationField.setText((String) tableModel.getValueAt(selectedRow, 2));
        phoneField.setText((String) tableModel.getValueAt(selectedRow, 3));
        emailField.setText((String) tableModel.getValueAt(selectedRow, 4));
    }

    private void addDoctor() {
        if (!validateForm())
            return;

        try {
            String query = "INSERT INTO doctors (name, specialization, phone, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, specializationField.getText().trim());
            pstmt.setString(3, phoneField.getText().trim());
            pstmt.setString(4, emailField.getText().trim());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Doctor added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctorData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDoctor() {
        if (selectedDoctorId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to update!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm())
            return;

        try {
            String query = "UPDATE doctors SET name=?, specialization=?, phone=?, email=? WHERE id=?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setString(2, specializationField.getText().trim());
            pstmt.setString(3, phoneField.getText().trim());
            pstmt.setString(4, emailField.getText().trim());
            pstmt.setInt(5, selectedDoctorId);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctorData();
                clearForm();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating doctor: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDoctor() {
        if (selectedDoctorId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete!",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this doctor?\nThis action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = "DELETE FROM doctors WHERE id=?";
                PreparedStatement pstmt = connection.prepareStatement(query);
                pstmt.setInt(1, selectedDoctorId);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Doctor deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDoctorData();
                    clearForm();
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting doctor: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        specializationField.setText("");
        phoneField.setText("");
        emailField.setText("");
        selectedDoctorId = -1;
        doctorTable.clearSelection();
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter doctor name!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (specializationField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter specialization!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            specializationField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter phone number!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        return true;
    }
}