package com.docreader.api;

public class Error {
    private int error;
    private String message;

    public Error(int error, String message) {
        this.error = error;
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error [error=" + error + ", message=" + message + "]";
    }
    
}

