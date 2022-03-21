package com.capstone.pasigsafety.Admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.capstone.pasigsafety.Adapter.Crime;
import com.capstone.pasigsafety.Adapter.CrimeAdapter;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.EditUserProfile;
import com.capstone.pasigsafety.User.UserDashboard;
import com.capstone.pasigsafety.databinding.ActivityAddNewCrimeBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddNewCrimeActivity extends AppCompatActivity {

    ActivityAddNewCrimeBinding binding;
    private String crimeType;
    private String crimeIcon;
    private String icon;
    private int hour, minute;
    String item;
    private ArrayList<String> crimeTypes;
    private double latitude;
    private double longitude;
    String brgy,street,date,time,monthCrime;
    private ArrayAdapter<FireStoreData> adapter;
    private DatabaseReference databaseReference;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_new_crime );

        binding = ActivityAddNewCrimeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        latitude = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("lng", 0);

        LatLng latLng= new LatLng(latitude, longitude);



        binding.selectDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                binding.selectDate.requestFocus();
                binding.selectDate.setInputType( InputType.TYPE_NULL );

                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        calendar.set( Calendar.YEAR, year );
                        calendar.set( Calendar.MONTH, month );
                        calendar.set( Calendar.DAY_OF_MONTH, day );

                        String myFormat = "MMM d, yyyy"; // In which you need put here
                        String monthFormat = "MMM";
                        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );
                        SimpleDateFormat sddf = new SimpleDateFormat( monthFormat, Locale.US );
                        monthCrime= sddf.format( calendar.getTime() );

                        binding.selectDate.setText( sdf.format( calendar.getTime() ) );
                    }
                };
                new DatePickerDialog( AddNewCrimeActivity.this, onDateSetListener, calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ),
                        calendar.get( Calendar.DAY_OF_MONTH ) ).show();
            }
        } );

        binding.selectTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.selectTime.requestFocus();
                binding.selectTime.setInputType( InputType.TYPE_NULL );

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                        hour = selectedHour;
                        minute = selectedMinute;


                        String am_pm = "";

                        Calendar datetime = Calendar.getInstance();
                        datetime.set( Calendar.HOUR_OF_DAY, hour );
                        datetime.set( Calendar.MINUTE, minute );

                        if (datetime.get( Calendar.AM_PM ) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get( Calendar.AM_PM ) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get( Calendar.HOUR ) == 0) ? "12" : datetime.get( Calendar.HOUR ) + "";

                        binding.selectTime.setText( strHrsToShow + ":" + datetime.get( Calendar.MINUTE ) + " " + am_pm );
                    }
                };


                TimePickerDialog timePickerDialog = new TimePickerDialog( AddNewCrimeActivity.this, /*style,*/ onTimeSetListener, hour, minute, false );

                timePickerDialog.setTitle( "Select Time" );
                timePickerDialog.show();

            }
        } );


        binding.saveNewData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText( requireContext(), "type"+item, Toast.LENGTH_SHORT ).show();

                savedCrimeType(item,icon);
            }



        } );


        //Spinner Select CrimeType
        List<Crime> crimes = new ArrayList<>();

        String[] crimeTypes = getResources().getStringArray( R.array.crime_type );
        String[] crimeDesc = getResources().getStringArray( R.array.crime_desc );
        final String[] crimeIcons = getResources().getStringArray(
                R.array.crime_ic );

        for (int i = 0; i < crimeDesc.length; i++) {
            Crime crime = new Crime();
            crime.setCrimeType( crimeTypes[i] );
            crime.setCrimeDesc( crimeDesc[i] );
            crime.setImage( crimeIcons[i] );
            crimes.add( crime );
        }
        final CrimeAdapter adapter = new CrimeAdapter( AddNewCrimeActivity.this, crimes );
        binding.crimeCategory.setAdapter( adapter );
        binding.crimeCategory.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                crimeType = ((Crime) adapter.getItem( position )).getCrimeType();
                crimeIcon = crimeIcons[position];


                item = ((Crime) adapter.getItem( position )).getCrimeType();
                icon = ((Crime) adapter.getItem( position )).getImage();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        binding.backArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( AddNewCrimeActivity.this, UserDashboard.class );
                startActivity( intent );
                finish();
            }
        } );


    }


    private void savedCrimeType(String item, String icon) {

        if (!validateLoginPassword()) {
            return;
        }


        brgy = binding.brgyName.getText().toString();
        street = binding.streetName.getText().toString();
        date = binding.selectDate.getText().toString();
        time = binding.selectTime.getText().toString();
        String crimeIcon = icon;
        String monthBrgy = monthCrime + brgy;

       Geocoder gcd = new Geocoder(AddNewCrimeActivity.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            String street2 = addresses.get(0).getThoroughfare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> crimetype = new HashMap<>();
        FireStoreData data = new FireStoreData( brgy, street, date, time, latitude, longitude, item,icon+"_marker", crimeIcon, monthBrgy);
        //crimetype.put(data);




        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child( "CrimeReport" ).push().setValue( data ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                dialog = new Dialog( AddNewCrimeActivity.this );

                dialog.setContentView( R.layout.crime_add_layout );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );


                Button okDialogBtn = dialog.findViewById( R.id.crime_ok_btn );


                okDialogBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( AddNewCrimeActivity.this, UserDashboard.class );
                        startActivity( intent );
                        finish();
                    }
                } );


                dialog.show();
            }
        } );
    }

    private boolean validateLoginPassword() {

        brgy = binding.brgyName.getText().toString();
        street = binding.streetName.getText().toString();
        date = binding.selectDate.getText().toString();
        time = binding.selectTime.getText().toString();


        if (brgy.isEmpty() || street.isEmpty() || date.isEmpty() || time.isEmpty()) {
            binding.brgyName.setError( "Field cannot be empty" );
            binding.brgyName.requestFocus();
            binding.streetName.setError( "Field cannot be empty" );
            binding.streetName.requestFocus();
            binding.selectDate.setError( "Field cannot be empty" );
            binding.selectDate.requestFocus();
            binding.selectTime.setError( "Field cannot be empty" );
            binding.selectTime.requestFocus();
            return false;
        } else {
            binding.brgyName.setError( null);
            binding.brgyName.setEnabled(false);
            binding.streetName.setError( null );
            binding.streetName.setEnabled(false);
            binding.selectDate.setError( null );
            binding.selectDate.setEnabled(false);
            binding.selectTime.setError( null );
            binding.selectTime.setEnabled(false);
            return true;
        }
    }


}