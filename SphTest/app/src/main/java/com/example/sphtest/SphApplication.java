package com.example.sphtest;

import android.app.Application;

import com.example.sphtest.cache.SqlManager;
import com.example.sphtest.net.HttpManager;
import com.example.sphtest.viewmodel.SphViewModel;

public class SphApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SqlManager.init(this);
        SphViewModel.init(this);
        HttpManager.init(3000);
        SphViewModel.init(this);
    }
}
