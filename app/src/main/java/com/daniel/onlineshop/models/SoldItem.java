package com.daniel.onlineshop.models;

public class SoldItem {

    private String email;

    private int quantity;

    private String itemName;

    private String priceTotal;

    public SoldItem() {
    }

    public SoldItem(String email, int quantity, String itemName, String priceTotal) {
        this.email = email;
        this.quantity = quantity;
        this.itemName = itemName;
        this.priceTotal = priceTotal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }
}
