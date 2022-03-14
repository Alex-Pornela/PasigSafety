package com.capstone.pasigsafety.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.pasigsafety.R;
import com.capstone.pasigsafety.databinding.PlaceItemLayoutBinding;

import java.util.List;

public class GooglePlaceAdapter extends RecyclerView.Adapter<GooglePlaceAdapter.ViewHolder> {


    private List<GooglePlaceModel> googlePlaceModels;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaceItemLayoutBinding binding = DataBindingUtil.inflate( LayoutInflater.from( parent.getContext() ),
                R.layout.place_item_layout, parent, false );
        return new ViewHolder( binding );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (googlePlaceModels != null) {
            GooglePlaceModel placeModel = googlePlaceModels.get( position );
            holder.binding.setGooglePlaceModel( placeModel );
        }

    }

    @Override
    public int getItemCount() {
        if (googlePlaceModels != null)
            return googlePlaceModels.size();
        else
            return 0;
    }

    public void setGooPlaceModels(List<GooglePlaceModel> googlePlaceModels) {
        this.googlePlaceModels = googlePlaceModels;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private PlaceItemLayoutBinding binding;

        public ViewHolder(@NonNull PlaceItemLayoutBinding binding) {
            super( binding.getRoot() );
            this.binding = binding;
        }
    }


}
