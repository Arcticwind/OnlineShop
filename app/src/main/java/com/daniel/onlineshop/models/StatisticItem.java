package com.daniel.onlineshop.models;

public class StatisticItem {

    private String year;

    private String month;

    private String day;

    private int addCount;

    private int updateCount;

    private int deleteCount;

    public StatisticItem() {
    }

    public StatisticItem(String year, String month, String day, int addCount, int updateCount, int deleteCount) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.addCount = addCount;
        this.updateCount = updateCount;
        this.deleteCount = deleteCount;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getAddCount() {
        return addCount;
    }

    public void setAddCount(int addCount) {
        this.addCount = addCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }
}
