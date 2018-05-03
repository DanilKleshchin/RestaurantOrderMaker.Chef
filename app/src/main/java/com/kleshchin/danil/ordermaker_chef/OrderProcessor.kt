package com.kleshchin.danil.ordermaker_chef

import com.kleshchin.danil.ordermaker_chef.models.Meal

/**
 * Created by Danil Kleshchin on 02-May-18.
 */
object OrderProcessor {

    private var queueMeals: ArrayList<Meal> = ArrayList()
    private var progressMeals: ArrayList<Meal> = ArrayList()
    private var doneMeals: ArrayList<Meal> = ArrayList()

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

    fun setQueueMeals(queueMeals: ArrayList<Meal>) {
        this.queueMeals = queueMeals
    }

    fun setProgressMeals(progressMeals: ArrayList<Meal>) {
        this.progressMeals = progressMeals
    }

    fun setDoneMeals(doneMeals: ArrayList<Meal>) {
        this.doneMeals = doneMeals
    }

    fun getQueueMeals(): ArrayList<Meal>? {
        return this.queueMeals
    }

    fun getProgressMeals(): ArrayList<Meal>? {
        return this.progressMeals
    }

    fun getDoneMeals(): ArrayList<Meal>? {
        return this.doneMeals
    }
}
