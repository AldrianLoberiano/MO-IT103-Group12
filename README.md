# MotorPH Payroll System

A desktop-based payroll management application for MotorPH employees. The system automates payroll processing, calculates statutory deductions, and generates payslips with attendance tracking capabilities.

## Description

The MotorPH Payroll System is designed to streamline payroll operations by:

- Managing employee information and records
- Processing payroll for two cutoff periods per month (1-15 and 16-End)
- Automatically calculating statutory deductions (SSS, PhilHealth, Pag-IBIG, Income Tax)
- Generating detailed payslips with gross and net salary calculations
- Tracking employee attendance records
- Providing employee profile lookup and verification

## Features

### Core Functionality

- **Employee Profile Management**: View detailed employee information including ID, name, position, department, and salary details
- **Payroll Processing**: Calculate monthly payroll for specified employees and date ranges
- **Deduction Calculations**: Automatically compute statutory deductions:
  - **SSS (Social Security System)**: Progressive contribution based on salary brackets
  - **PhilHealth**: 3% of gross salary (min PHP 300, max PHP 1,800, employee share)
  - **Pag-IBIG**: 1-2% of gross salary (max PHP 100)
  - **Income Tax**: Calculated on taxable income after mandatory deductions
- **Payslip Generation**: Detailed payslips showing:
  - First cutoff (Days 1-15) earnings and hours worked
  - Second cutoff (Days 16-End) earnings and hours worked
  - Itemized deductions
  - Net salary calculation

### User Interface

- Clean, intuitive Swing-based GUI with organized layout
- Input fields for employee identification and pay period selection
- Real-time system logs and results display
- Action buttons for profile viewing, payroll processing, and data clearing

## Tech Stack

| Component                   | Technology            |
| --------------------------- | --------------------- |
| **Language**                | Java 25               |
| **UI Framework**            | Swing (javax.swing)   |
| **Data Storage**            | CSV files             |
| **Build System**            | Apache Ant / NetBeans |
| **Development Environment** | NetBeans IDE          |

## Project Structure

```
MO-IT103-Group12/
├── src/
│   ├── MotorPHPayrollSystem.java      # Main application GUI
│   ├── Employee.java                  # Employee data model
│   ├── Payroll.java                   # Payroll processing & payslip generation
│   ├── Deduction.java                 # Statutory deduction calculations
│   └── resources/
│       ├── MotorPH - Employee Details.csv
│       └── MotorPH - Attendance Record.csv
├── resources/
│   ├── MotorPH - Employee Details.csv
│   └── MotorPH - Attendance Record.csv
├── build.xml                          # Ant build configuration
├── nbproject/                         # NetBeans project files
└── README.md                          # This file
```

## Requirements

- **Java Development Kit (JDK)**: Version 25 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512MB RAM
- **Disk Space**: Minimal (< 50MB)

## Installation & Setup

### Prerequisites

Ensure you have Java 25+ installed on your system.

```bash
# Check Java version
java -version
javac -version
```

### Building the Project

#### Option 1: Using Apache Ant (Recommended)

```bash
# Navigate to project directory
cd MO-IT103-Group12

# Build and run the project
ant run
```

#### Option 2: Manual Compilation with javac

```bash
# Navigate to project directory
cd MO-IT103-Group12

# Create build directory
mkdir build

# Compile all Java files
javac -d build src/*.java

# Copy resources
copy src\resources\* build\  # Windows
# OR
cp -r src/resources/* build/  # macOS/Linux

# Run the application
java -cp build com.motorphpayroll.motorphpayroll.MotorPHPayrollSystem
```

### Quick Local Run (Windows)

If you want to quickly compile and run the app on a Windows machine using PowerShell, follow these steps from the project root:

