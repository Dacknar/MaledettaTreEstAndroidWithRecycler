package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Linee {
    ArrayList<Linea> linee=new ArrayList<>();
    Context context;
    String sid;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    public Linee(Context context) {
        this.context=context;
        Log.d("DATA_ANALYS", "linee");
        if(linee.isEmpty()){
            Log.d("DATA_ANALYS", "linee is empty");
            setLinee();
        }
    }

    public void setLinee(){
        RequestQueue queue = Volley.newRequestQueue(context);

        getSid();

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getLines.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("lines");

                    if (jsonArray != null) {
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject linea= (JSONObject) jsonArray.get(i);
                            JSONObject termine1 =linea.getJSONObject("terminus1");
                            JSONObject termine2 =linea.getJSONObject("terminus2");
                            linee.add(new Linea(termine1, termine2, sid, context));
                        }

                        Log.d("TAG", "finish");

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "ERRORE: " + e.toString());
                }
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public int getSize(){
        return linee.size();
    }

    public Linea getLinea(int i){
        if(linee.isEmpty()){
           return null;
        }
        return linee.get(i);
    }

    public void getSid(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        sid=sharedPreferences.getString(SID, "");
    }

    public boolean isEmpty(){
        return linee.isEmpty();
    }
}
