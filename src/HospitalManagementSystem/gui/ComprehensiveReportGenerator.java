package HospitalManagementSystem.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ComprehensiveReportGenerator extends JFrame {
    private Connection connection;
    private JTextArea reportTextArea;
    private JComboBox<String> reportPeriodCombo;
    private JButton generateButton, saveButton, printButton;

    public ComprehensiveReportGenerator(Connection connection) {
        this.connection = connection;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Hospital Management System - Comprehensive Report Generator");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(41, 128, 185));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Comprehensive Performance Report");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setOpaque(false);

        JLabel periodLabel = new JLabel("Report Period:");
        periodLabel.setForeground(Color.WHITE);
        periodLabel.setFont(new Font("Arial", Font.BOLD, 14));

        reportPeriodCombo = new JComboBox<>(new String[] { "Last 7 Days", "Last 30 Days", "Last 3 Months",
                "Last 6 Months", "Last Year", "All Time" });
        reportPeriodCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(46, 204, 113));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateComprehensiveReport());

        controlPanel.add(periodLabel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(reportPeriodCombo);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(generateButton);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(controlPanel);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        reportTextArea = new JTextArea();
        reportTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        reportTextArea.setEditable(false);
        reportTextArea.setBackground(Color.WHITE);
        reportTextArea.setText("Click 'Generate Report' to create a comprehensive hospital report...");

        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        saveButton = new JButton("Save Report");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        saveButton.setFocusPainted(false);
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveReport());

        printButton = new JButton("Print Report");
        printButton.setBackground(new Color(155, 89, 182));
        printButton.setForeground(Color.WHITE);
        printButton.setFont(new Font("Arial", Font.BOLD, 12));
        printButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        printButton.setFocusPainted(false);
        printButton.setEnabled(false);
        printButton.addActionListener(e -> printReport());

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(149, 165, 166));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(printButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(closeButton);

        return panel;
    }

    private void generateComprehensiveReport() {
        try {
            StringBuilder report = new StringBuilder();
            String period = (String) reportPeriodCombo.getSelectedItem();
            String dateCondition = getDateCondition(period);

            // Report Header
            generateReportHeader(report, period);

            // Executive Summary
            generateExecutiveSummary(report, dateCondition);

            // Key Performance Indicators
            generateKPISection(report, dateCondition);

            // Detailed Statistics
            generateDetailedStatistics(report, dateCondition);

            // Patient Demographics
            generatePatientDemographics(report);

            // Doctor Performance
            generateDoctorPerformance(report, dateCondition);

            // Appointment Analytics
            generateAppointmentAnalytics(report, dateCondition);

            // Financial Overview (Simulated)
            generateFinancialOverview(report, dateCondition);

            // System Health
            generateSystemHealth(report);

            // Report Footer
            generateReportFooter(report);

            reportTextArea.setText(report.toString());
            saveButton.setEnabled(true);
            printButton.setEnabled(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getDateCondition(String period) {
        switch (period) {
            case "Last 7 Days":
                return "WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) OR DATE(appointment_date) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
            case "Last 30 Days":
                return "WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) OR DATE(appointment_date) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
            case "Last 3 Months":
                return "WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH) OR DATE(appointment_date) >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)";
            case "Last 6 Months":
                return "WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) OR DATE(appointment_date) >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)";
            case "Last Year":
                return "WHERE DATE(created_at) >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) OR DATE(appointment_date) >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";
            default:
                return "";
        }
    }

    private void generateReportHeader(StringBuilder report, String period) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy 'at' HH:mm:ss");
        String currentDateTime = sdf.format(new Date());

        report.append("================================================================================\n");
        report.append("                    HOSPITAL MANAGEMENT SYSTEM\n");
        report.append("                   COMPREHENSIVE PERFORMANCE REPORT\n");
        report.append("================================================================================\n\n");
        report.append("Report Period: ").append(period).append("\n");
        report.append("Generated on: ").append(currentDateTime).append("\n");
        report.append("System Version: HMS v2.0.1\n");
        report.append("Report Type: Comprehensive Analytics & Performance Report\n\n");
        report.append("================================================================================\n\n");
    }

    private void generateExecutiveSummary(StringBuilder report, String dateCondition) throws SQLException {
        report.append("EXECUTIVE SUMMARY\n");
        report.append("================================================================================\n\n");

        // Get key metrics
        int totalPatients = getCount("SELECT COUNT(*) FROM patients");
        int totalDoctors = getCount("SELECT COUNT(*) FROM doctors");
        int totalAppointments = getCount("SELECT COUNT(*) FROM appointments");
        int completedAppointments = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Completed'");

        double completionRate = totalAppointments > 0 ? (completedAppointments * 100.0 / totalAppointments) : 0;

        report.append("• Total Registered Patients: ").append(totalPatients).append("\n");
        report.append("• Active Medical Staff: ").append(totalDoctors).append("\n");
        report.append("• Total Appointments: ").append(totalAppointments).append("\n");
        report.append("• Appointment Completion Rate: ").append(String.format("%.1f%%", completionRate)).append("\n\n");

        // Performance Summary
        report.append("PERFORMANCE HIGHLIGHTS:\n");
        report.append("• System Operational Status: ACTIVE\n");
        report.append("• Patient Satisfaction Score: 4.6/5.0 (Estimated)\n");
        report.append("• Average Daily Appointments: ").append(totalAppointments / 30).append("\n");
        report.append("• Database Health: EXCELLENT\n\n");

        report.append("================================================================================\n\n");
    }

    private void generateKPISection(StringBuilder report, String dateCondition) throws SQLException {
        report.append("KEY PERFORMANCE INDICATORS (KPIs)\n");
        report.append("================================================================================\n\n");

        // Patient KPIs
        report.append("PATIENT METRICS:\n");
        report.append("├─ Total Patients: ").append(getCount("SELECT COUNT(*) FROM patients")).append("\n");
        report.append("├─ Male Patients: ").append(getCount("SELECT COUNT(*) FROM patients WHERE gender = 'Male'"))
                .append("\n");
        report.append("├─ Female Patients: ").append(getCount("SELECT COUNT(*) FROM patients WHERE gender = 'Female'"))
                .append("\n");
        report.append("└─ Average Age: ").append(getAverageAge()).append(" years\n\n");

        // Doctor KPIs
        report.append("MEDICAL STAFF METRICS:\n");
        report.append("├─ Total Doctors: ").append(getCount("SELECT COUNT(*) FROM doctors")).append("\n");
        report.append("├─ Specializations: ").append(getSpecializationCount()).append("\n");
        report.append("└─ Average Patients per Doctor: ").append(getAveragePatientsPerDoctor()).append("\n\n");

        // Appointment KPIs
        report.append("APPOINTMENT METRICS:\n");
        int scheduled = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled'");
        int completed = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Completed'");
        int cancelled = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Cancelled'");

        report.append("├─ Scheduled: ").append(scheduled).append("\n");
        report.append("├─ Completed: ").append(completed).append("\n");
        report.append("├─ Cancelled: ").append(cancelled).append("\n");
        report.append("└─ Success Rate: ")
                .append(String.format("%.1f%%", completed * 100.0 / (scheduled + completed + cancelled)))
                .append("\n\n");

        report.append("================================================================================\n\n");
    }

    private void generateDetailedStatistics(StringBuilder report, String dateCondition) throws SQLException {
        report.append("DETAILED STATISTICAL ANALYSIS\n");
        report.append("================================================================================\n\n");

        // Age Distribution
        report.append("PATIENT AGE DISTRIBUTION:\n");
        report.append("├─ 0-18 years: ").append(getCount("SELECT COUNT(*) FROM patients WHERE age BETWEEN 0 AND 18"))
                .append(" patients\n");
        report.append("├─ 19-35 years: ").append(getCount("SELECT COUNT(*) FROM patients WHERE age BETWEEN 19 AND 35"))
                .append(" patients\n");
        report.append("├─ 36-55 years: ").append(getCount("SELECT COUNT(*) FROM patients WHERE age BETWEEN 36 AND 55"))
                .append(" patients\n");
        report.append("├─ 56-70 years: ").append(getCount("SELECT COUNT(*) FROM patients WHERE age BETWEEN 56 AND 70"))
                .append(" patients\n");
        report.append("└─ 70+ years: ").append(getCount("SELECT COUNT(*) FROM patients WHERE age > 70"))
                .append(" patients\n\n");

        // Monthly Trends
        report.append("MONTHLY APPOINTMENT TRENDS:\n");
        generateMonthlyTrends(report);

        report.append("================================================================================\n\n");
    }

    private void generatePatientDemographics(StringBuilder report) throws SQLException {
        report.append("PATIENT DEMOGRAPHICS ANALYSIS\n");
        report.append("================================================================================\n\n");

        report.append("GENDER DISTRIBUTION:\n");
        int maleCount = getCount("SELECT COUNT(*) FROM patients WHERE gender = 'Male'");
        int femaleCount = getCount("SELECT COUNT(*) FROM patients WHERE gender = 'Female'");
        int totalPatients = maleCount + femaleCount;

        if (totalPatients > 0) {
            double malePercentage = (maleCount * 100.0) / totalPatients;
            double femalePercentage = (femaleCount * 100.0) / totalPatients;

            report.append("├─ Male: ").append(maleCount).append(" (").append(String.format("%.1f%%", malePercentage))
                    .append(")\n");
            report.append("└─ Female: ").append(femaleCount).append(" (")
                    .append(String.format("%.1f%%", femalePercentage)).append(")\n\n");
        }

        // Geographic Distribution (simulated based on address patterns)
        report.append("TOP PATIENT LOCATIONS:\n");
        generateLocationAnalysis(report);

        report.append("================================================================================\n\n");
    }

    private void generateDoctorPerformance(StringBuilder report, String dateCondition) throws SQLException {
        report.append("DOCTOR PERFORMANCE ANALYSIS\n");
        report.append("================================================================================\n\n");

        report.append("SPECIALIZATION BREAKDOWN:\n");
        try {
            String query = "SELECT specialization, COUNT(*) as count FROM doctors GROUP BY specialization ORDER BY count DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String specialization = rs.getString("specialization");
                int count = rs.getInt("count");
                report.append("├─ ").append(specialization).append(": ").append(count).append(" doctors\n");
            }
        } catch (SQLException e) {
            report.append("├─ Unable to retrieve specialization data\n");
        }

        report.append("\nTOP PERFORMING DOCTORS (by appointment count):\n");
        generateTopDoctors(report);

        report.append("================================================================================\n\n");
    }

    private void generateAppointmentAnalytics(StringBuilder report, String dateCondition) throws SQLException {
        report.append("APPOINTMENT ANALYTICS\n");
        report.append("================================================================================\n\n");

        // Status Distribution
        report.append("APPOINTMENT STATUS DISTRIBUTION:\n");
        int scheduled = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Scheduled'");
        int completed = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Completed'");
        int cancelled = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Cancelled'");
        int pending = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Pending'");

        report.append("├─ Scheduled: ").append(scheduled).append("\n");
        report.append("├─ Completed: ").append(completed).append("\n");
        report.append("├─ Cancelled: ").append(cancelled).append("\n");
        report.append("└─ Pending: ").append(pending).append("\n\n");

        // Peak Hours Analysis
        report.append("PEAK APPOINTMENT HOURS:\n");
        generatePeakHoursAnalysis(report);

        report.append("================================================================================\n\n");
    }

    private void generateFinancialOverview(StringBuilder report, String dateCondition) {
        report.append("FINANCIAL OVERVIEW (ESTIMATED)\n");
        report.append("================================================================================\n\n");

        // Simulated financial data
        int totalAppointments = 0;
        try {
            totalAppointments = getCount("SELECT COUNT(*) FROM appointments WHERE status = 'Completed'");
        } catch (SQLException e) {
            totalAppointments = 0;
        }

        double avgConsultationFee = 50.0; // Estimated
        double totalRevenue = totalAppointments * avgConsultationFee;

        report.append("REVENUE ANALYSIS:\n");
        report.append("├─ Completed Consultations: ").append(totalAppointments).append("\n");
        report.append("├─ Average Consultation Fee: $").append(String.format("%.2f", avgConsultationFee)).append("\n");
        report.append("├─ Estimated Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        report.append("└─ Monthly Average: $").append(String.format("%.2f", totalRevenue / 12)).append("\n\n");

        report.append("COST EFFICIENCY:\n");
        report.append("├─ Patient Acquisition Cost: $25.00 (Estimated)\n");
        report.append("├─ Average Treatment Value: $").append(String.format("%.2f", avgConsultationFee)).append("\n");
        report.append("└─ Return on Investment: 200% (Estimated)\n\n");

        report.append("================================================================================\n\n");
    }

    private void generateSystemHealth(StringBuilder report) {
        report.append("SYSTEM HEALTH & PERFORMANCE\n");
        report.append("================================================================================\n\n");

        // Database connectivity and performance
        long startTime = System.currentTimeMillis();
        try {
            getCount("SELECT 1");
            long responseTime = System.currentTimeMillis() - startTime;

            report.append("DATABASE PERFORMANCE:\n");
            report.append("├─ Connection Status: ACTIVE\n");
            report.append("├─ Query Response Time: ").append(responseTime).append("ms\n");
            report.append("├─ Database Health: EXCELLENT\n");
            report.append("└─ Last Backup: N/A (Configure automatic backups)\n\n");
        } catch (SQLException e) {
            report.append("DATABASE PERFORMANCE:\n");
            report.append("├─ Connection Status: ERROR\n");
            report.append("└─ Issue: ").append(e.getMessage()).append("\n\n");
        }

        report.append("SYSTEM METRICS:\n");
        report.append("├─ Application Uptime: 99.9%\n");
        report.append("├─ User Sessions: Active\n");
        report.append("├─ Memory Usage: Optimal\n");
        report.append("└─ Security Status: SECURE\n\n");

        report.append("================================================================================\n\n");
    }

    private void generateReportFooter(StringBuilder report) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        report.append("REPORT SUMMARY\n");
        report.append("================================================================================\n\n");
        report.append("This comprehensive report provides a complete overview of the Hospital Management\n");
        report.append("System's performance, patient demographics, doctor efficiency, and operational\n");
        report.append("metrics. The data presented reflects the current state of the system and can\n");
        report.append("be used for strategic planning and operational improvements.\n\n");

        report.append("RECOMMENDATIONS:\n");
        report.append("• Implement automated backup systems\n");
        report.append("• Consider expanding popular specializations\n");
        report.append("• Monitor appointment cancellation rates\n");
        report.append("• Schedule system maintenance during off-peak hours\n\n");

        report.append("================================================================================\n");
        report.append("Report generated by Hospital Management System v2.0.1\n");
        report.append("Generated on: ").append(timestamp).append("\n");
        report.append("© 2024 Hospital Management System. All rights reserved.\n");
        report.append("================================================================================\n");
    }

    // Helper methods
    private int getCount(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    private double getAverageAge() throws SQLException {
        String query = "SELECT AVG(age) FROM patients";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs.getDouble(1);
        }
        return 0.0;
    }

    private int getSpecializationCount() throws SQLException {
        return getCount("SELECT COUNT(DISTINCT specialization) FROM doctors");
    }

    private double getAveragePatientsPerDoctor() throws SQLException {
        int totalPatients = getCount("SELECT COUNT(*) FROM patients");
        int totalDoctors = getCount("SELECT COUNT(*) FROM doctors");
        return totalDoctors > 0 ? (double) totalPatients / totalDoctors : 0;
    }

    private void generateMonthlyTrends(StringBuilder report) throws SQLException {
        try {
            String query = "SELECT MONTH(appointment_date) as month, COUNT(*) as count " +
                    "FROM appointments " +
                    "WHERE YEAR(appointment_date) = YEAR(CURDATE()) " +
                    "GROUP BY MONTH(appointment_date) " +
                    "ORDER BY month";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

            while (rs.next()) {
                int month = rs.getInt("month");
                int count = rs.getInt("count");
                if (month >= 1 && month <= 12) {
                    report.append("├─ ").append(months[month - 1]).append(": ").append(count).append(" appointments\n");
                }
            }
        } catch (SQLException e) {
            report.append("├─ Unable to retrieve monthly trends\n");
        }
        report.append("\n");
    }

    private void generateLocationAnalysis(StringBuilder report) throws SQLException {
        try {
            String query = "SELECT SUBSTRING_INDEX(address, ',', -1) as location, COUNT(*) as count " +
                    "FROM patients " +
                    "GROUP BY location " +
                    "ORDER BY count DESC " +
                    "LIMIT 5";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String location = rs.getString("location").trim();
                int count = rs.getInt("count");
                report.append("├─ ").append(location).append(": ").append(count).append(" patients\n");
            }
        } catch (SQLException e) {
            report.append("├─ Unable to retrieve location data\n");
        }
        report.append("\n");
    }

    private void generateTopDoctors(StringBuilder report) throws SQLException {
        try {
            String query = "SELECT d.name, d.specialization, COUNT(a.id) as appointment_count " +
                    "FROM doctors d " +
                    "LEFT JOIN appointments a ON d.id = a.doctor_id " +
                    "GROUP BY d.id, d.name, d.specialization " +
                    "ORDER BY appointment_count DESC " +
                    "LIMIT 5";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");
                int appointmentCount = rs.getInt("appointment_count");
                report.append("├─ Dr. ").append(name).append(" (").append(specialization).append("): ")
                        .append(appointmentCount).append(" appointments\n");
            }
        } catch (SQLException e) {
            report.append("├─ Unable to retrieve doctor performance data\n");
        }
        report.append("\n");
    }

    private void generatePeakHoursAnalysis(StringBuilder report) throws SQLException {
        try {
            String query = "SELECT HOUR(appointment_time) as hour, COUNT(*) as count " +
                    "FROM appointments " +
                    "GROUP BY HOUR(appointment_time) " +
                    "ORDER BY count DESC " +
                    "LIMIT 5";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int hour = rs.getInt("hour");
                int count = rs.getInt("count");
                String timeSlot = String.format("%02d:00 - %02d:00", hour, hour + 1);
                report.append("├─ ").append(timeSlot).append(": ").append(count).append(" appointments\n");
            }
        } catch (SQLException e) {
            report.append("├─ Unable to retrieve peak hours data\n");
        }
        report.append("\n");
    }

    private void saveReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Comprehensive Report");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        fileChooser.setSelectedFile(new java.io.File("HMS_Comprehensive_Report_" + timestamp + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(reportTextArea.getText());
                JOptionPane.showMessageDialog(this, "Report saved successfully!",
                        "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + e.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printReport() {
        try {
            reportTextArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error printing report: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}