package com.docreader.updater;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import android.graphics.Bitmap;
import android.widget.Button;

public class ImageRangeUpdater extends ParagraphRangeUpdater {
    
    @SuppressWarnings("unused")
    private Button button;
    private Bitmap bitmap;

    public ImageRangeUpdater(Button button) {
        this.button = button;
    }
    
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, 200, (int) (bitmap.getHeight() * (200D / bitmap.getWidth())), false);
    }

    public XWPFRun updateRange(XWPFRun range) {
        if (null == bitmap) 
            return range;
        
        range.setText("", 0);
//        range.getCTR().addNewRPr().addNewHighlight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor.WHITE);

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            range.addPicture(new ByteArrayInputStream(stream.toByteArray()), XWPFDocument.PICTURE_TYPE_PNG, "image.png", Units.toEMU(200), Units.toEMU(200));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return range;
    }

    @Override
    public String toString() {
        return "ButtonRangeUpdater [bitmap=" + bitmap + "]";
    }

}