package com.kleshchin.danil.ordermaker_chef.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kleshchin.danil.ordermaker_chef.fragments.DoneFragment
import com.kleshchin.danil.ordermaker_chef.fragments.ProgressFragment
import com.kleshchin.danil.ordermaker_chef.fragments.QueueFragment

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class OrderMakerPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm) {
    internal val PAGE_COUNT = 3
    internal val QUEUE_FRAGMENT_POSITION = 0
    internal val PROGRESS_FRAGMENT_POSITION = 1
    internal val DONE_FRAGMENT_POSITION = 2
    private val tabTitles = arrayOf("В очереди", "В процессе", "Готово")

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            QUEUE_FRAGMENT_POSITION -> {
                QueueFragment()
            }
            PROGRESS_FRAGMENT_POSITION -> {
                ProgressFragment()
            }
            DONE_FRAGMENT_POSITION -> {
                DoneFragment()
            }
            else -> {
                throw IllegalStateException("Unknown page position " + position)
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }
}
