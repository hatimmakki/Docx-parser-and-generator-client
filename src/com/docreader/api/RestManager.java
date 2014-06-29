package com.docreader.api;

import org.androidannotations.annotations.EBean;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

import com.docreader.api.request.RequestCommand;
import com.docreader.api.request.RequestListener;

@EBean
public class RestManager {

    // Yes, I know about BuildConfig.DEBUG
//    public static final String SERVER = "http://192.168.56.1:4567";
    public static final String SERVER = "http://ryabenko.pro:4567";

    public RestCommand<?> createCommand(RequestCommand<?> request, RequestListener<?> listener) {
        RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(SERVER)
            .setLogLevel(LogLevel.FULL)
            .build();
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
        RestCommand<?> command = new RestCommand(adapter, "v1", request, listener);
        
        return command;
    }

}