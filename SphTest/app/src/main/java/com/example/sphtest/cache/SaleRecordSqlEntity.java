package com.example.sphtest.cache;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleRecordSqlEntity extends RealmObject {

    public String volume_of_mobile_data;
    public String quarter;
    @PrimaryKey
    public String _id;
}
