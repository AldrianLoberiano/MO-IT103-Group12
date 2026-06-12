package com.motorphpayroll.motorphpayroll;

class Deduction {
    private int deductionId;
    private double sssContribution;
    private double philHealthContribution;
    private double pagIbigContribution;
    private double withholdingTax;

    public Deduction(int deductionId, double monthlyGross) {
        this.deductionId = deductionId;
        this.sssContribution = calculateSSS(monthlyGross);
        this.philHealthContribution = calculatePhilHealth(monthlyGross);
        this.pagIbigContribution = calculatePagIbig(monthlyGross);

        double taxableIncome = Math.max(0,
                monthlyGross - (sssContribution + philHealthContribution + pagIbigContribution));
        this.withholdingTax = calculateTax(taxableIncome);
    }

    public double calculateSSS(double monthlyGross) {
        if (monthlyGross <= 0)
            return 0.0;
        if (monthlyGross < 3250)
            return 135.00;

        double baseLowerBound = 3250.0;
        double baseContribution = 157.50;
        double bracketStep = 500.0;
        double incrementPerStep = 22.50;
        double maximumContribution = 1125.00;

        double excessOverBase = monthlyGross - baseLowerBound;
        int bracketCount = (int) Math.floor(excessOverBase / bracketStep);
        double totalContribution = baseContribution + (bracketCount * incrementPerStep);

        return Math.min(totalContribution, maximumContribution);
    }

    public double calculatePhilHealth(double monthlyGross) {
        if (monthlyGross <= 0)
            return 0.0;
        double premium = monthlyGross * 0.03;
        if (premium < 300.0)
            premium = 300.0;
        if (premium > 1800.0)
            premium = 1800.0;
        return premium / 2.0;
    }

    public double calculatePagIbig(double monthlyGross) {
        if (monthlyGross < 1000)
            return 0.0;
        double contributionRate = (monthlyGross <= 1500) ? 0.01 : 0.02;
        double calculatedShare = monthlyGross * contributionRate;
        return (calculatedShare > 100.0) ? 100.0 : calculatedShare;
    }

    public double calculateTax(double taxableIncome) {
        if (taxableIncome <= 20832)
            return 0.0;
        else if (taxableIncome < 33333)
            return (taxableIncome - 20833) * 0.20;
        else if (taxableIncome < 66667)
            return 2500 + (taxableIncome - 33333) * 0.25;
        else if (taxableIncome < 166667)
            return 10833 + (taxableIncome - 66667) * 0.30;
        else if (taxableIncome < 666667)
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        else
            return 200833.33 + (taxableIncome - 666667) * 0.35;
    }

    public double computeTotalDeductions() {
        return sssContribution + philHealthContribution + pagIbigContribution + withholdingTax;
    }

    // Getters for report display
    public double getSss() {
        return sssContribution;
    }

    public double getPhilHealth() {
        return philHealthContribution;
    }

    public double getPagIbig() {
        return pagIbigContribution;
    }

    public double getTax() {
        return withholdingTax;
    }
}
