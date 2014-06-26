package com.docreader.updater;

import org.apache.poi.hwpf.usermodel.CharacterRun;

import android.widget.EditText;

public class EditTextRangeUpdater extends ParagraphRangeUpdater {
    
    private EditText editText;

    public EditTextRangeUpdater(EditText editText) {
        this.editText = editText;
    }
    
    public CharacterRun updateRange(CharacterRun range) {
        range.setHighlighted((byte) 0);
        range.replaceText(editText.getText().toString(), false);
        return range;
    }


    @Override
    public String toString() {
        return "EditRangeUpdater [editText=" + editText.getText().toString() + "]";
    }
    
}