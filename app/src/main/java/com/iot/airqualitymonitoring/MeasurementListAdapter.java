package com.iot.airqualitymonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MeasurementListAdapter extends ArrayAdapter<Measurement> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final ArrayList<Measurement> measurements;

    public MeasurementListAdapter(Context context, ArrayList<Measurement> measurements){
        super(context,0, measurements);
        this.context = context;
        this.measurements = measurements;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return measurements.size();
    }

    @Nullable
    @Override
    public Measurement getItem(int position) {
        return measurements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return measurements.get(position).hashCode();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.measurement_item, null);

            holder = new ViewHolder();
            holder.statusImg = (ImageView) convertView.findViewById(R.id.status_img);
            holder.airQualityLabel = (TextView) convertView.findViewById(R.id.air_quality);
            holder.dateLabel = (TextView) convertView.findViewById(R.id.date);
            holder.statusLabel = (TextView) convertView.findViewById(R.id.status_txt);
            convertView.setTag(holder);

        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        Measurement measurement = measurements.get(position);
        if(measurement != null){
            holder.statusImg.setImageResource(measurement.getStatusImg());
            holder.airQualityLabel.setText("Air Quality Value: " + String.valueOf(measurement.getAir_quality()) + " ppm");
            holder.dateLabel.setText("Date: " + measurement.getDate());
            holder.statusLabel.setText("Air Quality Status: " + measurement.getStatusTxt());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView airQualityLabel;
        TextView dateLabel;
        TextView statusLabel;
        ImageView statusImg;
    }
}