```powershell
# open PowerShell and run:
cd C:\path\to\MO-IT103-Group12

# create build directory for classes
if (!(Test-Path build\classes)) { New-Item -ItemType Directory -Path build\classes -Force }

# compile Java sources (compile package sources)
javac -d build\classes src\com\motorphpayroll\motorphpayroll\*.java

# copy resource CSV files into build folder so the app can read them
Copy-Item -Path resources\* -Destination build -Recurse -Force

# run the application
java -cp build\classes com.motorphpayroll.motorphpayroll.MotorPHPayrollSystem
```

Notes:

- Make sure `java` and `javac` are on your PATH (`java -version`, `javac -version`).
- The app expects the CSV files to be available relative to the working directory (we copy them to `build` above).
- If you use an IDE (NetBeans, VS Code), import the project as a Java project and run `MotorPHPayrollSystem` from the IDE.

## How to Use

### 1. Launch the Application

Run the compiled application (see Installation section above)

### 2. View Employee Profile

- Enter the **Employee ID** (numeric)
- Optionally enter **Employee Name** for verification
- Click **"View Profile Information"** button
- System will display employee details including salary and position

### 3. Process Payroll

- Enter the **Employee ID**
- Enter the **Month** (1-12, where 1=January, 12=December)
- Enter the **Year** (e.g., 2024)
- Click **"Process Cutoff Payroll"** button
- System will:
  - Fetch attendance records for the specified month
  - Calculate gross salary for both cutoff periods
  - Compute all statutory deductions
  - Display detailed payslip in the results area

### 4. Clear Data

- Click **"Clear Input Logs"** button to reset all input fields and output display

### 5. Example Workflow

```
1. Employee ID: 10001
2. Month: 3 (March)
3. Year: 2024
4. Click "Process Cutoff Payroll"
5. View generated payslip with deductions breakdown
```

## Data Files

The application requires two CSV files in the `resources/` directory:

### Employee Details CSV (`MotorPH - Employee Details.csv`)

Contains employee master data:

- Employee ID
- First Name, Last Name
- Position, Department
- Hourly Rate, Basic Salary

### Attendance Record CSV (`MotorPH - Attendance Record.csv`)

Contains attendance tracking:

- Employee ID
- Date
- Hours Worked
- Attendance Status

## Deduction Formulas

### SSS Contribution

- Below PHP 3,250: PHP 135.00 (flat)
- PHP 3,250 - PHP 24,750: Bracket-based with PHP 22.50 increments per PHP 500 interval
- Above PHP 24,750: PHP 1,125.00 (maximum)

### PhilHealth

- 3% of gross salary
- Minimum: PHP 300
- Maximum: PHP 1,800
- Employee contribution: 50% of calculated premium

### Pag-IBIG

- Below PHP 1,000: No contribution
- PHP 1,000 - PHP 1,500: 1% of gross
- Above PHP 1,500: 2% of gross
- Maximum: PHP 100

### Income Tax (BIR Withholding)

- Calculated on taxable income (after SSS, PhilHealth, Pag-IBIG deductions)
- Progressive tax brackets applied

## Troubleshooting

### Application Won't Start

- Verify Java 25+ is installed: `java -version`
- Check CSV files exist in `resources/` directory
- Ensure proper file permissions on resource files

### "Employee record not found"

- Verify employee ID exists in Employee Details CSV
- Check ID format matches CSV data

### Incorrect Payroll Calculations

- Verify attendance records exist for the selected month/year
- Check CSV data formatting (no extra spaces, proper delimiters)
- Ensure salary data is correctly formatted in Employee Details CSV

### Cannot Find Resources

- Ensure `resources/` folder is in the same directory as compiled classes
- When running manually with `java -cp`, include resource copy step

## Future Enhancements

- Database integration (replace CSV files)
- Report generation and export (PDF/Excel)
- Employee self-service portal
- Batch payroll processing
- Audit logs and history tracking
- Advanced search and filtering
- Multi-language support
- Cloud-based deployment

## Group Information

**Project:** MO-IT103-Group12  
**Application:** MotorPH Payroll System
