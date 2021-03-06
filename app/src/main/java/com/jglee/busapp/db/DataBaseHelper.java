package com.jglee.busapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Favorite.db";
    private static final String TABLE_NAME = "favorite_table";
    private static final String COL_1 = "favorite_id";
    private static final String COL_2 = "favorite_type";
    private static final String COL_3 = "favorite_item_id";
    private static final String COL_4 = "favorite_item_name";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME +
                " (FAVORITE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " FAVORITE_TYPE TEXT," +
                " FAVORITE_ITEM_ID TEXT," +
                " FAVORITE_ITEM_NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String favoriteType, String favoriteItemId, String favoriteItemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, favoriteType);
        contentValues.put(COL_3, favoriteItemId);
        contentValues.put(COL_4, favoriteItemName);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public String findDataId(String favoriteType, String favoriteItemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "select *" +
                " from " + TABLE_NAME +
                " where " + COL_2 + " = \'" + favoriteType + "\' and " + COL_3 + " = \'" + favoriteItemId + "\'", null);

        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            return id;
        }

        return null;
    }

    public boolean updateData(String favoriteId, String favoriteType, String favoriteItemId, String favoriteItemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, favoriteId);
        contentValues.put(COL_2, favoriteType);
        contentValues.put(COL_3, favoriteItemId);
        contentValues.put(COL_4, favoriteItemName);

        db.update(TABLE_NAME, contentValues, "FAVORITE_ID = ?", new String[] {favoriteId});
        return true;
    }

    public int deleteData(String favoriteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "FAVORITE_ID = ?", new String[] {favoriteId});
    }
}
