package com.example.storyapp.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtil {
    fun dateFormat(date: String?): String {
        if (date.isNullOrEmpty()) {
            return "Unknown Time"
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.getDefault())

                val parsedDate = LocalDateTime.parse(date, inputFormatter)
                parsedDate.format(outputFormatter)

            } else {
                val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())

                val parsedDate = inputFormatter.parse(date)
                parsedDate?.let { outputFormatter.format(it) } ?: "Unknown Time"
            }
        } catch (e: Exception) {
            "Unknown Time"
        }
    }
}