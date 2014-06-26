package com.docreader.updater;

import org.apache.poi.hwpf.usermodel.CharacterRun;

import android.widget.Button;

public class ImageRangeUpdater extends ParagraphRangeUpdater {
    
    private Button button;
    private String filename;

    public ImageRangeUpdater(Button button, String filename) {
        this.button = button;
        this.filename = filename;
    }
    
    public CharacterRun updateRange(CharacterRun range) {
        // range.replaceText("%IMAGE:" + filename + ":IMAGE%", false);
        range.setHighlighted((byte) 0);

        return range;
    }

    @Override
    public String toString() {
        return "ButtonRangeUpdater [filename=" + filename + "]";
    }

}