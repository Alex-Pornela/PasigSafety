package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Databases.CheckInternet;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.UserDashboard;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, password;
    RelativeLayout progressbar;
    CheckBox rememberMe;
    TextInputEditText phoneNumberEditText, passwordEditText;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //to remove status bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.activity_start_login );

        //Hooks
        progressbar = findViewById( R.id.login_progress_bar );
        countryCodePicker = findViewById( R.id.login_country_code_picker );
        phoneNumber = findViewById( R.id.login_Phone_Number );
        password = findViewById( R.id.login_Password );
        rememberMe = findViewById( R.id.remember_me );
        passwordEditText = findViewById( R.id.login_password_editText );
        phoneNumberEditText = findViewById( R.id.login_phone_number_editText );




        //Check weather phone number and password is already save in Shared Preferences or not
        SessionManager sessionManager = new SessionManager( Login.this,SessionManager.SESSION_REMEMBERME );
        if(sessionManager.checkRememberMe()){
            HashMap<String ,String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            phoneNumberEditText.setText( rememberMeDetails.get( SessionManager.KEY_SESSIONPHONENUMBER ) );
            passwordEditText.setText( rememberMeDetails.get( SessionManager.KEY_SESSIONPASSWORD ) );

        }


    }




    //method to login the user in the app
    public void letTheUserLoggedIn(View view) {

        // check internet connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected( this )) {
            showCustomDialog();

        }

        //validate username and password
        if (!validateLoginPassword() | !validateLoginPhoneNumber()) {
            return;
        }

        //get data from fields

        String _phoneNumber = Objects.requireNonNull( phoneNumber.getEditText() ).getText().toString().trim();
        String _password = Objects.requireNonNull( password.getEditText() ).getText().toString().trim();

        //progressbar
        progressbar = findViewById( R.id.login_progress_bar );
        progressbar.setVisibility( View.VISIBLE );


        if (_phoneNumber.charAt( 0 ) == '0') {
            _phoneNumber = _phoneNumber.substring( 1 );
        }

        countryCodePicker = findViewById( R.id.login_country_code_picker );
        String _completePhoneNumber = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNumber;

        //for remember Checkbox

        if( rememberMe.isChecked()){
            SessionManager sessionManager = new SessionManager( Login.this,SessionManager.SESSION_REMEMBERME );
            sessionManager.createRememberMeSession( _phoneNumber, _password );
        }



        /*
        Check user if exist
        or not in the database
         */
        Query checkUser = FirebaseDatabase.getInstance().getReference( "Users" ).orderByChild( "phoneNo" ).equalTo( _completePhoneNumber );

        checkUser.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    phoneNumber.setError( null );
                    phoneNumber.setErrorEnabled( false );

                    String systemPassword = dataSnapshot.child( _completePhoneNumber ).child( "password" ).getValue( String.class );
                    // if password exists and matches with user password then get other fields from database
                    if (systemPassword.equals( _password )) {
                        password.setError( null );
                        password.setErrorEnabled( false );

                        //Get user data from firebase database

                        String _fullname = dataSnapshot.child( _completePhoneNumber ).child( "fullName" ).getValue( String.class );
                        String _email = dataSnapshot.child( _completePhoneNumber ).child( "email" ).getValue( String.class );
                        String _phoneNo = dataSnapshot.child( _completePhoneNumber ).child( "phoneNo" ).getValue( String.class );
                        String _dateOfBirth = dataSnapshot.child( _completePhoneNumber ).child( "date" ).getValue( String.class );
                        String _username = dataSnapshot.child( _completePhoneNumber ).child( "username" ).getValue( String.class );
                        String _gender = dataSnapshot.child( _completePhoneNumber ).child( "gender" ).getValue( String.class );
                        String _password = dataSnapshot.child( _completePhoneNumber ).child( "password" ).getValue( String.class );
                        String _userRoles = dataSnapshot.child( _completePhoneNumber ).child( "role" ).getValue( String.class );

                        //Create a Session


                        SessionManager sessionManager = new SessionManager( Login.this, SessionManager.SESSION_USERSESSION );
                        sessionManager.createLoginSession( _fullname, _username, _email,_phoneNo, _password, _dateOfBirth, _gender,_userRoles );

                        startActivity( new Intent( getApplicationContext(), UserDashboard.class ) );
                        finish();

                        /*
                        get data from firebase and show it as Toast Message

                        Toast.makeText( Login.this, _fullname + "\n" + _email + "\n" + _phoneNo + "\n" + _dateOfBirth, Toast.LENGTH_SHORT ).show();

                         */


                    } else {
                        progressbar.setVisibility( View.GONE );
                        Toast.makeText( Login.this, "Password does not exist!", Toast.LENGTH_SHORT ).show();

                    }
                } else {
                    progressbar.setVisibility( View.GONE );
                    Toast.makeText( Login.this, "No such user exist!", Toast.LENGTH_SHORT ).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.setVisibility( View.GONE );
                Toast.makeText( Login.this, databaseError.getMessage(), Toast.LENGTH_SHORT ).show();

            }
        } );
    }

    //check user internet connection and show dialog box
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder( Login.this );
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

    //on click for create Account
    public void createAccount(View view) {
        startActivity( new Intent( getApplicationContext(), SignUp.class ) );
    }



    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
        finish();
    }

    //function to validate password
    private boolean validateLoginPassword() {
        password = findViewById( R.id.login_Password );
        String _password = Objects.requireNonNull( password.getEditText() ).getText().toString();
        if (_password.isEmpty()) {
            password.setError( "Field cannot be empty" );
            password.requestFocus();
            return false;
        } else {
            password.setError( null );
            password.setErrorEnabled( false );
            return true;
        }
    }

    //function to validate phone number
    private boolean validateLoginPhoneNumber() {
        phoneNumber = findViewById( R.id.login_Phone_Number );
        String _phoneNumber = Objects.requireNonNull( phoneNumber.getEditText() ).getText().toString();
        if (_phoneNumber.isEmpty()) {
            phoneNumber.setError( "Field cannot be empty" );
            phoneNumber.requestFocus();
            return false;
        } else {
            phoneNumber.setError( null );
            phoneNumber.setErrorEnabled( false );
            return true;
        }
    }

    public void callForgetPassword(View view) {
        startActivity( new Intent( getApplicationContext(), ForgetPassword.class ) );
    }


}