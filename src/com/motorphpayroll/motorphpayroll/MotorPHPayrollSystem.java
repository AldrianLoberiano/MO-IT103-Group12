package com.motorphpayroll.motorphpayroll;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class MotorPHPayrollSystem extends JFrame {

    // GUI Form Components
    private JTextField txtEmployeeId, txtEmployeeName, txtMonth, txtYear;
    private JEditorPane txtOutputArea;
    private JButton btnViewProfile, btnProcessPayroll, btnClear;

    // Backend Databases
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private ArrayList<String[]> rawAttendanceLines = new ArrayList<>();

    private final String employeeDetailsPath = "resources/MotorPH - Employee Details.csv";
    private final String attendanceRecordsPath = "resources/MotorPH - Attendance Record.csv";

    public MotorPHPayrollSystem() {
        // Step 1: Initialize System and Load CSV Records
        loadDataFromCSV();

        // Try to set a modern look and feel (Nimbus) if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        // Step 2: Set up Main App Windows Configuration
        setTitle("MotorPH Payroll System");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Root panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Left side: form + actions stacked
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));

        // Form fields layout using GridBag for nicer alignment
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Payroll Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Employee Number (ID):"), gbc);
        gbc.gridx = 1;
        txtEmployeeId = new JTextField(12);
        formPanel.add(txtEmployeeId, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Employee Name (optional):"), gbc);
        gbc.gridx = 1;
        txtEmployeeName = new JTextField(12);
        formPanel.add(txtEmployeeName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Month (1-12):"), gbc);
        gbc.gridx = 1;
        txtMonth = new JTextField(6);
        formPanel.add(txtMonth, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Year (e.g., 2026):"), gbc);
        gbc.gridx = 1;
        txtYear = new JTextField(6);
        formPanel.add(txtYear, gbc);

        // Action buttons (stacked nicely)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnViewProfile = new JButton("View Profile");
        btnProcessPayroll = new JButton("Process Payroll");
        btnClear = new JButton("Clear");
        actionPanel.add(btnViewProfile);
        actionPanel.add(btnProcessPayroll);
        actionPanel.add(btnClear);

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(actionPanel, BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(360, 400));

        // Right side: HTML-enabled output pane for rich formatting
        txtOutputArea = new JEditorPane();
        txtOutputArea.setEditable(false);
        txtOutputArea.setContentType("text/html");
        txtOutputArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        txtOutputArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(txtOutputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("System Output"));

        // Layout composition
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        /*
         * =====================================================================
         * ======================== EVENT LOGIC HANDLING =======================
         * =====================================================================
         */

        // Event Handling 1: Profile Details Viewer Action Binding
        btnViewProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int targetId = validateAndGetEmployeeId();
                    Employee emp = findEmployee(targetId);

                    if (emp == null) {
                        throw new IllegalArgumentException(
                                "Validation Error: Employee record identification sequence structural ID does not exist.");
                    }

                    txtOutputArea.setText(emp.viewEmployeeInfo());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MotorPHPayrollSystem.this, ex.getMessage(),
                            "Input Runtime Exception Alert", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Event Handling 2: Payroll Computation Action binding
        btnProcessPayroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int targetId = validateAndGetEmployeeId();
                    int targetMonth = validateAndGetMonth();
                    int targetYear = validateAndGetYear();

                    Employee emp = findEmployee(targetId);
                    if (emp == null) {
                        throw new IllegalArgumentException(
                                "System Tracking Defect: Parsed reference tracking profile context ID does not exist.");
                    }

                    // Process structural calculations based on file data inputs
                    double cutoffOneHours = 0.0;
                    double cutoffTwoHours = 0.0;
                    boolean alignmentRecordsFound = false;

                    for (String[] line : rawAttendanceLines) {
                        if (tryParseInt(line[0]) != targetId)
                            continue;

                        String[] dateParts = line[3].split("/");
                        if (dateParts.length == 3) {
                            int recordMonth = tryParseInt(dateParts[0]);
                            int recordDay = tryParseInt(dateParts[1]);
                            int recordYear = tryParseInt(dateParts[2]);

                            if (recordMonth == targetMonth && recordYear == targetYear) {
                                alignmentRecordsFound = true;
                                AttendanceRecord record = new AttendanceRecord(1, line[3], line[4], line[5]);
                                double calculatedHours = record.getRegularHoursWorked();

                                if (recordDay <= 15) {
                                    cutoffOneHours += calculatedHours;
                                } else {
                                    cutoffTwoHours += calculatedHours;
                                }
                            }
                        }
                    }

                    if (!alignmentRecordsFound) {
                        throw new IllegalArgumentException(
                                "Data Retrieval Gap: No work attendance files tracked matching configuration month / year constraints targets.");
                    }

                    double rate = tryParseDouble(emp.getHourlyRate());
                    double grossOne = cutoffOneHours * rate;
                    double grossTwo = cutoffTwoHours * rate;
                    double combinedMonthlyGross = grossOne + grossTwo;

                    // Execute domain calculation models instantiations
                    Deduction deductionModel = new Deduction(1, combinedMonthlyGross);
                    Payroll payrollModel = new Payroll(1, combinedMonthlyGross, deductionModel);

                    String[] months = { "January", "February", "March", "April", "May", "June", "July", "August",
                            "September", "October", "November", "December" };
                    String monthName = months[targetMonth - 1];

                    txtOutputArea.setText(payrollModel.generatePayslip(monthName, targetYear, grossOne, grossTwo,
                            cutoffOneHours, cutoffTwoHours));

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MotorPHPayrollSystem.this, ex.getMessage(),
                            "Calculation Execution Error Interrupt", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Event Handling 3: Clear Window Inputs Action fields
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEmployeeId.setText("");
                txtEmployeeName.setText("");
                txtMonth.setText("");
                txtYear.setText("");
                txtOutputArea.setText("");
            }
        });
    }

    /*
     * =====================================================================
     * ====================== ROBUST INPUT VALIDATION ======================
     * =====================================================================
     */

    private int validateAndGetEmployeeId() {
        String rawInput = txtEmployeeId.getText().trim();
        if (rawInput.isEmpty()) {
            throw new NumberFormatException(
                    "Required Input Missing: Please fill up structural data configuration requirements for Employee ID.");
        }
        try {
            return Integer.parseInt(rawInput);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "Invalid Typing Match: Character structure sequence parsed as Employee ID must be an integer numeral.");
        }
    }

    private int validateAndGetMonth() {
        String rawInput = txtMonth.getText().trim();
        if (rawInput.isEmpty()) {
            throw new NumberFormatException(
                    "Required Input Missing: Monthly structural runtime configuration constraint requires assignment tracking inputs.");
        }
        try {
            int month = Integer.parseInt(rawInput);
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException();
            }
            return month;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Calendar Value Range Error: Target runtime month entry values must map accurately within 1 to 12 limits.");
        }
    }

    private int validateAndGetYear() {
        String rawInput = txtYear.getText().trim();
        if (rawInput.isEmpty()) {
            throw new NumberFormatException(
                    "Required Input Missing: Data validation requires configuration parsing processing execution year targets inputs.");
        }
        try {
            return Integer.parseInt(rawInput);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "Invalid Typing Match: Year sequence target assignments require clean integer inputs format specifications.");
        }
    }

    private Employee findEmployee(int id) {
        for (Employee emp : employeeList) {
            if (emp.getEmployeeId() == id)
                return emp;
        }
        return null;
    }

    /*
     * =====================================================================
     * =================== FILE IO AND UTILITIES DATA LOADS ================
     * =====================================================================
     */

    private void loadDataFromCSV() {
        // Read employee list profiles data records
        try (BufferedReader reader = new BufferedReader(new FileReader(employeeDetailsPath))) {
            reader.readLine(); // Pop header
            String row;
            while ((row = reader.readLine()) != null) {
                String[] fields = parseCSVLine(row);
                if (fields.length >= 19) {
                    int id = tryParseInt(fields[0]);
                    String lastName = fields[1].replace("\"", "").trim();
                    String firstName = fields[2].replace("\"", "").trim();
                    String position = fields[11].replace("\"", "").trim();
                    String dept = fields[12].replace("\"", "").trim();
                    double basic = tryParseDouble(fields[13]);
                    String hourlyRate = fields[18].replace("\"", "").trim();

                    employeeList.add(new Employee(id, firstName, lastName, position, dept, hourlyRate, basic));
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "System Alert: Issue occurred while reading tracking target source configuration employee directory database profiles: "
                            + e.getMessage());
        }

        // Read processing runtime clock activity logs lines matching items
        try (BufferedReader reader = new BufferedReader(new FileReader(attendanceRecordsPath))) {
            reader.readLine(); // Pop header
            String row;
            while ((row = reader.readLine()) != null) {
                rawAttendanceLines.add(parseCSVLine(row));
            }
        } catch (Exception e) {
            System.out.println(
                    "System Alert: Issue tracking file operations source arrays reads for active log records databases instances: "
                            + e.getMessage());
        }
    }

    public double tryParseDouble(String rawValue) {
        if (rawValue == null)
            return 0.0;
        try {
            String cleaned = rawValue.replace("\"", "").replace(",", "").trim();
            if (cleaned.isEmpty())
                return 0.0;
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int tryParseInt(String rawValue) {
        if (rawValue == null)
            return 0;
        try {
            return Integer.parseInt(rawValue.replace("\"", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String[] parseCSVLine(String row) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < row.length() && row.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++; // skip escaped quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    public static void main(String[] args) {
        // Secure execution safety initialization paths inside native Swing threads
        // structures context loop assignments
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MotorPHPayrollSystem().setVisible(true);
            }
        });
    }
}
