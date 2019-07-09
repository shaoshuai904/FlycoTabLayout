package com.flyco.tablayoutsamples.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.widget.Toast
import com.flyco.tablayout.listener.OnTabSelectListener
import com.flyco.tablayoutsamples.R
import kotlinx.android.synthetic.main.activity_sliding_tab.*
import java.util.*

/**
 *
 * @author maple
 * @time 2019-07-09
 */
class SlidingTabActivity : FragmentActivity() {
    private val mContext = this
    private val mFragments = ArrayList<Fragment>()
    private val mTitles = arrayOf("热门", "iOS", "Android", "前端", "后端", "设计", "工具资源")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_tab)

        for (title in mTitles) {
            mFragments.add(SimpleCardFragment.getInstance(title))
        }
        vp_view.adapter = MyPagerAdapter(supportFragmentManager)


        /** 默认  */
        tl_1.setViewPager(vp_view)
        /**自定义部分属性 */
        tl_2.setViewPager(vp_view)
        /** 字体加粗,大写  */
        tl_3.setViewPager(vp_view)
        /** tab固定宽度  */
        tl_4.setViewPager(vp_view)
        /** indicator固定宽度  */
        tl_5.setViewPager(vp_view)
        /** indicator圆  */
        tl_6.setViewPager(vp_view)
        /** indicator矩形圆角  */
        tl_7.setViewPager(vp_view, mTitles)
        /** indicator三角形  */
        tl_8.setViewPager(vp_view, mTitles, this, mFragments)
        /** indicator圆角色块  */
        tl_9.setViewPager(vp_view)
        /** indicator圆角色块  */
        tl_10.setViewPager(vp_view)

        vp_view.currentItem = 4

        tl_1.showDot(4)
        tl_3.showDot(4)
        tl_4.showDot(4)

        tl_2.showMsg(3, 5)
        tl_2.setMsgMargin(3, 0f, 10f)
        val msgView = tl_2.getMsgView(3)
        msgView?.setBackgroundColor(Color.parseColor("#6D8FB0"))

        tl_2.showMsg(5, 5)
        tl_2.setMsgMargin(5, 0f, 10f)

        tl_2.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                Toast.makeText(mContext, "onTabSelect & position--> $position", Toast.LENGTH_SHORT).show()
            }

            override fun onTabReselect(position: Int) {
                Toast.makeText(mContext, "onTabReselect & position--> $position", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
}
