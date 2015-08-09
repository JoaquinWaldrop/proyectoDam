package com.holamundo.gabocst.holamundo.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener{

    private final Context ctx;
    double latitud, longitud;
    Location location;
    boolean gpsActivo;
    LocationManager locationManager;

    public LocationService() {
        super();
        this.ctx = this.getApplicationContext();
    }
    public LocationService(Context c){
        super();
        this.ctx = c;
        getLocation();
    }

    public void mostrar(Context x){
        Toast.makeText(x, "Coordenadas: "+latitud+", "+longitud, Toast.LENGTH_LONG).show();
    }

    public void getLocation(){
        try{
            locationManager = (LocationManager)this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(gpsActivo){
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000*30, 10, this);
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            latitud = location.getLatitude();
            longitud = location.getLongitude();

        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
