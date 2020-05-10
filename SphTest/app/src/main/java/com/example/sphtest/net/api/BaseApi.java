package com.example.sphtest.net.api;

import com.example.sphtest.net.Callback;
import com.example.sphtest.net.HttpManager;
import com.example.sphtest.net.tag.Response;

/**
 * 各种API命名规范   以项目后面的全部字符串命名+Api
 *
 * 比如：https://xxx.com/programname/search
 * 命令为SearchApi
 */
public class BaseApi<T extends BaseApi.BaseResponse> {

    public void async(Callback callback){
        HttpManager.async(this, callback);
    }

    public T sync(){
        return (T)HttpManager.sync(this);
    }

    @Response
    public static class BaseResponse {
        public boolean isDataError = false;
        public boolean isNetError = false;

        public String help;
        public boolean success;
    }
}
