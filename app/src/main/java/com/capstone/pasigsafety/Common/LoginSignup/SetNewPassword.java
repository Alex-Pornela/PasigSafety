package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.pasigsafety.Databases.CheckInternet;
import com.capstone.pasigsafety.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SetNewPassword extends AppCompatActivity {

    private Animation animation;
    private ImageView screenIcon;
    private TextView title, description;
    private Button updateBtn;
    private TextInputLayout newPassword, confirmPassword;
    RelativeLayout progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_new_password );

        //hooks
        screenIcon = findViewById( R.id.set_new_password_icon );
        title = findViewById( R.id.set_new_password_title );
        description = findViewById( R.id.set_new_password_description );
        updateBtn = findViewById( R.id.set_new_password_btn );
        newPassword = findViewById( R.id.new_password );
        confirmPassword = findViewById( R.id.confirm_password );
        progressBar = findViewById( R.id.progress_bar );


        //Animation Hook
        animation = AnimationUtils.loadAnimation(this, R.anim.slide_animation);

        //Set animation to all the elements
        screenIcon.setAnimation(animation);
        title.setAnimation(animation);
        description.setAnimation(animation);
        updateBtn.setAnimation(animation);
        newPassword.setAnimation(animation);
        confirmPassword.setAnimation(animation);


    }


    public void setNewPasswordBtn(View view){

        // check internet connection
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected( this )) {
            showCustomDialog();

        }

        //validate password and confirm password
        if (!validatePassword() | !validateConfirmPassword()) {
            return;
        }
        progressBar.setVisibility( View.VISIBLE );

        //get data from fields
        String _newPassword = newPassword.getEditText().getText().toString().trim();
        String _phoneNumber = getIntent().getStringExtra( "phoneNo" );


        //Update Data in Firebase and in Session
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child( _phoneNumber ).child( "password" ).setValue( _newPassword );

        startActivity( new Intent(getApplicationContext(),ForgetPasswordSuccessMessage.class) );
        finish();
    }


    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }



    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder( SetNewPassword.this );
        builder.setMessage( "Please connect to the internet to proceed" )
                .setCancelable( false )
                .setPositiveButton( "Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( Settings.ACTION_WIFI_SETTINGS ) );
                    }
                } )
                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( getApplicationContext(), StartUpScreen.class ) );
                        finish();
                    }
                } );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private boolean validatePassword() {
        String _newpassword = Objects.requireNonNull( newPassword.getEditText() ).getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 4 characters
                "$";

        if (_newpassword.isEmpty()) {
            newPassword.setError("Field can not be empty");
            return false;
        } else if (!_newpassword.matches(checkPassword)) {
            newPassword.setError("A minimum 8 characters password contains a combination of uppercase and lowercase letter and number without whitespace are required.");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String _confirmpassword = Objects.requireNonNull( confirmPassword.getEditText() ).getText().toString().trim();
        String _newPassword = newPassword.getEditText().getText().toString().trim();

        if (_confirmpassword.isEmpty()) {
            confirmPassword.setError("Field can not be empty");
            return false;
        } else if (!_confirmpassword.matches(_newPassword)) {
            confirmPassword.setError("Password does not match! Please try again.");
            return false;
        } else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }
    }
}