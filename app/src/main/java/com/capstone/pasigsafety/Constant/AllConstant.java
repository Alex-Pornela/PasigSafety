package com.capstone.pasigsafety.Constant;

import com.capstone.pasigsafety.Utility.PlaceModel;
import com.capstone.pasigsafety.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public interface AllConstant {

    int STORAGE_REQUEST_CODE = 1000;
    int LOCATION_REQUEST_CODE = 2000;
    String IMAGE_PATH = "/Profile/image_profile.jpg";


    ArrayList<PlaceModel> placesName = new ArrayList<>(
            Collections.singletonList(
                    new PlaceModel( 1, R.drawable.police_icon, "police", "police" )


            )
    );




}
