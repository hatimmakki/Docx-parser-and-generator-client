package com.docreader.api.request;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import retrofit.RestAdapter;
import android.graphics.Bitmap;

import com.docreader.api.RestManager;
import com.docreader.api.request.BuildDocumentCommand.Response;
import com.docreader.api.response.BaseResponse;
import com.docreader.updater.SegmentValueUpdater;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
public class BuildDocumentCommand extends RequestCommand<Response> {
    
    private static final int BUF_SIZE = 0x1000;
    
    class RequestItems {
        Map<String, BuilderRequestItem> data = new HashMap<String, BuilderRequestItem>();
    }
    
    public class Response extends BaseResponse {
        final public File file;

        public Response(File file) {
            this.file = file;
        }
        
    }
    
    private File file;
    private Map<String, SegmentValueUpdater> updaters;
    private Map<String, Bitmap> images;
    private String resultFilename;

    public BuildDocumentCommand(File file, Map<String, SegmentValueUpdater> updaters, Map<String, Bitmap> images, String resultFilename) {
        this.file = file;
        this.updaters = updaters;
        this.images = images;
        this.resultFilename = resultFilename;
    }

    @Override
    protected Response doRequest(String version, RestAdapter adapter) {
        try {
            return new Response(sendPhoto());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public File sendPhoto() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(RestManager.SERVER + "/build");

        Gson gson = new Gson();

        RequestItems items = new RequestItems();
        for (String key : updaters.keySet()) {
            items.data.put(key, updaters.get(key).getRequestItem());
        }

        MultipartEntity mpEntity = new MultipartEntity();
        try {
            ContentBody type = new StringBody(gson.toJson(items));
            mpEntity.addPart("data", type);
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception);
        }

        for (String filename : images.keySet()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            images.get(filename).compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            mpEntity.addPart(filename, new ByteArrayBody(byteArray, filename));
        }
        
        mpEntity.addPart("file", new FileBody(file));

        httppost.setEntity(mpEntity);
        httppost.addHeader("accept-encoding", "gzip, deflate");
        httppost.addHeader("accept", "application/json; charset=UTF-8");

        HttpResponse response = httpclient.execute(httppost);
        InputStream data = response.getEntity().getContent();
        
        try {
            File file = new File(resultFilename);
            OutputStream output = new FileOutputStream(file);
            try {
                copy(data, output);
            } finally {
                try {
                    output.close();
                } catch (IOException impossible) {
                    throw new AssertionError(impossible);
                }
            }

            return file;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    public static long copy(InputStream from, OutputStream to) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }


}
