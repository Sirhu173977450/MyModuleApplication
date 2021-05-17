package com.example.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    val daysOfWeek = daysOfWeekFromLocale()
//    val currentYearMonth = YearMonth.now()
    val currentYearMonth = YearMonth.of(
            2021,
            2
    )
    var mDataCalendarAdapter: DayBinderAdapter? = null
    var mCalendarData: MutableMap<LocalDate?, TargetDetailCalendarBean.Calendar> = HashMap()
    var popupWindow: PopupWindow? = null

    var mTargetDetailBean: TargetDetailBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        mDataCalendar.apply {
            setup(currentYearMonth.minusMonths(12),
                    currentYearMonth.plusMonths(0),
                    daysOfWeek.first())
            scrollToMonth(currentYearMonth)
        }
        mDataCalendarAdapter = DayBinderAdapter(this, mCalendarData) { calendar: TargetDetailCalendarBean.Calendar, view: View ->
            if (calendar.mood_img.isNotBlank() && calendar.news_id.isNotBlank()) {

            } else if (calendar.isYesterDay) {
                mCalendarData.forEach { it.value.isSelected = false }
                calendar.isSelected = !calendar.isSelected
                mDataCalendar.notifyCalendarChanged()
                showTipPopupWindow(view)
            }
        }
        mDataCalendar.dayBinder = mDataCalendarAdapter
        mDataCalendar.monthScrollListener = {
            tv_calendar_data.text = it.year.toString()
            tv_calendar_moth.text = it.month.toString()
            Log.e("TAG", " year: " + it.year.toString())
            Log.e("TAG", " month: " + it.month.toString())
            getCalendarData(it.year.toString(), it.month.toString())
        }
    }

    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    fun getJson(fileName: String?, context: Context): String? {
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        try {
            //获取assets资源管理器
            val assetManager: AssetManager = context.getAssets()
            //通过管理器打开文件并读取
            val bf = BufferedReader(InputStreamReader(
                    assetManager.open(fileName!!)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }


    //日历统计
    fun getCalendarData(year: String, month: String) {
        val str = getJson("calendar.json", this);
        Log.e("TAG", "$str")
        val calendarBean = Gson().fromJson(str, CalendarBean::class.java)

        for (item in calendarBean.data.calendar) {
            var data = if (item.date.isNotBlank()) item.date.split("-") else arrayListOf()
            mCalendarData[YearMonth.of(calendarBean.data.year, calendarBean.data.month)
                    .atDay(item.day).atTime(0, 0)?.toLocalDate()] =
                    TargetDetailCalendarBean.Calendar(
                            date = item.date,
                            day = item.day,
                            is_water = item.is_water,
                            mood_id = item.mood_id,
                            mood_img = item.mood_img,
                            mood_txt = item.mood_txt,
                            news_id = item.news_id.toString(),
                            state = 1,
                            splitDate = data,
                            year = if (data.isNotEmpty()) data[0].toInt() else YearMonth.now().year,
                            month = if (data.isNotEmpty() && data.size >= 1) data[1].toInt() else YearMonth.now().month.value,
                            time = YearMonth.of(
                                    if (data.isNotEmpty()) data[0].toInt() else YearMonth.now().year,
                                    if (data.isNotEmpty() && data.size >= 1) data[1].toInt() else YearMonth.now().month.value
                            ).atDay(item.day).atTime(0, 0),
                            isSelected = Random().nextBoolean(),
                            isYesterDay = false
                    )

        }
        mTargetDetailBean?.run {
            mDataCalendarAdapter?.setTargetRange(mTargetDetailBean?.contract_type
                    ?: "", start_day, end_day)
        }
        mDataCalendar.notifyCalendarChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showTipPopupWindow(anchorView: View): PopupWindow? {
        val contentView: View = LayoutInflater.from(this).inflate(R.layout.popuw_calendar_tag_layout, null)
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val iv_image = contentView.findViewById<ImageView>(R.id.iv_image)
        // 如果希望showAsDropDown方法能够在下面空间不足时自动在anchorView的上面弹出
        // 必须在创建PopupWindow的时候指定高度，不能用wrap_content
        val mShowMorePopupWindowWidth: Int = contentView.measuredWidth
        val mShowMorePopupWindowHeight: Int = contentView.measuredHeight
        popupWindow = PopupWindow(contentView, mShowMorePopupWindowWidth, mShowMorePopupWindowHeight, false)
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow?.setBackgroundDrawable(ColorDrawable())
        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow?.isOutsideTouchable = true
        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow?.isTouchable = true
        popupWindow?.isClippingEnabled = true
        // true时，点击返回键先消失 PopupWindow
        // false时PopupWindow不处理返回键
        popupWindow?.isFocusable = false
        popupWindow?.setTouchInterceptor { _, _ -> false }
        //获取需要在其上方显示的控件的位置信息
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        //在控件上方显示
        popupWindow?.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                (location[0] + anchorView.width / 2) - mShowMorePopupWindowWidth / 2
                , location[1] - mShowMorePopupWindowHeight /*+ ScreenUtils.dpToPx(8)*/)
        return popupWindow
    }

}