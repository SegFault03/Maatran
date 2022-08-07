package com.example.maatran;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

    public DBhelper( Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB ) {
        DB.execSQL("create Table Userdetails (email TEXT primary key, name TEXT,  age NUMERIC, gender TEXT, mobile TEXT, address TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Userdetails");
    }

    public boolean insertUserData(String email, String name, String gender, String address, String mobile, int age)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentvalues =  new ContentValues();
        contentvalues.put("name", name);
        contentvalues.put("email", email);
        contentvalues.put("gender", gender);
        contentvalues.put("age", age);
        contentvalues.put("address", address);
        contentvalues.put("mobile", mobile);
        long result=DB.insert("Userdetails", null, contentvalues);
        if(result==-1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean updateUserData(String email, String name, String gender, String address, String mobile, int age)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentvalues =  new ContentValues();
        contentvalues.put("name", name);
        contentvalues.put("gender", gender);
        contentvalues.put("age", age);
        contentvalues.put("address", address);
        contentvalues.put("mobile", mobile);

        Cursor cursor = DB.rawQuery("Select * from Userdetails where email=?", new String[] {email});
        if(cursor.getCount()>0) {
            long result = DB.update("Userdetails", contentvalues, "email=?", new String[]{email});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public boolean deleteUserData(String email, String name, String gender, String address, String mobile, int age)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Userdetails where email=?", new String[] {email});
        if(cursor.getCount()>0) {
            long result = DB.delete("Userdetails", "email=?", new String[]{email});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}
