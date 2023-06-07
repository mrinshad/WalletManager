package com.example.walletmanager;

public class MyData {
    private String id;
    private String date;
    private String time;
    private String amount;
    private String party_name;
    private String narratiob;
    private Double total;

    public String getNarratiob() {
        return narratiob;
    }

    public void setNarratiob(String narratiob) {
        this.narratiob = narratiob;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public MyData(String id, String date, String time, String amount, String party_name, String narratiob, double total) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.party_name = party_name;
        this.narratiob = narratiob;
        this.total = total;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }
}

