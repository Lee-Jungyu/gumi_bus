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
import com.jglee.busapp.domain.BusItem;
import com.jglee.busapp.ui.Activity.BusRouteActivity;

import java.util.List;

public class BusListFragmentAdapter extends RecyclerView.Adapter<BusListFragmentAdapter.ViewHolder> {

    private List<BusItem> list;

    public BusListFragmentAdapter(List<BusItem> list) { this.list = list; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusItem item = list.get(position);
        holder.textView_bus_number.setText(item.getBusNumber());
        holder.textView_bus_start_station.setText(item.getBusStartStationName());
        holder.textView_bus_end_station.setText(item.getBusEndStationName());

        if(position % 2 == 1) {
            holder.item_bus.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_bus.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView_bus_number;
        public final TextView textView_bus_start_station;
        public final TextView textView_bus_end_station;
        public final LinearLayout item_bus;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView_bus_number = (TextView) view.findViewById(R.id.textView_bus_number);
            textView_bus_start_station = (TextView) view.findViewById(R.id.textView_bus_start_station);
            textView_bus_end_station = (TextView) view.findViewById(R.id.textView_bus_end_station);
            item_bus = (LinearLayout) view.findViewById(R.id.item_bus);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        BusItem item = list.get(pos);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, BusRouteActivity.class);
                        intent.putExtra("busId", item.getBusId());
                        intent.putExtra("busNumber", item.getBusNumber());
                        intent.putExtra("busStartStationName", item.getBusStartStationName());
                        intent.putExtra("busEndStationName", item.getBusEndStationName());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
