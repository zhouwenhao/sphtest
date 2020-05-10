package com.example.sphtest.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleRecordEntity{
    /**
     *  {
     *         "volume_of_mobile_data": "0.000384",
     *         "quarter": "2004-Q3",
     *         "_id": 1
     *       }
     */

    private String volume_of_mobile_data;
    private String quarter;
    private String _id;

    public String getVolume_of_mobile_data() {
        return volume_of_mobile_data;
    }

    public void setVolume_of_mobile_data(String volume_of_mobile_data) {
        this.volume_of_mobile_data = volume_of_mobile_data;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
