package com.example.storyapp.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object DateUtil {
    fun dateFormat(date: String?): String {
        if (date.isNullOrEmpty()) {
            return "Unknown Time"
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.getDefault())

                val parsedDate = Instant.parse(date).atZone(ZoneId.systemDefault())
                parsedDate.format(outputFormatter)

            } else {
                val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormatter.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                outputFormatter.timeZone = TimeZone.getDefault()

                val parsedDate = inputFormatter.parse(date)
                parsedDate?.let { outputFormatter.format(it) } ?: "Unknown Time"
            }
        } catch (e: Exception) {
            "Unknown Time"
        }
    }
}