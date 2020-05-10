package com.example.sphtest.viewmodel;

import android.content.Context;

import com.example.sphtest.cache.SqlManager;
import com.example.sphtest.net.HttpManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.base.MainThread;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SphViewModelTest {





    @Test
    @MainThread
    public void loadSaleData() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SqlManager.init(appContext);
        SphViewModel.init(appContext);
        HttpManager.init(3000);
        SphViewModel.init(appContext);

        SphViewModel.getInstance().subscribeAppState(null, null);
    }
}