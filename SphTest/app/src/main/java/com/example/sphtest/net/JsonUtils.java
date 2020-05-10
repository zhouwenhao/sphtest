package com.example.sphtest.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2019/5/8.
 */

public class JsonUtils {

    public static<T> String objToJson(T obj){
        return JSON.toJSONString(obj);
    }

    public static<T> T jsonToObj(String jsonStr, Class claz){
        return (T)JSON.parseObject(jsonStr, claz);
    }

    public static JSONObject jsonToJsonObj(String jsonStr){
        return JSONObject.parseObject(jsonStr);
    }
}
