package com.example.sphtest.net;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.example.sphtest.net.api.BaseApi;
import com.example.sphtest.net.tag.Api;
import com.example.sphtest.net.tag.ParamBody;
import com.example.sphtest.net.tag.ParamForm;
import com.example.sphtest.net.tag.ParamGet;
import com.example.sphtest.net.tag.ParamHead;
import com.example.sphtest.net.tag.Response;


import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * @ProjectName: program
 * @Package: com.iflytek.autofly.program.common.http
 * @ClassName: HttpManager
 * @Description: 网络请求控制
 * @Author: whzhou
 * @CreateDate: 2019/5/6 17:48
 */
public class HttpManager {

    private static final String TAG = "HttpManager";

    private static OkHttpClient mOkHttpClient;
    private static Handler mMainHandler;

    public synchronized static void init(int timeOut){
        mOkHttpClient = new OkHttpClient.Builder()
                .callTimeout(timeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(timeOut, TimeUnit.MILLISECONDS)
                .readTimeout(timeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(timeOut, TimeUnit.MILLISECONDS)
                .build();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public static<T> void async(Object data, final Callback callback) throws Resources.NotFoundException {
        String inter = getApi(data);
        Class dataClaz = getResClass(data);
        if (TextUtils.isEmpty(inter)){
            throw new NullPointerException("inter must be not null");
        } else if (dataClaz.isAnnotationPresent(Response.class)){
            final Request request = requestToParams(data).addHeader("Connection", "keep-alive").build();
            mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handlErrorMain(callback);
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    try {
                        handlSucMain(generateResponse(response.body().string(), callback.getT()), callback);
                    }catch (Exception e){
                        e.printStackTrace();
                        handlErrorMain(callback);
                    }
                }
            });
        }else {
            throw new Resources.NotFoundException("Callback泛型必须为ResponseData注解型");
        }
    }

    public static BaseApi.BaseResponse sync(Object data) throws Resources.NotFoundException{
        String inter = getApi(data);
        Class resClaz = getResClass(data);
        if (TextUtils.isEmpty(inter)){
            throw new NullPointerException("inter must be not null");
        } else if (resClaz.isAnnotationPresent(Response.class)){
            Request request = requestToParams(data).build();
            try {
                okhttp3.Response response = mOkHttpClient.newCall(request).execute();
                return generateResponse(response.body().string(), resClaz);
            }catch (Exception e){
                try {
                    BaseApi.BaseResponse response = (BaseApi.BaseResponse) resClaz.newInstance();
                    response.isNetError = true;
                    return response;
                }catch (Exception e1){
                    return null;
                }
            }
        }else {
            throw new Resources.NotFoundException("Callback泛型必须为ResponseData注解型");
        }
    }

    private static BaseApi.BaseResponse generateResponse(String jsonStr, Class responseClaz){
        try {
            BaseApi.BaseResponse response = (BaseApi.BaseResponse) responseClaz.newInstance();
            if (TextUtils.isEmpty(jsonStr)) {
                response.isDataError = true;
                return response;
            }
            return JsonUtils.jsonToObj(jsonStr, responseClaz);
        } catch (Exception e){

        }
        return null;
    }

    private static void handlErrorMain(final Callback callback){
        if (callback != null && mMainHandler != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        BaseApi.BaseResponse response = (BaseApi.BaseResponse)callback.getT().newInstance();
                        response.isNetError = true;
                        callback.onFail(response);
                    }catch (Exception e){

                    }
                }
            });
        }
    }

    private static<T> void handlSucMain(final T response, final Callback callback){
        if (callback != null && mMainHandler != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(response);
                }
            });
        }
    }

    /**
     * 反射机制获取API
     * @param request
     * @return
     */
    private static String getApi(Object request){
        String api = "";
        Field[] fields = request.getClass().getDeclaredFields();
        for (Field f : fields){
            if (f.isAnnotationPresent(Api.class)){
                try {
                    f.setAccessible(true);
                    api = f.get(request).toString();
                }catch (Exception e){

                }
                break;
            }
        }
        return api;
    }

    /**
     * 反射机制获取返回data类型
     * @param request
     * @return
     */
    private static Class getResClass(Object request){
        Class dataClaz = Object.class;
        Class[] classes = request.getClass().getDeclaredClasses();
        for (Class claz : classes){
            if (claz.isAnnotationPresent(Response.class)){
                dataClaz = claz;
                break;
            }
        }
        return dataClaz;
    }

    private static Request.Builder requestToParams(Object request){
        Field[] fields = request.getClass().getDeclaredFields();
        StringBuffer sb = new StringBuffer();
        Request.Builder builder = new Request.Builder();
        JSONObject jsonObject = new JSONObject();
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        String url = "";
        sb.append("?");
        for (Field f : fields){
            if (f.isAnnotationPresent(Api.class)){
                try {
                    f.setAccessible(true);
                    url = f.get(request).toString();
                }catch (Exception e){

                }
            } else if (f.isAnnotationPresent(ParamGet.class)){
                try {
                    String key = URLEncoder.encode(f.getName(), "UTF-8");
                    f.setAccessible(true);
                    String value = URLEncoder.encode(f.get(request).toString(), "UTF-8");
                    sb.append(key + "=" + value + "&");
                }catch (Exception e){

                }
            }else if (f.isAnnotationPresent(ParamHead.class)){
                try {
                    String key = URLEncoder.encode(f.getName(), "UTF-8");
                    f.setAccessible(true);
                    String value = URLEncoder.encode(f.get(request).toString(), "UTF-8");
                    builder.addHeader(key, value);
                }catch (Exception e){

                }
            }else if (f.isAnnotationPresent(ParamBody.class)){
                try {
                    String key = URLEncoder.encode(f.getName(), "UTF-8");
                    f.setAccessible(true);
                    jsonObject.put(key, f.get(request));
                }catch (Exception e){

                }
            }else if (f.isAnnotationPresent(ParamForm.class)){
                try {
                    String key = URLEncoder.encode(f.getName(), "UTF-8");
                    f.setAccessible(true);
                    String value = URLEncoder.encode(f.get(request).toString(), "UTF-8");
                    formBodyBuilder.add(key, value);
                }catch (Exception e){

                }
            }
        }
        sb.deleteCharAt(sb.length()-1);

        url = leftAndParams(url, sb.toString());

        builder.get();

        String jsonBody = "";
        if (jsonObject.size() > 0){
            jsonBody = jsonObject.toString();
        }
        if (!TextUtils.isDigitsOnly(jsonBody)){
            RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);
            builder.post(body);
        }else {
            builder.post(formBodyBuilder.build());
        }

        builder.url(url);
        return builder;
    }

    /**
     * 拼接完整URL
     * @param left
     * @param params
     * @return
     */
    private static String leftAndParams(String left, String params){
        String url = "";
        if (TextUtils.isEmpty(params)){
            url = left;
        }else if (left.endsWith("?") && params.startsWith("?")){
            url = left.substring(0, left.length()-1) + params;
        }else if (!left.endsWith("?") && !params.startsWith("?")){
            url = left + "?" + params;
        }else {
            url = left + params;
        }
        return url;
    }
}
