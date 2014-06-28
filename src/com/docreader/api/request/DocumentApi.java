package com.docreader.api.request;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

import com.docreader.api.response.ParserResponse;

public interface DocumentApi {
    
    @Multipart
    @POST("/upload")
    public ParserResponse parseDocument(@Part("file") TypedFile file);
    
    

}
