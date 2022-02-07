package com.capstone.pasigsafety.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.R;

public class ChangePasswordSucces extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_change_password_succes );
    }

    public void backToHome(View view) {
        startActivity( new Intent( getApplicationContext(), Dashboard.class ) );
        finish();
    }
}