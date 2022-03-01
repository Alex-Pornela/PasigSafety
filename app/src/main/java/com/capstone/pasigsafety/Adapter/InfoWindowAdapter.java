package com.capstone.pasigsafety.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.pasigsafety.databinding.InfoWindowLayoutBinding;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private InfoWindowLayoutBinding binding;
    private Location location;
    private Context context;

    public InfoWindowAdapter(Location location, Context context) {

        this.location = location;
        this.context = context;

        binding = InfoWindowLayoutBinding.inflate( LayoutInflater.from( context ),null,false );
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {

        binding.txtLocationName.setText( marker.getTitle() );

        //used google map util class to calculate distance and time

        double distance = SphericalUtil.computeDistanceBetween( new LatLng( location.getLatitude(),location.getLongitude() ),
                marker.getPosition());

        if(distance > 1000){
            double kilometers = distance/1000;

            binding.txtLocationDistance.setText( distance+"KM" );
        }else{
            //int meters = (int)distance;
            binding.txtLocationDistance.setText( distance+"Meters" );
        }

        float speed = location.getSpeed();

        if(speed>0){
            double time = distance/speed;
            binding.txtLocationTime.setText( time+"sec" );
        }else{
            binding.txtLocationTime.setText( "N/A" );
        }
        return binding.getRoot();
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
