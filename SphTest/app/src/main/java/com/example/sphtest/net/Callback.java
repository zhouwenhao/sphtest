package com.example.sphtest.net;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Callback<T> {

    abstract public void onSuccess(T response);

    abstract public void onFail(T response);

    public Class getT(){
        Type t = this.getClass().getGenericSuperclass();
        System.out.println(t);
        if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
            ParameterizedType p=(ParameterizedType)t;
            return (Class) p.getActualTypeArguments()[0];
        }
        return this.getClass();
    }
}
