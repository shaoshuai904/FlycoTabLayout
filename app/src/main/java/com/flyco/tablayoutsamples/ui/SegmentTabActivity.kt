package com.flyco.tablayoutsamples.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.flyco.tablayout.listener.OnTabSelectListener
import com.flyco.tablayoutsamples.R
import kotlinx.android.synthetic.main.activity_segment_tab.*
import java.util.*

class SegmentTabActivity : FragmentActivity() {
    private val mFragments = ArrayList<Fragment>()
    private val mFragments2 = ArrayList<Fragment>()

    private val mTitles2 = arrayOf("首页", "消息", "联系人")
    private val mTitles3 = arrayOf("首页", "消息", "联系人", "更多")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segment_tab)

        for (title in mTitles3) {
            mFragments.add(SimpleCardFragment.getInstance("Switch ViewPager $title"))
        }

        for (title in mTitles2) {
            mFragments2.add(SimpleCardFragment.getInstance("Switch Fragment $title"))
        }

        tl_1.apply {
            setTabData(arrayOf("首页", "消息"))
            showDot(2) //显示未读红点
        }


        tl_2.apply {
            setTabData(mTitles2)
            showDot(2)
        }

        tl_3.apply {
            setTabData(mTitles3)
            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    vp_2.currentItem = position
                }

                override fun onTabReselect(position: Int) {}
            })
            showDot(1)
            showDot(2)
            val msgView2 = getMsgView(2)
            msgView2?.setBackgroundColor(Color.parseColor("#6D8FB0"))
        }

        vp_2.apply {
            adapter = MyPagerAdapter(supportFragmentManager)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    tl_3!!.currentTab = position
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
            currentItem = 1
        }


        tl_4.apply {
            setTabData(mTitles2, this@SegmentTabActivity, R.id.fl_change, mFragments2)
            showDot(1)
        }

        tl_5.setTabData(mTitles3)
    }


    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles3[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
}
