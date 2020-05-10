package com.example.sphtest.cache;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.sphtest.model.SaleFieldEntity;
import com.example.sphtest.model.SaleRecordEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SqlManager {

    @SuppressLint("StaticFieldLeak")
    private volatile static SqlManager mSelf;
    private Realm mRealm;

    private Context mContext;

    public static void init(Context context){
        if (mSelf == null){
            synchronized (SqlManager.class){
                if (mSelf == null){
                    mSelf = new SqlManager(context);
                }
            }
        }
    }

    public static SqlManager getInstance(){
        return mSelf;
    }

    private SqlManager(Context context){
        mContext = context;

        Realm.init(mContext);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

        mRealm = Realm.getDefaultInstance();
    }


    public List<SaleFieldEntity> getSaleFields(){
        List<SaleFieldEntity> list = new ArrayList<>(0);
        if (mRealm != null){
            mRealm.beginTransaction();
            RealmResults<SaleFieldSqlEntity> results = mRealm.where(SaleFieldSqlEntity.class).findAll();
            if (results != null){
                List<SaleFieldSqlEntity> tmpList = mRealm.copyFromRealm(results);
                if (tmpList != null){
                    for (SaleFieldSqlEntity item : tmpList){
                        SaleFieldEntity newItem = new SaleFieldEntity();
                        newItem.setId(item.id);
                        newItem.setType(item.type);
                        list.add(newItem);
                    }
                }
            }
            mRealm.commitTransaction();
        }
        return list;
    }

    public List<SaleRecordEntity> getSaleRecords(){
        List<SaleRecordEntity> list = new ArrayList<>(0);
        if (mRealm != null){
            mRealm.beginTransaction();
            RealmResults<SaleRecordSqlEntity> results = mRealm.where(SaleRecordSqlEntity.class).findAll();
            if (results != null){
                List<SaleRecordSqlEntity> tmpList = mRealm.copyFromRealm(results);
                if (tmpList != null){
                    for (SaleRecordSqlEntity item : tmpList){
                        SaleRecordEntity newItem = new SaleRecordEntity();
                        newItem.set_id(item._id);
                        newItem.setQuarter(item.quarter);
                        newItem.setVolume_of_mobile_data(item.volume_of_mobile_data);
                        list.add(newItem);
                    }
                }
            }
            mRealm.commitTransaction();
        }
        return list;
    }

    public void resetSaleFileds(List<SaleFieldEntity> list){
        mRealm.beginTransaction();
        mRealm.delete(SaleFieldSqlEntity.class);
        if (list != null && list.size() > 0){
            List<SaleFieldSqlEntity> newList = new ArrayList<>(list.size());
            for (SaleFieldEntity item : list){
                SaleFieldSqlEntity newItem = new SaleFieldSqlEntity();
                newItem.id = item.getId();
                newItem.type = item.getType();
                newList.add(newItem);
            }
            mRealm.copyToRealmOrUpdate(newList);
        }
        mRealm.commitTransaction();

    }

    public void resetSaleRecords(List<SaleRecordEntity> list){
        mRealm.beginTransaction();
        mRealm.delete(SaleRecordSqlEntity.class);
        if (list != null && list.size() > 0){
            List<SaleRecordSqlEntity> newList = new ArrayList<>(list.size());
            for (SaleRecordEntity item : list){
                SaleRecordSqlEntity newItem = new SaleRecordSqlEntity();
                newItem._id = item.get_id();
                newItem.quarter = item.getQuarter();
                newItem.volume_of_mobile_data = item.getVolume_of_mobile_data();
                newList.add(newItem);
                mRealm.copyToRealmOrUpdate(newList);
            }
        }
        mRealm.commitTransaction();

    }
}
