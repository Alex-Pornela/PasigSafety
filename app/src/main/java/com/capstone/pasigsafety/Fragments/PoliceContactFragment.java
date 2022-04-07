package com.capstone.pasigsafety.Fragments;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.Adapter.Crime;
import com.capstone.pasigsafety.Admin.AddNewCrimeActivity;
import com.capstone.pasigsafety.Admin.FireStoreData;
import com.capstone.pasigsafety.Admin.PoliceStationData;
import com.capstone.pasigsafety.Common.LoadingDialog;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.FragmentMainHomeBinding;
import com.capstone.pasigsafety.databinding.FragmentPoliceContactBinding;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class PoliceContactFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, RoutingListener {

    private FragmentPoliceContactBinding binding;
    private GoogleMap mGoogleMap;
    private static final String TAG = "PoliceContactFragment";
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    private Marker userLocationMarker;
    private Circle userLocationCircle;
    private boolean isTrafficEnable;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    Dialog dialog;
    private final int radius = 2000;
    private LoadingDialog loadingDialog;
    private Context context;
    private List<Crime> crimes;
    private LocationManager mLocationManager;
    LocationListener mLocationListeners;
    private List<Polyline> polylines;




    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPoliceContactBinding.inflate( inflater, container, false );


        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( Objects.requireNonNull( getActivity() ) );
        Places.initialize( requireContext(), "AIzaSyAfbGP14EEyceB__hkg5zbOrv5m3iF9vbY" );
        placesClient = Places.createClient( requireContext() );
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( requireContext() );


        loadingDialog = new LoadingDialog( requireActivity() );

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

                    case R.id.btnHybrid:
                        mGoogleMap.setMapType( GoogleMap.MAP_TYPE_HYBRID );
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


        binding.hotlines.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog( requireContext() );

                dialog.setContentView( R.layout.hotline_numbers_layout );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                ImageView closeDialog = dialog.findViewById( R.id.dialog_close );
               /* Button okDialogBtn = dialog.findViewById( R.id.dialog_ok_btn );*/

                closeDialog.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );

             /*   okDialogBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );*/


                dialog.show();

            }

        } );




        return binding.getRoot();

    }


    public boolean isAttachedToActivity() {
        boolean attached = isVisible() && getActivity() != null;
        return attached;
    }


    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );


        //sync map in layout
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById( R.id.homeMap );
        assert mapFragment != null;
        mapFragment.getMapAsync( this );



    }


    //Call Map
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {



        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLongClickListener( this );



        checkGps();
        //method to find nearby crime spot
        NearbyCrime();


        try {
            boolean success = mGoogleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( Objects.requireNonNull( getContext() ), R.raw.maps_style ) );
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
            if (ActivityCompat.shouldShowRequestPermissionRationale( Objects.requireNonNull( getActivity() ), Manifest.permission.ACCESS_FINE_LOCATION )) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE );
            } else {
                ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE );
            }
        }


    }


    @SuppressLint("PotentialBehaviorOverride")
    private void NearbyCrime() {

        loadingDialog.startLoading();

        SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();


        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference( "Users" ).child( userDetails.get( SessionManager.KEY_PHONENUMBER ) );

        GeoFire geoFire = new GeoFire(reference);

        reference.child( "myLocation" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                DatabaseReference robberyRef = db.child("Police Station");
                robberyRef.get().addOnCompleteListener( new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            DataSnapshot snapshot = task.getResult();
                            for (DataSnapshot ds : snapshot.getChildren()){




                                PoliceStationData data = ds.getValue(PoliceStationData.class);

                                Map<String,Object> places = new HashMap<>();


                                String name = data.getName();
                                String address = data.getAddress();
                                String number = data.getNumber();
                                double latitude = data.getLatitude();
                                double longitude = data.getLongitude();
                                String healthCenterIcon = data.getHealthIcon();

                                PoliceStationData details = new PoliceStationData(name,number,latitude,longitude,address,healthCenterIcon);

                                GeoQuery geoQuery = geoFire.queryAtLocation( new GeoLocation( latitude,longitude ),2 );


                                HashMap<String, Marker> markers = new HashMap<>();


                                geoQuery.addGeoQueryEventListener( new GeoQueryEventListener() {
                                    @Override
                                    public void onKeyEntered(String key, GeoLocation location) {



                                        if (isAttachedToActivity()){



                                            //get string icon using filename
                                            int resourceID = getResources().getIdentifier(
                                                    data.getHealthIcon(), "drawable",
                                                    getActivity().getPackageName() );

                                            //bitmapDescriptor for marker using filename
                                            BitmapDescriptor police_icon = BitmapDescriptorFactory.fromResource( resourceID );

                                            LatLng latLng = new LatLng( latitude, longitude );


                                            Marker marker = mGoogleMap.addMarker( new MarkerOptions().position( latLng )
                                                    .icon( police_icon ) );
                                            markers.put( key, marker );


                                            //detailMarkerMap.put(marker,detail);
                                            marker.setTag( details );


                                        }


                                        if(mGoogleMap != null){

                                            mGoogleMap.setInfoWindowAdapter( new GoogleMap.InfoWindowAdapter() {



                                                @Nullable
                                                @Override
                                                public View getInfoContents(@NonNull Marker marker) {



                                                    return null;
                                                }

                                                @Nullable
                                                @Override
                                                public View getInfoWindow(@NonNull Marker marker) {





                                                    marker.getTitle();


                                                    View view = getLayoutInflater().inflate(
                                                            R.layout.police_info_window, null);

                                                    TextView stationName = view.findViewById( R.id.station_name );
                                                    TextView stationDistance = view.findViewById( R.id.station_distance );
                                                    ImageView healthIcon =  view.findViewById( R.id.crime_ic );


                                                    PoliceStationData adata = (PoliceStationData) marker.getTag();

                                                    Location stationLocation = new Location("");
                                                    stationLocation.setLatitude( adata.getLatitude() );
                                                    stationLocation.setLongitude( adata.getLongitude() );

                                                    Location userLocation = new Location("");
                                                    userLocation.setLatitude( location.latitude );
                                                    userLocation.setLongitude( location.longitude );

                                                    LatLng userLoc = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                                                    LatLng stationLoc = new LatLng(stationLocation.getLatitude(), stationLocation.getLongitude());



                                                    double distance = (userLocation.distanceTo(stationLocation));
                                                    int i = (int) distance;
                                                    String UserToCrime = Integer.toString(i);

                                                    stationName.setText( adata.getName() );
                                                    stationDistance.setText( UserToCrime + "m away");


                                                    Routing routing = new Routing.Builder()
                                                            .travelMode( AbstractRouting.TravelMode.WALKING)
                                                            .withListener(PoliceContactFragment.this)
                                                            .alternativeRoutes(true)
                                                            .waypoints(userLoc, stationLoc)
                                                            .key("AIzaSyAfbGP14EEyceB__hkg5zbOrv5m3iF9vbY")  //also define your api key here.
                                                            .build();
                                                    routing.execute();

                                                    if(adata.getHealthIcon().equals( "health_icon" )){
                                                        int resourceID = getResources().getIdentifier(
                                                                "health_icon_marker", "drawable",
                                                                getActivity().getPackageName() );
                                                        healthIcon.setImageResource( resourceID );
                                                    }




                                                    return view;
                                                }
                                            } );
                                        }else {
                                            Toast.makeText( requireContext(), "No nearby crime incident found", Toast.LENGTH_SHORT ).show();
                                        }

                                    }

                                    @Override
                                    public void onKeyExited(String key) {

                                        Marker marker = markers.get(key);
                                        if (marker != null) {
                                            marker.remove();
                                            markers.remove(key);
                                        }

                                    }

                                    @Override
                                    public void onKeyMoved(String key, GeoLocation location) {

                                    }

                                    @Override
                                    public void onGeoQueryReady() {

                                    }

                                    @Override
                                    public void onGeoQueryError(DatabaseError error) {

                                    }
                                } );

                                loadingDialog.stopLoading();



                            }

                        }
                    }
                } );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }




    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
