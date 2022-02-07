package com.capstone.pasigsafety.Common;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstone.pasigsafety.Common.LoginSignup.StartUpScreen;
import com.capstone.pasigsafety.R;

public class OnBoardingFragment1 extends Fragment {

    TextView skip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding1,container,false);

//for skip btn
        skip = root.findViewById( R.id.skip );

        skip.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StartUpScreen.class);
                startActivity( intent );
            }
        } );

        return root;
    }
}
