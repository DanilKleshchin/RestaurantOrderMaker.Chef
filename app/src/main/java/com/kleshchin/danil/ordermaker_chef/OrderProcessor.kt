package com.kleshchin.danil.ordermaker_chef

import com.kleshchin.danil.ordermaker_chef.models.Order

/**
 * Created by Danil Kleshchin on 02-May-18.
 */
object OrderProcessor {

    private var progressOrders: ArrayList<Order> = ArrayList()
    private var doneOrders: ArrayList<Order> = ArrayList()

    interface OnQueueOrderStatusChangedListener {
        fun onQueueOrderStatusChanged(order: Order)
    }

    interface OnProgressOrderStatusChangedListener {
        fun onProgressOrderStatusChanged(order: Order)
    }

    private lateinit var onQueueOrderStatusChangedListener: OnQueueOrderStatusChangedListener
    private lateinit var onProgressOrderStatusChangedListener: OnProgressOrderStatusChangedListener

    fun changeQueueOrderStatus(order: Order) {
        onQueueOrderStatusChangedListener.onQueueOrderStatusChanged(order)
        OrderMakerRepository.sendOrderStatus(order)
    }

    fun changeProgressOrderStatus(order: Order) {
        onProgressOrderStatusChangedListener.onProgressOrderStatusChanged(order)
        OrderMakerRepository.sendOrderStatus(order)
    }

    fun setOnQueueOrderStatusChangedListener(listener: OnQueueOrderStatusChangedListener) {
        onQueueOrderStatusChangedListener = listener
    }

    fun setOnProgressOrderStatusChangedListener(listener: OnProgressOrderStatusChangedListener) {
        onProgressOrderStatusChangedListener = listener
    }

    fun setProgressOrder(progressMeals: ArrayList<Order>) {
        this.progressOrders = progressMeals
    }

    fun setDoneOrder(doneMeals: ArrayList<Order>) {
        this.doneOrders = doneMeals
    }

    fun getProgressOrder(): ArrayList<Order>? {
        return this.progressOrders
    }

    fun getDoneOrder(): ArrayList<Order>? {
        return this.doneOrders
    }
}
