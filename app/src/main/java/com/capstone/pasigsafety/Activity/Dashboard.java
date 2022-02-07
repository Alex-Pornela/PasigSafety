package com.capstone.pasigsafety.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.Common.LoginSignup.ForgetPassword;
import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Common.LoginSignup.SetNewPassword;
import com.capstone.pasigsafety.Common.LoginSignup.VerifyOTP;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.ChangePassword;
import com.capstone.pasigsafety.databinding.ActivityDashboardBinding;
import com.capstone.pasigsafety.databinding.NavDrawerLayoutBinding;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;

public class Dashboard extends AppCompatActivity  {

    //Variables
    private NavDrawerLayoutBinding navDrawerLayoutBinding;
    private ActivityDashboardBinding activityDashboardBinding;
    private long pressedTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        navDrawerLayoutBinding = NavDrawerLayoutBinding.inflate(getLayoutInflater());
        setContentView( navDrawerLayoutBinding.getRoot() );
        activityDashboardBinding = navDrawerLayoutBinding.dashboardActivity;

        //toggle for animation of navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                navDrawerLayoutBinding.navDrawer,
                R.string.open_navigation_drawer,
                R.string.close_navigation_drawer
        );

        navDrawerLayoutBinding.navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        //connect navgraph to drawer layout
        NavController navController = Navigation.findNavController(this,R.id.fragmentContainer);
        NavigationUI.setupWithNavController(
                navDrawerLayoutBinding.navigationView,
                navController
        );
    }



    @Override
    public void onBackPressed() {

        if (navDrawerLayoutBinding.navDrawer.isDrawerOpen(GravityCompat.START))
            navDrawerLayoutBinding.navDrawer.closeDrawer(GravityCompat.START);
        else{
            super.onBackPressed();
        }

    }

}