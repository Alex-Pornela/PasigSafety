package com.capstone.pasigsafety.Fragments.UserSettings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.User.ChangePassword;
import com.capstone.pasigsafety.User.ChangePasswordSucces;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class ChangePasswordFragment extends Fragment {




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate( R.layout.fragment_change_password, container, false );


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        SessionManager sessionManager = new SessionManager( Objects.requireNonNull( getActivity() ),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetails = sessionManager.getUsersDetailFromSession();
        String phoneNumber = userDetails.get( SessionManager.KEY_PHONENUMBER );

        Intent intent = new Intent(getActivity(), ChangePassword.class);
        intent.putExtra( "phoneNo",phoneNumber );
        startActivity( intent );
        getActivity().finish();

    }
}