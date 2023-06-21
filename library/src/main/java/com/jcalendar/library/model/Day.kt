package com.jcalendar.library.model

import java.time.DayOfWeek
import java.time.LocalDate

data class Day(
    val dayOfWeek: DayOfWeek,
    val date: LocalDate,
    val isSelected: Boolean,
    val isOutDay: Boolean
)
