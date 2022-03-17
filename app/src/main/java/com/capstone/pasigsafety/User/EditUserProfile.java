package com.capstone.pasigsafety.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.pasigsafety.Common.LoginSignup.Login;
import com.capstone.pasigsafety.Databases.SessionManager;
import com.capstone.pasigsafety.Databases.UserHelperClass;
import com.capstone.pasigsafety.Fragments.UserSettings.ProfileFragment;
import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.ActivityEditUserProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditUserProfile extends AppCompatActivity {

    private ActivityEditUserProfileBinding binding;
    DatabaseReference reference;
    String fullName;
    String email;
    String gender;
    String birthday;
    String password;
    String phoneNo;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityEditUserProfileBinding.inflate( getLayoutInflater() );
        View view = binding.getRoot();
        setContentView( view );

        reference = FirebaseDatabase.getInstance().getReference( "Users" );



        showUserData();
        binding.userUpdateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNameChanged() | isEmailChanged() | isBdayChanged() | isGenderChanged() | isPasswordChanged()){

                    dialog = new Dialog( EditUserProfile.this );

                    dialog.setContentView( R.layout.success_dialog_layout );
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );


                    Button okDialogBtn = dialog.findViewById( R.id.update_okay_btn );


                    okDialogBtn.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent( EditUserProfile.this, UserDashboard.class );
                            startActivity( intent );
                            finish();
                        }
                    } );


                    dialog.show();
                }

                else{
                    dialog = new Dialog( EditUserProfile.this );

                    dialog.setContentView( R.layout.update_failed_dialog );
                    dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );


                    Button okDialogBtn = dialog.findViewById( R.id.failed_okay_btn );


                    okDialogBtn.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    } );


                    dialog.show();
                }
            }
        } );


        binding.backArrow.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( EditUserProfile.this, UserDashboard.class );
                startActivity( intent );
                finish();
            }
        } );
    }

    private boolean isPasswordChanged() {

        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        if (!password.equals( binding.passwordEditText.getText().toString() )){

            reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) ).child( "password" ).setValue( binding.passwordEditText.getText().toString() );
            return true;

        }else{
            return false;
        }
    }

    private boolean isBdayChanged() {
        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        if (!birthday.equals( binding.birthdayEditText.getText().toString() )){

            reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) ).child( "date" ).setValue( binding.birthdayEditText.getText().toString() );
            return true;

        }else{
            return false;
        }
    }

    private boolean isGenderChanged() {

        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        if (!gender.equals( binding.genderEditText.getText().toString() )){

            reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) ).child( "gender" ).setValue( binding.genderEditText.getText().toString() );
            return true;

        }else{
            return false;
        }
    }

    private boolean isEmailChanged() {

        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        if (!email.equals( binding.emailEditText.getText().toString() )){

            reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) ).child( "email" ).setValue( binding.emailEditText.getText().toString() );
            return true;

        }else{
            return false;
        }
    }

    private boolean isNameChanged() {

        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        if (!fullName.equals( binding.fullNameEditText.getText().toString() )){

            reference.child( userDetails.get( SessionManager.KEY_PHONENUMBER ) ).child( "fullName" ).setValue( binding.fullNameEditText.getText().toString() );
            return true;

        }else{
            return false;
        }

    }

    private void showUserData() {

        SessionManager sessionManager = new SessionManager( getApplicationContext(), SessionManager.SESSION_USERSESSION );
        HashMap<String, String> userDetails = sessionManager.getUsersDetailFromSession();

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference( "Users" ).child( userDetails.get( SessionManager.KEY_PHONENUMBER ) );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserHelperClass data = snapshot.getValue( UserHelperClass.class );

                fullName = data.getFullName();
                email = data.getEmail();
                gender = data.getGender();
                birthday = data.getDate();
                password = data.getPassword();
                phoneNo = data.getPhoneNo();
                String female = "female_icon";
                String male = "male_icon";


                if (gender.equals( "Female" ) || gender.equals( "female" )) {

                    int resourceID = getResources().getIdentifier(
                            female, "drawable",
                            getApplicationContext().getPackageName() );

                    binding.profileIcon.setImageResource( resourceID );

                }
                if (gender.equals( "Male" ) || gender.equals( "male" )) {

                    int resourceID = getResources().getIdentifier(
                            male, "drawable",
                            getApplicationContext().getPackageName() );

                    binding.profileIcon.setImageResource( resourceID );
                }

                binding.phoneNo.setText( phoneNo );
                binding.fullNameEditText.setText( fullName );
                binding.emailEditText.setText( email );
                binding.genderEditText.setText( gender );
                binding.birthdayEditText.setText( birthday );
                binding.passwordEditText.setText( password );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }


}