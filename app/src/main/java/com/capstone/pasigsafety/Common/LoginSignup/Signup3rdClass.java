package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.capstone.pasigsafety.R;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class Signup3rdClass extends AppCompatActivity {

    Button next, login;
    ImageView backBtn;
    TextInputLayout phoneNumber;
    ScrollView scrollView;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_signup3rd_class );


        //Hooks
        backBtn = findViewById( R.id.signup_back_button );
        next = findViewById( R.id.signup_next_btn );
        scrollView = findViewById( R.id.signup_3rd_screen_scroll_view );

        //hooks for getting data
        phoneNumber = findViewById( R.id.signup_phone_number );
        countryCodePicker = findViewById( R.id.country_code_picker );

    }

    //method to call next sign up screen
    public void callOTPVerification(View view) {

        //call each function
        if (!validatePhoneNumber())  {
            return;
        }

        //Get all values passed from previous screen using Intent
        String _fullName = getIntent().getStringExtra( "fullName" );
        String _email = getIntent().getStringExtra( "email" );
        String _username = getIntent().getStringExtra( "username" );
        String _password = getIntent().getStringExtra( "password" );
        String _date = getIntent().getStringExtra( "date" );
        String _gender = getIntent().getStringExtra( "gender" );

        //Get complete phone number
        String _getUserEnteredPhoneNumber = Objects.requireNonNull( phoneNumber.getEditText() ).getText().toString().trim();

        //Remove first zero if entered
        if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
            _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
        }
        //Complete phone number
        final String _phoneNo = "+" + countryCodePicker.getSelectedCountryCode() + _getUserEnteredPhoneNumber;


        Intent intent = new Intent( getApplicationContext(), VerifyOTP.class );

        //pass all fields to the next activity
        intent.putExtra( "fullName", _fullName );
        intent.putExtra( "email", _email );
        intent.putExtra( "username", _username );
        intent.putExtra( "password", _password );
        intent.putExtra( "date", _date );
        intent.putExtra( "gender", _gender );
        intent.putExtra( "phoneNo", _phoneNo );
        intent.putExtra("whatToDO", "createNewUser"); // This is to identify that which action should OTP perform after verification.

        //Add Transition
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>( backBtn, "transition_back_arrow_btn" );
        pairs[1] = new Pair<View, String>( scrollView, "transition_OTP_screen" );

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( Signup3rdClass.this, pairs );
        startActivity( intent, options.toBundle() );
    }

    //method to call login screen
    public void callLoginFromSignUp(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), Signup2ndClass.class));
        finish();
    }

    //validate phone number
    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!val.matches(checkspaces)) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

}