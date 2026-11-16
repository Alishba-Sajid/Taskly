package com.example.taskly.core.extentionfunc

import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import java.util.Calendar

fun Fragment.showDatePicker(
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        requireContext(),
        { _, year, month, dayOfMonth ->
            onDateSelected(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
