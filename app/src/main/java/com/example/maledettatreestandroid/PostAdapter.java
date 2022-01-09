package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends BaseAdapter {

    Context context;
    ArrayList<Post> posts;
    LayoutInflater inflater;
    String sid;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";

    public PostAdapter(Context context, ArrayList<Post> posts_seguiti, ArrayList<Post> posts_non_seguiti) {
        this.context=context;
        this.posts=posts_seguiti;
        this.posts.addAll(posts_non_seguiti);
        inflater=LayoutInflater.from(context);
        getSid();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.post, null);
        TextView nome=convertView.findViewById(R.id.nome);
        TextView stato=convertView.findViewById(R.id.stato);
        TextView ritardo=convertView.findViewById(R.id.ritardo);
        TextView commento=convertView.findViewById(R.id.commento);
        TextView data=convertView.findViewById(R.id.data);
        Button follow=convertView.findViewById(R.id.follow);
        ImageView foto=convertView.findViewById(R.id.foto);
        //TODO: follow button & img

        Post post = posts.get(position);
        Log.d("POST ", post.getAutore());
        nome.setText(post.getNome());
        stato.setText(post.getStato());
        ritardo.setText(post.getRitardo());
        commento.setText(post.getCommento());
        data.setText(post.getDataPubblicazione());

        if(post.getFollowingAuthor()==true){
            follow.setText("UnFollow");
            follow.setBackgroundColor(Color.GRAY);
        } else {
            follow.setText("Follow");
            follow.setBackgroundColor(Color.GREEN);
        }
        if(post.getPicture()==null){
            foto.setImageDrawable(context.getResources().getDrawable(R.drawable.no_foto));
        } else {
            foto.setImageBitmap(post.getPicture());
        }

        follow.setOnClickListener(view -> {
            if(post.getFollowingAuthor()==true){
                Log.d("FOLLOW", "true");
                follow.setText("UnFollow");
                follow.setBackgroundColor(Color.GRAY);
                setUnFollow(post);
            } else {
                Log.d("FOLLOW", "false");
                follow.setText("Follow");
                follow.setBackgroundColor(Color.GREEN);
                setFollow(post);
            }
        });

        return convertView;
    }

    public void getSid(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        sid=sharedPreferences.getString(SID, "");
    }

    public void setFollow(Post post){
        String uid=post.getAutore();
        Log.d("FOLLOW", "sto per seguire l'utente "+uid);

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/follow.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
            jsonObject.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            Log.d("FOLLOW", "follow"+uid);
            post.setFollowingAuthor(true);
        }, error -> Log.d("FOLLOW", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }

    public void setUnFollow(Post post){
        String uid=post.getAutore();
        Log.d("FOLLOW", "sto smettendo di seguire l'utente "+uid);

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://ewserver.di.unimi.it/mobicomp/treest/unfollow.php";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("sid", sid);
            jsonObject.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
            Log.d("FOLLOW", "unFollow"+uid);
            post.setFollowingAuthor(false);
        }, error -> Log.d("FOLLOW", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }
}
