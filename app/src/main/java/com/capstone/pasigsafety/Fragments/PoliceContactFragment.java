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
import android.graphics.Color;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.GooglePlaceModel;
import com.capstone.pasigsafety.Model.GoogleResponseModel;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.Utility.LoadingDialog;
import com.capstone.pasigsafety.Utility.PlaceModel;
import com.capstone.pasigsafety.WebServices.RetrofitAPI;
import com.capstone.pasigsafety.WebServices.RetrofitClient;
import com.capstone.pasigsafety.databinding.FragmentPoliceContactBinding;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PoliceContactFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private static final String TAG = "PoliceContactFragment";
    private FragmentPoliceContactBinding binding;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    Marker userLocationMarker;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location currentLocation;
    private FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;
    private int radius = 1000;
    private RetrofitAPI retrofitAPI;
    private List<GooglePlaceModel> googlePlaceModelList;
    private PlaceModel selectedPlace;


    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPoliceContactBinding.inflate( inflater, container, false );

        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( Objects.requireNonNull( getActivity() ) );
        Places.initialize( requireContext(), "AIzaSyCLy0pBjCXHYXWYBzi3y8gRsp5TcJa2Mbo" );
        placesClient = Places.createClient( requireContext() );
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( requireContext() );


        loadingDialog = new LoadingDialog( requireActivity() );
        retrofitAPI = RetrofitClient.getRetrofitClient().create( RetrofitAPI.class );
        googlePlaceModelList = new ArrayList<>();

        PlaceModel placeModel = new PlaceModel( 1, R.drawable.police_icon, "police", "police" );
        selectedPlace = placeModel;
        getPlaces( placeModel.getPlaceType() );


        checkGps();

       // showNearbyPoliceStation();


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




    private void checkGps() {
        //LOCATION REQUEST
        locationRequest = LocationRequest.create();
        locationRequest.setInterval( 10000 );
        locationRequest.setFastestInterval( 5000 );
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
        isLocationPermissionOk = true;

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
                mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 14.7f ) );

                if (userLocationMarker != null) {
                    userLocationMarker.remove();
                }

                userLocationMarker = mGoogleMap.addMarker( markerOptions );
                userLocationMarker.setTag( 703 );


            }
        } );
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



    //location request call back
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult( locationResult );
            Log.d( TAG, "onLocationResult: " + locationResult.getLastLocation() );
            if (mGoogleMap != null) {
                setUserLocationMarker( locationResult.getLastLocation() );

            }

            PlaceModel placeModel = new PlaceModel( 1, R.drawable.police_icon, "police", "police" );
            selectedPlace = placeModel;
            getPlaces( placeModel.getPlaceType() );

            getPlaces("police" );

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
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 14.7f ) );

            if (userLocationMarker != null) {
                userLocationMarker.remove();
            }

            userLocationMarker = mGoogleMap.addMarker( markerOptions );
            userLocationMarker.setTag( 703 );


        } else {
            //use the previously created marker
            userLocationMarker.setPosition( latLng );
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 14.7f) );
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


    //getting nearby police station
    private void getPlaces(String placeName) {
        isLocationPermissionOk = true;
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( requireContext() );
        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
                LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude() );


                if (isLocationPermissionOk) {
                     //loadingDialog.startLoading();
                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
                                + "&radius=" + radius + "&type=" + placeName + "&key=" +
                            getResources().getString( R.string.API_KEY );


                    if (currentLocation != null) {
                        retrofitAPI.getNearByPlaces( url ).enqueue( new Callback<GoogleResponseModel>() {
                            @Override
                            public void onResponse(@NonNull Call<GoogleResponseModel> call, Response<GoogleResponseModel> response) {

                                if (response.errorBody() == null) {
                                    if (response.body() != null) {
                                        if (response.body().getGooglePlaceModelList() != null && response.body().getGooglePlaceModelList().size() > 0) {



                                            googlePlaceModelList.clear();
                                            mGoogleMap.clear();
                                            for (int i = 0; i < response.body().getGooglePlaceModelList().size(); i++) {
                                                googlePlaceModelList.add( response.body().getGooglePlaceModelList().get( i ) );
                                                addMarker( response.body().getGooglePlaceModelList().get( i ), i );

                                                mGoogleMap.addCircle(new CircleOptions()
                                                        .center(latLng)
                                                        .radius(radius)
                                                        .strokeColor( Color.GREEN)
                                                        .strokeWidth(0f)
                                                        .fillColor(Color.parseColor("#1271CCE7")));


                                            }
                                        } else {
                                            mGoogleMap.clear();
                                            googlePlaceModelList.clear();

                                            //request for 5km radius then if we don't get any place add 1km in it and request again the process run in this way
                                            radius += 1000;
                                            getPlaces( placeName );
                                        }
                                    }

                                } else {
                                    Log.d( "TAG", "onResponse:" + response.errorBody() );
                                    Toast.makeText( requireContext(), "Error: " + response.errorBody(), Toast.LENGTH_SHORT ).show();
                                }

                                   //loadingDialog.stopLoading();

                            }

                            @Override
                            public void onFailure(Call<GoogleResponseModel> call, Throwable t) {

                                Log.d( "TAG", "onFailure" + t );
                                   loadingDialog.stopLoading();

                            }
                        } );
                    }
                }
            }

        } );

        /*
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;

         */








    }

    //add custom marker on nearby places
    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                        googlePlaceModel.getGeometry().getLocation().getLng()))
                .title(googlePlaceModel.getName())
                .snippet(googlePlaceModel.getVicinity());
        markerOptions.icon( bitmapDescriptorFromVector( getContext(),R.drawable.police_marker ));
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    private BitmapDescriptor getCustomIcon() {

        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        background.setTint(getResources().getColor(R.color.quantum_googred900, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




}