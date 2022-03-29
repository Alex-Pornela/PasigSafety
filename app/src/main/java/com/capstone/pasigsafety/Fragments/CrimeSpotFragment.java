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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.About;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class CrimeSpotFragment extends Fragment  {

    private FragmentCrimeSpotBinding binding;




    @SuppressLint("MissingPermission")
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCrimeSpotBinding.inflate( inflater, container, false );



        return new AboutPage(getContext())
                .isRTL(false)
                .addItem( aboutTitle() )
                .setDescription(" You don't have to be anxious about your safety; this application can help you overcome your wariness about going somewhere!\n" +
                        "\n" +
                        "Pasig Safety Application has a lot of features! When you log in, the application will request GPS permission. To ensure that the application knows your current location, you must click Allow. Then simply click the search button to quickly find your desired location!\n" +
                        "\n" +
                        "You will be taken directly to the main dashboard. A blue circle with a radius of 500m represents your current location on the map. Any crime within that range will be displayed. When you click the crime marker, it will show you the crime rates and how safe your location is.\n" +
                        "\n" +
                        "This application also displays marked crime spots and shows the user's nearest police contact location.\n" +
                        "\n" +
                        "Crime spots can be viewed by clicking the \"Crime Spot\" button.\n" +
                        "\n" +
                        "New crime spots with types of crime and user experience can be added by long pressing and holding on a spot on the map.\n" +
                        "\n" +
                        "The \"Police Contact\" button must be clicked to find the nearest police contact location. This app also shows the path of a police contact location from a given location.")
                .addItem( new Element().setTitle( "Carl Jeremiah Dela Cruz" ) )
                .addItem( new Element().setTitle( "Richie Grace Factor" ) )
                .addItem( new Element().setTitle( "Thomas Allyson Jumawan" ) )
                .addItem( new Element().setTitle( "Alex Pornela" ) )
                .addItem( connectUs() )
                .addEmail("pasigsafety@gmail.com")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(createCopyright())
                .create();


    }

    private Element connectUs() {
        Element connect = new Element();
        connect.setTitle( "Connect With Us" );
        connect.setGravity( Gravity.CENTER );

        return connect;
    }

    private Element createCopyright() {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by Pasig Safety", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity( Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( requireContext(),copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }

    private Element aboutTitle() {
        Element about = new Element();
        about.setTitle( "Developers" );
        about.setGravity( Gravity.CENTER );

        return about;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );


    }

}