package com.capstone.pasigsafety.Common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.pasigsafety.Common.LoginSignup.StartUpScreen;
import com.capstone.pasigsafety.HelperClasses.SliderAdapter;
import com.capstone.pasigsafety.R;

public class OnBoarding extends AppCompatActivity {

    public static ViewPager viewPager;
    LinearLayout dots_layout;

    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted;
    Animation animation;
    int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        //hooks
        viewPager = findViewById(R.id.slider);
        dots_layout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);


        //call slider adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

    }
// for skip button
    public void skip(View view) {
        startActivity(new Intent(this, StartUpScreen.class));
        finish();
    }

    //for get started button
    public void getStarted(View view) {
        startActivity(new Intent(this, StartUpScreen.class));
        finish();
    }

    // for next button
    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    // adding dots
    private void addDots(int position){

        dots = new TextView[4];
        dots_layout.removeAllViews();

        for(int i=0; i<dots.length; i++){
            dots[i] = new TextView( this );
            dots[i].setText( Html.fromHtml("&#8226;") );
            dots[i].setTextSize( 35 );

            dots_layout.addView(dots[i]);
        }

        if(dots.length > 0){
            dots[position].setTextColor( getResources().getColor(R.color.yellow) );
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            //animation of lets get started button
            currentPos = position;

            if (position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                letsGetStarted.setVisibility(View.INVISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.bottom_animation);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}