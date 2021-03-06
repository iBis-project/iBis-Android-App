package de.opencyclecompass.app.android;


import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimePickedListener mOPTListener;

    public void onTimeSet(TimePicker view, int hour, int minute) {
        this.mOPTListener.onTimePicked(hour, minute);
    }

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(this.getActivity());
        try {
            this.mOPTListener = (OnTimePickedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimePickedListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // use current time as default
        final Calendar c = Calendar.getInstance();
        int current_hour = c.get(Calendar.HOUR_OF_DAY);
        int current_minute = c.get(Calendar.MINUTE);

        // create and return new instance
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, current_hour, current_minute, DateFormat.is24HourFormat(getActivity()));
        tpd.setTitle("Gewünschte Ankunftszeit");
        return tpd;
    }

    public interface OnTimePickedListener {
        void onTimePicked(int hour, int minute);
    }
}