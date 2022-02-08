package com.capstone.pasigsafety.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.pasigsafety.Common.LoginSignup.StartUpScreen;
import com.capstone.pasigsafety.Common.OnBoarding;
import com.capstone.pasigsafety.Common.SplashScreen;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.UserDashboard;

public class IntroScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 5000;

    ImageView backgroundImage;
    TextView poweredByLine;

    //Animations
    Animation sideAnim, bottomAnim;

    SharedPreferences mShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        setContentView( R.layout.activity_intro_screen );

        //hooks
        backgroundImage = findViewById( R.id.background_image );
        poweredByLine = findViewById(R.id.pasig_safety);

        //Animations
        sideAnim = AnimationUtils.loadAnimation( this,R.anim.side_animation );
        bottomAnim = AnimationUtils.loadAnimation( this,R.anim.bottom_animation );

        //set animations on elements
        backgroundImage.setAnimation( sideAnim );
        poweredByLine.setAnimation( bottomAnim );

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {

                mShared = getSharedPreferences( "onBoardingScreen", MODE_PRIVATE );
                boolean isFirtTime = mShared.getBoolean( "firstTime", true );

                //shared preference in session manager
                SessionManager sessionManager = new SessionManager( IntroScreen.this, SessionManager.SESSION_USERSESSION );


                if (isFirtTime) {
                    SharedPreferences.Editor editor = mShared.edit();
                    editor.putBoolean( "firstTime", false );
                    editor.commit();

                    Intent intent = new Intent( getApplicationContext(), OnBoarding.class );
                    startActivity( intent );
                    finish();
                } else {
                    if (sessionManager.checkLogin()) {
                        Intent intent = new Intent( IntroScreen.this, UserDashboard.class );
                        startActivity( intent );
                        finish();

                    } else {
                        Intent intent = new Intent( IntroScreen.this, StartUpScreen.class );
                        startActivity( intent );
                        finish();
                    }

                }
            }
        },SPLASH_TIMER);
    }
}