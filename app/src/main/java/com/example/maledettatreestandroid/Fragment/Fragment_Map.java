package com.example.maledettatreestandroid.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maledettatreestandroid.Linea;
import com.example.maledettatreestandroid.Linee;
import com.example.maledettatreestandroid.R;
import com.example.maledettatreestandroid.Stazione;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Fragment_Map extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    Linee linee;
    Context context;
    Fragment_Linee fragmentLinee_fragment;
    Polyline polyLine=null;
    float zoomLevel = 11f;
    Boolean post;

    public static final String SHARED_PREFS="sharedPrefers";
    public static final String SID="sid";
    public static final String LINEA="nLinea";
    public static final String DIREZIONE="nDirezione";

    public Fragment_Map(Linee linee, Context context, Boolean post) {
        this.linee = linee;
        if(linee.isEmpty()){
            Log.d("DATA_ANALYS", "Nella mappa, linee is empty");
        }
        this.context=context;
        this.post=post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MAPPA", "sto creando la mappa");

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        return view;

    }

    public void setLineeFragment(Fragment_Linee fragmentLinee_fragment){
        this.fragmentLinee_fragment = fragmentLinee_fragment;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        Log.d("MAPPA", "mappa pronta");

        if(post==false){
            drawAllLines(-1);
        } else {
            drawMyLinea(getnLinea());
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void drawAllLines(int daColorare){
        Log.d("MAPPA", "sto ripulendo la mappa");
        if(googleMap!=null){
            googleMap.clear();

            Log.d("MAPPA", "sto iniziando a disegnare la mappa");
            for (int i=0; i<linee.getSize(); i++){
                Linea linea=linee.getLinea(i);
                Log.d("TAG", "start"+i);
                if(i==daColorare){
                    drawAllStops(linea, R.drawable.train_black, R.drawable.circle_black_light, Color.BLUE);
                }else{
                    drawAllStops(linea, R.drawable.train_green, R.drawable.circle_green_light, Color.GRAY);
                }
            }
            Log.d("MAPPA", "ho disegnato la mappa");
        }
    }

    public void drawAllStops(Linea linea, int train,  int circle, int color){
        ArrayList<Stazione> stazioni=linea.getDirezione1().getStazioni();
        /*Log.d("TAG", "stazione"+0+": "+stazioniDirezione.get(0).getNome()+": "+stazioniDirezione.get(0).getLat()+", "+stazioniDirezione.get(0).getLog());*/
        for(int var=0; var<stazioni.size(); var++){
            MarkerOptions markerOptions=new MarkerOptions();
            if(var==0||var==stazioni.size()-1){
                markerOptions.icon(getMarkerIconFromDrawable(context.getResources().getDrawable(train)));
            } else {
                    markerOptions.icon(getMarkerIconFromDrawable(context.getResources().getDrawable(circle)));
            }
            if(var!=stazioni.size()-1){
                LatLng latLngStart = new LatLng(stazioni.get(var).getLat(), stazioni.get(var).getLog());
                LatLng latLngEnd = new LatLng(stazioni.get(var+1).getLat(), stazioni.get(var+1).getLog());
                drawLinea(latLngStart, latLngEnd, color);
            }
            drawPoint(stazioni.get(var), markerOptions);
        }
        zoom(stazioni);
    }

    public void drawLinea(LatLng latLngStart, LatLng latLngEnd, int color){
        Log.d("TAG", "here");
        polyLine=googleMap.addPolyline(new PolylineOptions().add(latLngStart).add(latLngEnd).color(color));
    }

    public void drawPoint(Stazione direzione,  MarkerOptions markerOptions) {
        Log.d("MAPPA", "disegno un punto"+direzione.getNome());
        LatLng latLng = new LatLng(direzione.getLat(), direzione.getLog());
        markerOptions.position(latLng)
                .title("Stazione " + direzione.getNome());
        googleMap.addMarker(markerOptions);
    }

    public void zoom(ArrayList<Stazione> stazioni){
        if(!stazioni.isEmpty()){
            if(getnDirezione()!=0){
                Log.d("DIREZIONE", "La direzione è presente ed è celoria");
                LatLng latLng = new LatLng(stazioni.get(0).getLat(), stazioni.get(0).getLog());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                Log.d("DIREZIONE", "La direzione è presente ed è rogoredo"+stazioni.get(stazioni.size()-1).getNome());
                LatLng latLng = new LatLng(stazioni.get(stazioni.size()-1).getLat(), stazioni.get(stazioni.size()-1).getLog());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    public void setPoint(LatLng latLng, String nome) {
        float zoomLevel = 11.0f; //This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Stazione " + nome)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

        );
    }

    public void drawMyLinea(int nLinea){
        this.zoomLevel=18.0f;
        Log.d("MAPPA", "sto ripulendo la mappa");

        if(googleMap!=null){
            googleMap.clear();

            Log.d("MAPPA", "sto iniziando a disegnare la mappa");
            for (int i=0; i<linee.getSize(); i++){
                Linea linea=linee.getLinea(i);
                Log.d("TAG", "start"+i);
                if(i==nLinea){
                    drawAllStops(linea, R.drawable.train_black, R.drawable.circle_black_light, Color.BLUE);
                }
            }
            Log.d("MAPPA", "ho disegnato la mappa");
        }
    }

    public int getnLinea(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        return sharedPreferences.getInt(LINEA, -1);
    }

    public int getnDirezione(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(SHARED_PREFS, 0);
        return sharedPreferences.getInt(DIREZIONE, -1);
    }
}