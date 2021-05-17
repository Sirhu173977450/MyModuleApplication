package com.example.calendar.calendar.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.calendar.R
import com.example.calendar.TargetDetailCalendarBean
import com.example.calendar.calendar.model.CalendarDay
import com.example.calendar.calendar.model.DayOwner
import org.threeten.bp.LocalDate

class DayBinderAdapter(val context: Context,
                       val calendarData: MutableMap<LocalDate?, TargetDetailCalendarBean.Calendar>,
                       val onClick: (TargetDetailCalendarBean.Calendar, view: View) -> Unit)
    : DayBinder<DayViewContainer> {

    private val today = LocalDate.now()
    var targetType = ""
    var start_day = ""
    var end_day = ""


    fun setTargetRange(target_type: String, startday: String, endday: String) {
        targetType = target_type
        start_day = startday
        end_day = endday
    }

    override fun create(view: View) = DayViewContainer(view)

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        container.day = day
        container.textView.text = day.date.dayOfMonth.toString()

        Log.e("TAG","calendar day: " + day.date.dayOfMonth.toString())
        if (day.owner == DayOwner.THIS_MONTH) {
            Log.e("TAG","calendar day 2: " + day.date.dayOfMonth.toString())
//            var currentStartYear = -1
//            var currentStartMonth = -1
//            var currentStartDay = -1
//            var currentEndYear = -1
//            var currentEndMonth = -1
//            var currentEndDay = -1
//            if (start_day.isNotBlank()) {
//                val startSplit = start_day.split("-")
//                if (startSplit.size >= 3) {
//                    currentStartYear = startSplit[0].toInt()
//                    currentStartMonth = startSplit[1].toInt()
//                    currentStartDay = startSplit[2].toInt()
//                }
//            }
//            if (end_day.isNotBlank()) {  //start_day=2021-01-01
//                val endSplit = end_day.split("-")
//                if (endSplit.size >= 3) {
//                    currentEndYear = endSplit[0].toInt()
//                    currentEndMonth = endSplit[1].toInt()
//                    currentEndDay = endSplit[2].toInt()
//                }
//            }
            val calendar = calendarData[day.date]
            Log.e("TAG","calendar event:" + calendar.toString())
            container.textView.visibility = View.VISIBLE
            if (null != calendar) {
                Log.e("TAG","calendar day.date.dayOfMonth:" + day.date.dayOfMonth)
                Log.e("TAG","calendar calendar.day:" + calendar.day)
                if (/*calendar.mood_id > 0 &&*/ calendar.mood_img.isNotBlank()) {
                    Log.e("TAG","calendar calendar.day 1:" + calendar.day)
                    container.textView.visibility = View.INVISIBLE
                    container.iv_image.visibility = View.VISIBLE
                    Glide.with(context).load(calendar.mood_img).into(container.iv_image)
                } else {
//                    container.textView.visibility = View.VISIBLE
                    container.iv_image.visibility = View.INVISIBLE
                    //state  是否打卡（0否1是）
//                    calendar.state = if (Random().nextBoolean()) 0 else 1

                    //默认周期内 灰底，黑字
                    if (/*currentStartYear != -1 && currentEndYear != -1 &&
                        currentStartMonth != -1 && currentEndMonth != -1 &&
                        currentStartDay != -1 && currentEndDay != -1 &&*/
                            start_day.isNotBlank() && end_day.isNotBlank() &&
                            //比较当前对象日期是否在other对象日期之前: 2017-03-03
                            day.date.isBefore(LocalDate.parse(end_day).plusDays(1)) &&
                            //比较当前对象日期是否在other对象日期之后
                            day.date.isAfter(LocalDate.parse(start_day).minusDays(1))

                    ) {
                        //2、已过去未打卡周期浅红色圆底区分；增加选中状态
                        if (day.date.isBefore(today) && calendar.state == 0) {
                            container.textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                            //如果是免费团队模式
                            if (calendar.isSelected) {
                                container.textView.setTextColor(Color.WHITE)
                                container.textView.setBackgroundResource(R.drawable.example_selected_bg)
                            } else {
                                container.textView.setTextColor(Color.parseColor("#D9666F"))
                                container.textView.setBackgroundResource(R.drawable.example_yesterday_bg)
                            }
                            calendar.isYesterDay = true
                        } else {
                            container.textView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                            container.textView.setTextColor(Color.parseColor("#4A4A4A"))
                            container.textView.setBackgroundResource(R.drawable.example_3_today_bg)
                        }
                    } else {
                        container.textView.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                        container.textView.setTextColor(Color.parseColor("#ADADAD"))
                        container.textView.background = null
                    }
                }
//                calendar.is_water = if (Random().nextBoolean()) 0 else 1
                if (day.date == today) {
                    Log.e("TAG","calendar isToday: " + day.date.dayOfMonth + " calendar.day: " + calendar.day)
                    container.iv_dot_view.visibility = View.VISIBLE
                    container.iv_dot_view.setImageResource(R.mipmap.ic_target_detail_red)
                } else if (calendar.is_water == 1) {
                    //is_water:是否灌水（0否1是）
//                    container.textView.visibility = View.VISIBLE
                    container.iv_dot_view.visibility = View.VISIBLE
                    container.iv_dot_view.setImageResource(R.mipmap.ic_target_detail_water)
                } else {
                    container.iv_dot_view.visibility = View.INVISIBLE
                }
            } else {
//                container.textView.visibility = View.VISIBLE
                container.iv_image.visibility = View.INVISIBLE
            }
            container.view.setOnClickListener {
                calendar?.run { onClick(this, container.textView) }
            }
        } else {
            container.textView.visibility = View.INVISIBLE
            container.iv_image.visibility = View.INVISIBLE
            container.iv_dot_view.visibility = View.INVISIBLE
        }
    }
}