package com.capstone.pasigsafety.Fragments.UserSettings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.capstone.pasigsafety.Adapter.Crime;
import com.capstone.pasigsafety.Adapter.CrimeAdapter;
import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.CrimeTypeItemBinding;
import com.capstone.pasigsafety.databinding.FragmentAdminBinding;
import com.capstone.pasigsafety.databinding.FragmentProfileBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate( inflater, container, false );


        binding.userFeedback.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("http://www.stackoverflow.com/"));
                startActivity(viewIntent);
            }
        } );



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