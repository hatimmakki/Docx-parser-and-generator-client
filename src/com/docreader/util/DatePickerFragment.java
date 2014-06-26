package com.docreader.util;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        public void onDateSet(int year, int month, int day);        
    }

    private DatePickerListener listener;
    protected boolean cancelled;
    
    public DatePickerFragment(DatePickerListener listener) {
        this.listener = listener;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.setCancelable(true);
        
        if (hasJellyBeanAndAbove()) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getActivity().getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelled = true;
                }
            });
        }

        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (!cancelled && null != listener)
            listener.onDateSet(year, month, day);
    }
    
    private static boolean hasJellyBeanAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    
}
