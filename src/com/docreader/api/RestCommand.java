package com.docreader.api;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import android.os.AsyncTask;
import android.util.Log;

import com.docreader.api.request.RequestCommand;
import com.docreader.api.request.RequestListener;
import com.docreader.api.response.BaseResponse;
import com.docreader.api.response.Response;

public class RestCommand<R extends BaseResponse> extends AsyncTask<Void, Void, Response<R>> {
    
    private static final String LOG_TAG = "RestCommand";
    
    private RestAdapter adapter;
    private RequestListener<R> listener;
    private RequestCommand<R> request;
    private String version;

    RestCommand(RestAdapter adapter, String version, RequestCommand<R> request, RequestListener<R> listener) {
        this.adapter = adapter;
        this.request = request;
        this.listener = listener;
        this.version = version;
    }

    @Override
    protected Response<R> doInBackground(Void... params) {
        Response<R> response = null;
        
        // TODO: add more checks
        try {
            response = request.request(version, adapter);
            // TODO: check for status
            if (null == response)
                return null;
            
        } catch (RetrofitError re) {
            retrofit.client.Response rr = re.getResponse();
            if (null != rr) {
                Log.e(LOG_TAG, rr.toString());
                rr.getStatus();
            } else {
                Log.e(LOG_TAG, "Response is null");
            }
            
            response = new Response<R>(new Error(0, "Server error"));
        } catch (Exception exception) {
            Log.d(LOG_TAG, "Error, while making request.");
            exception.printStackTrace();
        }
        
        return response;
    }

    @Override
    protected void onPostExecute(Response<R> result) {
        if (null != result && result.getStatus() == Response.Status.SUCCESS) {
            listener.onResponse(result.getContent(), request);
            listener.onComplete(request);
        } else {
            Error error = null == result ? new Error(0, "") : result.getError();
            
            listener.onError(request, error);
            listener.onComplete(request);
        }
    }

    @Override
    protected void onCancelled() {
        listener.onCancel(request);
        listener.onComplete(request);
    }
    
}
