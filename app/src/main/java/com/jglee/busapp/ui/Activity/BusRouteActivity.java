package com.jglee.busapp.ui.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.adapter.BusRouteActivityAdapter;
import com.jglee.busapp.controller.BusController;
import com.jglee.busapp.controller.FavoriteController;
import com.jglee.busapp.domain.BusRouteData;

import java.util.ArrayList;
import java.util.List;

public class BusRouteActivity extends AppCompatActivity {

    private TextView textView_bus_route_bus_name;
    private Button button_bus_route_favorite;
    private Button button_bus_route_reload;
    private RecyclerView recyclerView;
    private List<BusRouteData> list = new ArrayList<>();
    private BusRouteActivityAdapter adapter;

    private ProgressDialog progressDialog;

    private String busId;
    private String busNumber;
    private String busStartStationName;
    private String busEndStationName;
    private String favoriteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        Intent intent = getIntent();
        busId = intent.getExtras().getString("busId");
        busNumber = intent.getExtras().getString("busNumber");
        busStartStationName = intent.getExtras().getString("busStartStationName");
        busEndStationName = intent.getExtras().getString("busEndStationName");

        textView_bus_route_bus_name = (TextView) findViewById(R.id.textView_bus_route_bus_name);
        textView_bus_route_bus_name.setText(busNumber);

        favoriteId = FavoriteController.findFavoriteId("bus", busId);
        button_bus_route_favorite = (Button) findViewById(R.id.button_bus_route_favorite);
        button_bus_route_favorite.setText("★");
        button_bus_route_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteId == null) {
                    FavoriteController.insertFavorite("bus", busId, busNumber);
                    button_bus_route_favorite.setBackgroundColor(getResources().getColor(R.color.red_200));
                    favoriteId = FavoriteController.findFavoriteId("bus", busId);
                }
                else {
                    FavoriteController.deleteFavorite(favoriteId);
                    button_bus_route_favorite.setBackgroundColor(getResources().getColor(R.color.skyBlue_500));
                    favoriteId = FavoriteController.findFavoriteId("bus", busId);
                }
            }
        });

        if(favoriteId == null) {
            button_bus_route_favorite.setBackgroundColor(getResources().getColor(R.color.skyBlue_500));
        }
        else {
            button_bus_route_favorite.setBackgroundColor(getResources().getColor(R.color.red_200));
        }

        button_bus_route_reload = (Button) findViewById(R.id.button_bus_route_reload);
        button_bus_route_reload.setText("↺");
        button_bus_route_reload.setPadding(0,0,0,0);
        button_bus_route_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(BusRouteActivity.this,"로딩 중..." , "도착 정보 로딩 중.");

                Thread thread = new Thread(null, reloadBusRouteList);
                thread.start();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.list_bus_route);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BusRouteActivityAdapter(list);
        recyclerView.setAdapter(adapter);

        Thread thread = new Thread(null, loadBusRouteList);
        thread.start();
    }

    final Handler handler = new Handler();
    void showToast(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BusRouteActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void notifyChange() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    Runnable loadBusRouteList = new Runnable() {
        @Override
        public void run() {
            List<BusRouteData> item = BusController.loadBusRouteList(getApplicationContext(), busId);

            if(item != null) {
                list.clear();
                list.addAll(item);
                notifyChange();
            }
            else {
                showToast("인터넷을 확인해 주세요.");
            }
        }
    };

    Runnable reloadBusRouteList = new Runnable() {
        @Override
        public void run() {
            List<BusRouteData> item = BusController.loadBusRouteList(getApplicationContext(), busId);

            if(item != null) {
                list.clear();
                list.addAll(item);
                notifyChange();
            }
            else {
                showToast("인터넷을 확인해 주세요");
            }

            progressDialog.dismiss();
        }
    };
}
