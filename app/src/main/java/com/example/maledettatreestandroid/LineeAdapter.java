package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.example.maledettatreestandroid.Fragment.Fragment_Linee;
import com.example.maledettatreestandroid.Fragment.Fragment_Map;
import com.example.maledettatreestandroid.Fragment_Test.Posts_Fragment_Middle;

import java.util.ArrayList;

public class LineeAdapter extends BaseExpandableListAdapter {
    Linee linee;
    Fragment_Map fragmentMap_fragment;
    Context context;
    SharedPreferences sharedPreferences;
    FragmentManager manager;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    public LineeAdapter(Linee linee, Context context, Fragment_Map fragmentMap_fragment, FragmentManager manager) {
        this.linee = linee;
        this.fragmentMap_fragment = fragmentMap_fragment;
        this.context=context;
        this.manager=manager;
    }

    @Override
    public int getGroupCount() {
        return linee.getSize();
    }

    @Override
    public int getChildrenCount(int i) {
        return 2;
    }

    @Override
    public Object getGroup(int i) {
        return linee.getLinea(i).getTitolo();
    }

    @Override
    public Object getChild(int i, int i1) {
        if(i1==0){
            return linee.getLinea(i).termine1.getNome();
        } else {
            return linee.getLinea(i).termine2.getNome();
        }
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        Log.d("TAG", "getGroupView");
        view= LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_expandable_list_item_1,
                        viewGroup, false);
        TextView textView=view.findViewById(android.R.id.text1);
        String sGroup=String.valueOf(getGroup(i));
        textView.setText(sGroup);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.BLUE);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        /*ArrayList<Stazione> stazioniDirezione=linee.get(i).getDirezione1().getStazioni();*/
        fragmentMap_fragment.drawAllLines(i);

        view= LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_selectable_list_item,
                        viewGroup, false);
        TextView textView=view.findViewById(android.R.id.text1);
        String sChild=String.valueOf(getChild(i, i1));
        textView.setText("Dierzione: "+sChild);

        textView.setOnClickListener(view1 -> {
            setLinea(i);
            setDirezione(i1);

            Posts_Fragment_Middle fragment_posts =new Posts_Fragment_Middle(context, manager);
            manager.beginTransaction()
                    .replace(R.id.frame_layout, fragment_posts)
                    .commit();

        });
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
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
