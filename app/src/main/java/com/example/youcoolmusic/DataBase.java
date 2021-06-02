package com.example.youcoolmusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    public  static  final  String DATABASE_NAME = "YouCoolMusic_DataBase";
    public  static final int DATABASE_VERSION = 1;

    public  static  final  String TABLE_PLAY_LIST="Play_List";

    public  static  final  String ID="id";
    public  static  final  String TITLE="title";
    public  static  final  String IMG="img";


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PLAY_LIST + "(" + ID + " varchar(50) primary key, " + TITLE + " varchar(200), "
                + IMG + " varchar(200)" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PLAY_LIST);
        db.execSQL("create table " + TABLE_PLAY_LIST + "(" + ID + " varchar(50) primary key, " + TITLE + " varchar(200), "
                + IMG + " varchar(200)" + ")");
    }

}
