package com.ndomx.dicom

import android.app.DatePickerDialog
import android.widget.DatePicker
import java.util.*

class DatePickListener: DatePickerDialog.OnDateSetListener
{
    var date = Calendar.getInstance().time!!

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
    {
        date = with (Calendar.getInstance()) {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            time
        }
    }
}