package com.flyco.tablayout.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.flyco.tablayout.R

/**
 *
 * @author maple
 * @time 2019-07-16
 */
class TabView : FrameLayout {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        initView()
    }

    lateinit var tvTitle: TextView
    lateinit var rtvMsgTip: MsgView

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_tab_segment, this, true)
        tvTitle = view.findViewById(R.id.tv_tab_title)
        rtvMsgTip = view.findViewById(R.id.rtv_msg_tip)
    }

    fun setTitle(title: CharSequence) {
        tvTitle.text = title
    }

}
