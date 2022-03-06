package com.capstone.pasigsafety.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.pasigsafety.Activity.Dashboard;
import com.capstone.pasigsafety.Databases.UserHelperClass;
import com.capstone.pasigsafety.R;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    PinView pinFromUser;
    String codeBySystem;
    TextView otpDescriptionText;
    String fullName, phoneNo, email, username, password, date, gender, whatToDO, userRoles;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_verify_otp );

        //Hooks
        pinFromUser = findViewById( R.id.pin_view );
        otpDescriptionText = findViewById( R.id.otp_description_text );


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Get all the data from Intent
        fullName = getIntent().getStringExtra( "fullName" );
        email = getIntent().getStringExtra( "email" );
        username = getIntent().getStringExtra( "username" );
        password = getIntent().getStringExtra( "password" );
        date = getIntent().getStringExtra( "date" );
        gender = getIntent().getStringExtra( "gender" );
        phoneNo = getIntent().getStringExtra( "phoneNo" );
        userRoles = "user";
        whatToDO = getIntent().getStringExtra( "whatToDO" );


        otpDescriptionText.setText( "Enter One Time Password Sent On " + phoneNo );

        sendVerificationCodeToUser( phoneNo );
    }


    //method for sending verification code to User
    private void sendVerificationCodeToUser(String phoneNo) {
        // [START start_phone_auth]
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder( mAuth ) //mAuth is defined on top
                .setPhoneNumber( phoneNo )       // Phone number to verify
                .setTimeout( 60L, TimeUnit.SECONDS ) // Timeout and unit
                .setActivity( this )                 // Activity (for callback binding)
                .setCallbacks( mCallbacks )          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber( options );
        // [END start_phone_auth]
    }

    /*Function which will be called to check auto code send, manual code send or verification failed.
    ON VERIFICATION STATE CHANGED CALLBACKS
     */
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent( s, forceResendingToken );
                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText( code );
                        verifyCode( code );
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText( VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT ).show();
                }
            };


    /*Function  which will be called to execute manual or auto methods and verifying the code
    send by the system and enter by the user.
     */
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential( codeBySystem, code );
        signInUsingCredential( credential );
    }

    private void signInUsingCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential( credential )
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Verification completed successfully here either
                            // store the data or verify the old user
                            if (whatToDO.equals("updateData")) {
                                updateOldUserData();
                            } else if (whatToDO.equals("createNewUser")) {
                                storeNewUsersData();
                            }

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText( VerifyOTP.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    }
                } );
    }

    private void updateOldUserData() {
        Intent intent = new Intent(getApplicationContext(),SetNewPassword.class);
        intent.putExtra( "phoneNo",phoneNo );
        startActivity( intent );
        finish();
    }


    /* function which will be called when there is a new user what to create account.
    Perform Firebase queries
     */
    private void storeNewUsersData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference( "Users" );

        //Create helperclass reference and store data using firebase
        UserHelperClass addNewUser = new UserHelperClass( fullName, username, email, phoneNo, password, date, gender, userRoles );
        reference.child( phoneNo ).setValue( addNewUser );

        startActivity( new Intent( getApplicationContext(), Login.class ) );
        finish();
    }


    //method to back to previous screen
    public void backArrow(View view) {
        startActivity(new Intent(getApplicationContext(), StartUpScreen.class));
        finish();
    }


    // first check the call and then redirect the user accordingly to the Profile or to Set New Password Screen
    public void callNextScreenFromOTP(View view) {
        String code = Objects.requireNonNull( pinFromUser.getText() ).toString();
        if (!code.isEmpty()) {
            verifyCode( code );
        }
    }
}