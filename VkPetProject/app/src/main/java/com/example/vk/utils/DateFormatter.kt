package com.example.vk.utils

import android.content.Context
import android.text.format.DateUtils
import com.example.vk.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun formatBirthdayDate(birthdayDate: String): String {
        val outputFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        val inputFormat: DateFormat =
            SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        val date: Date = inputFormat.parse(birthdayDate)
        return outputFormat.format(date)
    }

    private fun formatDate(date: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = Constants.MILLISECONDS * date
        val df = SimpleDateFormat("dd.MM.yyyy 'в' HH:mm", Locale.ENGLISH)
        return df.format(calendar.time)
    }

    private fun formatDateExt(date: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = Constants.MILLISECONDS * date
        val df = SimpleDateFormat(" 'в' HH:mm", Locale.ENGLISH)
        return df.format(calendar.time)
    }

    fun formatLastDateAdv(date: Long, context: Context): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date * Constants.MILLISECONDS
        return when {
            DateUtils.isToday(date * Constants.MILLISECONDS) ->
                context.getString(R.string.today_last_seen) + formatDateExt(date)
            DateUtils.isToday(date * Constants.MILLISECONDS + Constants.DAY) ->
                context.getString(R.string.yesterday_last_seen) + formatDateExt(date)
            else ->
                formatDate(date)
        }
    }
}