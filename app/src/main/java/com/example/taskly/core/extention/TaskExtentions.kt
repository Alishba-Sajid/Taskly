package com.example.taskly.core.extention

import android.content.Context
import com.example.taskly.R
import com.example.taskly.domain.model.Task
import com.example.taskly.domain.model.TaskStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Task.isOverdue(): Boolean {
    val today = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ).format(Date())

    return status == TaskStatus.IN_PROGRESS && dueDate < today
}

fun Task.toDueDateText(context: Context): String {
    val parsedDate = SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.US
    ).parse(dueDate)

    val displayDate = parsedDate?.let {
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(it)
    } ?: dueDate

    return context.getString(
        R.string.task_due_date_display,
        displayDate
    )
}

