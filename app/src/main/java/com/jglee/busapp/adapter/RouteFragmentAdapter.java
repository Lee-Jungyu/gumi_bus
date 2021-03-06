package com.jglee.busapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.controller.RouteController;
import com.jglee.busapp.domain.RouteItem;
import com.jglee.busapp.ui.Activity.RouteMapActivity;

import java.util.List;

public class RouteFragmentAdapter extends RecyclerView.Adapter<RouteFragmentAdapter.ViewHolder> {

    private List<RouteItem> list;
    private String dest;

    public RouteFragmentAdapter(List<RouteItem> list, String dest) {
        this.list = list;
        this.dest = dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteItem item = list.get(position);

        SpannableStringBuilder routeStr = RouteController.getRouteRow(item, dest);
        if(routeStr != null) holder.textView.setText(routeStr);
        else holder.textView.setText("");

        if(position % 2 == 1) {
            holder.item_route.setBackgroundColor(Color.parseColor("#dddddd"));
        }
        else {
            holder.item_route.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView textView;
        public final LinearLayout item_route;
        public final ImageButton button_route_map;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            textView = (TextView) view.findViewById(R.id.textView_route);
            item_route = (LinearLayout) view.findViewById(R.id.item_route);
            button_route_map = (ImageButton) view.findViewById(R.id.button_route_map);
            button_route_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        RouteItem item = list.get(pos);
                        Context context = mView.getContext();
                        if(item != null) {
                            Intent intent = new Intent(context, RouteMapActivity.class);
                            intent.putExtra("source_lat", item.getSource().getLatitude());
                            intent.putExtra("source_lon", item.getSource().getLongitude());

                            intent.putExtra("dest_lat", item.getDest().getLatitude());
                            intent.putExtra("dest_lon", item.getDest().getLongitude());

                            intent.putExtra("info", item.getInfo().toString());
                            intent.putExtra("subPathList", item.getSubPathList().toString());

                            context.startActivity(intent);
                        }
                    }
                }
            });
        }

    }
}
