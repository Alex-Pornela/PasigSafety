package com.capstone.pasigsafety.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Common.LoginSignup.StartUpScreen;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {


    //variables
    ImageView logo;
    ImageView logoName;
    ImageView splashImage;
    LottieAnimationView lottieAnimationView;

    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    private static int SPLASH_TIME_OUT = 4000;
    SharedPreferences mSharedPref;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//to remove status bar
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView( R.layout.splash_screen );


        viewPager = findViewById( R.id.pager );
        pagerAdapter = new ScreenSlidePagerAdapter( getSupportFragmentManager() );
        viewPager.setAdapter( pagerAdapter );

//hooks the design
        logo = findViewById( R.id.logo );
        logoName = findViewById( R.id.logo_name );
        splashImage = findViewById( R.id.img );
        lottieAnimationView = findViewById( R.id.lottie );

        splashImage.animate().translationY( -2300 ).setDuration( 1000 ).setStartDelay( 4000 );
        logo.animate().translationY( 1800 ).setDuration( 1000 ).setStartDelay( 4000 );
        logoName.animate().translationY( 1400 ).setDuration( 1000 ).setStartDelay( 4000 );
        lottieAnimationView.animate().translationY( 1400 ).setDuration( 1200 ).setStartDelay( 4000 );

        //Shared Preference for splash screen to skip onboarding screen for the 2nd time opening the app

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {

                //Shared Preference for splash screen to skip onboarding screen for the 2nd time opening the app
                mSharedPref = getSharedPreferences( "SharedPref", MODE_PRIVATE );
                boolean isFirtTime = mSharedPref.getBoolean( "firstTime",true );

                //shared preference in session manager
                SessionManager sessionManager = new SessionManager( SplashScreen.this, SessionManager.SESSION_USERSESSION );

                if (isFirtTime) {
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean( "firstTime",false );
                    editor.commit();
                } else {
                    if (sessionManager.checkLogin()) {
                        Intent intent = new Intent( SplashScreen.this, Dashboard.class );
                        startActivity( intent );
                        finish();

                    }else{
                        Intent intent = new Intent( SplashScreen.this, StartUpScreen.class );
                        startActivity( intent );
                        finish();
                    }
                }
            }
        }, SPLASH_TIME_OUT );


    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super( fm );
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OnBoardingFragment1 tab1 = new OnBoardingFragment1();
                    return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {

            return NUM_PAGES;
        }
    }


}