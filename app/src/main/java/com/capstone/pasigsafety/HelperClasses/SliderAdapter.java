package com.capstone.pasigsafety.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.capstone.pasigsafety.R;

//this adapter show na images or slides in the screen

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

//context is dor activity to show if sliderAdapter class is called

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] images = {
            R.drawable.first_slide,
            R.drawable.slide_two,
            R.drawable.slide_three

    };

    public int[] headings = {
            R.string.first_slide_title,
            R.string.third_slide_title,
            R.string.second_slide_title


    };

    public int[] descriptions = {
            R.string.first_slide_description,
            R.string.third_slide_description,
            R.string.second_slide_description

    };

//number of slides uses in the project
    @Override
    public int getCount() {
        return headings.length;
    }

    //set view using this object
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    //instance of item ang change each slide
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //request the system to use the design
        layoutInflater = (LayoutInflater) context.getSystemService( context.LAYOUT_INFLATER_SERVICE );
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);

//hooks for the design
        ImageView imageView = (ImageView)view.findViewById(R.id.slider_image);
        TextView heading = (TextView)view.findViewById(R.id.slider_heading);
        TextView description = (TextView)view.findViewById(R.id.slider_description);

        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        description.setText(descriptions[position]);




        //holds the data
        container.addView(view);


        return view;
    }

    //destroy the item view
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
