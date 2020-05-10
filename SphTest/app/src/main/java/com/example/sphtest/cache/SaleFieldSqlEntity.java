package com.example.sphtest.cache;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SaleFieldSqlEntity extends RealmObject {

    public String type;
    @PrimaryKey
    public String id;
}
