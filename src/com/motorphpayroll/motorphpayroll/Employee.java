package com.motorphpayroll.motorphpayroll;

class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private String hourlyRate; // Kept as String per class diagram
    private double basicSalary;

    public Employee(int employeeId, String firstName, String lastName, String position, String department,
            String hourlyRate, double basicSalary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.hourlyRate = hourlyRate;
        this.basicSalary = basicSalary;
    }

    // Getters
    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public String viewEmployeeInfo() {
        String basicFormatted = String.format("%,.2f", basicSalary);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<div style='font-family: sans-serif;'>");
        sb.append("<h2 style='margin:4px 0;'>").append(lastName).append(", ").append(firstName).append("</h2>");
        sb.append("<p style='margin:6px 0; line-height:1.3;'>");
        sb.append("<b>Employee ID:</b> ").append(employeeId).append("<br/>");
        sb.append("<b>Position:</b> ").append(position).append("<br/>");
        sb.append("<b>Immediate Supervisor:</b> ").append(department).append("<br/>");
        sb.append("<b>Basic Salary:</b> PHP ").append(basicFormatted).append("<br/>");
        sb.append("<b>Hourly Rate:</b> PHP ").append(hourlyRate).append("</p>");
        sb.append("</div></html>");
        return sb.toString();
    }
}
