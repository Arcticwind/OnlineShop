package com.daniel.onlineshop.models;

public class HistoryItem {

    private String title;

    private String email;

    private String operation;

    private String date;

    private double price;

    public HistoryItem() {
    }

    public HistoryItem(String title, String email, String operation, String date, double price) {
        this.title = title;
        this.email = email;
        this.operation = operation;
        this.date = date;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
