package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    int ritardo, stato;
    String commento, nome, pVersion, uid, dataPubblicazione;
    Boolean followingAuthor;
    Context context;
    String sid;
    Bitmap picture=null;
    MyDatabaseHelper myDB;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    public Post(JSONObject post, Context context){
        /*{ "delay":2,
            "status":1,
            "comment":"commentolungolunghissssiimoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo",
            "followingAuthor":false,
            "datetime":"2021-12-31 11:55:13.664840",
            "authorName":"Mila",
            "pversion":"7",
            "author":"117473"}*/
        this.context=context;
        myDB=new MyDatabaseHelper(context);


        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        sid=sharedPreferences.getString(SID, "");

        try {
            this.ritardo=post.getInt("delay");
        } catch (JSONException e) {
            this.ritardo = -1;
        }

        try {
            this.stato=post.getInt("status");
        } catch (JSONException e) {
            this.stato=-1;
        }
        try {
            this.commento=post.getString("comment");
        } catch (JSONException e) {
            this.commento="";
        }
        try {
            this.followingAuthor=post.getBoolean("followingAuthor");
        } catch (JSONException e) {
            this.followingAuthor=false;
        }
        try {
            this.dataPubblicazione=post.getString("datetime");
        } catch (JSONException e) {
            this.dataPubblicazione="";
        }
        try {
            this.nome=post.getString("authorName");
        } catch (JSONException e) {
            this.nome="";
        }
        try {
            this.pVersion=post.getString("pversion");
        } catch (JSONException e) {
            this.pVersion="";
        }
        try {
            this.uid=post.getString("author");
        } catch (JSONException e) {
            this.uid="";
        }
    }

    public String getRitardo() {
        if(ritardo==-1){
            return "Ritardo: nessun valore";
        }
        switch (ritardo){
            case 0:
                return "Ritardo: In orario";
            case 1:
                return "Ritardo: Ritardo di pochi minuti";
            case 2:
                return "Ritardo: Ritardo oltre i 15 minuti";
            case 3:
                return "Ritardo: Treni soppressi";
            default:
                return "Ritardo: Non specificato";
        }

    }

    public String getStato() {
        if(stato==-1){
            return "Stato: nessun valore";
        }
        switch (stato){
            case 0:
                return "Stato: Situazione ideale";
            case 1:
                return "Stato: Accettabile";
            case 2:
                return "Stato: Gravi problemi";
            default:
                return "Stato: Non specificato";
        }
    }

    public String getCommento() {
        if(commento.equals("")){
            return "nessun commento";
        }
        return commento;
    }

    public String getNome() {
        return nome;
    }

    public String getpVersion() {
        return pVersion;
    }

    public String getAutore() {
        return uid;
    }

    public String getDataPubblicazione() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dataPubblicazione);
            String updatedMinute = date.getMinutes() < 10 ? "0" + date.getMinutes() : "" + date.getMinutes();
            return "Pubblicato il: " + (date.getDay() + 2) + "/" +(date.getMonth() +1 ) + "/" +(date.getYear() - 100) + " alle ore: "+date.getHours()+":"+updatedMinute;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Boolean getFollowingAuthor() {
        return followingAuthor;
    }

    public void setFollowingAuthor(Boolean followingAuthor){
        this.followingAuthor=followingAuthor;
    }

    public Bitmap getPicture(){
        Bitmap picture=null;
        Cursor cursor=myDB.readPictureVersione(uid);
        if (cursor!=null){
            if(cursor.getCount()==0){
                Log.d("PICTURE", "No database");
                picture=getServerPicture(true);
            } else {
                while(cursor.moveToNext()){
                    if(Integer.parseInt(cursor.getString(0))==Integer.parseInt(this.getpVersion())){
                        Cursor c = myDB.readPicture(uid);
                        if (c.getCount() == 0) {
                            Log.d("PICTURE", "No image");
                            picture=null;
                        } else{
                            while(c.moveToNext()){
                                Log.d("PICTURE", "C'è l'immagine");
                                picture=decodeImage(c.getString(0));
                            }
                        }
                    } else {
                        Log.d("PICTURE", "Immagine da aggiornare");
                        picture=getServerPicture(false);
                        //TODO: l'immagine c'è ma va aggiornata picture=getServerPicture();
                    }
                }
            }
        }
        //if pversion in DB == getpVersion take from DB
        //else getServerPicture
        return picture;
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

    public Bitmap getServerPicture(Boolean newImage){
        Log.d("PICTURE", "getServerPicture sid: "+sid+" uid: "+uid);
        if(getAutore()!=""){
            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://ewserver.di.unimi.it/mobicomp/treest/getUserPicture.php";
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("sid", sid);
                jsonObject.put("uid", uid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try {
                    /*uid, pversion, picture*/
                    picture=decodeImage(response.getString("picture"));
                    if(newImage){
                        myDB.addImage(response.getString("uid"), response.getString("pversion"), response.getString("picture"));
                    } else {
                        myDB.updateImage(response.getString("uid"), response.getString("pversion"), response.getString("picture"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                picture=null;
                Log.d("POSTS", "ERROR IMMAGE: " + error.toString());
            });
            queue.add(jsonObjectRequest);
        }

        return picture;}

    /*ImageView image =(ImageView)findViewById(R.id.image);

        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //decode base64 string to image
        imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image.setImageBitmap(decodedImage)*/
}





/*{
    "delay":2,
    "status":1,
    "comment":"commentolungolunghissssiimoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo",
    "followingAuthor":false,
    "datetime":"2021-12-31 11:55:13.664840",
    "authorName":"Mila",
    "pversion":"7",
    "author":"117473"*/
