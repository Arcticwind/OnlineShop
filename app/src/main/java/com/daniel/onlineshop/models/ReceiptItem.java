package com.daniel.onlineshop.models;

public class ReceiptItem {

    private String datetime;

    private String paymentId;

    private String totalPrice;

    public ReceiptItem() {
    }

    public ReceiptItem(String datetime, String paymentId, String totalPrice) {
        this.datetime = datetime;
        this.paymentId = paymentId;
        this.totalPrice = totalPrice;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
