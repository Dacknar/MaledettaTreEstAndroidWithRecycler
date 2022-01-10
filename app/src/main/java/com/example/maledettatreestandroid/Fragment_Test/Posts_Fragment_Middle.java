package com.example.maledettatreestandroid.Fragment_Test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maledettatreestandroid.Direzione;
import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;
import com.example.maledettatreestandroid.Fragment.Fragment_Map;
import com.example.maledettatreestandroid.Linea;
import com.example.maledettatreestandroid.Linee;
import com.example.maledettatreestandroid.Post;
import com.example.maledettatreestandroid.PostAdapter;
import com.example.maledettatreestandroid.PostRecyclerAdapter;
import com.example.maledettatreestandroid.R;

import java.util.ArrayList;

public class Posts_Fragment_Middle extends Fragment {
    Context context;
    FragmentManager manager;
    Linee linee;
    RecyclerView lista_di_posts;
    ArrayList<Post> posts_seguiti=new ArrayList<>();
    ArrayList<Post> posts_non_seguiti=new ArrayList<>();
    TextView title, direzione;
    ImageButton changeDirection;
    int nLinea, nDirezione;
    Fragment_Map fragmentMap_fragment;
    SharedPreferences sharedPreferences;
    View view;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    public Posts_Fragment_Middle(Context context, FragmentManager manager) {
        Log.d("DATA_ANALYS", "Post_Fragment_Middle");
        this.context=context;
        this.manager=manager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DATA_ANALYS", "onCreatView Posts");
        View view=inflater.inflate(R.layout.fragment_posts_middle, container, false);
        this.view=view;

        linee = new Linee(context);

        reaLoadData();

        title = view.findViewById(R.id.title);
        direzione=view.findViewById(R.id.direzione);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            setMap();
            setPosts(nLinea, nDirezione);
        }, 1000);

        changeDirection=view.findViewById(R.id.change_direction);
        changeDirection.setOnClickListener(v -> {
            nDirezione=(nDirezione-1)*(nDirezione-1);
            setDirezione(nDirezione);
            Log.d("POSTS", "cliccatoo"+nDirezione);
            setPosts(nLinea, nDirezione);
            setMap();
        });

        return view;
    }

    public void reaLoadData(){
        getnLinea();
        getnDirezione();
    }

    public void setPosts(int nLinea, int nDirezione){
        lista_di_posts=view.findViewById(R.id.lista_di_post);
        Linea linea=linee.getLinea(nLinea);
        if(linea!=null){
            title.setText(linea.getTitolo());

            if(nDirezione==0){
                Direzione direzione1=linea.getDirezione1();
                posts_seguiti=direzione1.getPosts_seguiti();
                posts_non_seguiti=direzione1.getPosts_non_seguiti();
                direzione.setText("Direzione "+direzione1.getNome());
            } else {
                Direzione direzione2=linea.getDirezione2();
                posts_seguiti=direzione2.getPosts_seguiti();
                posts_non_seguiti=direzione2.getPosts_non_seguiti();
                direzione.setText("Direzione "+direzione2.getNome());
            }

            //PostAdapter postAdapter= new PostAdapter(getApplicationContext(), posts_seguiti, posts_non_seguiti);
            PostRecyclerAdapter adapter = new PostRecyclerAdapter(getContext(), posts_seguiti, posts_non_seguiti);
            //lista_di_posts.setAdapter(postAdapter);
            lista_di_posts.setLayoutManager(new LinearLayoutManager(getContext()));
            lista_di_posts.setAdapter(adapter);
        }
    }

    public void setMap(){
        Log.d("MAPPA", "sto per caricare la mappa");
        fragmentMap_fragment =new Fragment_Map(linee, context, true);
        manager.beginTransaction()
                .replace(R.id.map, fragmentMap_fragment)
                .commit();
    }

    public void getnLinea(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        nLinea=sharedPreferences.getInt(LINEA, -1);
    }

    public void getnDirezione(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        nDirezione=sharedPreferences.getInt(DIREZIONE, -1);
    }

    public void setDirezione(int nDirezione){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DIREZIONE, nDirezione);
        editor.apply();
    }
}