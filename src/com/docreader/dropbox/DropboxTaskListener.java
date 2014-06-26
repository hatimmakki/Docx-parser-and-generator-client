package com.docreader.dropbox;

public interface DropboxTaskListener<R> {
    public void onDownloadSuccess(R data);
    public void onDownloadError();
    public void onDownloadComplete();
    public void onDownloadProgress(int percent);
}