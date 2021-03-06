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
import com.jglee.busapp.adapter.BusListFragmentAdapter;
import com.jglee.busapp.controller.BusController;
import com.jglee.busapp.domain.BusItem;

import java.util.ArrayList;
import java.util.List;

public class BusListFragment extends Fragment {

    private EditText editText_bus;
    private RecyclerView recyclerView;
    private BusListFragmentAdapter adapter;
    private List<BusItem> list = new ArrayList<>();
    private boolean onLoad = false;

    public BusListFragment() { }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_list, container, false);

        editText_bus = (EditText) view.findViewById(R.id.editText_bus);
        editText_bus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Thread thread = new Thread(null, getBusList);
                thread.start();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if(!onLoad) {
            Thread thread = new Thread(null, loadAllBusList);
            thread.start();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.list_bus);
        recyclerView.setHasFixedSize(true);

        adapter = new BusListFragmentAdapter(list);
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

    private Runnable loadAllBusList = new Runnable() {
        @Override
        public void run() {
            List<BusItem> item = BusController.getBusList("");

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

    private Runnable getBusList = new Runnable() {
        @Override
        public void run() {
            List<BusItem> item = BusController.getBusList(editText_bus.getText().toString());

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
