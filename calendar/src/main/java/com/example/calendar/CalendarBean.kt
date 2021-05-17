package com.example.calendar

data class CalendarBean(
    val code: Int,
    val `data`: Data,
    val message: String,
    val server_time: Int
)

data class Data(
    val calendar: List<Calendar>,
    val month: Int,
    val punch_day: Int,
    val water_day: Int,
    val year: Int
)

data class Calendar(
    val date: String,
    val day: Int,
    val is_water: Int,
    val mood_id: Int,
    val mood_img: String,
    val mood_txt: String,
    val news_id: Int,
    val state: Int
)