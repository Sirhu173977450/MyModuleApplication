package com.example.calendar

import java.io.Serializable

class TargetDetailBean() : Serializable {

    var target_id = ""
    var name = ""
    var days = ""           //挑战天数
    var contract_id = ""
    var contract_name=""        //契约组名称（分享会用到？）
    var contract_type = ""     //契约组类型：模式 0 个人目标 1 免费(默认模式) 2 契约金 3 免费(团队模式)
    var contract_leader=""      //契约组组长（分享会用到？）
    var start_time = ""      //
    var end_time  = ""       //
    var start_day=""
    var end_day=""
    var all_punch_day=""         //累计打卡天数
    var max_continuous_day=""    //最高连续天数
    var succ_punch_day=""        //达标天数
    var power_per=0F              //行动力百分比
    var power_des=""              //行动力图
    var qrcode=""                //分享二维码
    var apply_time=""      //报名截止日期
    var is_apply=""        //是否已报名   ：0 没有报名  1已经报名
    var must_day = ""       //目标周期天数
    var remind_lock= "1"      //时间提醒开关 1开2关
    var is_leader=0         //是否是组长：0否 1是
    var remind_time = arrayListOf<String>()    //时间提醒
    //时间提醒频次json ["1","2"]  1-7代表周一到周日 8休息日 9工作日 10每天
    var remind_day = arrayListOf<String>()
    var ing_status = 0       //目标进行状态（0未开始 1进行中 2已结束 3已关闭）

    var is_step= 0           //是否开启步数(默认-1不支持,0否,1是)
    var set_step= 0L          //达标步数
    var step= 0L              //步数（已打卡显示打卡时记录的步数）

    /**
     * 以下是通用其他页面的字段
     */
    var add_up_day = ""     //累计打卡天数
    var close_days = ""     //目标截止天数
    var created_at = ""
    var des = ""
    var high_day = ""
    var last_target_time = ""
    //1：打卡频次type(频次1、按天 2、按周 3、按月)
    var rate = 1
    var rate_time = arrayListOf<String>()       //打卡频次对应Type
    var redue_day= ""       //距离目标完成剩余天数
    var series_day = ""      //
    var unseries_day = ""       //
    var updated_at = ""       //
    var user_id  = ""       //

    var user: User? = null
    class User: Serializable{
        var id=""
        var uid	=""
        var nickname =""
        var avatar	=""
    }

}