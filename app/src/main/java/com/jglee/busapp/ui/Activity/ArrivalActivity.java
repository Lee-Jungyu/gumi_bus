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
import com.jglee.busapp.adapter.ArrivalActivityAdapter;
import com.jglee.busapp.controller.FavoriteController;
import com.jglee.busapp.controller.StationController;
import com.jglee.busapp.domain.ArrivalData;

import java.util.ArrayList;
import java.util.List;

public class ArrivalActivity extends AppCompatActivity {

    private String stationId;
    private String stationName;
    private String favoriteId = null;

    private TextView textView_arrival_station_name;
    private Button button_arrival_favorite;
    private Button button_arrival_reload;
    private RecyclerView recyclerView;
    private List<ArrivalData> list = new ArrayList<>();
    private ArrivalActivityAdapter adapter;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrival);

        Intent intent = getIntent();
        stationId = intent.getExtras().getString("stationId");
        stationName = intent.getExtras().getString("stationName");

        textView_arrival_station_name = (TextView) findViewById(R.id.textView_arrival_station_name);
        textView_arrival_station_name.setText(stationName);

        favoriteId = FavoriteController.findFavoriteId("station", stationId);
        button_arrival_favorite = (Button) findViewById(R.id.button_arrival_favorite);
        button_arrival_favorite.setText("★");
        button_arrival_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favoriteId == null) {
                    FavoriteController.insertFavorite("station", stationId, stationName);
                    button_arrival_favorite.setBackgroundColor(getResources().getColor(R.color.red_200));
                    favoriteId = FavoriteController.findFavoriteId("station", stationId);
                }
                else {
                    FavoriteController.deleteFavorite(favoriteId);
                    button_arrival_favorite.setBackgroundColor(getResources().getColor(R.color.skyBlue_500));
                    favoriteId = FavoriteController.findFavoriteId("station", stationId);
                }
            }
        });

        if(favoriteId == null) {
            button_arrival_favorite.setBackgroundColor(getResources().getColor(R.color.skyBlue_500));
        }
        else {
            button_arrival_favorite.setBackgroundColor(getResources().getColor(R.color.red_200));
        }


        button_arrival_reload = (Button) findViewById(R.id.button_arrival_reload);
        button_arrival_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(ArrivalActivity.this,"로딩 중..." , "도착 정보 로딩 중.");

                Thread thread = new Thread(null, reloadArrivalList);
                thread.start();
            }
        });
        button_arrival_reload.setText("↺");
        button_arrival_reload.setPadding(0,0,0,0);

        recyclerView = (RecyclerView) findViewById(R.id.list_arrival);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ArrivalActivityAdapter(list, stationName);
        recyclerView.setAdapter(adapter);

        Thread thread = new Thread(null, loadArrivalList);
        thread.start();
    }

    final Handler handler = new Handler();
    void showToast(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ArrivalActivity.this, text, Toast.LENGTH_SHORT).show();
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

    Runnable loadArrivalList = new Runnable() {
        @Override
        public void run() {
            List<ArrivalData> item = StationController.loadArrivalList(stationId);

            if(item != null) {
                list.clear();
                list.addAll(item);
                notifyChange();
            }
            else {
                showToast("인터넷을 확인해 주세요");
            }
        }
    };

    Runnable reloadArrivalList = new Runnable() {
        @Override
        public void run() {
            List<ArrivalData> item = StationController.loadArrivalList(stationId);

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
