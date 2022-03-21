package com.capstone.pasigsafety.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrimeDetails extends AppCompatActivity {

    private ActivityCrimeDetailsBinding binding;

    String userPosition;
    DatabaseReference ref;
    String brgy;
    long numbers;





    //private TextView count, place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //setContentView( R.layout.activity_crime_details );

        binding = ActivityCrimeDetailsBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
        
        ref = FirebaseDatabase.getInstance().getReference();

        /*count = findViewById(R.id.number);
        place = findViewById(R.id.place);*/


        Intent i = getIntent();
        FireStoreData  data = (FireStoreData) i.getSerializableExtra("data");


        //int crimeNumber = getIntent().getIntExtra( "numberCrime", 0 );
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
        brgy = data.getMonthBrgy();



        binding.userLocation.setText( userPosition );
        binding.crimeType.setText( crimeType );
        binding.placeOccurence.setText( placeCrime );
        binding.date.setText( date );
        binding.time.setText( time );
        binding.distance.setText( crimeDistance + " away from you location" );
        //binding.crimeNumber.setText( number );


        binding.backArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CrimeDetails.this, UserDashboard.class );
                startActivity( intent );
                finish();
            }
        } );

        setCrimeRange( numbers );

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
        
        if(data.getBrgy().equals( "Palatiw" )){
            getCrimeRate();
        }





    }

    private void getCrimeRate() {

     /*   ArrayList<String> list = new ArrayList<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference robberyRef = db.child( "CrimeReport" );
        robberyRef.get().addOnCompleteListener( new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        FireStoreData data = ds.getValue( FireStoreData.class );
                        String palatiw = "Palatiw";

                        assert data != null;
                        list.add( String.valueOf( data.getBrgy().equals( "Palatiw" ) && data.getItem().equals( "Robbery" ) ) );

                        String number = String.valueOf( list.size() );

                       binding.crimeNumber.setText( number );

                    }
                }
            }
        } );*/

        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        String currentMonth = sdf.format(new Date());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        Query usersRef = rootRef.child("CrimeReport").orderByChild( "monthBrgy").equalTo( brgy);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                numbers = snapshot.getChildrenCount();
                String number = String.valueOf( numbers );



            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);

       /* ref.child( "CrimeReport" )
                .orderByChild( "brgy" ).equalTo( "Palatiw" ).get().addOnSuccessListener( new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        long numbers = dataSnapshot.getChildrenCount();
                        String number = String.valueOf( numbers );

                        binding.crimeNumber.setText( number );

                    }
                } );*/

    }

    private void setPrecautions(String crimeType) {

        if(crimeType.equals( "Robbery" )){
            binding.precautions.setText( R.string.robberyPrecautions);
        }else if(crimeType.equals( "Physical Injury" )){
            binding.precautions.setText( R.string.physicalInjuryPrecautions );
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCrimeRange(long crimeNumber) {

        String safe = "Safe";
        String moderate = "Moderately Safe";
        String dangerous = "Dangerous";
        String extreme = "Extremely Dangerous";

        if(numbers<=5){
            //25% safe
            binding.safetyRange.setText( safe );
        }
        else if(numbers>5 && numbers<=10){
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