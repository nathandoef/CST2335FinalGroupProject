package com.example.joe.cst2335finalgroupproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Custom DatePicker fragment that is used to select the date a fuel purchase was made
 * @author 		Nathan Doef
 * @version		2
 */
public class c_DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    /** Activity where the DatePicker should be loaded */
    private Activity parentActivity;

    /** Edit Text to display the date selected from the DatePicker */
    private EditText display;

    /** Calendar object used to handle date functionality */
    private Calendar calendar = Calendar.getInstance();

    /**
     * creates a new instance of a Calendar and sets the date to the current date
     * @param savedInstanceState
     * @return DatePicker fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(parentActivity, this, year, month, day);
    }

    /**
     * Gets a handle to the parent activity
     * @param activity
     */
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = activity;
    }

    /**
     * displays the date selected in the Edit Text
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        display.setText("");
        display.setText(c_CarTrackerActivity.DD_MM_YYYY.format(calendar.getTime()));
    }

    /**
     * connects the Edit Text to the DatePicker
     * @param editText
     */
    public void setDisplay(EditText editText) {
        this.display = editText;
    }
}