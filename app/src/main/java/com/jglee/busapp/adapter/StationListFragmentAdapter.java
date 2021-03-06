package com.jglee.busapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.domain.StationItem;
import com.jglee.busapp.ui.Activity.ArrivalActivity;

import java.util.List;

public class StationListFragmentAdapter extends RecyclerView.Adapter<StationListFragmentAdapter.ViewHolder> {

    private List<StationItem> list;

    public StationListFragmentAdapter(List<StationItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StationItem item = list.get(position);
        holder.textView_station_name.setText(item.getStationName());
        holder.textView_station_number.setText(item.getStationNumber());

        if(position % 2 == 1) {
            holder.item_station.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_station.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView_station_name;
        public final TextView textView_station_number;
        public final LinearLayout item_station;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView_station_name = (TextView) view.findViewById(R.id.textView_station_name);
            textView_station_number = (TextView) view.findViewById(R.id.textView_station_number);
            item_station = (LinearLayout) view.findViewById(R.id.item_station);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        StationItem item = list.get(pos);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ArrivalActivity.class);
                        intent.putExtra("stationId", item.getStationId());
                        intent.putExtra("stationName", item.getStationName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
