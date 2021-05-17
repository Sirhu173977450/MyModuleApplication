package com.example.calendar

import org.threeten.bp.LocalDateTime
import java.io.Serializable

class DetailCalendarBean() : Serializable {

    //数据总览进度、心情图
    var data = arrayListOf<CalendarData>()
    var power = 0F
    var power_des = ""

    class CalendarData : Serializable {
        var date=""
        var day = 0
        var num = 0      //（数据总览）0未打卡 1部分打卡 2全部打卡      /  (曲线图)打卡数量
        var week = 0      // （数据总览） (曲线图)第几周

        //以下是格式化数据
        var splitDate: List<String> = arrayListOf()
        var year = 0
        var month = 0
        var time: LocalDateTime? =null

        override fun toString(): String {
            return "CalendarData(day=$day, num=$num, week=$week)"
        }

    }

    override fun toString(): String {
        return "DetailCalendarBean(data=$data, power=$power, power_des='$power_des')"
    }

}