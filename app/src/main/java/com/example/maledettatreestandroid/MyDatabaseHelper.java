package com.example.maledettatreestandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "ProfileImage.db";
    private static final int DATABASE_VERSION=1;

    private static final String TABLE_NAME="Profile_Foto";
    private static final String COLUMN_ID="sid";
    private static final String COLUMN_VERSION="pVersion";
    private static final String COLUMN_IMAGE="Image";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query= "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " TEXT PRIMARY KEY, " +
                        COLUMN_VERSION + " TEXT, " +
                        COLUMN_IMAGE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void addImage(String sid, String pVersion, String image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(COLUMN_ID, sid);
        cv.put(COLUMN_VERSION, pVersion);
        cv.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result==-1){
            Log.d("PICTURE", "Fail");
        } else {
            Log.d("PICTURE", "Added successfully!");
        }
    }

    public Cursor readPictureVersione(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COLUMN_VERSION+" FROM Profile_Foto WHERE "+COLUMN_ID+" = '"+uid+"'";

        Cursor cursor=null;
        if(db!=null){
            Log.d("PICTURE", "Extract pVersion");
            cursor=db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readPicture(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+COLUMN_IMAGE+" FROM Profile_Foto WHERE "+COLUMN_ID+" = '"+uid+"'";

        Cursor cursor=null;
        if(db!=null){
            Log.d("PICTURE", "Extract image");
            cursor=db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateImage(String sid, String pVersion, String image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();

        cv.put(COLUMN_VERSION, pVersion);
        cv.put(COLUMN_IMAGE, image);

        long result=db.update(TABLE_NAME, cv, COLUMN_ID+"=?", new String[]{sid});

        if(result==-1){
            Log.d("PICTURE", "update fail");
        } else {
            Log.d("PICTURE", "update success");
        }


        String query = "UPDATE "+TABLE_NAME +
                "SET "+COLUMN_VERSION + " = '" + pVersion + "', " +
                COLUMN_IMAGE + " = '" + image +
                "WHERE " + COLUMN_ID + "= '" + sid + "'";
        ;

        Cursor cursor = db.rawQuery(query, null);
        /*UPDATE Profile_Foto
SET pVersion='2',Image='aaaa'
WHERE sid='122584'*/
    }
}
