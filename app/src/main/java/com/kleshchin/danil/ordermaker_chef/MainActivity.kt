package com.kleshchin.danil.ordermaker_chef

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kleshchin.danil.ordermaker_chef.adapters.OrderMakerPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewpager.adapter = OrderMakerPagerAdapter(supportFragmentManager, this)
        sliding_tabs.setupWithViewPager(viewpager)
    }
}
