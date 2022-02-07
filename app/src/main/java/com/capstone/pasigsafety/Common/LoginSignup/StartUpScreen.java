package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StartUpScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        //to remove status bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.activity_start_up_screen );


    }


    //calling loginscreen method with animation
    public void callLoginScreen(View view) {

        Intent intent = new Intent( getApplicationContext(), Login.class );

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>( findViewById( R.id.login_btn ), "transition_login" );

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( StartUpScreen.this, pairs );
        startActivity( intent, options.toBundle() );

    }

    public void callSignUpScreen(View view) {


        Intent intent = new Intent( getApplicationContext(), SignUp.class );

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>( findViewById( R.id.signup_btn ), "transition_signup" );

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( StartUpScreen.this, pairs );
        startActivity( intent, options.toBundle() );


    }
}