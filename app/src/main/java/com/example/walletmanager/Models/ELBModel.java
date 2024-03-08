package com.example.walletmanager.Models;

public class ELBModel {
    private String id;
    private String date;
    private String time;
    private String partyName;
    private double amount;
    private String narration;

    // Required empty constructor for Firestore
    public ELBModel() {
    }

    public ELBModel(String id, String date, String time, String partyName, double amount, String narration) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.partyName = partyName;
        this.amount = amount;
        this.narration = narration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}