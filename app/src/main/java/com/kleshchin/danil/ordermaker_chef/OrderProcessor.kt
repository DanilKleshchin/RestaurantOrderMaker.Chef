package com.kleshchin.danil.ordermaker_chef

import com.kleshchin.danil.ordermaker_chef.models.Meal

/**
 * Created by Danil Kleshchin on 02-May-18.
 */
object OrderProcessor {

    interface OnQueueOrderStatusChangedListener {
        fun onQueueOrderStatusChanged(meal: Meal)
    }

    interface OnProgressOrderStatusChangedListener {
        fun onProgressOrderStatusChanged(meal: Meal)
    }

    private lateinit var onQueueOrderStatusChangedListener: OnQueueOrderStatusChangedListener
    private lateinit var onProgressOrderStatusChangedListener: OnProgressOrderStatusChangedListener

    fun changeQueueOrderStatus(meal: Meal) {
        onQueueOrderStatusChangedListener.onQueueOrderStatusChanged(meal)
    }

    fun changeProgressOrderStatus(meal: Meal) {
        onProgressOrderStatusChangedListener.onProgressOrderStatusChanged(meal)
    }

    fun setOnQueueOrderStatusChangedListener(listener: OnQueueOrderStatusChangedListener) {
        onQueueOrderStatusChangedListener = listener
    }

    fun setOnProgressOrderStatusChangedListener(listener: OnProgressOrderStatusChangedListener) {
        onProgressOrderStatusChangedListener = listener
    }
}
