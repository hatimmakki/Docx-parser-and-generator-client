package com.docreader.api.request;

import com.docreader.api.response.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class BuilderRequestItem extends BaseResponse {

    public enum Type {
        @SerializedName("edit") EDIT,
        @SerializedName("date") DATE,
        @SerializedName("image") IMAGE
    }
    
    private Type type;
    private String content;
    private int w, h;

    public BuilderRequestItem(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public BuilderRequestItem(Type type, String content, int w, int h) {
        this.type = type;
        this.content = content;
        this.w = w;
        this.h = h;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

}
