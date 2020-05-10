package com.example.sphtest;

import android.content.Context;

import com.example.sphtest.cache.SqlManager;
import com.example.sphtest.net.HttpManager;
import com.example.sphtest.viewmodel.SphViewModel;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.sphtest", appContext.getPackageName());

        SqlManager.init(appContext);
        SphViewModel.init(appContext);
        HttpManager.init(3000);
        SphViewModel.init(appContext);

    }
}
