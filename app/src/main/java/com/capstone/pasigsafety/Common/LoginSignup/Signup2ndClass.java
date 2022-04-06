package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.R;
import com.google.android.material.textfield.TextInputLayout;

import java.security.Signature;

public class Signup2ndClass extends AppCompatActivity {

    ImageView backBtn;
    Button next, login;
    TextView titleText, slideText;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.activity_signup2nd_class );

//Hooks
        backBtn = findViewById( R.id.signup_back_button );
        next = findViewById( R.id.signup_next_btn );
        slideText = findViewById( R.id.signup_slide_text );
        radioGroup = findViewById( R.id.radio_group );
        datePicker = findViewById( R.id.age_picker );
        login = findViewById( R.id.signup_login_btn );
        titleText = findViewById( R.id.signup_title );


    }

    //method to call 3rd signup screen
    public void call3rdSignupScreen(View view) {

        //call each function
        if (!validateGender())  {
            return;
        }

        selectedGender = findViewById( radioGroup.getCheckedRadioButtonId() );
        String _gender = selectedGender.getText().toString();

        int selectedMonth =  datePicker.getMonth()+1;

        int day = datePicker.getDayOfMonth();
        int year = datePicker.getYear();

        String _date = day+"/"+ selectedMonth +"/"+year;


        //Get all valued passed from previous screen using Intent
        String _fullName = getIntent().getStringExtra("fullName");
        String _email = getIntent().getStringExtra("email");
        String _password = getIntent().getStringExtra("password");
        String _username = getIntent().getStringExtra("username");




        Intent intent = new Intent( getApplicationContext(), Signup3rdClass.class );

        //Pass all fields to the next activity
        intent.putExtra("fullName", _fullName);
        intent.putExtra("email", _email);
        intent.putExtra("password", _password);
        intent.putExtra("username", _username);
        intent.putExtra("date", _date);
        intent.putExtra("gender", _gender);

        //Add Transition and call next activity
        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>( backBtn, "transition_back_arrow_btn" );
        pairs[1] = new Pair<View, String>( next, "transition_next_btn" );
        pairs[2] = new Pair<View, String>( login, "transition_login_btn" );
        pairs[3] = new Pair<View, String>( titleText, "transition_title_text" );
        pairs[4] = new Pair<View, String>( slideText, "transition_slide_text" );

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( Signup2ndClass.this, pairs );
        startActivity( intent, options.toBundle() );


    }

    //method to call login screen
    public void callLoginFromSignUp(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), SignUp.class));
        finish();
    }


    /*
    Validation Functions
     */

    //validate Gender
    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText( this, "Please Select Gender", Toast.LENGTH_SHORT ).show();
            return false;
        } else {
            return true;
        }
    }


}