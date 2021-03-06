package com.jglee.busapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.controller.BusController;
import com.jglee.busapp.controller.StationController;
import com.jglee.busapp.domain.BusItem;
import com.jglee.busapp.domain.FavoriteItem;
import com.jglee.busapp.domain.StationItem;
import com.jglee.busapp.ui.Activity.ArrivalActivity;
import com.jglee.busapp.ui.Activity.BusRouteActivity;

import java.util.List;

public class FavoritesFragmentAdapter extends RecyclerView.Adapter<FavoritesFragmentAdapter.ViewHolder> {

    private List<FavoriteItem> list;

    public FavoritesFragmentAdapter(List<FavoriteItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem item = list.get(position);
        holder.textView_favorite.setText(item.getItemName());

        if(item.getFavoriteType().equals("bus")) {
            StringBuilder sb = new StringBuilder();
            BusItem bus = BusController.getBusItem(item.getItemId());
            if (bus != null) {
                sb.append(bus.getBusStartStationName());
                sb.append(" ➞ ");
                sb.append(bus.getBusEndStationName());
                String sub = sb.toString();
                holder.textView_favorite_sub.setText(sub);
            }
            else {
                Toast.makeText(holder.mView.getContext(), "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT);
            }
        }
        else {
            StationItem station = StationController.getStationItem(item.getItemId());

            if (station != null) {
                String sub = station.getStationNumber();
                holder.textView_favorite_sub.setText(sub);
            }
            else {
                Toast.makeText(holder.mView.getContext(), "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT);
            }
        }

        if(position % 2 == 1) {
            holder.item_favorite.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_favorite.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView_favorite;
        public final TextView textView_favorite_sub;
        public final LinearLayout item_favorite;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView_favorite = (TextView) view.findViewById(R.id.textView_favorite);
            textView_favorite_sub = (TextView) view.findViewById(R.id.textView_favorite_sub);
            item_favorite = (LinearLayout) view.findViewById(R.id.item_favorite);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        FavoriteItem item = list.get(pos);
                    }
                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        FavoriteItem fav = list.get(pos);
                        if(fav.getFavoriteType().equals("bus")) {
                            BusItem item = BusController.getBusItem(fav.getItemId());
                            Context context = v.getContext();
                            if(item != null) {
                                Intent intent = new Intent(context, BusRouteActivity.class);
                                intent.putExtra("busId", item.getBusId());
                                intent.putExtra("busNumber", item.getBusNumber());
                                intent.putExtra("busStartStationName", item.getBusStartStationName());
                                intent.putExtra("busEndStationName", item.getBusEndStationName());
                                context.startActivity(intent);
                            }
                            else {
                                Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            StationItem item = StationController.getStationItem(fav.getItemId());
                            Context context = v.getContext();
                            if(item != null) {
                                Intent intent = new Intent(context, ArrivalActivity.class);
                                intent.putExtra("stationId", item.getStationId());
                                intent.putExtra("stationName", item.getStationName());
                                context.startActivity(intent);
                            }
                            else {
                                Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                }
            });
        }
    }
}
