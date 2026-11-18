package com.example.taskly.core.extentionfunc

import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
fun Fragment.showDatePicker(
    initialDate: String? = null,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val calendar = Calendar.getInstance()

    initialDate?.let { date ->
        val parsedDate = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).parse(date)

        parsedDate?.let {
            calendar.time = it
        }
    }

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


