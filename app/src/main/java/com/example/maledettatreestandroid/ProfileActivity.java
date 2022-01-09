package com.example.maledettatreestandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    String sid, uid, nome, foto, pVersion;
    EditText nomeUtente;
    FragmentManager manager;
    MyDatabaseHelper myDB;
    ImageView image_profile;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        myDB=new MyDatabaseHelper(this);
        manager = getSupportFragmentManager();

        getSid();
        takeValue();
        Fragment_BottonButtons fragment_bottonButtons = new Fragment_BottonButtons(1, this);
        manager.beginTransaction()
                .replace(R.id.bottonButtons, fragment_bottonButtons, fragment_bottonButtons.getTag())
                .commit();

        nomeUtente=findViewById(R.id.nomeUtenteEdit);
        image_profile=findViewById(R.id.image_profile);


    }

    public void getSid(){
        SharedPreferences sharedPreferences=this.getSharedPreferences(SHARED_PREFS, 0);
        sid=sharedPreferences.getString(SID, "");
    }

    public void takeValue(){
        Log.d("TAG", "takeValueProfile");

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getProfile.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                this.uid=response.getString("uid");
            } catch (Exception e) {
                this.uid="";
                e.printStackTrace();
            }
            try {
                this.nome=response.getString("name");
            } catch (Exception e) {
                this.nome="";
                e.printStackTrace();
            }
            nomeUtente.setText(nome);
            try {
                this.foto=response.getString("foto");
            } catch (Exception e) {
                this.foto=null;
                e.printStackTrace();
            }
            try {
                this.pVersion=response.getString("pversion");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(getFoto()==null){
                image_profile.setImageDrawable(this.getResources().getDrawable(R.drawable.no_foto));
            } else {
                image_profile.setImageBitmap(getFoto());
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);

    }

    public void registraUtente(String sid, String nomeUtente){
        Log.d("TAG", "registraUtente");

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/setProfile.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
            jsonObject.put("name", nomeUtente);
            jsonObject.put("picture", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                Intent intent = new Intent(this, ProfileActivity.class);
                this.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public Bitmap getFoto(){
        Bitmap picture=null;
        Cursor cursor=myDB.readPictureVersione(uid);
        if (cursor!=null){
            if(cursor.getCount()==0){
                Log.d("PICTURE", "No database");
                picture=getServerPicture(true);
            } else {
                while(cursor.moveToNext()){
                    if(Integer.parseInt(cursor.getString(0))==Integer.parseInt(pVersion)){
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
        final Bitmap[] picture = new Bitmap[1];
        Log.d("PICTURE", "getServerPicture sid: "+sid+" uid: "+uid);
        if(uid!=""){
            RequestQueue queue = Volley.newRequestQueue(this);

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
                    picture[0] =decodeImage(response.getString("picture"));
                    if(newImage){
                        myDB.addImage(response.getString("uid"), response.getString("pversion"), response.getString("picture"));
                    } else {
                        myDB.updateImage(response.getString("uid"), response.getString("pversion"), response.getString("picture"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                picture[0] =null;
                Log.d("POSTS", "ERROR IMMAGE: " + error.toString());
            });
            queue.add(jsonObjectRequest);
        }

        return picture[0];}
}