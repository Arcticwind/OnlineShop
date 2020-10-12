package com.daniel.onlineshop.models;

import java.io.Serializable;

public class ShoppingCartItem implements Serializable {

    private StoreItem storeItem;

    private int itemsWanted;

    public ShoppingCartItem() {
    }

    public StoreItem getStoreItem() {
        return storeItem;
    }

    public void setStoreItem(StoreItem storeItem) {
        this.storeItem = storeItem;
    }

    public int getItemsWanted() {
        return itemsWanted;
    }

    public void setItemsWanted(int itemsWanted) {
        this.itemsWanted = itemsWanted;
    }
}
