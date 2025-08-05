package com.example.forecastfable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WeatherListAdapter extends ArrayAdapter<WeatherDataItem> {
    public WeatherListAdapter(Context context, ArrayList<WeatherDataItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherDataItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView labelTextView = convertView.findViewById(R.id.idTVLabel);
        TextView valueTextView = convertView.findViewById(R.id.idTVValue);

        labelTextView.setText(item.getLabel());
        valueTextView.setText(item.getValue());

        return convertView;
    }
}
