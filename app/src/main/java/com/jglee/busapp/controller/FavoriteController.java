package com.jglee.busapp.controller;

import android.content.Context;
import android.database.Cursor;
import com.jglee.busapp.db.DataBaseHelper;
import com.jglee.busapp.domain.FavoriteItem;

import java.util.ArrayList;
import java.util.List;

public class FavoriteController {
    static DataBaseHelper db;
    static List<FavoriteItem> favoriteList;

    public static List<FavoriteItem> getAllFavoriteList() {
        return favoriteList;
    }

    public static void insertFavorite(String favoriteType, String favoriteItemId, String favoriteItemName) {
        db.insertData(favoriteType, favoriteItemId, favoriteItemName);
        favoriteList.clear();
        Cursor cursor;
        cursor = db.getAllData();

        while(cursor.moveToNext()) {
            String c1 = cursor.getString(0);
            String c2 = cursor.getString(1);
            String c3 = cursor.getString(2);
            String c4 = cursor.getString(3);

            favoriteList.add(new FavoriteItem(c1, c2, c3, c4));
        }

        cursor.close();
    }

    public static void deleteFavorite(String favoriteId) {
        db.deleteData(favoriteId);

        favoriteList.clear();
        Cursor cursor;
        cursor = db.getAllData();

        while(cursor.moveToNext()) {
            String c1 = cursor.getString(0);
            String c2 = cursor.getString(1);
            String c3 = cursor.getString(2);
            String c4 = cursor.getString(3);

            favoriteList.add(new FavoriteItem(c1, c2, c3, c4));
        }

        cursor.close();
    }

    public static String findFavoriteId(String favoriteType, String favoriteItemId) {
        String favoriteId = db.findDataId(favoriteType, favoriteItemId);
        return favoriteId;
    }

    public static void loadFavoriteList(Context context) {
        db = new DataBaseHelper(context);
        favoriteList = new ArrayList<>();

        Cursor cursor;
        cursor = db.getAllData();

        while(cursor.moveToNext()) {
            String favoriteId = cursor.getString(0);
            String favoriteType = cursor.getString(1);
            String favoriteItemId = cursor.getString(2);
            String favoriteItemName = cursor.getString(3);

            favoriteList.add(new FavoriteItem(favoriteId, favoriteType, favoriteItemId, favoriteItemName));
        }

        cursor.close();
    }



}
