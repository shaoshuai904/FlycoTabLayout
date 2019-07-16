package com.flyco.tablayout.utils

import android.content.Context

object DensityUtils {

    @JvmStatic
    fun dp2px(mContext: Context, dp: Float): Int {
        val scale = mContext.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun sp2px(mContext: Context, sp: Float): Int {
        val scale = mContext.resources.displayMetrics.scaledDensity
        return (sp * scale + 0.5f).toInt()
    }

//    fun dp2px(dp: Float): Int {
//        val scale = mContext.getResources().getDisplayMetrics().density
//        return (dp * scale + 0.5f).toInt()
//    }
//
//    fun sp2px(sp: Float): Int {
//        val scale = this.mContext.getResources().getDisplayMetrics().scaledDensity
//        return (sp * scale + 0.5f).toInt()
//    }
}
