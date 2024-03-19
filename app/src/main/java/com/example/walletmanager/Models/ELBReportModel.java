package com.example.walletmanager.Models;

public class ELBReportModel {

    public ELBReportModel(String partyName, String narration, double amount) {

        this.setPatyName(partyName);
        this.setNarration(narration);
        this.setAmount(amount);
    }
    String patyName ="",narration="";
    double amount;

    public String getPatyName() {
        return patyName;
    }

    public void setPatyName(String patyName) {
        this.patyName = patyName;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
