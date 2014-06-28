package com.docreader.api.response;

import com.docreader.api.Error;

public class Response<R extends BaseResponse> {

    public enum Status {
        NEW, SUCCESS, ERROR
    }

    private Status status = Status.NEW;

    private R content;

    private Error error;

    public Response(R content) {
        status = Status.SUCCESS;
        this.content = content;
    }

    public Response(Error error) {
        status = Status.ERROR;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public Error getError() {
        return error;
    }
    
    public R getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Response [status=" + status + ", content=" + content + ", error=" + error + "]";
    }
    
}
