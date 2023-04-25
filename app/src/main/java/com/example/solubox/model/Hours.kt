package com.example.solubox.model

import java.text.SimpleDateFormat
import java.util.*

class Hours {
    var sunday: String? = null
    var monday: String? = null
    var tuesday: String? = null
    var wednesday: String? = null
    var thursday: String? = null
    var friday: String? = null
    var saturday: String? = null
    val todaysHours: String?
        get() {
            val calendar = Calendar.getInstance()
            val date = calendar.time
            val day = SimpleDateFormat("EEEE", Locale.GERMAN).format(date.time)
            return when (day) {
                "Sunday" -> sunday
                "Monday" -> monday
                "Tuesday" -> tuesday
                "Wednesday" -> wednesday
                "Thursday" -> thursday
                "Friday" -> friday
                "Saturday" -> saturday
                else -> sunday
            }
        }
}