package com.docreader.updater;

import java.util.List;

import android.graphics.Bitmap;
import android.widget.Button;

import com.docreader.api.request.BuilderRequestItem;
import com.docreader.api.request.BuilderRequestItem.Type;

public class ImageSegmentUpdater extends SegmentValueUpdater {
    
    @SuppressWarnings("unused")
    private Button button;
    private String filename;
    private List<Bitmap> files;
    private Bitmap bitmap;

    public ImageSegmentUpdater(Button button, String filename, List<Bitmap> requestImages) {
        this.button = button;
        this.filename = filename;
        this.files = requestImages;
    }
    
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    
    @Override
    public String toString() {
        return "ButtonRangeUpdater [filename=" + filename + "]";
    }

    @Override
    public BuilderRequestItem getRequestItem() {
        files.add(bitmap);
        return new BuilderRequestItem(Type.IMAGE, filename, bitmap.getWidth(), bitmap.getHeight());
    }

}