package com.jglee.busapp.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.adapter.FavoritesFragmentAdapter;
import com.jglee.busapp.controller.FavoriteController;
import com.jglee.busapp.domain.FavoriteItem;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesFragmentAdapter adapter;
    private List<FavoriteItem> list = new ArrayList<>();

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_favorite);
        recyclerView.setHasFixedSize(true);

        adapter = new FavoritesFragmentAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        Thread thread = new Thread(null, getFavoriteList);
        thread.start();

        return view;
    }

    @Override
    public void onStart() {
        List<FavoriteItem> item = FavoriteController.getAllFavoriteList();

        list.clear();
        list.addAll(item);
        notifyChange();

        super.onStart();
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

    Runnable getFavoriteList = new Runnable() {
        @Override
        public void run() {
            List<FavoriteItem> item = FavoriteController.getAllFavoriteList();

            list.clear();
            list.addAll(item);
            notifyChange();
        }
    };
}
