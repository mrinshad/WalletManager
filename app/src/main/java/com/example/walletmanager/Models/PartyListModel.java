package com.example.walletmanager.Models;

public class PartyListModel {
    private String id;
    private String name;
    private double balance;

    public PartyListModel(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}
