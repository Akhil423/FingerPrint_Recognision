package com.example.greenhat.b_passer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.apache.http.*;

/**
 * Created by greenhat on 9/4/17.
 */

public class ImageProcess extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="register.db";
    private static final int SCHEMA_VERSION=1;

    public ImageProcess(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS arch(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),imageblob BLOB);");
        db.execSQL("CREATE TABLE IF NOT EXISTS tarch(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),imageblob BLOB);");
        db.execSQL("CREATE TABLE IF NOT EXISTS whorl(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),imageblob BLOB);");
        db.execSQL("CREATE TABLE IF NOT EXISTS lloop(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),imageblob BLOB);");
        db.execSQL("CREATE TABLE IF NOT EXISTS rloop(userid INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),imageblob BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getAll(String re) {
        return(getReadableDatabase().rawQuery("SELECT imageblob FROM "+re+"",null));
    }
    public void insert(byte[] bytes,String nme,String tbname)
    {
        ContentValues cv=new ContentValues();

        cv.put("imageblob",bytes);
        cv.put("name",nme);
        Log.e("inserted", "inserted");
        getWritableDatabase().insert(tbname,null,cv); //hve to send name of table wivh print matches

    }
    public byte[] getImage(Cursor c)
    {
        return(c.getBlob(1));
    }
}
