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
import com.jglee.busapp.domain.ArrivalData;
import com.jglee.busapp.ui.Activity.BusRouteActivity;

import java.util.List;

public class ArrivalActivityAdapter extends RecyclerView.Adapter<ArrivalActivityAdapter.ViewHolder> {

    private List<ArrivalData> list;
    private String stationName;

    public ArrivalActivityAdapter(List<ArrivalData> list, String stationName) {
        this.list = list;
        this.stationName = stationName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_arrival, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArrivalData item = list.get(position);
        holder.textView_arrival_bus_number.setText(item.getBus().getBusNumber());
        holder.textView_arrival_bus_route.setText(
                item.getBus().getBusStartStationName() + " ➞ " + item.getBus().getBusEndStationName());

        holder.textView_arrival_remaining_time.setText(item.getRemainingTime() + "분");
        holder.textView_prev_station_cnt.setText(item.getPrevStationCnt() + "정거장 남음");

        if(item.getBus().getBusStartStationName().equals(stationName)) {
            holder.textView_prev_station_cnt.setText("출발 대기 중");
        }

        if(item.getRemainingTime() == 10000) {
            holder.textView_arrival_remaining_time.setText("");
            holder.textView_prev_station_cnt.setText("");
        }

        if(position % 2 == 1) {
            holder.item_arrival.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_arrival.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView_arrival_bus_number;
        public final TextView textView_arrival_bus_route;
        public final TextView textView_arrival_remaining_time;
        public final TextView textView_prev_station_cnt;
        public final LinearLayout item_arrival;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView_arrival_bus_number = (TextView) view.findViewById(R.id.textView_arrival_bus_number);
            textView_arrival_bus_route = (TextView) view.findViewById(R.id.textView_arrival_bus_route);
            textView_arrival_remaining_time = (TextView) view.findViewById(R.id.textView_arrival_remaining_time);
            textView_prev_station_cnt = (TextView) view.findViewById(R.id.textView_prev_station_cnt);
            item_arrival = (LinearLayout) view.findViewById(R.id.item_arrival);

            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        ArrivalData item = list.get(pos);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BusRouteActivity.class);
                        intent.putExtra("busId", item.getBus().getBusId());
                        intent.putExtra("busNumber", item.getBus().getBusNumber());
                        intent.putExtra("busStartStationName", item.getBus().getBusStartStationName());
                        intent.putExtra("busEndStationName", item.getBus().getBusEndStationName());
                        context.startActivity(intent);
                    }
                    return true;
                }
            });

        }
    }
}
