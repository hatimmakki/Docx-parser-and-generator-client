package com.docreader.updater;

import java.util.GregorianCalendar;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.docreader.api.request.BuilderRequestItem;
import com.docreader.api.request.BuilderRequestItem.Type;
import com.docreader.util.DatePickerFragment;
import com.docreader.util.DatePickerFragment.DatePickerListener;
import com.docreader.util.UtilDate;

public class DatepickerSegmentUpdater extends SegmentValueUpdater implements DatePickerListener {
    
    private String date = "";
    private Button button;

    public DatepickerSegmentUpdater(final FragmentManager fragmentManager, Button button) {
        this.button = button;
        button.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                DatePickerFragment dpf = new DatePickerFragment();
                dpf.setListener(DatepickerSegmentUpdater.this); 
                dpf.show(fragmentManager, "date-picker");
            }
        });

    }
    
    @Override
    public String toString() {
        return "ButtonRangeUpdater [date=" + date + "]";
    }
    
    @Override
    public void onDateSet(int year, int month, int day) {
        GregorianCalendar cal = new GregorianCalendar(year, month, day);
        
        date = UtilDate.formatDate(cal);
        button.setText(date);
    }

    @Override
    public BuilderRequestItem getRequestItem() {
        return new BuilderRequestItem(Type.DATE, date);
    }
    
}