//           getLastKnownLocation();
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

                currentLocation = location;


/*
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position( latLng );
                markerOptions.icon( bitmapDescriptorFromVector( getContext(), R.drawable.ic_round_location_on_24 ) );
                markerOptions.snippet( userDetails.get( SessionManager.KEY_FULLNAME ) );*/
                mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 14 ) );
                /*mGoogleMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .strokeColor( Color.GREEN)
                        .strokeWidth(0f)
                        .fillColor(Color.parseColor("#5B71CCE7")));*/

                if (userLocationMarker != null) {
                    userLocationMarker.remove();
                }

                /*userLocationMarker = mGoogleMap.addMarker( markerOptions );*/
                //  userLocationMarker.setTag( 703 );
            }
        } );
    }

    //location request call back
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult( locationResult );
            Log.d( TAG, "onLocationResult: " + locationResult.getLastLocation() );


            SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
            HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();
            HashMap<String, Object> userLocation = new HashMap<>();

            FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
            DatabaseReference reference = rootNode.getReference( "Users" ).child( userDetails.get( SessionManager.KEY_PHONENUMBER ) );
            GeoFire geoFire = new GeoFire(reference);



            //saving position of the user and update realtime
            double latitude = locationResult.getLastLocation().getLatitude();
            double longitude = locationResult.getLastLocation().getLongitude();

            geoFire.setLocation("myLocation", new GeoLocation(latitude,longitude));



            if (mGoogleMap != null) {
                setUserLocationCircle(locationResult.getLastLocation());
            }


        }
    };


    private void setUserLocationCircle(Location location) {

        LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );

        if(userLocationCircle == null){

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius( radius );
            circleOptions.strokeColor( Color.GREEN );
            circleOptions.strokeWidth( 0f );
            circleOptions.fillColor( Color.parseColor( "#202196F3" ) );


            if(userLocationCircle != null){
                userLocationMarker.remove();
            }

            userLocationCircle = mGoogleMap.addCircle(circleOptions);
            userLocationCircle.setTag( 703 );


        }else{
            userLocationCircle.setCenter(latLng);
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
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLngOfPlace, 15f );

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


    public void onMapLongClick(@NonNull LatLng latLng) {

        SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();



        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference( "Users" );
        Query checkUser = reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) );

        checkUser.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("userRoles").getValue(String.class).equals("user")) {

                    Toast.makeText( requireContext(), "For admin user only", Toast.LENGTH_SHORT ).show();
                }else{

                    Intent intent = new Intent( requireContext(), AddNewCrimeActivity.class );
                    intent.putExtra( "lat", latLng.latitude );
                    intent.putExtra( "lng", latLng.longitude );
                    startActivity( intent );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        } );



    }


    @Override
    public void onRoutingFailure(RouteException e) {

        if (e != null) {
            Toast.makeText( requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG ).show();
        } else {
            Toast.makeText( requireContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    //If Route finding success
    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {

        if(polylines!=null) {
           clearPolyline();
        }



        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < arrayList.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color( getResources().getColor( R.color.colorPrimary ) );
                polyOptions.width( 7 );
                polyOptions.addAll( arrayList.get( shortestRouteIndex ).getPoints() );
                Polyline polyline = mGoogleMap.addPolyline( polyOptions );
                polylineStartLatLng = polyline.getPoints().get( 0 );
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get( k - 1 );
                polylines.add( polyline );

            }

        }


    }

    @Override
    public void onRoutingCancelled() {

    }

    private void clearPolyline(){

        for(Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
    }
}