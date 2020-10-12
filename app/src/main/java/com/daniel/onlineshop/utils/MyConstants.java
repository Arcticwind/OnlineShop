package com.daniel.onlineshop.utils;

public abstract class MyConstants {

    private MyConstants() {
    }

    /*ADMIN EMAIL*/
    public static final String ADMIN_EMAIL = "like1venom@gmail.com";
    public static final String ADMIN_EMAIL_TWO = "like2venom@gmail.com";

    /*COLLECTION NAMES*/
    public static final String COLLECTION_ITEM_NAME = "ItemCollection";
    public static final String COLLECTION_HISTORY_NAME = "HistoryCollection";
    public static final String COLLECTION_STATISTIC_NAME = "StatisticCollection";
    public static final String COLLECTION_SHOPPING_CART = "ShoppingCartCollection";
    public static final String SUB_COLLECTION_USER_CART = "UserShoppingCart";
    public static final String COLLECTION_RECEIPT = "ReceiptCollection";
    public static final String SUB_COLLECTION_RECEIPT = "UserReceipt";
    public static final String COLLECTION_SOLD_ITEMS = "SoldItemsCollection";
    public static final String SUB_COLLECTION_SOLD_ITEMS = "SoldItemsSubCollection";

    public static final String STORAGE_UPLOAD_NAME = "uploads";

    /*COLLECTION CONSTANTS*/
    public static final String ITEM_NAME = "title";
    public static final String ITEM_NAME_ORDER_BY = "title";
    public static final String HISTORY_ORDER_BY = "date";
    public static final String STATISTIC_ORDER_BY = "year";
    public static final String SHOPPING_CART_ORDER_BY = "itemsWanted";
    public static final String RECEIPT_ORDER_BY = "datetime";

    /*REQUEST CODES*/
    public static final int REQUEST_ADD_ITEM = 1;
    public static final int REQUEST_EDIT_ITEM = 2;
    public static final int REQUEST_SELECT_IMAGE = 3;

    /*INTENT EXTRAS*/
    public static final String EXTRA_ID = "com.daniel.onlineshop.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.daniel.onlineshop.EXTRA_NAME";
    public static final String EXTRA_DESCRIPTION = "com.daniel.onlineshop.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRICE = "com.daniel.onlineshop.EXTRA_PRICE";
    public static final String EXTRA_IMAGE_URL = "com.daniel.onlineshop.EXTRA_IMAGE_URL";
    public static final String EXTRA_QUANTITY = "com.daniel.onlineshop.EXTRA_QUANTITY";
    public static final String EXTRA_STORE_ITEM_OBJECT = "com.daniel.onlineshop.EXTRA_STORE_ITEM_OBJECT";

    /*STATISTIC COLLECTION KEYS*/
    public static final String STATISTIC_KEY_YEAR = "year";
    public static final String STATISTIC_KEY_MONTH = "month";
    public static final String STATISTIC_KEY_DAY = "day";

    /*CRUD OPERATION NAMES*/
    public static final String OPERATION_ADD = "ADD";
    public static final String OPERATION_UPDATE = "UPDATE";
    public static final String OPERATION_DELETE = "DELETE";

    /*SHARED PREFERENCES KEYS*/
    public static final String CURRENCY_NAME = "currency";
    public static final String CURRENCY_VALUE = "value";

    /*WEBSERVICE CURRENCY NAMES*/
    public static final String BASE_CURRENCY_NAME = "EUR";
    public static final Double BASE_CURRENCY_VALUE = 1.0D;
    public static final String HRK = "HRK";
    public static final String EUR = "EUR";
    public static final String USD = "USD";
    public static final String CHF = "CHF";

    /*PAYPAL INFORMATION*/
    public static final String PAYPAL_MERCHANT_NAME = "Daniel";
    public static final String PAYPAL_CLIENT_ID = "AchWVfPIO6trXAsIM6UWIz5u_XsePIc7duthFv_mJVR_Wu5sKoUd4js4l3iq7q_FLZL168o-jRrhF97p";
    public static final int PAYPAL_REQUEST_CODE = 100;
}
