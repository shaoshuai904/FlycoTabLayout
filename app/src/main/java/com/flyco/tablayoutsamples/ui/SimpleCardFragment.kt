package com.flyco.tablayoutsamples.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.flyco.tablayoutsamples.R


class SimpleCardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fr_simple_card, null)
        val mTitle = arguments!!.get(ARGS_BUNDLE) as String
        view.findViewById<TextView>(R.id.card_title_tv).text = mTitle
        return view
    }

    companion object {
        const val ARGS_BUNDLE = ":Bundle"

        fun getInstance(title: String): SimpleCardFragment {
            val fragment = SimpleCardFragment()
            val args = Bundle(1)
            args.putString(ARGS_BUNDLE, title)
            fragment.arguments = args
            return fragment
        }
    }
}
