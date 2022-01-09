package com.example.maledettatreestandroid.Fragment_Test;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;
import com.example.maledettatreestandroid.MyDatabaseHelper;
import com.example.maledettatreestandroid.Profile;
import com.example.maledettatreestandroid.ProfileActivity;
import com.example.maledettatreestandroid.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Profile_Fragment_Middle extends Fragment {
    EditText nomeUtente;
    ImageView image_profile;
    Context context;
    Activity activity;
    Button modify;
    Profile profile;
    String sid;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    public Profile_Fragment_Middle(Context context, Activity activity) {
        this.context = context;
        this.activity=activity;
        getSid();
        profile=new Profile(context);
        takeValue();
    }

    public void getSid(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        this.sid=sharedPreferences.getString(SID, "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_middle, container, false);

        nomeUtente=view.findViewById(R.id.nomeUtenteEdit);
        image_profile=view.findViewById(R.id.image_profile);
        modify=view.findViewById(R.id.modifica);

        /*takeValue();*/



        //Log.d("PROFILE", "Profile: "+profile.getUid()+", "+profile.getNome()+", "+profile.getpVersion()+", "+ profile.getFoto());


        /*final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Log.d("ELEMENTI", "nome: "+profile.getNome());
            nomeUtente.setHint(profile.getNome());

            Log.d("PROFILE", "foto: "+profile.getFoto());
        }, 1000);*/

        /*modify.setOnClickListener(view1 -> {
            registraUtente();
        });*/

        image_profile.setOnLongClickListener((View.OnLongClickListener) view12 -> {
            Log.d("PICTURE", "image pressed");

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String [] permissions= {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }

            return false;
        });

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery();
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==activity.RESULT_OK&&requestCode==IMAGE_PICK_CODE){
            Uri imageUri=data.getData();
            Bitmap bitmap = null;
            String newImage=null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                Bitmap resizedImage = getResizedBitmap(bitmap, 230);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                newImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                profile.setFoto(newImage);
                registraUtente();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void takeValue(){
        Log.d("TAG", "takeValueProfile");

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getProfile.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                profile.setUid(response.getString("uid"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                profile.setNome(response.getString("name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Log.d("PROFILE", "FOTOO " + decodeImage(response.getString("picture")));
                profile.setFoto(response.getString("picture"));
                image_profile.setImageBitmap(decodeImage(response.getString("picture")));
                Log.d("fotoo", "foto"+profile.getFoto());
                //TODO: DECODE
            } catch (Exception e) {
                Log.d("PROFILE", "no foto");
                profile.setFoto(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_foto));
                image_profile.setImageDrawable(getResources().getDrawable(R.drawable.no_foto));
                e.printStackTrace();
            }
            //image_profile.setImageBitmap(profile.getFoto());
            try {
                profile.setpVersion(response.getString("pversion"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public void registraUtente(){
        Log.d("TAG", "registraUtente");

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/setProfile.php";
        JSONObject jsonObject = new JSONObject();

        try {
            if(profile.getFoto()==null){
                jsonObject.put("sid", sid);
                jsonObject.put("name", profile.getNome());
                jsonObject.put("picture", encodeImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_foto)));
            } else {
                jsonObject.put("sid", sid);
                jsonObject.put("name", profile.getNome());
                jsonObject.put("picture", profile.getFoto());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                modify.setText("Modificato");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }



    /*public String encodeImage(Bitmap bitmap){
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }*/

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

}