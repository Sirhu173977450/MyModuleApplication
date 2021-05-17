package com.example.calendar.calendar.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.calendar.R
import com.example.calendar.calendar.model.CalendarDay

class DayViewContainer (view: View) : ViewContainer(view) {
    lateinit var day: CalendarDay // Will be set when this container is bound.
    val textView = view.findViewById<TextView>(R.id.exThreeDayText)
    val iv_dot_view =  view.findViewById<ImageView>(R.id.iv_dot_view)
    val iv_image =  view.findViewById<ImageView>(R.id.iv_image)
    init {
        view.setOnClickListener {
//                    if (day.owner == DayOwner.THIS_MONTH) {
//                        selectDate(day.date)
//                    }
        }
    }
}