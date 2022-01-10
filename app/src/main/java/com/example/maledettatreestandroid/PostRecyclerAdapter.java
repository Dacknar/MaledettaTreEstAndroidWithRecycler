package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Post> posts;
    LayoutInflater inflater;
    String sid;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new PostRecyclerAdapter.ViewHolder(view);
    }

    public PostRecyclerAdapter(Context context, ArrayList<Post> posts_seguiti, ArrayList<Post> posts_non_seguiti) {
        this.context = context;
        this.posts=posts_seguiti;
        this.posts.addAll(posts_non_seguiti);
        getSid();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nome.setText(posts.get(position).getNome());
        holder.stato.setText(posts.get(position).getStato());
        holder.ritardo.setText(posts.get(position).getRitardo());
        holder.commento.setText(posts.get(position).getCommento());
        holder.data.setText(posts.get(position).getDataPubblicazione());
        Log.d("RecyclerPosts", "FINZIONA RECYCLER: ");
        if(posts.get(position).getFollowingAuthor()==true){
            holder.follow.setText("UnFollow");
            holder.follow.setBackgroundColor(Color.GRAY);
        } else {
            holder.follow.setText("Follow");
            holder.follow.setBackgroundColor(Color.GREEN);
        }
        if(posts.get(position).getPicture()==null){
            holder.foto.setImageDrawable(context.getResources().getDrawable(R.drawable.no_foto));
        } else {
            holder.foto.setImageBitmap(posts.get(position).getPicture());
        }

        holder.follow.setOnClickListener(view -> {
            if(posts.get(position).getFollowingAuthor()==true){
                Log.d("FOLLOW", "true");
                holder.follow.setText("UnFollow");
                holder.follow.setBackgroundColor(Color.GRAY);
                setUnFollow(posts.get(position));
            } else {
                Log.d("FOLLOW", "false");
                holder.follow.setText("Follow");
                holder.follow.setBackgroundColor(Color.GREEN);
                setFollow(posts.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nome, stato, ritardo, commento, data;
        Button follow;
        ImageView foto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nome=itemView.findViewById(R.id.nome);
            stato=itemView.findViewById(R.id.stato);
            ritardo=itemView.findViewById(R.id.ritardo);
            commento=itemView.findViewById(R.id.commento);
            data=itemView.findViewById(R.id.data);
            follow=itemView.findViewById(R.id.follow);
            foto=itemView.findViewById(R.id.foto);
        }
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
            for(int i = 0; i < posts.size(); i++){
                if(posts.get(i).getAutore().equals(post.getAutore())){
                    posts.get(i).setFollowingAuthor(true);
                }
            }
            //post.setFollowingAuthor(true);
            notifyDataSetChanged();

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
            for(int i = 0; i < posts.size(); i++){
                if(posts.get(i).getAutore().equals(post.getAutore())){
                    posts.get(i).setFollowingAuthor(false);
                }
            }
            //post.setFollowingAuthor(false);
            notifyDataSetChanged();
        }, error -> Log.d("FOLLOW", "ERROR: " + error.toString()));
        queue.add(jsonObjectRequest);
    }
}
