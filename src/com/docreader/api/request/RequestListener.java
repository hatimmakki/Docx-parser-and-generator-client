package com.docreader.api.request;

import com.docreader.api.Error;
import com.docreader.api.response.BaseResponse;

public interface RequestListener<R extends BaseResponse> {
    
    public void onResponse(R response, RequestCommand<R> request);
    
    public void onError(RequestCommand<R> request, Error error);
    
    public void onCancel(RequestCommand<R> request);
    
    public void onComplete(RequestCommand<R> request);

}
