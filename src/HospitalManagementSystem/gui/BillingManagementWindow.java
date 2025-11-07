package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.text.DecimalFormat;

public class BillingManagementWindow extends JFrame {
    private Connection connection;
    private JComboBox<String> patientComboBox;
    private JComboBox<String> appointmentComboBox;
    private JTextField billDateField;
    private JTextField consultationFeeField;
    private JTextField medicineChargesField;
    private JTextField labChargesField;
    private JTextField otherChargesField;
    private JTextField totalAmountField;
    private JTextField paidAmountField;
    private JComboBox<String> paymentStatusComboBox;
    private JComboBox<String> paymentMethodComboBox;
    private JTextField insuranceClaimField;
    private JTextArea notesArea;

    private JTable billingTable;
    private DefaultTableModel tableModel;
    private JTextArea billDetailsArea;

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    public BillingManagementWindow(Connection connection) {
        this.connection = connection;
        initializeGUI();
        loadPatients();
        loadAppointments();
        loadBillingRecords();
    }

    private void initializeGUI() {
        setTitle("Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Billing Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(41, 128, 185));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add Bill Tab
        JPanel addBillPanel = createAddBillPanel();
        tabbedPane.addTab("Create Bill", addBillPanel);

        // View Bills Tab
        JPanel viewBillsPanel = createViewBillsPanel();
        tabbedPane.addTab("View Bills", viewBillsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createAddBillPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Patient selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        patientComboBox = new JComboBox<>();
        patientComboBox.setPreferredSize(new Dimension(200, 25));
        patientComboBox.addActionListener(e -> loadAppointmentsForPatient());
        formPanel.add(patientComboBox, gbc);

        // Appointment selection
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Appointment:"), gbc);
        gbc.gridx = 3;
        appointmentComboBox = new JComboBox<>();
        appointmentComboBox.setPreferredSize(new Dimension(200, 25));
        appointmentComboBox.addActionListener(e -> loadConsultationFee());
        formPanel.add(appointmentComboBox, gbc);

        // Bill date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Bill Date:"), gbc);
        gbc.gridx = 1;
        billDateField = new JTextField(LocalDate.now().toString(), 15);
        billDateField.setToolTipText("YYYY-MM-DD format");
        formPanel.add(billDateField, gbc);

        // Payment status
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 3;
        paymentStatusComboBox = new JComboBox<>(new String[] {
                "Pending", "Partial", "Paid", "Overdue", "Cancelled"
        });
        formPanel.add(paymentStatusComboBox, gbc);

        // Charges section
        JPanel chargesPanel = createChargesPanel();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(chargesPanel, gbc);

        // Payment details section
        JPanel paymentPanel = createPaymentPanel();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(paymentPanel, gbc);

        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(notesArea), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton calculateBtn = new JButton("Calculate Total");
        calculateBtn.setBackground(new Color(230, 126, 34));
        calculateBtn.setForeground(Color.WHITE);
        calculateBtn.addActionListener(e -> calculateTotal());

        JButton saveBillBtn = new JButton("Save Bill");
        saveBillBtn.setBackground(new Color(46, 204, 113));
        saveBillBtn.setForeground(Color.WHITE);
        saveBillBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBillBtn.addActionListener(e -> saveBill());

        JButton printBillBtn = new JButton("Print Bill");
        printBillBtn.setBackground(new Color(52, 152, 219));
        printBillBtn.setForeground(Color.WHITE);
        printBillBtn.addActionListener(e -> printBill());

        JButton clearBtn = new JButton("Clear Form");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(calculateBtn);
        buttonPanel.add(saveBillBtn);
        buttonPanel.add(printBillBtn);
        buttonPanel.add(clearBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createChargesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Charges Breakdown"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Consultation fee
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Consultation Fee:"), gbc);
        gbc.gridx = 1;
        consultationFeeField = new JTextField("0.00", 10);
        consultationFeeField.setToolTipText("Doctor consultation charges");
        panel.add(consultationFeeField, gbc);

        // Medicine charges
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Medicine Charges:"), gbc);
        gbc.gridx = 3;
        medicineChargesField = new JTextField("0.00", 10);
        medicineChargesField.setToolTipText("Total medicine costs");
        panel.add(medicineChargesField, gbc);

        // Lab charges
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Lab Charges:"), gbc);
        gbc.gridx = 1;
        labChargesField = new JTextField("0.00", 10);
        labChargesField.setToolTipText("Laboratory test charges");
        panel.add(labChargesField, gbc);

        // Other charges
        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(new JLabel("Other Charges:"), gbc);
        gbc.gridx = 3;
        otherChargesField = new JTextField("0.00", 10);
        otherChargesField.setToolTipText("Additional charges (procedures, etc.)");
        panel.add(otherChargesField, gbc);

        // Total amount (read-only)
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountField = new JTextField("0.00", 15);
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(new Color(230, 230, 230));
        totalAmountField.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(totalAmountField, gbc);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Paid amount
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Paid Amount:"), gbc);
        gbc.gridx = 1;
        paidAmountField = new JTextField("0.00", 10);
        paidAmountField.setToolTipText("Amount already paid by patient");
        panel.add(paidAmountField, gbc);

        // Payment method
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 3;
        paymentMethodComboBox = new JComboBox<>(new String[] {
                "", "Cash", "Credit Card", "Debit Card", "Bank Transfer", "Check", "Insurance", "UPI"
        });
        panel.add(paymentMethodComboBox, gbc);

        // Insurance claim
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Insurance Claim:"), gbc);
        gbc.gridx = 1;
        insuranceClaimField = new JTextField("0.00", 10);
        insuranceClaimField.setToolTipText("Amount claimed from insurance");
        panel.add(insuranceClaimField, gbc);

        return panel;
    }

    private JPanel createViewBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Patient:"));

        JComboBox<String> filterPatientComboBox = new JComboBox<>();
        filterPatientComboBox.addItem("All Patients");
        loadPatientsToComboBox(filterPatientComboBox);
        filterPatientComboBox.addActionListener(e -> filterBillingRecords(filterPatientComboBox));
        filterPanel.add(filterPatientComboBox);

        filterPanel.add(new JLabel("Payment Status:"));
        JComboBox<String> filterStatusComboBox = new JComboBox<>();
        filterStatusComboBox.addItem("All Status");
        filterStatusComboBox.addItem("Pending");
        filterStatusComboBox.addItem("Partial");
        filterStatusComboBox.addItem("Paid");
        filterStatusComboBox.addItem("Overdue");
        filterStatusComboBox.addActionListener(e -> filterByPaymentStatus(filterStatusComboBox));
        filterPanel.add(filterStatusComboBox);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadBillingRecords());
        filterPanel.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        String[] columns = { "Bill ID", "Patient", "Date", "Total Amount", "Paid Amount", "Balance", "Status",
                "Method" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billingTable = new JTable(tableModel);
        billingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billingTable.setRowHeight(25);
        billingTable.getTableHeader().setReorderingAllowed(false);

        // Add mouse listener for row selection
        billingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    showBillDetails();
                } else if (e.getClickCount() == 2) {
                    updatePayment();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(billingTable);
        tableScrollPane.setPreferredSize(new Dimension(1150, 250));

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Bill Details"));
        detailsPanel.setPreferredSize(new Dimension(1150, 250));

        billDetailsArea = new JTextArea();
        billDetailsArea.setEditable(false);
        billDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billDetailsArea.setBackground(new Color(248, 249, 250));
        billDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(billDetailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Add buttons to details panel
        JPanel detailsButtonPanel = new JPanel(new FlowLayout());
        JButton updatePaymentBtn = new JButton("Update Payment");
        updatePaymentBtn.setBackground(new Color(46, 204, 113));
        updatePaymentBtn.setForeground(Color.WHITE);
        updatePaymentBtn.addActionListener(e -> updatePayment());

        JButton printInvoiceBtn = new JButton("Print Invoice");
        printInvoiceBtn.setBackground(new Color(52, 152, 219));
        printInvoiceBtn.setForeground(Color.WHITE);
        printInvoiceBtn.addActionListener(e -> printSelectedBill());

        detailsButtonPanel.add(updatePaymentBtn);
        detailsButtonPanel.add(printInvoiceBtn);
        detailsPanel.add(detailsButtonPanel, BorderLayout.SOUTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsPanel);
        splitPane.setDividerLocation(270);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPatients() {
        loadPatientsToComboBox(patientComboBox);
    }

    private void loadPatientsToComboBox(JComboBox<String> comboBox) {
        try {
            String query = "SELECT id, name FROM patients ORDER BY name";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (comboBox != patientComboBox) {
                comboBox.removeAllItems();
                comboBox.addItem("All Patients");
            } else {
                comboBox.removeAllItems();
            }

            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void loadAppointments() {
        try {
            String query = "SELECT a.id, p.name as patient_name, d.name as doctor_name, " +
                    "a.appointment_date FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "ORDER BY a.appointment_date DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            appointmentComboBox.removeAllItems();
            appointmentComboBox.addItem("No Appointment");

            while (rs.next()) {
                appointmentComboBox.addItem(rs.getInt("id") + " - " +
                        rs.getString("patient_name") + " with Dr. " +
                        rs.getString("doctor_name") + " (" + rs.getDate("appointment_date") + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    private void loadAppointmentsForPatient() {
        if (patientComboBox.getSelectedItem() == null)
            return;

        try {
            String patientStr = patientComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);

            String query = "SELECT a.id, d.name as doctor_name, a.appointment_date " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "WHERE a.patient_id = ? " +
                    "ORDER BY a.appointment_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            appointmentComboBox.removeAllItems();
            appointmentComboBox.addItem("No Appointment");

            while (rs.next()) {
                appointmentComboBox.addItem(rs.getInt("id") + " - Dr. " +
                        rs.getString("doctor_name") + " (" + rs.getDate("appointment_date") + ")");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading patient appointments: " + e.getMessage());
        }
    }

    private void loadConsultationFee() {
        if (appointmentComboBox.getSelectedItem() == null ||
                appointmentComboBox.getSelectedItem().toString().equals("No Appointment")) {
            consultationFeeField.setText("0.00");
            return;
        }

        try {
            String appointmentStr = appointmentComboBox.getSelectedItem().toString();
            int appointmentId = Integer.parseInt(appointmentStr.split(" - ")[0]);

            String query = "SELECT d.consultation_fee FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.id WHERE a.id = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double fee = rs.getDouble("consultation_fee");
                consultationFeeField.setText(currencyFormat.format(fee));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading consultation fee: " + e.getMessage());
        }
    }

    private void calculateTotal() {
        try {
            double consultation = parseAmount(consultationFeeField.getText());
            double medicine = parseAmount(medicineChargesField.getText());
            double lab = parseAmount(labChargesField.getText());
            double other = parseAmount(otherChargesField.getText());

            double total = consultation + medicine + lab + other;
            totalAmountField.setText(currencyFormat.format(total));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid amounts!");
        }
    }

    private double parseAmount(String text) {
        if (text == null || text.trim().isEmpty())
            return 0.0;
        return Double.parseDouble(text.trim().replace(",", ""));
    }

    private void saveBill() {
        if (patientComboBox.getSelectedItem() == null ||
                billDateField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all required fields!");
            return;
        }

        calculateTotal(); // Ensure total is calculated

        try {
            // Extract patient ID
            String patientStr = patientComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);

            // Extract appointment ID if selected
            Integer appointmentId = null;
            if (appointmentComboBox.getSelectedItem() != null &&
                    !appointmentComboBox.getSelectedItem().toString().equals("No Appointment")) {
                String appointmentStr = appointmentComboBox.getSelectedItem().toString();
                appointmentId = Integer.parseInt(appointmentStr.split(" - ")[0]);
            }

            String query = "INSERT INTO billing (patient_id, appointment_id, bill_date, " +
                    "consultation_fee, medicine_charges, lab_charges, other_charges, " +
                    "total_amount, paid_amount, payment_status, payment_method, " +
                    "insurance_claim_amount, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            stmt.setObject(2, appointmentId);
            stmt.setDate(3, Date.valueOf(billDateField.getText().trim()));
            stmt.setDouble(4, parseAmount(consultationFeeField.getText()));
            stmt.setDouble(5, parseAmount(medicineChargesField.getText()));
            stmt.setDouble(6, parseAmount(labChargesField.getText()));
            stmt.setDouble(7, parseAmount(otherChargesField.getText()));
            stmt.setDouble(8, parseAmount(totalAmountField.getText()));
            stmt.setDouble(9, parseAmount(paidAmountField.getText()));
            stmt.setString(10, paymentStatusComboBox.getSelectedItem().toString());
            stmt.setString(11, paymentMethodComboBox.getSelectedItem().toString());
            stmt.setDouble(12, parseAmount(insuranceClaimField.getText()));
            stmt.setString(13, notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Bill saved successfully!");
            clearForm();
            loadBillingRecords();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving bill: " + e.getMessage());
        }
    }

    private void loadBillingRecords() {
        try {
            String query = "SELECT b.id, p.name as patient_name, b.bill_date, " +
                    "b.total_amount, b.paid_amount, " +
                    "(b.total_amount - b.paid_amount) as balance, " +
                    "b.payment_status, b.payment_method " +
                    "FROM billing b " +
                    "JOIN patients p ON b.patient_id = p.id " +
                    "ORDER BY b.bill_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getDate("bill_date"),
                        currencyFormat.format(rs.getDouble("total_amount")),
                        currencyFormat.format(rs.getDouble("paid_amount")),
                        currencyFormat.format(rs.getDouble("balance")),
                        rs.getString("payment_status"),
                        rs.getString("payment_method")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading billing records: " + e.getMessage());
        }
    }

    private void filterBillingRecords(JComboBox<String> filterComboBox) {
        if (filterComboBox.getSelectedItem() == null ||
                filterComboBox.getSelectedItem().toString().equals("All Patients")) {
            loadBillingRecords();
            return;
        }

        try {
            String patientStr = filterComboBox.getSelectedItem().toString();
            int patientId = Integer.parseInt(patientStr.split(" - ")[0]);

            String query = "SELECT b.id, p.name as patient_name, b.bill_date, " +
                    "b.total_amount, b.paid_amount, " +
                    "(b.total_amount - b.paid_amount) as balance, " +
                    "b.payment_status, b.payment_method " +
                    "FROM billing b " +
                    "JOIN patients p ON b.patient_id = p.id " +
                    "WHERE b.patient_id = ? " +
                    "ORDER BY b.bill_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getDate("bill_date"),
                        currencyFormat.format(rs.getDouble("total_amount")),
                        currencyFormat.format(rs.getDouble("paid_amount")),
                        currencyFormat.format(rs.getDouble("balance")),
                        rs.getString("payment_status"),
                        rs.getString("payment_method")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering billing records: " + e.getMessage());
        }
    }

    private void filterByPaymentStatus(JComboBox<String> statusComboBox) {
        if (statusComboBox.getSelectedItem() == null ||
                statusComboBox.getSelectedItem().toString().equals("All Status")) {
            loadBillingRecords();
            return;
        }

        try {
            String status = statusComboBox.getSelectedItem().toString();

            String query = "SELECT b.id, p.name as patient_name, b.bill_date, " +
                    "b.total_amount, b.paid_amount, " +
                    "(b.total_amount - b.paid_amount) as balance, " +
                    "b.payment_status, b.payment_method " +
                    "FROM billing b " +
                    "JOIN patients p ON b.patient_id = p.id " +
                    "WHERE b.payment_status = ? " +
                    "ORDER BY b.bill_date DESC";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getDate("bill_date"),
                        currencyFormat.format(rs.getDouble("total_amount")),
                        currencyFormat.format(rs.getDouble("paid_amount")),
                        currencyFormat.format(rs.getDouble("balance")),
                        rs.getString("payment_status"),
                        rs.getString("payment_method")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering by status: " + e.getMessage());
        }
    }

    private void showBillDetails() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow < 0)
            return;

        int billId = (Integer) tableModel.getValueAt(selectedRow, 0);
        loadBillDetails(billId);
    }

    private void loadBillDetails(int billId) {
        try {
            String query = "SELECT b.*, p.name as patient_name, p.phone, p.address " +
                    "FROM billing b " +
                    "JOIN patients p ON b.patient_id = p.id " +
                    "WHERE b.id = ?";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder details = new StringBuilder();
            if (rs.next()) {
                details.append("===================== HOSPITAL BILL =====================\n\n");
                details.append("Bill ID: ").append(rs.getInt("id")).append("\n");
                details.append("Date: ").append(rs.getDate("bill_date")).append("\n\n");

                details.append("PATIENT INFORMATION:\n");
                details.append("Name: ").append(rs.getString("patient_name")).append("\n");
                details.append("Phone: ").append(rs.getString("phone")).append("\n");
                details.append("Address: ").append(rs.getString("address")).append("\n\n");

                details.append("CHARGES BREAKDOWN:\n");
                details.append("──────────────────────────────────────────────────────\n");
                details.append("Consultation Fee:      ")
                        .append(currencyFormat.format(rs.getDouble("consultation_fee"))).append("\n");
                details.append("Medicine Charges:      ")
                        .append(currencyFormat.format(rs.getDouble("medicine_charges"))).append("\n");
                details.append("Lab Charges:           ").append(currencyFormat.format(rs.getDouble("lab_charges")))
                        .append("\n");
                details.append("Other Charges:         ").append(currencyFormat.format(rs.getDouble("other_charges")))
                        .append("\n");
                details.append("──────────────────────────────────────────────────────\n");
                details.append("TOTAL AMOUNT:          ").append(currencyFormat.format(rs.getDouble("total_amount")))
                        .append("\n\n");

                details.append("PAYMENT INFORMATION:\n");
                details.append("──────────────────────────────────────────────────────\n");
                details.append("Paid Amount:           ").append(currencyFormat.format(rs.getDouble("paid_amount")))
                        .append("\n");
                details.append("Balance Due:           ").append(currencyFormat.format(
                        rs.getDouble("total_amount") - rs.getDouble("paid_amount"))).append("\n");
                details.append("Payment Status:        ").append(rs.getString("payment_status")).append("\n");
                details.append("Payment Method:        ").append(rs.getString("payment_method")).append("\n");

                if (rs.getDouble("insurance_claim_amount") > 0) {
                    details.append("Insurance Claim:       ")
                            .append(currencyFormat.format(rs.getDouble("insurance_claim_amount"))).append("\n");
                }

                if (rs.getString("notes") != null && !rs.getString("notes").trim().isEmpty()) {
                    details.append("\nNOTES:\n");
                    details.append(rs.getString("notes")).append("\n");
                }

                details.append("\nGenerated: ").append(rs.getTimestamp("created_at"));
                details.append("\n======================================================");
            }

            billDetailsArea.setText(details.toString());
            billDetailsArea.setCaretPosition(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bill details: " + e.getMessage());
        }
    }

    private void updatePayment() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill to update!");
            return;
        }

        int billId = (Integer) tableModel.getValueAt(selectedRow, 0);

        String paidAmount = JOptionPane.showInputDialog(this, "Enter Paid Amount:", "0.00");
        if (paidAmount == null)
            return;

        String paymentMethod = (String) JOptionPane.showInputDialog(this, "Payment Method:", "Payment Update",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "Cash", "Credit Card", "Debit Card", "Bank Transfer", "Check", "Insurance", "UPI" },
                "Cash");
        if (paymentMethod == null)
            return;

        String status = (String) JOptionPane.showInputDialog(this, "Payment Status:", "Status Update",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "Pending", "Partial", "Paid", "Overdue", "Cancelled" },
                "Paid");
        if (status == null)
            return;

        try {
            String query = "UPDATE billing SET paid_amount = ?, payment_method = ?, payment_status = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setDouble(1, parseAmount(paidAmount));
            stmt.setString(2, paymentMethod);
            stmt.setString(3, status);
            stmt.setInt(4, billId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Payment updated successfully!");
            loadBillingRecords();
            showBillDetails(); // Refresh details

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating payment: " + e.getMessage());
        }
    }

    private void printBill() {
        if (totalAmountField.getText().equals("0.00")) {
            JOptionPane.showMessageDialog(this, "Please calculate total amount first!");
            return;
        }

        // Create a printable bill summary
        StringBuilder bill = new StringBuilder();
        bill.append("HOSPITAL BILL\n");
        bill.append("Date: ").append(billDateField.getText()).append("\n\n");
        bill.append("Patient: ").append(patientComboBox.getSelectedItem()).append("\n");
        bill.append("Consultation Fee: ").append(consultationFeeField.getText()).append("\n");
        bill.append("Medicine Charges: ").append(medicineChargesField.getText()).append("\n");
        bill.append("Lab Charges: ").append(labChargesField.getText()).append("\n");
        bill.append("Other Charges: ").append(otherChargesField.getText()).append("\n");
        bill.append("Total Amount: ").append(totalAmountField.getText()).append("\n");

        try {
            JTextArea printArea = new JTextArea(bill.toString());
            printArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing bill: " + e.getMessage());
        }
    }

    private void printSelectedBill() {
        if (billDetailsArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No bill details to print!");
            return;
        }

        try {
            billDetailsArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing bill: " + e.getMessage());
        }
    }

    private void clearForm() {
        billDateField.setText(LocalDate.now().toString());
        consultationFeeField.setText("0.00");
        medicineChargesField.setText("0.00");
        labChargesField.setText("0.00");
        otherChargesField.setText("0.00");
        totalAmountField.setText("0.00");
        paidAmountField.setText("0.00");
        insuranceClaimField.setText("0.00");
        notesArea.setText("");
        paymentStatusComboBox.setSelectedIndex(0);
        paymentMethodComboBox.setSelectedIndex(0);
        appointmentComboBox.removeAllItems();
        appointmentComboBox.addItem("No Appointment");
    }
}