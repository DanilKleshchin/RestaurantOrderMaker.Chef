package com.kleshchin.danil.ordermaker_chef.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.models.Order
import com.kleshchin.danil.ordermaker_chef.utils.inflate
import kotlinx.android.synthetic.main.item_meal_recycler_view.view.*

class OrderAdapter(private var orderList: ArrayList<Order>) : RecyclerView.Adapter<OrderAdapter.MealViewHolder>() {


    private var listener: MealViewHolder.OnOrderClickListener? = null

    override fun getItemCount() = orderList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val inflatedView = parent.inflate(R.layout.item_meal_recycler_view, false)
        return MealViewHolder(inflatedView, listener)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val order = orderList[position]
        holder.bindOrder(order)
    }

    fun setOnMealClickListener(listener: MealViewHolder.OnOrderClickListener) {
        this.listener = listener
    }

    fun setOrderList(orderList: ArrayList<Order>) {
        this.orderList = orderList
        notifyDataSetChanged()
    }

    class MealViewHolder(v: View, private var listener: OnOrderClickListener?) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var order: Order? = null

        interface OnOrderClickListener {
            fun onOrderClick(order: Order?)
        }

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (listener != null) {
                listener!!.onOrderClick(order)
            }
        }

        fun bindOrder(order1: Order) {
            this.order = order1
            view.meal_name.text = order1.mealName
        }
    }
}
