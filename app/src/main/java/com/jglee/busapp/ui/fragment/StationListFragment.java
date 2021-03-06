package com.jglee.busapp.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.adapter.StationListFragmentAdapter;
import com.jglee.busapp.controller.StationController;
import com.jglee.busapp.domain.StationItem;

import java.util.ArrayList;
import java.util.List;

public class StationListFragment extends Fragment {

    private EditText editText_station;
    private RecyclerView recyclerView;
    private StationListFragmentAdapter adapter;
    private List<StationItem> list = new ArrayList<>();
    private boolean onLoad = false;

    public StationListFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_list, container, false);

        editText_station = (EditText) view.findViewById(R.id.editText_station);
        editText_station.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Thread thread = new Thread(null, getStationList);
                thread.start();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if(!onLoad) {
            Thread thread = new Thread(null, loadAllStationList);
            thread.start();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.list_station);
        recyclerView.setHasFixedSize(true);

        adapter = new StationListFragmentAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    final Handler handler = new Handler();
    void showToast(final CharSequence text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
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

    private Runnable loadAllStationList = new Runnable() {
        @Override
        public void run() {
            List<StationItem> item = StationController.getStationList("");

            if(item != null) {
                list.clear();
                list.addAll(item);
                notifyChange();
                onLoad = true;
            }
            else {
                showToast("인터넷을 확인해 주세요");
            }
        }
    };


    private Runnable getStationList = new Runnable() {
        @Override
        public void run() {

            List<StationItem> item = StationController.getStationList(editText_station.getText().toString());

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
}
