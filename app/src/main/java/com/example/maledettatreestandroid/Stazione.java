package com.example.maledettatreestandroid;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Stazione {
    private String nome;
    private Double lat, log;
    public Stazione(JSONObject stazione){
        /*{"sname":"Milano Celoria","lat":"45.47669","lon":"9.23217"}*/

        try {
            this.nome=stazione.getString("sname");
            this.lat=stazione.getDouble("lat");
            this.log=stazione.getDouble("lon");

        } catch (JSONException e) {
            this.nome="";
            this.lat= Double.valueOf(-1);
            this.log=Double.valueOf(-1);
            e.printStackTrace();
        }
    }

    public String getNome() {
        return nome;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLog() {
        return log;
    }
}
