package com.example.sphtest.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.sphtest.cache.SqlManager;
import com.example.sphtest.model.AppState;
import com.example.sphtest.model.SaleFieldEntity;
import com.example.sphtest.model.SaleRecordEntity;
import com.example.sphtest.net.Callback;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class SphViewModel {

    @SuppressLint("StaticFieldLeak")
    private volatile static SphViewModel mSelf;

    private Context mContext;

    private MutableLiveData<AppState> mAppStateLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SaleFieldEntity>> mSaleFieldLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SaleRecordEntity>> mSaleRecordLiveData = new MutableLiveData<>();

    public static void init(Context context){
        if (context == null){
            return;
        }
        if (mSelf == null){
            synchronized (SphViewModel.class){
                if (mSelf == null){
                    mSelf = new SphViewModel(context);
                }
            }
        }
    }

    public static SphViewModel getInstance(){
        return mSelf;
    }

    private SphViewModel(Context context){
        mContext = context;

        if (mAppStateLiveData.getValue() == null){
            mAppStateLiveData.setValue(new AppState());
        }
        if (mSaleFieldLiveData.getValue() == null){
            List<SaleFieldEntity> list = SqlManager.getInstance().getSaleFields();
            mSaleFieldLiveData.setValue(list);
        }
        if (mSaleRecordLiveData.getValue() == null){
            List<SaleRecordEntity> list = SqlManager.getInstance().getSaleRecords();
            mSaleRecordLiveData.setValue(list);
        }
    }

    public void subscribeAppState(@Nullable LifecycleOwner owner, @Nullable Observer<AppState> observer){
        mAppStateLiveData.observe(owner, observer);
    }

    public void subscribeSaleField(@Nullable LifecycleOwner owner, @Nullable Observer<List<SaleFieldEntity>> observer){
        mSaleFieldLiveData.observe(owner, observer);
    }

    public void subscribeSaleRecord(@Nullable LifecycleOwner owner, @Nullable Observer<List<SaleRecordEntity>> observer){
        mSaleRecordLiveData.observe(owner, observer);
    }



    public void loadSaleData(){
        new SaleDataApi(100).async(new Callback<SaleDataApi.SaleResponse>() {
            @Override
            public void onSuccess(SaleDataApi.SaleResponse response) {
                if (response != null && response.getResult() != null && !response.isDataError && !response.isNetError && response.success){
                    updateSaleNet(true);
                    SqlManager.getInstance().resetSaleFileds(response.getResult().getFields());
                    SqlManager.getInstance().resetSaleRecords(response.getResult().getRecords());
                    mSaleFieldLiveData.postValue(response.getResult().getFields());
                    mSaleRecordLiveData.postValue(response.getResult().getRecords());
                }else {
                    updateSaleNet(false);
                }
            }

            @Override
            public void onFail(SaleDataApi.SaleResponse response) {
                updateSaleNet(false);
            }
        });
    }

    private void updateSaleNet(boolean isOk){
        AppState appState = mAppStateLiveData.getValue();
        if (appState.mIsNetOk != isOk){
            appState.mIsNetOk = isOk;
            mAppStateLiveData.postValue(appState);
        }
    }
}
