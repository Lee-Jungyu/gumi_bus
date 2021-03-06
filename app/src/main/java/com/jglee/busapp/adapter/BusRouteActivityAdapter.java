package com.jglee.busapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.domain.BusRouteData;
import com.jglee.busapp.ui.Activity.ArrivalActivity;

import java.util.List;

public class BusRouteActivityAdapter extends RecyclerView.Adapter<BusRouteActivityAdapter.ViewHolder> {

    private List<BusRouteData> list;

    public BusRouteActivityAdapter(List<BusRouteData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_route, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusRouteData item = list.get(position);
        holder.textView_bus_route_station_name.setText(item.getStation().getStationName());

        if(item.isCurrentBusPose()) {
            holder.imageView_current_bus_pose.setImageResource(R.drawable.bus);
        }
        else {
            holder.imageView_current_bus_pose.setImageResource(0);
        }

        if(position % 2 == 1) {
            holder.item_bus_route.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_bus_route.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView_bus_route_station_name;
        public final ImageView imageView_current_bus_pose;
        public final LinearLayout item_bus_route;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView_bus_route_station_name = (TextView) view.findViewById(R.id.textView_bus_route_station_name);
            imageView_current_bus_pose = (ImageView) view.findViewById(R.id.imageView_current_bus_pose);
            item_bus_route = (LinearLayout) view.findViewById(R.id.item_bus_route);

            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        BusRouteData item = list.get(pos);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ArrivalActivity.class);
                        intent.putExtra("stationId", item.getStation().getStationId());
                        intent.putExtra("stationName", item.getStation().getStationName());
                        context.startActivity(intent);
                    }

                    return true;
                }
            });
        }
    }
}
