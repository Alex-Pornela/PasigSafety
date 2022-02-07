package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.pasigsafety.Databases.CheckInternet;
import com.capstone.pasigsafety.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class ForgetPassword extends AppCompatActivity {

    //variables

    private ImageView screenIcon;
    private TextView title, description;
    private TextInputLayout phoneNumberTextField;
    private CountryCodePicker countryCodePicker;
    private Button nextBtn;
    private Animation animation;
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_forget_password );

        //hooks
        screenIcon = findViewById( R.id.forget_password_icon );
        title = findViewById( R.id.forget_password_title );
        description = findViewById( R.id.forget_password_description);
        countryCodePicker = findViewById( R.id.country_code_picker );
        nextBtn = findViewById( R.id.forget_password_next_btn );
        phoneNumberTextField = findViewById( R.id.forget_password_phone_number );
        progressBar = findViewById( R.id.progress_bar );

        //Animation Hook
        animation = AnimationUtils.loadAnimation(this, R.anim.slide_animation);

        //Set animation to all the elements
        screenIcon.setAnimation(animation);
        title.setAnimation(animation);
        description.setAnimation(animation);
        phoneNumberTextField.setAnimation(animation);
        countryCodePicker.setAnimation(animation);
        nextBtn.setAnimation(animation);

    }

    /*
    Call OTP screen
        and pass phone number for verification
     */

    public void verifyPhoneNumber(View view){


        // check internet connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected( this )) {
            showCustomDialog();

        }

        //validate username and password
        if (!validateLoginPhoneNumber()) {
            return;
        }
        progressBar.setVisibility( View.VISIBLE );


        //get data from fields
        String _phoneNumber = Objects.requireNonNull( phoneNumberTextField.getEditText() ).getText().toString().trim();

        if (_phoneNumber.charAt( 0 ) == '0') {
            _phoneNumber = _phoneNumber.substring( 1 );
        }

        String _completePhoneNumber = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNumber;


        //Check weather User exist or not in the database
        Query checkUser = FirebaseDatabase.getInstance().getReference( "Users" ).orderByChild( "phoneNo" ).equalTo( _completePhoneNumber );
        checkUser.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    phoneNumberTextField.setError( null );
                    phoneNumberTextField.setErrorEnabled( false );

                    Intent intent = new Intent( getApplicationContext(), VerifyOTP.class );
                    intent.putExtra( "phoneNo", _completePhoneNumber );
                    intent.putExtra("whatToDO", "updateData");
                    startActivity( intent );
                    finish();

                    progressBar.setVisibility( View.GONE );
                } else {
                    progressBar.setVisibility( View.GONE );
                    phoneNumberTextField.setError( "No such user exist" );
                    phoneNumberTextField.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );


    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder( ForgetPassword.this );
        builder.setMessage( "Please connect to the internet to proceed" )
                .setCancelable( false )
                .setPositiveButton( "Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( Settings.ACTION_WIFI_SETTINGS ) );
                    }
                } )
                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( getApplicationContext(), StartUpScreen.class ) );
                        finish();
                    }
                } );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }




    //function to validate phone number
    private boolean validateLoginPhoneNumber() {
        String _phoneNumber = Objects.requireNonNull( phoneNumberTextField.getEditText() ).getText().toString();
        if (_phoneNumber.isEmpty()) {
            phoneNumberTextField.setError("Field cannot be empty");
            phoneNumberTextField.requestFocus();
            return false;
        } else {
            phoneNumberTextField.setError(null);
            phoneNumberTextField.setErrorEnabled(false);
            return true;
        }
    }
}