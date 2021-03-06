package com.jglee.busapp.domain;

public class FavoriteItem {
    private String favoriteId;
    private String favoriteType;
    private String itemId;
    private String itemName;

    public FavoriteItem(String favoriteId, String favoriteType, String itemId, String itemName) {
        this.favoriteId = favoriteId;
        this.favoriteType = favoriteType;
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public String getFavoriteId() { return favoriteId; }

    public String getFavoriteType() {
        return favoriteType;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() { return itemName; }

}
