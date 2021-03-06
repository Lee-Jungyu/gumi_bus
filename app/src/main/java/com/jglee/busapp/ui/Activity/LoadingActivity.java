package com.jglee.busapp.ui.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.jglee.busapp.R;
import com.jglee.busapp.controller.BusController;
import com.jglee.busapp.controller.StationController;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream  caInput = new BufferedInputStream(new FileInputStream("load-der.cret"));
        } catch (CertificateException | FileNotFoundException e) {
            e.printStackTrace();
        }

        Thread thread1 = new Thread(null, loadAllBusList);
        thread1.start();

        Thread thread2 = new Thread(null, loadAllStationList);
        thread2.start();

        startLoading();
    }

    Runnable loadAllBusList = new Runnable() {
        @Override
        public void run() {
            BusController.loadBusList(getBaseContext());
        }
    };

    Runnable loadAllStationList = new Runnable() {
        @Override
        public void run() {
            StationController.loadStationList(getBaseContext());
        }
    };

    private void startLoading() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
