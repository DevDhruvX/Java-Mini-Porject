package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

// Import advanced feature windows
import HospitalManagementSystem.gui.PrescriptionManagementWindow;
import HospitalManagementSystem.gui.MedicalHistoryWindow;
import HospitalManagementSystem.gui.LabTestManagementWindow;
import HospitalManagementSystem.gui.BillingManagementWindow;

public class HospitalGUI extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "";

    private Connection connection;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public HospitalGUI() {
        initializeDatabase();
        initializeGUI();
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeGUI() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Set look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Use default look and feel
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add different screens
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");

        add(mainPanel);

        // Show login screen first
        cardLayout.show(mainPanel, "LOGIN");

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(new Color(33, 150, 243));

        // Title
        JLabel titleLabel = new JLabel("Hospital Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(50, 0, 30, 0));

        // Login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 40, 40, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login title
        JLabel loginTitle = new JLabel("Admin Login");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));
        loginTitle.setForeground(new Color(51, 51, 51));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(loginTitle, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(userLabel, gbc);

        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(new EmptyBorder(12, 30, 12, 30));
        loginButton.setFocusPainted(false);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            if ("admin".equals(user) && "admin".equals(pass)) {
                cardLayout.show(mainPanel, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(loginButton, gbc);

        // Center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(33, 150, 243));
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        formPanel.setPreferredSize(new Dimension(400, 300));
        centerPanel.add(formPanel, centerGbc);

        loginPanel.add(titleLabel, BorderLayout.NORTH);
        loginPanel.add(centerPanel, BorderLayout.CENTER);

        return loginPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(245, 245, 245));

        // Top menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(33, 150, 243));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(Color.WHITE);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Hospital Management System v1.0\nDeveloped with Java Swing",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        // Left sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(38, 50, 56));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("Arial", Font.BOLD, 16));
        navTitle.setForeground(Color.WHITE);
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navTitle);

        sidebar.add(Box.createVerticalStrut(20));

        // Navigation buttons
        JButton patientsBtn = createSidebarButton("Patients", () -> openPatientManagement());
        JButton doctorsBtn = createSidebarButton("Doctors", () -> openDoctorManagement());
        JButton appointmentsBtn = createSidebarButton("Appointments", () -> openAppointmentManagement());
        JButton prescriptionsBtn = createSidebarButton("Prescriptions", () -> openPrescriptionManagement());
        JButton medicalHistoryBtn = createSidebarButton("Medical History", () -> openMedicalHistory());
        JButton labTestsBtn = createSidebarButton("Lab Tests", () -> openLabTests());
        JButton billingBtn = createSidebarButton("Billing", () -> openBilling());
        JButton reportsBtn = createSidebarButton("Reports", () -> openReports());
        JButton logoutBtn = createSidebarButton("Logout", () -> cardLayout.show(mainPanel, "LOGIN"));

        sidebar.add(patientsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(doctorsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(appointmentsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(prescriptionsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(medicalHistoryBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(labTestsBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(billingBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reportsBtn);
        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(logoutBtn);

        // Center content
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(new Color(245, 245, 245));
        centerContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(51, 51, 51));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(welcomeLabel);

        // Add current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm:ss");
        JLabel timeLabel = new JLabel("Last Updated: " + sdf.format(new Date()));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(102, 102, 102));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(timeLabel);

        centerContent.add(Box.createVerticalStrut(30));

        // Dashboard cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        cardsPanel.setBackground(new Color(245, 245, 245));

        // Get real data from database
        String patientCount = getDatabaseCount("SELECT COUNT(*) FROM patients");
        String doctorCount = getDatabaseCount("SELECT COUNT(*) FROM doctors");
        String todayAppointments = getDatabaseCount(
                "SELECT COUNT(*) FROM appointments WHERE DATE(appointment_date) = CURDATE()");
        String thisWeekAppointments = getDatabaseCount(
                "SELECT COUNT(*) FROM appointments WHERE YEARWEEK(appointment_date) = YEARWEEK(CURDATE())");
        String completedAppointments = getDatabaseCount(
                "SELECT COUNT(*) FROM appointments WHERE status = 'Completed'");
        String pendingAppointments = getDatabaseCount(
                "SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled' AND appointment_date >= CURDATE()");

        // Create cards with real data
        cardsPanel.add(createDashboardCard("Total Patients", patientCount, new Color(76, 175, 80)));
        cardsPanel.add(createDashboardCard("Total Doctors", doctorCount, new Color(33, 150, 243)));
        cardsPanel.add(createDashboardCard("Today's Appointments", todayAppointments, new Color(255, 152, 0)));
        cardsPanel.add(createDashboardCard("This Week", thisWeekAppointments, new Color(156, 39, 176)));
        cardsPanel.add(createDashboardCard("Completed", completedAppointments, new Color(76, 175, 80)));
        cardsPanel.add(createDashboardCard("Pending", pendingAppointments, new Color(244, 67, 54)));

        centerContent.add(cardsPanel);

        // Add refresh button
        centerContent.add(Box.createVerticalStrut(20));
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(33, 150, 243));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        refreshButton.setFocusPainted(false);
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> refreshDashboard());
        centerContent.add(refreshButton);

        // Add quick stats panel
        centerContent.add(Box.createVerticalStrut(20));
        JPanel quickStatsPanel = createQuickStatsPanel();
        centerContent.add(quickStatsPanel);

        dashboardPanel.add(menuBar, BorderLayout.NORTH);
        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(centerContent, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private JButton createSidebarButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(38, 50, 56));
        button.setBorder(new EmptyBorder(12, 15, 12, 15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));

        button.addActionListener(e -> action.run());

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(55, 71, 79));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(38, 50, 56));
            }
        });

        return button;
    }

    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(30, 30, 30, 30)));
        card.setPreferredSize(new Dimension(200, 150));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(102, 102, 102));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);

        return card;
    }

    private void openPatientManagement() {
        new PatientManagementWindow(connection).setVisible(true);
    }

    private void openDoctorManagement() {
        new DoctorManagementWindow(connection).setVisible(true);
    }

    private void openAppointmentManagement() {
        new AppointmentManagementWindow(connection).setVisible(true);
    }

    private void openReports() {
        // Create a dialog to choose report type
        Object[] options = { "Basic Reports", "Comprehensive Report", "PDF Report", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                "Choose the type of report you want to generate:",
                "Report Selection",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Basic Reports
                new ReportsWindow(connection).setVisible(true);
                break;
            case 1: // Comprehensive Report
                new ComprehensiveReportGenerator(connection).setVisible(true);
                break;
            case 2: // PDF Report
                new PDFReportGenerator(connection).setVisible(true);
                break;
            default: // Cancel or close
                break;
        }
    }

    private void openPrescriptionManagement() {
        new PrescriptionManagementWindow(connection).setVisible(true);
    }

    private void openMedicalHistory() {
        new MedicalHistoryWindow(connection).setVisible(true);
    }

    private void openLabTests() {
        new LabTestManagementWindow(connection).setVisible(true);
    }

    private void openBilling() {
        new BillingManagementWindow(connection).setVisible(true);
    }

    private String getDatabaseCount(String query) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private void refreshDashboard() {
        // Show loading message
        JOptionPane.showMessageDialog(this, "Dashboard refreshed with latest data!",
                "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);

        // Recreate dashboard to refresh all data
        cardLayout.show(mainPanel, "DASHBOARD");

        // Remove current dashboard and recreate it
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel) {
                mainPanel.remove(comp);
                break;
            }
        }
        mainPanel.add(createDashboardPanel(), "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");

        // Repaint to ensure UI updates
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createQuickStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Quick Statistics"),
                new EmptyBorder(15, 15, 15, 15)));
        panel.setMaximumSize(new Dimension(600, 200));

        // Get additional statistics
        String avgAge = getDatabaseAverage("SELECT AVG(age) FROM patients");
        String mostCommonGender = getDatabaseString(
                "SELECT gender FROM patients GROUP BY gender ORDER BY COUNT(*) DESC LIMIT 1");
        String busyDoctor = getDatabaseString(
                "SELECT d.name FROM doctors d JOIN appointments a ON d.id = a.doctor_id GROUP BY d.id ORDER BY COUNT(*) DESC LIMIT 1");
        String nextAppointment = getDatabaseString(
                "SELECT DATE(appointment_date) FROM appointments WHERE appointment_date > NOW() ORDER BY appointment_date ASC LIMIT 1");

        // Create stats text
        JTextArea statsText = new JTextArea();
        statsText.setFont(new Font("Arial", Font.PLAIN, 12));
        statsText.setEditable(false);
        statsText.setBackground(Color.WHITE);

        StringBuilder stats = new StringBuilder();
        stats.append("Average Patient Age: ").append(avgAge).append(" years\n");
        stats.append("Most Common Gender: ").append(mostCommonGender != null ? mostCommonGender : "N/A").append("\n");
        stats.append("Busiest Doctor: ").append(busyDoctor != null ? busyDoctor : "N/A").append("\n");
        stats.append("Next Appointment: ")
                .append(nextAppointment != null ? nextAppointment : "No upcoming appointments");

        statsText.setText(stats.toString());
        panel.add(statsText);

        return panel;
    }

    private String getDatabaseAverage(String query) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                double avg = rs.getDouble(1);
                return String.format("%.1f", avg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getDatabaseString(String query) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalGUI());
    }
}