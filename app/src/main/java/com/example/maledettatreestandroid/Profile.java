package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Profile {
    /*sid,
name,
picture*/
    String sid="", uid="", nome="", foto="", pVersion="";
    Context context;
    MyDatabaseHelper myDB;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    public Profile(Context context) {
        this.context=context;
        getSid();
    }

    public void getSid(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        sid=sharedPreferences.getString(SID, "");
    }

    public String encodeImage(Bitmap bitmap){
        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);*/
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    public Bitmap decodeImage(String imageString){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome){
        this.nome=nome;
    }

    public Bitmap getFoto() {
        return decodeImage(foto);
    }

    public void setFoto(Bitmap foto) {
        this.foto = encodeImage(foto);
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getpVersion() {
        return pVersion;
    }

    public void setpVersion(String pVersion) {
        this.pVersion = pVersion;
    }
}