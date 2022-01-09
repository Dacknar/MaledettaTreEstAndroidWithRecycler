package com.example.maledettatreestandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;
import com.example.maledettatreestandroid.Fragment.Fragment_Map;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity {
    FragmentManager manager;
    Linee linee;
    Context context;
    ListView lista_di_posts;
    ArrayList<Post> posts_seguiti=new ArrayList<>();
    ArrayList<Post> posts_non_seguiti=new ArrayList<>();
    TextView title, direzione;
    ImageButton changeDirection;
    int nLinea, nDirezione;
    Fragment_Map fragmentMap_fragment;
    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        context = this;
        linee = new Linee(context);

        getnLinea();
        getnDirezione();

        title = findViewById(R.id.title);
        direzione=findViewById(R.id.direzione);
        changeDirection=findViewById(R.id.change_direction);
        changeDirection.setOnClickListener(view -> {
            nDirezione=(nDirezione-1)*(nDirezione-1);
            setDirezione(nDirezione);
            Log.d("POSTS", "cliccatoo"+nDirezione);
            setPosts(nLinea, nDirezione);
            /*fragmentMap_fragment.drawAllLines(nLinea, nDirezione);*/
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            manager = getSupportFragmentManager();
            setBottonButtons();
            setPosts(nLinea, nDirezione);
            setMap();
        }, 1000);
    }

    public void setBottonButtons(){
        Fragment_BottonButtons fragment_bottonButtons = new Fragment_BottonButtons(0, this);
        manager.beginTransaction()
                .replace(R.id.bottonButtons, fragment_bottonButtons, fragment_bottonButtons.getTag())
                .commit();
    }

    public void setPosts(int nLinea, int nDirezione){
        lista_di_posts=findViewById(R.id.lista_di_post);
        Linea linea=linee.getLinea(nLinea);
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

        PostAdapter postAdapter=new PostAdapter(getApplicationContext(), posts_seguiti, posts_non_seguiti);
        lista_di_posts.setAdapter(postAdapter);
    }

    public void setMap(){
        Log.d("MAPPA", "sto per caricare la mappa");
        fragmentMap_fragment =new Fragment_Map(linee, context, true);
        manager.beginTransaction()
                .replace(R.id.map, fragmentMap_fragment, fragmentMap_fragment.getTag())
                .commit();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            /*fragmentMap_fragment.drawAllLines(nLinea, nDirezione);*/
        }, 1000);
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
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DIREZIONE, nDirezione);
        editor.apply();
    }

}