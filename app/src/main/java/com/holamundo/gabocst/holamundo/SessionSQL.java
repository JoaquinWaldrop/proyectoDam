package com.holamundo.gabocst.holamundo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gabocst on 07/08/15.
 */
public class SessionSQL extends SQLiteOpenHelper{
    String sqlTable = "CREATE TABLE sesion( token TEXT, user INTEGER, userType INTEGER )";

    public SessionSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST sesion");
        db.execSQL(sqlTable);
    }
}
