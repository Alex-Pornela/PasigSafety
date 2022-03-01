package com.capstone.pasigsafety.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.pasigsafety.R;

import java.util.List;

public class CrimeAdapter extends BaseAdapter {

    private Context context;
    private List<Crime> crimes;

    public CrimeAdapter(Context context, List<Crime> crimes) {
        this.context = context;
        this.crimes = crimes;
    }

    @Override
    public int getCount() {
        return crimes.size();
    }

    @Override
    public Object getItem(int position) {
        return crimes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView crimeType;
        TextView crimeDesc;
        ImageView crimeIcon;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate( R.layout.crime_type_item, null);

            crimeType = (TextView) convertView
                    .findViewById(R.id.crime_type);
            crimeDesc = (TextView) convertView
                    .findViewById(R.id.crime_desc);
            crimeIcon = (ImageView) convertView
                    .findViewById(R.id.crime_icon);
        } else {
            crimeType = (TextView) convertView
                    .findViewById(R.id.crime_type);
            crimeDesc = (TextView) convertView
                    .findViewById(R.id.crime_desc);
            crimeIcon = (ImageView) convertView
                    .findViewById(R.id.crime_icon);
        }
        crimeType.setText(crimes.get(position).getCrimeType());
        crimeDesc.setText(crimes.get(position).getCrimeDesc());

        int resourceID = context.getResources().getIdentifier(
                crimes.get(position).getImage(), "drawable",
                context.getPackageName());
        crimeIcon.setImageResource(resourceID);
        return convertView;
    }

}
