package com.capstone.pasigsafety.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Fragments.UserSettings.ProfileFragment;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.ActivityEditUserProfileBinding;

public class EditUserProfile extends AppCompatActivity {

    ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_user_profile );

        backArrow = findViewById(R.id.back_arrow);


        backArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onBackPressed();
            }
        } );
    }
}