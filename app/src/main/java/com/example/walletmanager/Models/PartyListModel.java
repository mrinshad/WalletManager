package com.example.walletmanager.Models;

public class PartyListModel {
    private long id;
    private String name;
    private double balance;

    public PartyListModel(long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}
