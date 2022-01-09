package com.example.maledettatreestandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;
import com.example.maledettatreestandroid.Fragment.Fragment_Linee;
import com.example.maledettatreestandroid.Fragment.Fragment_Map;

public class TrainActivity extends AppCompatActivity {
    FragmentManager manager;
    Context context;
    Linee linee;

    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    public TrainActivity() {
        manager = getSupportFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        context=this;
        linee=new Linee(context);

        setLinea(-1);
        setDirezione(-1);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            setFragments();
        }, 500);

        /*setFragments(new Fragment_Map(stazioni, this), new Fragment_Linee(this));*/
    }

    public void setFragments(){
        /*linee.get(0).getDirezione2().getStazioni().get(0).getNome();*/
        Fragment_Map fragment_Map =new Fragment_Map(linee, this, false);
        Fragment_Linee fragment_Linee =new Fragment_Linee(linee, this, manager);
        Fragment_BottonButtons fragment_bottonButtons = new Fragment_BottonButtons(0, this);

        manager.beginTransaction()
                .replace(R.id.map, fragment_Map, fragment_Map.getTag())
                .commit();

        manager.beginTransaction()
                .replace(R.id.linee, fragment_Linee, fragment_Linee.getTag())
                .commit();

        manager.beginTransaction()
                .replace(R.id.bottonButtons, fragment_bottonButtons, fragment_bottonButtons.getTag())
                .commit();

        fragment_Map.setLineeFragment(fragment_Linee);
        fragment_Linee.setMapFragment(fragment_Map);
    }

    public void setLinea(int nLinea){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LINEA, nLinea);
        editor.apply();
    }

    public void setDirezione(int nDirezione){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DIREZIONE, nDirezione);
        editor.apply();
    }
}