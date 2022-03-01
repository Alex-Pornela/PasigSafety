package com.capstone.pasigsafety.Fragments.UserSettings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
    private String crimeType;
    private String crimeIcon;
    private int hour,minute;


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

        selectCrimeType();
        selectBrgy();
        setTime();
        setDate();

       // crimeAdapter = ArrayAdapter.createFromResource( requireContext(), R.array.crime_type, R.layout.spinner_layout );







        return binding.getRoot();
    }

    private void selectBrgy() {
        ArrayAdapter <String> arrayAdapter;
        String[] brgy = getResources().getStringArray(R.array.barangay_select);

        arrayAdapter = new ArrayAdapter<String>( requireContext(),R.layout.barangay_select_item,brgy );

        binding.brgySelect.setAdapter(arrayAdapter);

        binding.brgySelect.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );


    }

    private void setDate() {

        binding.selectDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                binding.selectDate.requestFocus();
                binding.selectDate.setInputType( InputType.TYPE_NULL);

                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        String myFormat = "MM/dd/yy"; // In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        binding.selectDate.setText(sdf.format(calendar.getTime()));
                    }
                };
                new DatePickerDialog(requireContext(), onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        } );
    }

    private void setTime() {

        binding.selectTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.selectTime.requestFocus();
                binding.selectTime.setInputType( InputType.TYPE_NULL);

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                        hour = selectedHour;
                        minute = selectedMinute;


                        String am_pm = "";

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hour);
                        datetime.set(Calendar.MINUTE, minute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";

                        binding.selectTime.setText( strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm );
                    }
                };


                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), /*style,*/ onTimeSetListener, hour, minute, false);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        } );
    }

    private void selectCrimeType() {

        List<Crime> crimes = new ArrayList<>();

        String[] crimeTypes = getResources().getStringArray(R.array.crime_type);
        String[] crimeDesc = getResources().getStringArray(R.array.crime_desc);
        final String[] crimeIcons = getResources().getStringArray(
                R.array.crime_ic);

        for (int i = 0; i < crimeDesc.length; i++) {
            Crime crime = new Crime();
            crime.setCrimeType(crimeTypes[i]);
            crime.setCrimeDesc(crimeDesc[i]);
            crime.setImage(crimeIcons[i]);
            crimes.add(crime);
        }
        final CrimeAdapter adapter = new CrimeAdapter(requireContext(), crimes);
        binding.crimeCategory.setAdapter(adapter);
        binding.crimeCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                crimeType = ((Crime) adapter.getItem(position)).getCrimeType();
                crimeIcon = crimeIcons[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}