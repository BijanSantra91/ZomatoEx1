package com.example.bijan.zomatoex1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bijan on 1/27/2017.
 */

public class MyDatabase {

    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    public MyDatabase(Context context){
        myHelper = new MyHelper(context, "resturent.db", null, 1);
    }

    public void open(){
        sqLiteDatabase = myHelper.getWritableDatabase();
    }

    public void insertResturent(String name, String imageUrl, String locality, String address, String latitude, String longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("imageUrl", imageUrl);
        contentValues.put("locality", locality);
        contentValues.put("address", address);
        contentValues.put("longitude", longitude);
        contentValues.put("latitude", latitude);

        sqLiteDatabase.insert("resturent", null, contentValues);
    }

    public Cursor quaryResturent(){
        Cursor cursor = null;
        cursor = sqLiteDatabase.query("resturent", null, null, null, null, null, null);
        return cursor;
    }
//    public Cursor queryLatitude()
//    {
//        Cursor cursor1 = null;
//        cursor1 = sqLiteDatabase.query("resturent",null," ",null,null,null,null);
//        return cursor1;
//    }

    public void close(){
        sqLiteDatabase.close();
    }

    private class MyHelper extends SQLiteOpenHelper{

        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table resturent(_integer primary key, name text, imageUrl text, locality text, address text, latitude text, longitude text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
