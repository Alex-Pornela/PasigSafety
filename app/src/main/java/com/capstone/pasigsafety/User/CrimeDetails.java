package com.capstone.pasigsafety.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.capstone.pasigsafety.Adapter.Crime;
import com.capstone.pasigsafety.Admin.FireStoreData;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.ActivityCrimeDetailsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CrimeDetails extends AppCompatActivity {

    private ActivityCrimeDetailsBinding binding;
    private String userPosition;
    private DatabaseReference ref;
    private String brgy,monthBrgy,month,year;
    private long numbers;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );


        binding = ActivityCrimeDetailsBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        
        ref = FirebaseDatabase.getInstance().getReference();


        Intent i = getIntent();
        FireStoreData  data = (FireStoreData) i.getSerializableExtra("data");





        int distance = getIntent().getIntExtra( "distanceCrime", 0 );
        double lat = getIntent().getDoubleExtra( "userLatitude", 0 );
        double lng = getIntent().getDoubleExtra( "userLongitude", 0 );


        Geocoder gcd = new Geocoder(CrimeDetails.this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation( lat, lng, 1);
            userPosition = addresses.get( 0 ).getFeatureName() + " " +  addresses.get( 0 ).getThoroughfare() +  ", " + data.getBrgy() + ", " +  addresses.get( 0 ).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //String number = String.valueOf( crimeNumber );
        String crimeType = data.getItem();
        String placeCrime = data.getStreet();
        String date = data.getDate();
        String time = data.getTime();
        String crimeDistance = String.valueOf( distance );
        monthBrgy = data.getMonthBrgy();
        brgy = data.getBrgy();

        final Calendar calendar = Calendar.getInstance();
        String myFormat = "LLL d, yyyy";
        String format = "MMM";
        SimpleDateFormat sdf = new SimpleDateFormat( myFormat, Locale.US );
        SimpleDateFormat sdff = new SimpleDateFormat(format, Locale.US );

        try {
            calendar.setTime( Objects.requireNonNull( sdf.parse( date ) ) );
            month= sdff.format( calendar.getTime() );
            //month = String.valueOf( calendar.get( Calendar.MONTH ) );
            year = String.valueOf( calendar.get( Calendar.YEAR ) );
        } catch (ParseException e) {
            e.printStackTrace();
        }





        binding.monthRate.setText( month + " " + year );
        binding.userLocation.setText( userPosition );
        binding.crimeType.setText( crimeType );
        binding.placeOccurence.setText( placeCrime );
        binding.date.setText( date );
        binding.time.setText( time );
        binding.distance.setText( crimeDistance + " m away from you location" );


        binding.backArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CrimeDetails.this, UserDashboard.class );
                startActivity( intent );
                finish();
            }
        } );


        getCrimeRate(brgy,crimeType);



    }



    private void getCrimeRate(String brgy, String crimeType) {


        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        String currentMonth = sdf.format(new Date());


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query usersRef = rootRef.child("CrimeReport").orderByChild( "monthBrgy").equalTo( monthBrgy);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                long number = snapshot.getChildrenCount();

                numbers = number;

                setCrimeRange();

                setPrecautions(crimeType);


                //for crime rate
                if(numbers <=5){
                    //25% safe
                    startAnimationCounter( 0,25 );
                }
                else if (numbers>5 && numbers<=10){
                    //50% Moderately Safe
                    startAnimationCounter( 0,50 );
                }
                else if (numbers>10 && numbers<=15){
                    //75% Dangerous
                    startAnimationCounter( 0,75 );
                }
                else if (numbers>15 && numbers<=20){
                    //100% Extremely
                    startAnimationCounter( 0,100 );
                }







            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);



    }

    private void setPrecautions(String crimeType) {


        String[] robbery = getResources().getStringArray( R.array.robbery_precautions );
        String[] scam = getResources().getStringArray( R.array.scam_precautions );
        String[] gambling = getResources().getStringArray( R.array.gambling_precautions );
        String[] physical_injury = getResources().getStringArray( R.array.physical_injury_precautions);
        String[] theft = getResources().getStringArray( R.array.theft_precautions );
        String[] carnapping = getResources().getStringArray( R.array.carnapping_precautions );

        if(crimeType.equals( "Robbery" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, robbery);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }else if(crimeType.equals( "Scam" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, scam);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }else if(crimeType.equals( "Theft" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, theft);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }else if(crimeType.equals( "Gambling" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, gambling);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }else if(crimeType.equals( "CarNapping" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, carnapping);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }else if(crimeType.equals( "Physical Injury" )){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.custom_list_view, android.R.id.text1, scam);

            binding.precautions.setDividerHeight( 0 );
            binding.precautions.setAdapter(adapter);
        }


        /*ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.robbery_precautions, android.R.layout.activity_list_item);
        binding.precautions.setAdapter(aa);*/

        /*if(crimeType.equals( "Robbery" )){
            binding.precautions.setText( R.string.robberyPrecautions);
        }else if(crimeType.equals( "Physical Injury" )){
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }else if(crimeType.equals( "Scam" )) {
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }else if(crimeType.equals( "Gambling" )) {
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }else if(crimeType.equals( "Theft" )) {
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }else if(crimeType.equals( "CarNapping" )) {
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }*/
    }

    @SuppressLint("SetTextI18n")
    private void setCrimeRange() {


        String safe = "Safe";
        String moderate = "Moderately Safe";
        String dangerous = "Dangerous";
        String extreme = "Extremely Dangerous";

        if(numbers<=5){
            //25% safe
            binding.safetyRange.setText( safe );
        }
        else if(numbers<=10){
            //50%
            binding.safetyRange.setText( moderate  );
        }
        else if(numbers>10 && numbers<=15){
            //75%
            binding.safetyRange.setText(dangerous );
        }
        else if(numbers>15 && numbers<=20){
            //100%
            binding.safetyRange.setText(extreme);
        }
    }

    public void startAnimationCounter(int start_no, int end_no){
        ValueAnimator animator = ValueAnimator.ofInt( start_no,end_no );
        animator.setDuration( 5000 );
        animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                binding.percentage.setText( animation.getAnimatedValue().toString()+ "%" );
                binding.progressBar.setProgress( Integer.parseInt( animation.getAnimatedValue().toString() ) );
            }
        } );
        animator.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }



}