package com.pasco.pascocustomer.userFragment.pageradaper

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.pasco.pascocustomer.R

class ViewPagerAdapter(
    private val context: Context,
    private val images: List<Int>,
    private val titles: List<String>,
   private val titles1: List<String>
) :
    PagerAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.landing_viewpager, container, false)
        val imageView = view.findViewById<ImageView>(R.id.viewPagerImg)
        val textView = view.findViewById<TextView>(R.id.headingTxt)
        val subHeading = view.findViewById<TextView>(R.id.subHeading)
        imageView.setImageResource(images[position])
        textView.text = titles[position]
        subHeading.text = titles1[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}