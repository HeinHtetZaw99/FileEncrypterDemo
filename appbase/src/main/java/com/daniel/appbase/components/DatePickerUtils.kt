package com.daniel.appbase.components

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerUtils : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)
        val dialog = DatePickerDialog(
            Objects.requireNonNull<Context>(context),
            context as DatePickerDialog.OnDateSetListener?,
            year,
            month,
            date
        )
        dialog.datePicker.maxDate = Date().time
        return dialog
    }
}
