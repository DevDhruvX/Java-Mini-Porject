package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class PrescriptionViewerWindow extends JFrame {
    private Connection connection;
    private JTable prescriptionTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;

    public PrescriptionViewerWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadPrescriptions();
    }

    private void initializeGUI() {
        setTitle("Prescription Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("All Prescriptions", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[] columns = { "ID", "Patient", "Doctor", "Date", "Diagnosis", "Next Visit" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        prescriptionTable = new JTable(tableModel);
        prescriptionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionTable.setRowHeight(25);
        prescriptionTable.getTableHeader().setReorderingAllowed(false);

        // Add mouse listener for row selection
        prescriptionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    showPrescriptionDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(prescriptionTable);
        scrollPane.setPreferredSize(new Dimension(850, 250));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        detailsPanel.setPreferredSize(new Dimension(850, 250));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setBackground(new Color(248, 249, 250));
        detailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, detailsPanel);
        splitPane.setDividerLocation(270);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadPrescriptions());

        JButton printBtn = new JButton("Print Prescription");
        printBtn.setBackground(new Color(46, 204, 113));
        printBtn.setForeground(Color.WHITE);
        printBtn.addActionListener(e -> printSelectedPrescription());

        buttonPanel.add(refreshBtn);
        buttonPanel.add(printBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPrescriptions() {
        try {
            String query = "SELECT p.id, pt.name as patient_name, d.name as doctor_name, " +
                    "p.prescription_date, p.diagnosis, p.next_visit_date " +
                    "FROM prescriptions p " +
                    "JOIN patients pt ON p.patient_id = pt.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "ORDER BY p.prescription_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("prescription_date"),
                        rs.getString("diagnosis"),
                        rs.getDate("next_visit_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading prescriptions: " + e.getMessage());
        }
    }

    private void showPrescriptionDetails() {
        int selectedRow = prescriptionTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        int prescriptionId = (Integer) tableModel.getValueAt(selectedRow, 0);
        loadPrescriptionDetails(prescriptionId);
    }

    private void loadPrescriptionDetails(int prescriptionId) {
        try {
            // Get prescription details
            String prescriptionQuery = "SELECT p.*, pt.name as patient_name, pt.age, pt.gender, " +
                    "d.name as doctor_name, d.specialization " +
                    "FROM prescriptions p " +
                    "JOIN patients pt ON p.patient_id = pt.id " +
                    "JOIN doctors d ON p.doctor_id = d.id " +
                    "WHERE p.id = ?";

            PreparedStatement prescriptionStmt = connection.prepareStatement(prescriptionQuery);
            prescriptionStmt.setInt(1, prescriptionId);
            ResultSet prescriptionRs = prescriptionStmt.executeQuery();

            StringBuilder details = new StringBuilder();
            if (prescriptionRs.next()) {
                details.append("==================== PRESCRIPTION ====================\n\n");
                details.append("Prescription ID: ").append(prescriptionRs.getInt("id")).append("\n");
                details.append("Date: ").append(prescriptionRs.getDate("prescription_date")).append("\n");
                details.append("Time: ").append(prescriptionRs.getTime("prescription_time")).append("\n\n");

                details.append("PATIENT INFORMATION:\n");
                details.append("Name: ").append(prescriptionRs.getString("patient_name")).append("\n");
                details.append("Age: ").append(prescriptionRs.getInt("age")).append(" years\n");
                details.append("Gender: ").append(prescriptionRs.getString("gender")).append("\n\n");

                details.append("DOCTOR INFORMATION:\n");
                details.append("Name: Dr. ").append(prescriptionRs.getString("doctor_name")).append("\n");
                details.append("Specialization: ").append(prescriptionRs.getString("specialization")).append("\n\n");

                details.append("CHIEF COMPLAINT:\n");
                details.append(prescriptionRs.getString("chief_complaint")).append("\n\n");

                details.append("DIAGNOSIS:\n");
                details.append(prescriptionRs.getString("diagnosis")).append("\n\n");

                if (prescriptionRs.getString("notes") != null && !prescriptionRs.getString("notes").trim().isEmpty()) {
                    details.append("NOTES:\n");
                    details.append(prescriptionRs.getString("notes")).append("\n\n");
                }
            }

            // Get medicines
            String medicineQuery = "SELECT * FROM prescription_medicines WHERE prescription_id = ? ORDER BY id";
            PreparedStatement medicineStmt = connection.prepareStatement(medicineQuery);
            medicineStmt.setInt(1, prescriptionId);
            ResultSet medicineRs = medicineStmt.executeQuery();

            details.append("MEDICATIONS:\n");
            details.append("──────────────────────────────────────────────────────\n");
            int medicineCount = 1;

            while (medicineRs.next()) {
                details.append(medicineCount).append(". ").append(medicineRs.getString("medicine_name")).append("\n");
                details.append("   Dosage: ").append(medicineRs.getString("dosage")).append("\n");
                details.append("   Frequency: ").append(medicineRs.getString("frequency")).append("\n");
                details.append("   Duration: ").append(medicineRs.getString("duration")).append("\n");
                details.append("   Quantity: ").append(medicineRs.getInt("quantity")).append("\n");

                if (medicineRs.getString("instructions") != null
                        && !medicineRs.getString("instructions").trim().isEmpty()) {
                    details.append("   Instructions: ").append(medicineRs.getString("instructions")).append("\n");
                }
                details.append("\n");
                medicineCount++;
            }

            // Add next visit date if available
            prescriptionRs.first(); // Reset to first row
            if (prescriptionRs.getDate("next_visit_date") != null) {
                details.append("NEXT VISIT DATE: ").append(prescriptionRs.getDate("next_visit_date")).append("\n\n");
            }

            details.append("========================================================");

            detailsArea.setText(details.toString());
            detailsArea.setCaretPosition(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading prescription details: " + e.getMessage());
        }
    }

    private void printSelectedPrescription() {
        int selectedRow = prescriptionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a prescription to print!");
            return;
        }

        if (detailsArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No prescription details to print!");
            return;
        }

        try {
            // Use the existing printing functionality
            boolean printed = detailsArea.print();
            if (printed) {
                JOptionPane.showMessageDialog(this, "Prescription printed successfully!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing prescription: " + e.getMessage());
        }
    }
}