package com.example.maledettatreestandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Linea {
    Direzione termine1, termine2;
    Context context;
    String sid;


    public Linea(JSONObject termine1, JSONObject termine2, String sid, Context context) {
        this.sid=sid;
        this.context=context;
        this.termine1=new Direzione(termine1, sid, context);
        this.termine2=new Direzione(termine2, sid, context);
    }

    public Direzione getDirezione1() {
        return termine1;
    }

    public Direzione getDirezione2() {
        return termine2;
    }

    public String getTitolo(){ return termine1.getNome()+"-"+termine2.getNome(); }
}
