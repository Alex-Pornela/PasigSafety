package com.capstone.pasigsafety.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.Permission.AppPermissions;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.FragmentHomeBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.model.AutocompletePrediction;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private FragmentHomeBinding binding;
    private GoogleMap mGoogleMap;
    private boolean  isTrafficEnable;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    private Marker currentMarker;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate the layout
        binding = FragmentHomeBinding.inflate( inflater, container, false );

        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( getActivity() );
        Places.initialize( requireContext(), "AIzaSyCLy0pBjCXHYXWYBzi3y8gRsp5TcJa2Mbo" );
        placesClient = Places.createClient( requireContext() );
        AutocompleteSessionToken.newInstance();

        checkPermission();

        //Map type with pop-up menu
        binding.btnMapType.setOnClickListener( view -> {
            PopupMenu popupMenu = new PopupMenu( requireContext(), view );
            popupMenu.getMenuInflater().inflate( R.menu.map_type_menu, popupMenu.getMenu() );


            popupMenu.setOnMenuItemClickListener( item -> {
                switch (item.getItemId()) {
                    case R.id.btnNormal:
                        mGoogleMap.setMapType( GoogleMap.MAP_TYPE_NORMAL );
                        break;

                    case R.id.btnSatellite:
                        mGoogleMap.setMapType( GoogleMap.MAP_TYPE_SATELLITE );
                        break;

                    case R.id.btnTerrain:
                        mGoogleMap.setMapType( GoogleMap.MAP_TYPE_TERRAIN );
                        break;
                }
                return true;
            } );

            popupMenu.show();
        } );

        //show traffic lines on the map
        binding.enableTraffic.setOnClickListener( view -> {

            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled( false );
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled( true );
                    isTrafficEnable = true;
                }
            }

        } );

        // user location button
        binding.currentLocation.setOnClickListener( searchLocation -> CheckGps() );

        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );

        //sync map in layout
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.homeMap );
        assert mapFragment != null;
        mapFragment.getMapAsync( this );

    }


    //calling map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled( true );
        mGoogleMap.getUiSettings().setTiltGesturesEnabled( true );

    }

    //Check runtime permission using dexter library
    private void checkPermission() {
        Dexter.withContext( requireContext() ).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                Toast.makeText( requireContext(), "Permission Granted", Toast.LENGTH_SHORT ).show();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                Uri uri = Uri.fromParts( "package", Objects.requireNonNull( getActivity() ).getPackageName(), "" );
                intent.setData( uri );
                startActivity( intent );
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();

            }
        } ).check();
    }


    //checking the location and gps if enabled or not
    private void CheckGps() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
        locationRequest.setInterval( 10000 );
        locationRequest.setFastestInterval( 5000 );

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest( locationRequest )
                .setAlwaysShow( true );

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient( Objects.requireNonNull( getContext() ) )
                .checkLocationSettings( builder.build() );

        locationSettingsResponseTask.addOnCompleteListener( new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult( ApiException.class );
                    getDeviceLocation();

                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult( Objects.requireNonNull( getActivity() ), 101 );
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText( requireContext(), "Settings not available", Toast.LENGTH_SHORT ).show();
                    }
                }

            }
        } );
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText( requireContext(), "Cancelled GPS", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            currentLocation = task.getResult();
                            if (currentLocation != null) {
                                SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
                                HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

                                LatLng latLng = new LatLng( currentLocation.getLatitude(), currentLocation.getLongitude() );
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 18 );

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position( latLng )
                                        .title( "Current Location" )
                                        .icon( bitmapDescriptorFromVector( getContext(),R.drawable.ic_round_location_on_24 ) )
                                        .snippet( userDetails.get( SessionManager.KEY_FULLNAME ) );


                                if (currentMarker != null) {
                                    currentMarker.remove();
                                }

                                currentMarker = mGoogleMap.addMarker( markerOptions );
                                currentMarker.setTag( 703 );
                                mGoogleMap.animateCamera( cameraUpdate );

                            } else {
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval( 10000 );
                                locationRequest.setFastestInterval( 5000 );
                                locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(@NonNull LocationResult locationResult) {
                                        super.onLocationResult( locationResult );

                                        SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
                                        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

                                        if (locationResult == null) {
                                            return;
                                        }
                                        currentLocation = locationResult.getLastLocation();

                                        LatLng latLng = new LatLng( currentLocation.getLatitude(), currentLocation.getLongitude() );
                                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 18 );

                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position( latLng )
                                                .title( "Current Location" )
                                                .icon( bitmapDescriptorFromVector( getContext(),R.drawable.ic_round_location_on_24 ))
                                                .snippet( userDetails.get( SessionManager.KEY_FULLNAME ) );


                                        if (currentMarker != null) {
                                            currentMarker.remove();
                                        }

                                        currentMarker = mGoogleMap.addMarker( markerOptions );
                                        currentMarker.setTag( 703 );
                                        mGoogleMap.animateCamera( cameraUpdate );

                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                    }
                                };
                                if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.getMainLooper() );
                            }
                        }else{
                            Toast.makeText( requireContext(), "Unable to get last location", Toast.LENGTH_SHORT ).show();
                        }

                    }
                } );
    }

    //For Custom google map Marker
    private BitmapDescriptor bitmapDescriptorFromVector (Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable( context, vectorResId );
        vectorDrawable.setBounds( 0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight() );
        Bitmap bitmap = Bitmap.createBitmap( vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( bitmap );
        vectorDrawable.draw( canvas );
        return BitmapDescriptorFactory.fromBitmap( bitmap );
    }


}


