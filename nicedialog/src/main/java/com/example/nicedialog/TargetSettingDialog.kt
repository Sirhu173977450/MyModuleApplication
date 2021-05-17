package com.example.nicedialog

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.nicedialog.nicedialog.BaseNiceDialog
import com.example.nicedialog.nicedialog.ViewHolder
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dailog_target_setting.*

/**
 *  目标设置页码选项弹窗
 *  yichao.hu  2020/9/18
 */

class TargetSettingDialog() : BaseNiceDialog(), TabTargetSettingFragment.OnItemSelectedListener {

    var mOnPunchFrequencyClickListener: OnPunchFrequencyClickListener? = null

    var mFragmentList: MutableList<Fragment> = arrayListOf()

    var mList = arrayListOf<MultiItemEntity>()

    var mTitles: MutableList<String> = arrayListOf("按天", "按周x天", "按月x天")

    var mAdapter: TimeSettingAdapter? = null

    //进入类型： 1：打卡频次(频次1、按天 2、按周 3、按月)，4：坚持天数
    var mType = 1

    companion object {
        fun init(): TargetSettingDialog {
            return TargetSettingDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mType == 1) {
            mFragmentList.add(TabTargetSettingFragment(this, 1))
            mFragmentList.add(TabTargetSettingFragment(this, 2))
            mFragmentList.add(TabTargetSettingFragment(this, 3))
        }
    }

    override fun intLayoutId(): Int {
        return R.layout.dailog_target_setting
    }

