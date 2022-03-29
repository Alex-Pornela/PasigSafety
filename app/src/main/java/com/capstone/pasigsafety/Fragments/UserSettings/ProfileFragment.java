package com.capstone.pasigsafety.Fragments.UserSettings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.Databases.UserHelperClass;
import com.capstone.pasigsafety.User.About;
import com.capstone.pasigsafety.User.EditUserProfile;
import com.capstone.pasigsafety.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;



    @SuppressWarnings( "deprecation" )
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate( inflater, container, false );


        binding.userFeedback.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScC_qRRdqEuWTLe9p7NauFQnJG_WmMDDyyOXsySxzZ5g0ELJQ/viewform"));
                startActivity(viewIntent);
            }
        } );

        binding.userInfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent( requireActivity(), EditUserProfile.class));
                requireActivity().finish();


            }
        } );


        binding.aboutUs.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( requireActivity(), About.class));
                requireActivity().finish();
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

        setUserInfo();



        return binding.getRoot();
    }

    public boolean isAttachedToActivity() {
        boolean attached = isVisible() && getActivity() != null;
        return attached;
    }



    private void setUserInfo() {

        SessionManager sessionManager = new SessionManager( requireContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference( "Users" ).child( userDetails.get( SessionManager.KEY_PHONENUMBER ) );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserHelperClass data = snapshot.getValue(UserHelperClass.class);

                String female = "female_icon";
                String male = "male_icon";
                String gender = data.getGender();

                if (isAttachedToActivity()){
                    if(gender.equals( "Female" ) || gender.equals( "female" )){

                        int resourceID = getResources().getIdentifier(
                                female, "drawable",
                                getActivity() .getPackageName());

                        binding.userAvatar.setImageResource( resourceID );

                    }
                    if(gender.equals( "Male" ) || gender.equals( "male" )){

                        int resourceID = getResources().getIdentifier(
                                male, "drawable",
                                getActivity() .getPackageName());

                        binding.userAvatar.setImageResource( resourceID );
                    }

                }




                String fullName = data.getFullName();
                String phoneNo = data.getPhoneNo();

                binding.userFullName.setText( fullName );
                binding.userEmail.setText( phoneNo );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );



    }


}