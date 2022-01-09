package com.example.maledettatreestandroid;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Direzione {
    String nome;
    int did;
    ArrayList<Stazione> stazioni=new ArrayList<>();
    ArrayList<Post> posts_seguiti=new ArrayList<>();
    ArrayList<Post> posts_non_seguiti=new ArrayList<>();
    String sid;
    Context context;
    Boolean finish=false;

    public Direzione(JSONObject termine, String sid, Context context) {
        try {
            this.nome = termine.getString("sname");
            this.did = termine.getInt("did");
        } catch (JSONException e) {
            nome="";
            did=-1;
            e.printStackTrace();
        }
        this.sid=sid;
        this.context=context;

        if(stazioni.isEmpty()){
            setStazioni();
        }
        if(posts_seguiti.isEmpty()||posts_non_seguiti.isEmpty()){
            setPosts();
        }
    }

    public void setStazioni(){
        Log.d("TAG", "setStazioni");

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getStations.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
            jsonObject.put("did", did);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("stations");
                if (jsonArray != null) {
                    for (int i=0;i<jsonArray.length();i++){
                        Stazione stazione= new Stazione((JSONObject) jsonArray.get(i));
                        stazioni.add(stazione);
                    }
                    Log.d("TAG", "finish Stazioni");
                    finish=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("TAG", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public void setPosts(){
        Log.d("POSTS", "setPosts");
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/getPosts.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
            jsonObject.put("did", did);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("posts");
                if (jsonArray != null) {
                    for (int i=0;i<jsonArray.length();i++){
                        Post post= new Post((JSONObject) jsonArray.get(i), context);
                        if(post.getFollowingAuthor()==true){
                            posts_seguiti.add(post);
                        } else {
                            posts_non_seguiti.add(post);
                        }
                    }
                    Log.d("POSTS", "finish Post");
                    finish=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.d("POSTS", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public String getNome() {
        return nome;
    }

    public int getDid() {
        return did;
    }

    public ArrayList<Stazione> getStazioni() {
        return stazioni;
    }

    public ArrayList<Post> getPosts_seguiti(){
        return posts_seguiti;
    }

    public ArrayList<Post> getPosts_non_seguiti(){
        return posts_non_seguiti;
    }
}
