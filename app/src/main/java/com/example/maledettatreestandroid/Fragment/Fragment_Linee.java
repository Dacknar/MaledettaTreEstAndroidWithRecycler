package com.example.maledettatreestandroid.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.maledettatreestandroid.Linea;
import com.example.maledettatreestandroid.Linee;
import com.example.maledettatreestandroid.LineeAdapter;
import com.example.maledettatreestandroid.R;

import java.util.ArrayList;

public class Fragment_Linee extends Fragment {
    Linee linee;
    Context context;
    Fragment_Map fragmentMap_fragment;
    ExpandableListView lineeView;
    LineeAdapter lineeAdapter;
    private int lastExpandedPosition = -1;
    int openGroups=0;
    FragmentManager manager;

    public Fragment_Linee(Linee linee, Context context, FragmentManager manager) {
        this.linee = linee;
        this.context=context;
        this.manager=manager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_linee, container, false);

        lineeView=view.findViewById(R.id.lineeView);

        /*view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "booh");
            }
        });*/

        lineeAdapter =new LineeAdapter(linee, context, fragmentMap_fragment, manager);
        lineeView.setAdapter(lineeAdapter);

        lineeView.setOnGroupExpandListener(groupPosition -> {
            openGroups++;
            Log.d("Cliccato", "cliccato in apertura: "+openGroups);
            if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                if(openGroups==1){
                    openGroups++;
                }
                lineeView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition;
        });

        lineeView.setOnGroupCollapseListener(groupPosition -> {
            openGroups--;
            Log.d("Cliccato", "Domanda: "+openGroups);
            if (openGroups == 0){
                Log.d("Cliccato", "chiuso manualmente");
                fragmentMap_fragment.drawAllLines(-1);
            }
        });

        return view;
    }

    public void setMapFragment(Fragment_Map fragmentMap_fragment){
        this.fragmentMap_fragment = fragmentMap_fragment;
    }
}