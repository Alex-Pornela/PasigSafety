package com.capstone.pasigsafety.Model;

import com.capstone.pasigsafety.GooglePlaceModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

    //return the list of GooglePlaceModel

public class GoogleResponseModel {

    @SerializedName( "results" )
    @Expose
    private List<GooglePlaceModel> googlePlaceModelList;

    public List<GooglePlaceModel> getGooglePlaceModelList() {
        return googlePlaceModelList;
    }

    public void setGooglePlaceModelList(List<GooglePlaceModel> googlePlaceModelList) {
        this.googlePlaceModelList = googlePlaceModelList;
    }
}
