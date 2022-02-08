package com.capstone.pasigsafety.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.Common.LoginSignup.ForgetPassword;
import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Common.LoginSignup.SetNewPassword;
import com.capstone.pasigsafety.Common.LoginSignup.VerifyOTP;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.Fragments.CrimeSpotFragment;
import com.capstone.pasigsafety.Fragments.HomeFragment;
import com.capstone.pasigsafety.Fragments.PoliceContactFragment;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.ChangePassword;
import com.capstone.pasigsafety.databinding.ActivityDashboardBinding;
import com.capstone.pasigsafety.databinding.FragmentCrimeSpotBinding;
import com.capstone.pasigsafety.databinding.FragmentHomeBinding;
import com.capstone.pasigsafety.databinding.NavDrawerLayoutBinding;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.model.AutocompletePrediction;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //Variables for google maps
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
    private FloatingActionButton btnMapType,trafficBtn,locationBtn;
    private FragmentCrimeSpotBinding binding;

    //Variables for navigation drawer
    private MaterialSearchBar materialSearchBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );



        //maps hooks
        btnMapType = findViewById( R.id.btnMapType );
        trafficBtn = findViewById( R.id.enableTraffic );
        locationBtn = findViewById( R.id.currentLocation );

        //Menu Hooks
        drawerLayout = findViewById( R.id.drawer_layout );
        navigationView = findViewById( R.id.navigation_view );
        materialSearchBar = findViewById( R.id.searchBar );

        navigationDrawer();

        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this );
        Places.initialize( this, "AIzaSyCLy0pBjCXHYXWYBzi3y8gRsp5TcJa2Mbo" );
        placesClient = Places.createClient( this );
        AutocompleteSessionToken.newInstance();

        //check first runtime permission
        checkPermission();


        //sync map in layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.homeMap );
        assert mapFragment != null;
        mapFragment.getMapAsync( this );


        //Map type with pop-up menu
        btnMapType.setOnClickListener( view -> {
            PopupMenu popupMenu = new PopupMenu( Dashboard.this, view );
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
        trafficBtn.setOnClickListener( view -> {

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
        locationBtn.setOnClickListener( searchLocation -> CheckGps() );




    }




    //Check location runtime permission using dexter library
    private void checkPermission() {
        Dexter.withContext( this ).withPermission( Manifest.permission.ACCESS_FINE_LOCATION ).withListener( new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                Toast.makeText( Dashboard.this, "Permission Granted", Toast.LENGTH_SHORT ).show();

            }

            //when user denied the permission go to settings to allow
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                if(permissionDeniedResponse.isPermanentlyDenied()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder( Dashboard.this );
                    builder.setTitle( "Permission Denied" )
                            .setMessage( "Permission to access access device location is permanently denied. You need to go to setting to allow the permission" )
                            .setNegativeButton( "Cancel", null )
                            .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
                                    Uri uri = Uri.fromParts( "package", getPackageName(), "" );
                                    intent.setData( uri );
                                    startActivity( intent );
                                }
                            } )
                            .show();
                }else{
                    Toast.makeText(Dashboard.this , "Permission Denied", Toast.LENGTH_SHORT ).show();
                }
            }



            //continue asking permission if it is denied
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();

            }
        } ).check();
    }

    //Navigation Drawer functions
    private void navigationDrawer() {

        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener( this );
        navigationView.setCheckedItem( R.id.nav_home );

        //menu icon in searchbar to open navigation drawer
        materialSearchBar.setOnSearchActionListener( new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    if (drawerLayout.isDrawerVisible( GravityCompat.START ))
                        drawerLayout.closeDrawer( GravityCompat.START );
                    else drawerLayout.openDrawer( GravityCompat.START );


                }
            }


        } );


    }

    //Item on Menu when clicked
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked( true );
        drawerLayout.closeDrawers();


        if (id == R.id.logout) {
            SessionManager sessionManager = new SessionManager( Dashboard.this, SessionManager.SESSION_USERSESSION );
            sessionManager.logoutUserFromSession();
            startActivity( new Intent( Dashboard.this, Login.class ) );
            finish();
        } else if (id == R.id.change_password) {
            SessionManager sessionManager = new SessionManager( this, SessionManager.SESSION_USERSESSION );
            HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();
            String phoneNumber = userDetails.get( SessionManager.KEY_PHONENUMBER );

            Intent intent = new Intent( getApplicationContext(), ChangePassword.class );
            intent.putExtra( "phoneNo", phoneNumber );
            startActivity( intent );
            finish();
        } else if(id == R.id.nav_crime_spot){
            getSupportFragmentManager().beginTransaction().replace( R.id.parent_container, new CrimeSpotFragment()).commit();
            materialSearchBar.setVisibility( View.GONE );
            locationBtn.setVisibility( View.GONE );
            trafficBtn.setVisibility( View.GONE );
            btnMapType.setVisibility( View.GONE );
        }else if (id == R.id.nav_home) {
            startActivity( new Intent( Dashboard.this, Dashboard.class ) );
            finish();
        }





        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );

        return true;

    }




    //backpress for navigation drawer
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen( GravityCompat.START ))
            drawerLayout.closeDrawer( GravityCompat.START );
        else {
            super.onBackPressed();
        }

    }

    //call Google Maps
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled( true );
        mGoogleMap.getUiSettings().setTiltGesturesEnabled( true );

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

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient( this )
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
                            resolvableApiException.startResolutionForResult( Dashboard.this, 101 );
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText( Dashboard.this, "Settings not available", Toast.LENGTH_SHORT ).show();
                    }
                }

            }
        } );
    }

    //result for gps request enable
    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText( Dashboard.this, "Cancelled GPS", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    //get Current Location and move the camera
    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener( new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()) {
                            currentLocation = task.getResult();
                            if (currentLocation != null) {
                                SessionManager sessionManager = new SessionManager( Dashboard.this, SessionManager.SESSION_USERSESSION );
                                HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

                                LatLng latLng = new LatLng( currentLocation.getLatitude(), currentLocation.getLongitude() );
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( latLng, 18 );

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position( latLng )
                                        .title( "Current Location" )
                                        .icon( bitmapDescriptorFromVector( Dashboard.this,R.drawable.ic_round_location_on_24 ) )
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

                                        SessionManager sessionManager = new SessionManager( Dashboard.this, SessionManager.SESSION_USERSESSION );
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
                                                .icon( bitmapDescriptorFromVector( Dashboard.this,R.drawable.ic_round_location_on_24 ))
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
                                if (ActivityCompat.checkSelfPermission( Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission( Dashboard.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                fusedLocationProviderClient.requestLocationUpdates( locationRequest, locationCallback, Looper.getMainLooper() );
                            }
                        }else{
                            Toast.makeText( Dashboard.this, "Unable to get last location", Toast.LENGTH_SHORT ).show();
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
