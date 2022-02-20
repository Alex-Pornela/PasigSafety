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
import com.capstone.pasigsafety.databinding.FragmentAdminBinding;
import com.capstone.pasigsafety.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate( inflater, container, false );

        binding.logBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager sessionManager = new SessionManager( requireActivity(), SessionManager.SESSION_USERSESSION );
                sessionManager.logoutUserFromSession();
                startActivity(new Intent( requireActivity(), Login.class));
                requireActivity().finish();

            }
        } );

        return binding.getRoot();
    }
}