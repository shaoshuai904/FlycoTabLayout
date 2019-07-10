package com.flyco.tablayoutsamples.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.flyco.tablayout.utils.UnreadMsgUtils
import com.flyco.tablayoutsamples.R
import com.flyco.tablayoutsamples.entity.TabEntity
import kotlinx.android.synthetic.main.activity_common_tab.*
import java.util.*

class CommonTabActivity : FragmentActivity() {
    private val mContext = this
    private val mFragments = ArrayList<Fragment>()
    private val mFragments2 = ArrayList<Fragment>()
    private val mTabEntities: ArrayList<CustomTabEntity> = arrayListOf(
            TabEntity("首页", R.mipmap.tab_home_select, R.mipmap.tab_home_unselect),
            TabEntity("消息", R.mipmap.tab_speech_select, R.mipmap.tab_speech_unselect),
            TabEntity("联系人", R.mipmap.tab_contact_select, R.mipmap.tab_contact_unselect),
            TabEntity("更多", R.mipmap.tab_more_select, R.mipmap.tab_more_unselect)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_tab)

        for (tab in mTabEntities) {
            mFragments.add(SimpleCardFragment.getInstance("Switch ViewPager ${tab.tabTitle}"))
            mFragments2.add(SimpleCardFragment.getInstance("Switch Fragment ${tab.tabTitle}"))
        }

        /** with nothing  */
        tl_1.setTabData(mTabEntities)
        tl_1.showDot(2) //显示未读红点
        /** indicator固定宽度  */
        tl_4.setTabData(mTabEntities)
        /** indicator固定宽度  */
        tl_5.setTabData(mTabEntities)
        /** indicator矩形圆角  */
        tl_6.setTabData(mTabEntities)
        /** indicator三角形  */
        tl_7.setTabData(mTabEntities)
        /** indicator圆角色块  */
        tl_8.setTabData(mTabEntities)
        tl_8.showDot(1) //显示未读红点
        tl_8.currentTab = 2

        /** with ViewPager  */
        tl_2.apply {
            setTabData(mTabEntities)
            //两位数
            showMsg(0, 55)
            setMsgMargin(0, -5f, 5f)

            //三位数
            showMsg(1, 100)
            setMsgMargin(1, -5f, 5f)

            //设置未读消息红点
            showDot(2)
            val msgView2 = getMsgView(2)
            if (msgView2 != null) {
                UnreadMsgUtils.setSize(msgView2, dp2px(7.5f))
            }

            //设置未读消息背景
            showMsg(3, 5)
            setMsgMargin(3, 0f, 5f)
            val msgView3 = getMsgView(3)
            msgView3?.setBackgroundColor(Color.parseColor("#6D8FB0"))

            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    vp_2.currentItem = position
                }

                override fun onTabReselect(position: Int) {
                    if (position == 0) {
                        showMsg(0, Random().nextInt(100) + 1)
                        // UnreadMsgUtils.show(tl_2.getMsgView(0), mRandom.nextInt(100) + 1);
                    }
                }
            })
        }

        /** with Fragments  */
        tl_3.apply {
            setTabData(mTabEntities, this@CommonTabActivity, R.id.fl_change, mFragments2)
            showDot(1)
            setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    tl_1.currentTab = position
                    tl_2.currentTab = position
                    tl_4.currentTab = position
                    tl_5.currentTab = position
                    tl_6.currentTab = position
                    tl_7.currentTab = position
                    tl_8.currentTab = position
                }

                override fun onTabReselect(position: Int) {}
            })
            currentTab = 1
        }


        vp_2.apply {
            adapter = MyPagerAdapter(supportFragmentManager)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    tl_2.currentTab = position
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
            currentItem = 1
        }

    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTabEntities[position].tabTitle
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }

    private fun dp2px(dp: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}
