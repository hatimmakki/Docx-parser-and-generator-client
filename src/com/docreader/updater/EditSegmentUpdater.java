package com.docreader.updater;

import android.widget.EditText;

import com.docreader.api.request.BuilderRequestItem;
import com.docreader.api.request.BuilderRequestItem.Type;

public class EditSegmentUpdater extends SegmentValueUpdater {
    
    private EditText editText;

    public EditSegmentUpdater(EditText editText) {
        this.editText = editText;
    }

    @Override
    public String toString() {
        return "EditRangeUpdater [editText=" + editText.getText().toString() + "]";
    }

    @Override
    public BuilderRequestItem getRequestItem() {
        return new BuilderRequestItem(Type.EDIT, editText.getText().toString());
    }
    
}