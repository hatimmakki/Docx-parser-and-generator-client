package com.docreader.api.request;

import com.docreader.api.response.BaseResponse;
import com.docreader.api.Error;

abstract public class DefaultRequestListener<R extends BaseResponse> implements RequestListener<R> {
    
    @Override
    public void onError(RequestCommand<R> request, Error error) { }
    
    @Override
    public void onCancel(RequestCommand<R> request) { }
    
    @Override
    public void onComplete(RequestCommand<R> request) { }
    
}
