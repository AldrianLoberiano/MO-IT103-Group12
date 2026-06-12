package com.motorphpayroll.motorphpayroll;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class AttendanceRecord {
    private int recordId;
    private String date; // MM/DD/YYYY format from CSV
    private String timeIn;
    private String timeOut;
    private double regularHoursWorked;

    public AttendanceRecord(int recordId, String date, String timeIn, String timeOut) {
        this.recordId = recordId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.regularHoursWorked = calculateHoursWorked();
    }

    public String getDate() {
        return date;
    }

    public double calculateHoursWorked() {
        try {
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");
            LocalTime loginTime = LocalTime.parse(timeIn, timeFormat);
            LocalTime logoutTime = LocalTime.parse(timeOut, timeFormat);

            LocalTime standardStart = LocalTime.of(8, 0);
            LocalTime standardEnd = LocalTime.of(17, 0);
            LocalTime effectiveLogout = logoutTime.isAfter(standardEnd) ? standardEnd : logoutTime;

            LocalTime gracePeriodLimit = LocalTime.of(8, 10);
            LocalTime effectiveLogin = loginTime.isBefore(standardStart) ? standardStart : loginTime;
            if (!loginTime.isAfter(gracePeriodLimit)) {
                effectiveLogin = standardStart;
            }

            // Unpaid lunch overlap calculations
            LocalTime startLunch = LocalTime.of(12, 0);
            LocalTime endLunch = LocalTime.of(13, 0);

            LocalTime maxStart = effectiveLogin.isAfter(startLunch) ? effectiveLogin : startLunch;
            LocalTime minEnd = effectiveLogout.isBefore(endLunch) ? effectiveLogout : endLunch;
            double lunchMinutes = 0.0;
            if (minEnd.isAfter(maxStart)) {
                lunchMinutes = Duration.between(maxStart, minEnd).toMinutes();
            }

            return (Duration.between(effectiveLogin, effectiveLogout).toMinutes() - lunchMinutes) / 60.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getRegularHoursWorked() {
        return regularHoursWorked;
    }
}
