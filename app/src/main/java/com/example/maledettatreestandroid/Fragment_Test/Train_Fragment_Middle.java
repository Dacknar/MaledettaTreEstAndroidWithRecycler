package com.example.maledettatreestandroid.Fragment_Test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maledettatreestandroid.Fragment.Fragment_BottonButtons;
import com.example.maledettatreestandroid.Fragment.Fragment_Linee;
import com.example.maledettatreestandroid.Fragment.Fragment_Map;
import com.example.maledettatreestandroid.Linee;
import com.example.maledettatreestandroid.PostsActivity;
import com.example.maledettatreestandroid.R;
import com.example.maledettatreestandroid.TrainActivity;

public class Train_Fragment_Middle extends Fragment {
    FragmentManager manager;
    Context context;
    Linee linee;

    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";


    public Train_Fragment_Middle(Context context, FragmentManager manager) {
        Log.d("DATA_ANALYS", "Train_Fragment_Middle");
        this.context=context;
        this.manager=manager;
        linee=new Linee(context);

        setLinea(-1);
        setDirezione(-1);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Log.d("DATA_ANALYS", "setFragments");
            setFragments();
        }, 1000);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DATA_ANALYS", "onCreatView");

        View view=inflater.inflate(R.layout.fragment_train_middle, container, false);
        return view;
    }

    public void setFragments(){
        /*linee.get(0).getDirezione2().getStazioni().get(0).getNome();*/
        Fragment_Map fragment_Map =new Fragment_Map(linee, context, false);
        Fragment_Linee fragment_Linee =new Fragment_Linee(linee, context, manager);

        manager.beginTransaction()
                .replace(R.id.map, fragment_Map, fragment_Map.getTag())
                .commit();

        manager.beginTransaction()
                .replace(R.id.linee, fragment_Linee, fragment_Linee.getTag())
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