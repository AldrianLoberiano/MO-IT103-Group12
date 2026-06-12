package com.motorphpayroll.motorphpayroll;

import java.time.YearMonth;

class Payroll {
    private int payrollId;
    private String periodStartDate; // Used programmatically as representations
    private String periodEndDate;
    private double grossPay;
    private double netPay;
    private Deduction deduction;

    public Payroll(int payrollId, double grossPay, Deduction deduction) {
        this.payrollId = payrollId;
        this.grossPay = grossPay;
        this.deduction = deduction;
        this.netPay = calculateNetPay();
    }

    public double calculateGrossPay() {
        return grossPay;
    }

    public double calculateNetPay() {
        return grossPay - deduction.computeTotalDeductions();
    }

    public String generatePayslip(String monthName, int year, double grossOne, double grossTwo, double c1Hours,
            double c2Hours) {
        StringBuilder payslip = new StringBuilder();
        payslip.append("=========================================\n");
        payslip.append("            MOTORPH PAYSLIP REPORT        \n");
        payslip.append("=========================================\n");
        payslip.append("Pay Coverage: ").append(monthName).append(" ").append(year).append("\n\n");

        payslip.append("[1st Cutoff: Days 1 to 15]\n");
        payslip.append("  Hours Worked: ").append(String.format("%.2f", c1Hours)).append(" hours\n");
        payslip.append("  Gross Salary: PHP ").append(String.format("%.2f", grossOne)).append("\n");
        payslip.append("  Net Salary  : PHP ").append(String.format("%.2f", grossOne)).append("\n\n");

        payslip.append("[2nd Cutoff: Days 16 to ").append(YearMonth.of(year, 1).lengthOfMonth()).append("]\n");
        payslip.append("  Hours Worked: ").append(String.format("%.2f", c2Hours)).append(" hours\n");
        payslip.append("  Gross Salary: PHP ").append(String.format("%.2f", grossTwo)).append("\n");
        payslip.append("  Deductions Applied (Monthly Combined Total):\n");
        payslip.append("    - SSS        : PHP ").append(String.format("%.2f", deduction.getSss())).append("\n");
        payslip.append("    - PhilHealth : PHP ").append(String.format("%.2f", deduction.getPhilHealth())).append("\n");
        payslip.append("    - Pag-IBIG   : PHP ").append(String.format("%.2f", deduction.getPagIbig())).append("\n");
        payslip.append("    - Income Tax : PHP ").append(String.format("%.2f", deduction.getTax())).append("\n");
        payslip.append("  Total Deductions: PHP ").append(String.format("%.2f", deduction.computeTotalDeductions()))
                .append("\n");
        payslip.append("  Net Salary  : PHP ")
                .append(String.format("%.2f", (grossTwo - deduction.computeTotalDeductions()))).append("\n");
        payslip.append("=========================================");
        return payslip.toString();
    }
}
