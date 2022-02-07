package com.capstone.pasigsafety.Fragments.UserSettings;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;


public class LogoutFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION );
            sessionManager.logoutUserFromSession();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();


        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
}