    override fun convertView(holder: ViewHolder, dialog: BaseNiceDialog) {
        val viewSwitcher = holder.getView<ViewSwitcher>(R.id.viewSwitcher)
        val tv_title = holder.getView<TextView>(R.id.tv_title)
        val tv_next = holder.getView<TextView>(R.id.tv_next)
        val tv_bottom_text = holder.getView<TextView>(R.id.tv_bottom_text)
        val tabLayout = holder.getView<TabLayout>(R.id.tb_layout)
        val viewPager = holder.getView<ViewPager>(R.id.viewPager)
        val rv_view = holder.getView<RecyclerView>(R.id.rv_view)

        if (mType == 1) {   //1：打卡频次(频次1、按天 2、按周 3、按月)
            viewSwitcher.displayedChild = 0
            tv_title.text = "设置打卡频次"
            viewPager.adapter = TabTargetSettingAdapter(mFragmentList, mTitles, dialog.childFragmentManager)
            viewPager.offscreenPageLimit = 3;
            tabLayout!!.tabMode = TabLayout.MODE_SCROLLABLE
            tabLayout.setupWithViewPager(viewPager)

            for (i in mTitles.indices) {
                val tab1 = tabLayout.getTabAt(i)
                if (null != tab1) {
                    tab1.customView = getTabView(context, i)
                }
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabUnselected(tab: TabLayout.Tab) {
                    if (tab.customView != null) {
                        val tv_tab_title: TextView = tab.customView!!.findViewById(R.id.tab_text_left)
                        tv_tab_title.setTypeface(null, Typeface.NORMAL);
                    }
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (tab.customView != null) {
                        val tv_tab_title: TextView = tab.customView!!.findViewById(R.id.tab_text_left)
                        tv_tab_title.setTypeface(null, Typeface.BOLD);
                        when (tab.position) {
                            0 -> {
                                tv_bottom_text.text = "本目标任务将在首页按以上设置时间显示"
                            }
                            1 -> {
                                val result = (mFragmentList[tab.position] as TabTargetSettingFragment).getEditTextContent()
                                val data = result.toString().replace("[", "").replace("]", "").trim()
                                tv_bottom_text.text = Html.fromHtml("一周内完成<font color=\"#FFB305\">" + data + "天</font>后,本项目将不再本周显示")
                            }
                            2 -> {
                                val result = (mFragmentList[tab.position] as TabTargetSettingFragment).getEditTextContent()
                                val data = result.toString().replace("[", "").replace("]", "").trim()
                                tv_bottom_text.text = Html.fromHtml("一月内完成<font color=\"#FFB305\">" + data + "天</font>后,本项目将不再本月显示")
                            }
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            tv_bottom_text.text = "本目标任务将在首页按以上设置时间显示"
            tv_next.setOnClickListener {
                val result = (mFragmentList[viewPager.currentItem]
                        as TabTargetSettingFragment).getEditTextContent()
                if (result.isNotEmpty()) {
                    //处理tab 2 输入0的情况
                    if (viewPager.currentItem == 2 && result[0] == "0") {
                        Toast.makeText(context,"选择天数必须大于0！",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        val type = (mFragmentList[viewPager.currentItem]
                                as TabTargetSettingFragment).getType()
                        mOnPunchFrequencyClickListener?.onNextClick(type, result)
                    }
                }
            }
        } else {    //，4：坚持天数
            viewSwitcher.displayedChild = 1
            mList.clear()
            tv_title.text = "设置坚持天数"
            mAdapter = TimeSettingAdapter(arrayListOf())
            mList.add(TimeSettingBean("1天"))
            mList.add(TimeSettingBean("7天"))
            mList.add(TimeSettingBean("14天"))
            mList.add(TimeSettingBean("21天", true))
            mList.add(TimeSettingBean("30天"))
            mList.add(TimeSettingBean("100天"))
            mList.add(TimeSettingBean("180天"))
            mList.add(TimeSettingBean("365天"))
            mAdapter?.setList(mList)
            rv_view.adapter = mAdapter
            rv_view.layoutManager = GridLayoutManager(context, 4)
            mAdapter!!.setOnItemChildClickListener { adapter, view, position ->
                if (adapter.data.isEmpty()) return@setOnItemChildClickListener
                when (adapter.getItemViewType(position)) {
                    TimeSettingAdapter.ITEM_TYPE_CONTENT -> {
                        when (view.id) {
                            R.id.tv_time -> {
                                for (i in adapter.data.indices) {
                                    val item = adapter.data[i] as TimeSettingBean
                                    //仅支持单选
                                    if (item.selected && i == position) {
                                        return@setOnItemChildClickListener
                                    }
                                    if (i == position) {
                                        item.selected = !item.selected
                                    } else {
                                        item.selected = false
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
            tv_next.setOnClickListener {
                if (null == mAdapter) return@setOnClickListener
                mOnPunchFrequencyClickListener?.onNextClick(mType, arrayListOf(),(
                        mAdapter!!.data.find { (it as TimeSettingBean).selected }
                                as TimeSettingBean).title)
            }
        }
    }

    //打卡频次弹窗回调
    override fun onOnItemSelected(type: Int, result: String) {
        when (type) {
            1 -> {
                tv_bottom_text.text = "本目标任务将在首页按以上设置时间显示"
            }
            2 -> {
                tv_bottom_text.text = Html.fromHtml("一周内完成<font color=\"#FFB305\">" + result
                        + "天</font>后,本项目将不再本周显示")
            }
            3 -> {
                val resultStr = if (!TextUtils.isEmpty(result)) result else "1"
                tv_bottom_text.text = Html.fromHtml("一月内完成<font color=\"#FFB305\">" + resultStr + "天</font>后,本项目将不再本月显示")
            }
        }
    }

    fun getTabView(context: Context?, position: Int): View {
        val mInflater = LayoutInflater.from(context)
        val view = mInflater.inflate(R.layout.item_tab_left, null)
        val tv = view.findViewById<View>(R.id.tab_text_left) as TextView
        tv.text = mTitles[position]
        return view
    }

    private fun getData() {
        mList.clear()
        mList.add(TimeSettingBean())
        mList.add(TimeSettingBean())
        mList.add(TimeSettingBean())
        mList.add(TimeSettingBean())
        mList.add(TimeSettingBean())
        mAdapter?.setList(mList)
    }

    fun setFromType(type: Int): TargetSettingDialog {
        mType = type;
        return this
    }

    fun setOnPunchFrequencyClickListener(listener: OnPunchFrequencyClickListener): TargetSettingDialog {
        mOnPunchFrequencyClickListener = listener;
        return this
    }

    interface OnPunchFrequencyClickListener{
        fun onNextClick(type:Int, result:ArrayList<String>,resultStr:String = ""){}
    }
}