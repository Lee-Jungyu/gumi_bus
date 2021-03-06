package com.jglee.busapp.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jglee.busapp.R;
import com.jglee.busapp.adapter.RouteFragmentAdapter;
import com.jglee.busapp.controller.RouteController;
import com.jglee.busapp.domain.RouteItem;

import java.util.ArrayList;
import java.util.List;

public class RouteFragment extends Fragment {

    private EditText editText_source;
    private EditText editText_dest;
    private TextView textView_source;
    private TextView textView_dest;
    private Button button_find_source_pose;
    private Button button_find_dest_pose;
    private Button button_search_route;
    private RecyclerView recyclerView;
    private RouteFragmentAdapter adapter;
    private List<RouteItem> list = new ArrayList<>();

    private Location source;
    private Location dest;

    private ProgressDialog progressDialog;

    public RouteFragment() {

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        //initialize views
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        editText_source = (EditText) view.findViewById(R.id.editText_source);
        editText_dest = (EditText) view.findViewById(R.id.editText_dest);

        textView_source = (TextView) view.findViewById(R.id.textView_source);
        textView_dest = (TextView) view.findViewById(R.id.textView_dest);

        button_find_source_pose = (Button) view.findViewById(R.id.button_find_source_pose);
        button_find_source_pose.setPadding(0,0,0,0);
        button_find_source_pose.setText("\uD83D\uDD0D");
        button_find_source_pose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(null, getSourceLocation);
                thread.start();
            }
        });

        button_find_dest_pose = (Button) view.findViewById(R.id.button_find_dest_pose);
        button_find_dest_pose.setPadding(0,0,0,0);
        button_find_dest_pose.setText("\uD83D\uDD0D");
        button_find_dest_pose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(null, getDestLocation);
                thread.start();
            }
        });

        button_search_route = (Button) view.findViewById(R.id.button_search_route);
        button_search_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if(source == null) {
                    showToast("???????????? ??????????????????.");
                    return;
                }
                if(dest == null) {
                    showToast("???????????? ??????????????????");
                    return;
                }

                progressDialog = ProgressDialog.show(getContext(),"?????? ?????? ???..." , "????????? ???????????? ????????????.");
                Thread thread = new Thread(null, getRoute);
                thread.start();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.list_route);
        recyclerView.setHasFixedSize(true);

        //setting adapter
        adapter = new RouteFragmentAdapter(list, textView_dest.getText().toString());
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

    private Runnable getSourceLocation = new Runnable() {
        @Override
        public void run() {
            String text_source = editText_source.getText().toString();
            if(text_source.equals("")) {
                showToast("???????????? ???????????? ????????????.");
                return;
            }

            source = RouteController.findGeoPose(getContext(), text_source);

            if(source == null) {
                showToast("???????????? ????????????");
            }
            else {
                textView_source.setText(editText_source.getText().toString());
                showToast("?????? ??????");
            }
        }
    };

    private Runnable getDestLocation = new Runnable() {
        @Override
        public void run() {
            String text_dest = editText_dest.getText().toString();
            if(text_dest.equals("")) {
                showToast("???????????? ???????????? ????????????.");
                return;
            }

            dest = RouteController.findGeoPose(getContext(), text_dest);

            if(dest == null) {
                showToast("???????????? ????????????");
            }
            else {
                textView_dest.setText(editText_dest.getText().toString());
                showToast("?????? ??????");
                adapter.setDest(textView_dest.getText().toString());
            }
        }
    };

    private Runnable getRoute = new Runnable() {
        @Override
        public void run() {

            List<RouteItem> item = RouteController.getRoute(getContext(), source, dest);

            if(item != null) {
                list.clear();
                list.addAll(item);
                notifyChange();
            }
            else {
                showToast("???????????? ????????? ?????????");
            }
            progressDialog.dismiss();
        }
    };
}
