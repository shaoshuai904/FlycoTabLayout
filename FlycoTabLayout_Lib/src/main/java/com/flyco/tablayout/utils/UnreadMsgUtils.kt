package com.flyco.tablayout.utils


import android.view.View
import android.widget.RelativeLayout
import com.flyco.tablayout.widget.MsgView

/**
 * 未读消息提示View,显示小红点或者带有数字的红点:
 * 数字一位,圆
 * 数字两位,圆角矩形,圆角是高度的一半
 * 数字超过两位,显示99+
 */
object UnreadMsgUtils {

    @JvmStatic
    fun show(msgView: MsgView, num: Int) {
        val lp = msgView.layoutParams as RelativeLayout.LayoutParams
        val dm = msgView.resources.displayMetrics
        msgView.visibility = View.VISIBLE
        if (num <= 0) {//圆点,设置默认宽高
            msgView.setStrokeWidth(0F)
            msgView.text = ""

            lp.width = (5 * dm.density).toInt()
            lp.height = (5 * dm.density).toInt()
            msgView.layoutParams = lp
        } else {
            lp.height = (18 * dm.density).toInt()
            when (num) {
                in 1..9 -> {//圆
                    lp.width = (18 * dm.density).toInt()
                    msgView.text = num.toString()
                }
                in 10..99 -> {//圆角矩形,圆角是高度的一半,设置默认padding
                    lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                    msgView.setPadding((6 * dm.density).toInt(), 0, (6 * dm.density).toInt(), 0)
                    msgView.text = num.toString()
                }
                else -> {//数字超过两位,显示99+
                    lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                    msgView.setPadding((6 * dm.density).toInt(), 0, (6 * dm.density).toInt(), 0)
                    msgView.text = "99+"
                }
            }
            msgView.layoutParams = lp
        }
    }

    @JvmStatic
    fun setDpSize(rtv: MsgView, size: Float) {
        setSize(rtv, DensityUtils.dp2px(rtv.context, size))
    }

    private fun setSize(rtv: MsgView, size: Int) {
        val lp = rtv.layoutParams as RelativeLayout.LayoutParams
        lp.width = size
        lp.height = size
        rtv.layoutParams = lp
    }
}
