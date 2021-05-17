package com.example.calendar

import org.threeten.bp.LocalDateTime
import java.io.Serializable

data class TargetDetailCalendarBean(
        var calendar: List<Calendar>,
        var punch_day: String,      //达标卡天数
        var water_day: Int,         //灌水卡天数
        var year: String,           //年份
        var month: String): Serializable {        //月份
    data class Calendar (
            var date: String,
            var day: Int,
            var state: Int,         //是否打卡（0否1是）
            var mood_id: Int,       //心情id type
            var mood_txt: String,   //心情文案
            var mood_img: String,    //心情图片
            var is_water: Int,       //是否灌水（0否1是）
            var news_id: String,
            //以下是格式化数据
            var splitDate: List<String> = arrayListOf(),
            var year: Int,
            var month: Int,
            var time: LocalDateTime? =null,
            var isSelected: Boolean,
            //是否是 已过去未打卡周期内
            var isYesterDay: Boolean


    ): Serializable {
        override fun toString(): String {
            return "Calendar(date='$date', day=$day, state=$state, mood_id=$mood_id" +
                    ", mood_txt='$mood_txt', mood_img='$mood_img', is_water=$is_water" +
                    ", news_id=$news_id, splitDate=$splitDate, year=$year" +
                    ", month=$month, time=$time)"
        }
    }

    override fun toString(): String {
        return "TargetDetailCalendarBean(calendar=$calendar, punch_day='$punch_day'" +
                ", water_day=$water_day, year='$year', month='$month')"
    }
}