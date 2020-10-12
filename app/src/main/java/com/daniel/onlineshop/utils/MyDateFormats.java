package com.daniel.onlineshop.utils;

import android.app.ProgressDialog;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDateFormats {

    public String getDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return df.format(new Date(System.currentTimeMillis()));
    }

    public String getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(new Date(System.currentTimeMillis()));
    }

    public String getYear() {
        DateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
        return df.format(new Date(System.currentTimeMillis()));
    }

    public String getMonth() {
        DateFormat df = new SimpleDateFormat("MM", Locale.getDefault());
        return df.format(new Date(System.currentTimeMillis()));
    }

    public String getDay() {
        DateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
        return df.format(new Date(System.currentTimeMillis()));
    }


}
