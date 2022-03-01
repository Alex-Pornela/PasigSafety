package com.capstone.pasigsafety.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.FragmentCrimeSpotBinding;
import com.capstone.pasigsafety.databinding.FragmentMainHomeBinding;
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
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class CrimeSpotFragment extends Fragment implements OnMapReadyCallback {

    private FragmentCrimeSpotBinding binding;
    private GoogleMap mGoogleMap;
    private static final String TAG = "MainHomeFragment";
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private Marker userLocationMarker;
    private boolean isTrafficEnable;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    LocationManager locationManager;
    private FirebaseFirestore fireDatabase;


    @SuppressLint("MissingPermission")
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCrimeSpotBinding.inflate( inflater, container, false );


        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( Objects.requireNonNull( getActivity() ) );
        Places.initialize( requireContext(), "AIzaSyCLy0pBjCXHYXWYBzi3y8gRsp5TcJa2Mbo" );
        placesClient = Places.createClient( requireContext() );
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( requireContext() );

        fireDatabase = FirebaseFirestore.getInstance();


        //checkGps();

        //search bar for places
        binding.searchBar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field List
                List<Place.Field> fieldList = Arrays.asList( Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME );

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder( AutocompleteActivityMode.OVERLAY
                        , fieldList ).build( requireContext() );
                //start activity result
                startActivityForResult( intent, 100 );
            }
        } );

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
        binding.currentLocation.setOnClickListener( searchLocation -> checkGps() );

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

    //Call Map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mGoogleMap = googleMap;

        showLocationInFirestore();

        try {
            boolean success = mGoogleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( getContext(), R.raw.maps_style ) );
            if (!success)
                Log.e( "MAP ERROR", "style parsing error" );
        } catch (Resources.NotFoundException e) {
            Log.e( "MAP_ERROR", e.getMessage() );
        }

        if (ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.
                PERMISSION_GRANTED) {
            enableUserLocation();
            //zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION )) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE );
            } else {
                ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE );
            }
        }

    }

    private void showLocationInFirestore() {
        /*
        CollectionReference spot = fireDatabase.collection( "CrimeSpot" );
        DocumentReference crime = spot.document( "robbery" );
        spot.get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {



                                GeoPoint geoPoint = snapshot.getGeoPoint( "location" );
                                double lat = geoPoint.getLatitude();
                                double lng = geoPoint.getLongitude();
                                LatLng latLng = new LatLng(lat, lng);

                                mGoogleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("Firestore Data")
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                            }
                        }
                    }

                } );

         */
/*
        CollectionReference spot = fireDatabase.collection( "CrimeSpot" );
        DocumentReference crime = spot.document( "robbery" );
        spot.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //spotGetData data = queryDocumentSnapshots.toObjects(spotGetData.class).spots;
                List<Spot> spots = queryDocumentSnapshots.toObjects(spotGetData.class).spot;

                //String stringGeoPoint = data.getLocation().get("position").toString();


                String[] afterSplitLoc = stringGeoPoint.split(" ");
                double lat = Double.parseDouble(afterSplitLoc[0]);
                double lng = Double.parseDouble(afterSplitLoc[1]);
                LatLng latLng = new LatLng(lat, lng);



                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Firestore Data")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            }
        } );

 */
/*
        CollectionReference spot = fireDatabase.collection( "CrimeSpot" );
        DocumentReference crime = spot.document( "robbery" );
        spot.get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {

                       // List<Spot> users = document.getList("users");

                        List<Map<String, Object>> users = (List<Map<String, Object>>) snapshot.get("position");

                        List<Spot> spots = snapshot.toObject( spotGetData.class).spots;

                        GeoPoint geoPoint = snapshot.getGeoPoint("position");
                        //spotGetData data = snapshot.toObject(spotGetData.class);

                        double lat = spots.get( spots.size() ).spotLocation.getLatitude();
                        double lng = spots.get( spots.size() ).spotLocation.getLongitude();
                        LatLng latLng = new LatLng(lat, lng);

                       // String stringGeoPoint = spots.get( s )getLocation().get("position").toString();


                       // String[] afterSplitLoc = stringGeoPoint.split(" ");
                      //  double lat = Double.parseDouble(afterSplitLoc[0]);
                       // double lng = Double.parseDouble(afterSplitLoc[1]);
                       // LatLng latLng = new LatLng(latitude,longitude);



                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Firestore Data")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }
        } );*/

    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
//            getLastLocation();
            checkSettingsAndStartLocationUpdates();
        } else {
            checkPermission();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void checkGps() {
        //LOCATION REQUEST
        locationRequest = LocationRequest.create();
        locationRequest.setInterval( 4000 );
        locationRequest.setFastestInterval( 2000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        //Check if gps enabled or not
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
                    zoomToUserLocation();

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

    //Check location runtime permission using dexter library
    private void checkPermission() {
        Dexter.withContext( requireContext() ).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                //Toast.makeText( requireContext(), "Permission Granted", Toast.LENGTH_SHORT ).show();

                if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mGoogleMap.setMyLocationEnabled( true );
                mGoogleMap.getUiSettings().setTiltGesturesEnabled( true );
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled( false );

            }

            //when user denied the permission go to settings to allow
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if (permissionDeniedResponse.isPermanentlyDenied()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( requireContext() );
                    builder.setTitle( "Permission Denied" )
                            .setMessage( "Permission to access access device location is permanently denied. You need to go to setting to allow the permission" )
                            .setNegativeButton( "Cancel", null )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                                    Uri uri = Uri.fromParts( "package", Objects.requireNonNull( getActivity() ).getPackageName(), "" );
                                    intent.setData( uri );
                                    startActivity( intent );
                                }
                            } )
                            .show();
                } else {
                    Toast.makeText( requireContext(), "Permission Denied", Toast.LENGTH_SHORT ).show();
                }
            }


            //continue asking permission if it is denied
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();

            }
        } ).check();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled( true );
        mGoogleMap.getUiSettings().setTiltGesturesEnabled( true );
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled( false );

    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );
                SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
                HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position( latLng );
                markerOptions.icon( bitmapDescriptorFromVector( getContext(), R.drawable.ic_round_location_on_24 ) );
                markerOptions.snippet( userDetails.get( SessionManager.KEY_FULLNAME ) );
                mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 18f ) );

                if (userLocationMarker != null) {
                    userLocationMarker.remove();
                }

                userLocationMarker = mGoogleMap.addMarker( markerOptions );
                userLocationMarker.setTag( 703 );
            }
        } );
    }

    //location request call back
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult( locationResult );
            Log.d( TAG, "onLocationResult: " + locationResult.getLastLocation() );
            if (mGoogleMap != null) {
                setUserLocationMarker( locationResult.getLastLocation() );
            }
        }
    };

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );
        SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();


        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position( latLng );
            markerOptions.icon( bitmapDescriptorFromVector( getContext(), R.drawable.ic_round_location_on_24 ) );
            markerOptions.snippet( userDetails.get( SessionManager.KEY_FULLNAME ) );
            mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( latLng, 18f ) );

            if (userLocationMarker != null) {
                userLocationMarker.remove();
            }

            userLocationMarker = mGoogleMap.addMarker( markerOptions );
            userLocationMarker.setTag( 703 );
        } else {
            //use the previously created marker
            userLocationMarker.setPosition( latLng );
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 18f ) );
        }

    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest( locationRequest ).build();
        SettingsClient client = LocationServices.getSettingsClient( requireContext() );

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings( request );
        locationSettingsResponseTask.addOnSuccessListener( new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                startLocationUpdates();
            }
        } );
        locationSettingsResponseTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult( getActivity(), 1001 );
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } );
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.getMainLooper() );
    }

    //stop location update
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates( locationCallback );
    }

    //For handling gps location and on selected place in autocomplete search bar
    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                zoomToUserLocation();

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText( requireContext(), "Cancelled GPS", Toast.LENGTH_SHORT ).show();
            }
            //For auto search place and also once place is selected

        } else if (requestCode == 100 & resultCode == RESULT_OK) {
            //Initialize place
            Place place = Autocomplete.getPlaceFromIntent( data );

            Log.i( "mytag", "Place found: " + place.getName() );
            LatLng latLngOfPlace = place.getLatLng();
            if (latLngOfPlace != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLngOfPlace, 18f );

                MarkerOptions markerOptions = new MarkerOptions()
                        .position( latLngOfPlace )
                        .title( place.getName() )
                        .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_AZURE ) );

                if (userLocationMarker != null) {
                    userLocationMarker.remove();
                }

                userLocationMarker = mGoogleMap.addMarker( markerOptions );
                userLocationMarker.setTag( 703 );
                mGoogleMap.animateCamera( cameraUpdate );

            }
            stopLocationUpdates();
        }
    }

    //Handling user permission
    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                //We can show a dialog that permission is not granted...
            }
        }
    }

    //For Custom google map Marker
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
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