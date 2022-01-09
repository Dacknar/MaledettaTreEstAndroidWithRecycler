package com.example.maledettatreestandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.maledettatreestandroid.Fragment_Test.Posts_Fragment_Middle;
import com.example.maledettatreestandroid.Fragment_Test.Profile_Fragment_Middle;
import com.example.maledettatreestandroid.Fragment_Test.Train_Fragment_Middle;

public class MiddleActivity extends AppCompatActivity {
    /*final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {

                    }, 500);*/

    MeowBottomNavigation bottomNavigation;
    Context context;
    int nDirezione;
    SharedPreferences sharedPreferences;
    int i=0;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle);

        Log.d("NAVIGAZIONE", "onCreate");

        this.context=this;
        sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);

        getnDirezione();

        bottomNavigation=findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.train_40));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.profile_40));

        bottomNavigation.setOnShowListener(item -> {
            if(i<1) {
                Log.d("NAVIGAZIONE", "showListener");
                Fragment fragment = null;
                FragmentManager manager = getSupportFragmentManager();
                switch (item.getId()) {
                    case 2:
                        if (nDirezione == -1) {
                            Log.d("NAVIGAZIONE", "train");
                            fragment = new Train_Fragment_Middle(context, manager);
                        } else {
                            fragment = new Posts_Fragment_Middle(context, manager);
                        }
                        break;
                    case 3:
                        fragment = new Profile_Fragment_Middle(context, this);
                        break;
                }
                loadFragment(fragment);
                i++;
            }
        });

        bottomNavigation.setCount(1, "10");
        bottomNavigation.show(2, true);

        bottomNavigation.setOnClickMenuListener(item -> {
            getnDirezione();
            Log.d("NAVIGAZIONE", "cliccato"+nDirezione);
            Fragment fragment=null;
            FragmentManager manager = getSupportFragmentManager();
            switch (item.getId()){
                case 2:
                    if(nDirezione==-1){
                        fragment=new Train_Fragment_Middle(context, manager);
                    } else {
                        fragment=new Posts_Fragment_Middle(context, manager);
                    }
                    break;
                case 3:
                    fragment=new Profile_Fragment_Middle(context, this);
                    break;
            }
            loadFragment(fragment);
        });

        bottomNavigation.setOnReselectListener(item -> {
            getnDirezione();
            Log.d("NAVIGAZIONE", "ricliccato"+nDirezione);
            Fragment fragment=null;
            FragmentManager manager = getSupportFragmentManager();
            switch (item.getId()){
                case 2:
                    setDirezione(-1);
                    fragment=new Train_Fragment_Middle(context, manager);
                    break;
                case 3:
                    fragment=new Profile_Fragment_Middle(context, this);
                    break;
            }
            loadFragment(fragment);
        });
    }

    private void loadFragment(Fragment fragmnet){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragmnet)
                .commit();
    }

    public void getnDirezione(){
        nDirezione=sharedPreferences.getInt(DIREZIONE, -1);
    }

    public void setDirezione(int nDirezione){
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DIREZIONE, nDirezione);
        editor.apply();
    }
}