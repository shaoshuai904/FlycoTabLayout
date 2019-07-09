package com.flyco.tablayoutsamples.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.flyco.tablayoutsamples.R
import kotlinx.android.synthetic.main.activity_home.*

class SimpleHomeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bt_sliding_tab.setOnClickListener { jumpPage(SlidingTabActivity::class.java) }
        bt_common_tab.setOnClickListener { jumpPage(CommonTabActivity::class.java) }
        bt_segment_tab.setOnClickListener { jumpPage(SegmentTabActivity::class.java) }
    }

    private fun jumpPage(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}
