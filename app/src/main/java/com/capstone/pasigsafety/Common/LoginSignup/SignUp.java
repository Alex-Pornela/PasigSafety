package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    ImageView backBtn;
    Button next, login;
    TextView titleText;



    //get variables
    TextInputLayout fullName, username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_start_sign_up );

        //Hooks for animation
        backBtn = findViewById( R.id.signup_back_button );
        next = findViewById( R.id.signup_next_btn );
        login = findViewById( R.id.signup_login_btn );
        titleText = findViewById( R.id.signup_title );

        //hooks for getting data
        fullName = findViewById( R.id.signup_fullname );
        email = findViewById( R.id.signup_email );
        username = findViewById( R.id.signup_username );
        password = findViewById( R.id.signup_password );
    }

    //onclick method to call next sign up screen
    public void callNextSignupScreen(View view){

//call each function
        if (!validateFullName() | !validateUsername() | !validateEmail() | !validatePassword()) {
            return;
        }


//Get all valued passed from previous screen using Intent
        String _fullName = Objects.requireNonNull( fullName.getEditText() ).getText().toString();
        String _email = Objects.requireNonNull( email.getEditText() ).getText().toString();
        String _password = Objects.requireNonNull( password.getEditText() ).getText().toString();
        String _username = Objects.requireNonNull( username.getEditText() ).getText().toString();


        Intent intent = new Intent(getApplicationContext(),Signup2ndClass.class);

        //Pass all fields to the next activity
        intent.putExtra("fullName", _fullName);
        intent.putExtra("email", _email);
        intent.putExtra("password", _password);
        intent.putExtra( "username",_username );

        //Add Transition
        Pair[] pairs = new Pair[4];

        pairs[0] = new Pair<View,String>(backBtn,"transition_back_arrow_btn");
        pairs[1] = new Pair<View,String>(next,"transition_next_btn");
        pairs[2] = new Pair<View,String>(login,"transition_login_btn");
        pairs[3] = new Pair<View,String>(titleText,"transition_title_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this,pairs);
        startActivity(intent, options.toBundle());
    }

    //method to call login screen
    public void callLoginFromSignUp(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
        finish();
    }

     /*
    Validation Functions
     */

    //validate full Name
    public boolean validateFullName(){
        String val = Objects.requireNonNull( fullName.getEditText() ).getText().toString().trim();

        if(val.isEmpty()){
            fullName.setError( "Field can not be empty" );
            return false;
        }
        else{
            fullName.setError( null );
            fullName.setErrorEnabled( false );
            return true;
        }
    }

    //validate username
    public boolean validateUsername(){
        String val = Objects.requireNonNull( username.getEditText() ).getText().toString().trim();
        String checkspaces = "\\A\\w{4,20}\\z";

        if(val.isEmpty()){
            username.setError( "Field can not be empty" );
            return false;
        }else if(val.length()>20){
            username.setError( "Username is too large" );
            return false;
        }else if(!val.matches( (checkspaces) )){
            username.setError( "A minimum 4 characters and No White spaces are allowed" );
            return false;
        }
        else{
            username.setError( null );
            username.setErrorEnabled( false );
            return true;
        }
    }

    //validate email
    private boolean validateEmail() {
        String val = Objects.requireNonNull( email.getEditText() ).getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    //validate password
    private boolean validatePassword() {
        String val = Objects.requireNonNull( password.getEditText() ).getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            password.setError("A minimum 8 characters password contains a combination of uppercase and lowercase letter and number without whitespace are required.");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }


}