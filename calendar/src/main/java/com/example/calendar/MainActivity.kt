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
        //???json?????????????????????
        val stringBuilder = StringBuilder()
        try {
            //??????assets???????????????
            val assetManager: AssetManager = context.getAssets()
            //????????????????????????????????????
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


    //????????????
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
        // ????????????showAsDropDown?????????????????????????????????????????????anchorView???????????????
        // ???????????????PopupWindow?????????????????????????????????wrap_content
        val mShowMorePopupWindowWidth: Int = contentView.measuredWidth
        val mShowMorePopupWindowHeight: Int = contentView.measuredHeight
        popupWindow = PopupWindow(contentView, mShowMorePopupWindowWidth, mShowMorePopupWindowHeight, false)
        // ???????????????PopupWindow????????????????????????????????????????????????????????????????????????????????????Back????????????dismiss??????
        popupWindow?.setBackgroundDrawable(ColorDrawable())
        // setOutsideTouchable????????????????????????setTouchable(true)???setFocusable(false)
        popupWindow?.isOutsideTouchable = true
        // ?????????true?????????PopupWindow???????????? ???????????????????????????
        popupWindow?.isTouchable = true
        popupWindow?.isClippingEnabled = true
        // true?????????????????????????????? PopupWindow
        // false???PopupWindow??????????????????
        popupWindow?.isFocusable = false
        popupWindow?.setTouchInterceptor { _, _ -> false }
        //??????????????????????????????????????????????????????
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        //?????????????????????
        popupWindow?.showAtLocation(anchorView, Gravity.NO_GRAVITY,
                (location[0] + anchorView.width / 2) - mShowMorePopupWindowWidth / 2
                , location[1] - mShowMorePopupWindowHeight /*+ ScreenUtils.dpToPx(8)*/)
        return popupWindow
    }

}