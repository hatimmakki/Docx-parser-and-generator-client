package com.docreader.updater;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import android.widget.EditText;

public class EditTextRangeUpdater extends ParagraphRangeUpdater {
    
    private EditText editText;

    public EditTextRangeUpdater(EditText editText) {
        this.editText = editText;
    }
    
    public XWPFRun updateRange(XWPFRun range) {
        range.setText(editText.getText().toString(), 0);
//        range.getCTR().addNewRPr().addNewHighlight().setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor.WHITE);
        return range;
    }

    @Override
    public String toString() {
        return "EditRangeUpdater [editText=" + editText.getText().toString() + "]";
    }
    
}