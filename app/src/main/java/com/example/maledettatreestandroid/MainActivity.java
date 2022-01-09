package com.example.maledettatreestandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText nomeUtente;
    TextView testo;
    Button vai, entra;
    SharedPreferences sharedPreferences;
    RequestQueue queue;
    String url =" https://ewserver.di.unimi.it/mobicomp/treest/register.php";

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String NOME="nomeUtente";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("VAI", "here");

        testo = findViewById(R.id.testo);
        nomeUtente = findViewById(R.id.nomeUtente);
        vai=findViewById(R.id.iscrizione);
        entra=findViewById(R.id.entra);

        vai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("VAI", "cliccato");
            }
        });

        SharedPreferences sharedPreferences=this.getSharedPreferences(SHARED_PREFS, 0);
        if(sharedPreferences.getString(SID, "")==""){
            nomeUtente.setVisibility(View.VISIBLE);
            vai.setVisibility(View.VISIBLE);
            entra.setVisibility(View.INVISIBLE);
            vai.setOnClickListener(view -> setSid());
        } else {
            testo.setText("Ciao "+sharedPreferences.getString(NOME, "")+"!");
            goNextActivity();
        }
    }

    private void setSid() {
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Convert String to json object
                    JSONObject json = null;
                    try {
                        json = new JSONObject(response);
                        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SID, (String) json.get("sid"));
                        editor.putString(NOME, (String) nomeUtente.getText().toString());
                        editor.apply();
                        Log.d("SID", "SID: "+json.get("sid"));

                        registraUtente(json.getString("sid"), nomeUtente.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {});

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void registraUtente(String sid, String nomeUtente){
        Log.d("TAG", "setStazioni");

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
                goNextActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public void goNextActivity(){
        Intent intent = new Intent(this, MiddleActivity.class);
        this.startActivity(intent);
    }

    public void goNextActivity(View view) {
        Intent intent = new Intent(this, MiddleActivity.class);
        this.startActivity(intent);
    }
}

