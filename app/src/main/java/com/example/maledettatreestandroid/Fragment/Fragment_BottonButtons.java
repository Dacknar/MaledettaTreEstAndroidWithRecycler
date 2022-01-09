package com.example.maledettatreestandroid.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.maledettatreestandroid.ProfileActivity;
import com.example.maledettatreestandroid.TrainActivity;
import com.example.maledettatreestandroid.R;
public class Fragment_BottonButtons extends Fragment {
    int i;
    ImageButton train_button, profile_button;
    Context context;

    public Fragment_BottonButtons(int i, Context context) {
        this.context=context;
        this.i=i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment__botton_buttons, container, false);
        train_button=view.findViewById(R.id.train_button);
        profile_button=view.findViewById(R.id.profile_button);

        if(i==0){
            train_button.setImageResource(R.drawable.train_50);
            profile_button.setImageResource(R.drawable.profile_40);
        } else {
            train_button.setImageResource(R.drawable.train_40);
            profile_button.setImageResource(R.drawable.profile_50);
        }

        train_button.setOnClickListener(view1-> {
            Intent intent = new Intent(context, TrainActivity.class);
            context.startActivity(intent);
        });

        profile_button.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
        });



        return view;
    }
}