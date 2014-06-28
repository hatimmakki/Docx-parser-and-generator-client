package com.docreader.api.request;

import java.io.File;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

import com.docreader.api.response.ParserResponse;

public class ParseCommand extends RequestCommand<ParserResponse> {
    
    private File file;

    public ParseCommand(File file) {
        this.file = file;
    }

    @Override
    protected ParserResponse doRequest(String version, RestAdapter adapter) {
        DocumentApi api = adapter.create(DocumentApi.class);
        ParserResponse result = api.parseDocument(new TypedFile("", file));
        
        return result;
    }

}
