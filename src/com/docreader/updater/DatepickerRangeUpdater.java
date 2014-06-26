package com.docreader.updater;

import java.util.GregorianCalendar;

import org.apache.poi.hwpf.usermodel.CharacterRun;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.docreader.util.DatePickerFragment;
import com.docreader.util.DatePickerFragment.DatePickerListener;
import com.docreader.util.UtilDate;

public class DatepickerRangeUpdater extends ParagraphRangeUpdater implements DatePickerListener {
    
    private String date = "";
    private Button button;

    public DatepickerRangeUpdater(final FragmentManager fragmentManager, Button button) {
        this.button = button;
        button.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                DatePickerFragment dpf = new DatePickerFragment(DatepickerRangeUpdater.this); 
                dpf.show(fragmentManager, "date-picker");
            }
        });

    }

    @Override
    public CharacterRun updateRange(CharacterRun range) {
        range.replaceText(date, false);
        range.setHighlighted((byte) 0);
        return range;
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
    
}