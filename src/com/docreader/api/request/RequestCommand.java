package com.docreader.api.request;

import retrofit.RestAdapter;

import com.docreader.api.response.BaseResponse;
import com.docreader.api.response.Response;

abstract public class RequestCommand<R extends BaseResponse> {
    
    public Response<R> request(String version, RestAdapter adapter) {
        return new Response<R>(doRequest(version, adapter));
    }

    abstract protected R doRequest(String version, RestAdapter adapter);  
    
}
