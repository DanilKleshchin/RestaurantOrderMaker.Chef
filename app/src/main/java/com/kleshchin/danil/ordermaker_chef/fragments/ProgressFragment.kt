package com.kleshchin.danil.ordermaker_chef.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.OrderProcessor
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.adapters.OrderAdapter
import com.kleshchin.danil.ordermaker_chef.models.Order
import kotlinx.android.synthetic.main.fragment_progress.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class ProgressFragment : Fragment(),  OrderAdapter.MealViewHolder.OnOrderClickListener,
        OrderProcessor.OnQueueOrderStatusChangedListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: OrderAdapter
    private var orders: ArrayList<Order> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        OrderProcessor.setOnQueueOrderStatusChangedListener(this)
        val view = inflater?.inflate(R.layout.fragment_progress, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val meals = OrderProcessor.getProgressOrder()
        if (meals != null && !meals.isEmpty()) {
            onMealReceive(meals)
        }
    }

    override fun onOrderClick(order: Order?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message_done)
        builder.setPositiveButton(R.string.button_accept) { dialog, id ->
            if (order != null) {
                OrderProcessor.changeProgressOrderStatus(order)
                orders.remove(order)
                order.status = Order.OrderStatus.Done
                OrderProcessor.setProgressOrder(orders)
                (progress_recycler_view.adapter as OrderAdapter).setOrderList(orders)
                if (orders.isEmpty()) {
                    changeRecyclerViewVisibility()
                }
            }
        }
        builder.setNegativeButton(R.string.button_cancel) { dialog, id ->
            // No need action
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onQueueOrderStatusChanged(order: Order) {
        orders.add(order)
        OrderProcessor.setProgressOrder(orders)
        adapter = OrderAdapter(orders)
        adapter.setOnMealClickListener(this)
        linearLayoutManager = LinearLayoutManager(context)
        progress_recycler_view.layoutManager = this.linearLayoutManager
        progress_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    private fun onMealReceive(orderList: ArrayList<Order>?) {
        if (orderList == null || orderList.isEmpty()) {
            return
        }
        orders = orderList
        OrderProcessor.setProgressOrder(orders)
        adapter = OrderAdapter(orders)
        adapter.setOnMealClickListener(this)
        linearLayoutManager = LinearLayoutManager(context)
        progress_recycler_view.layoutManager = this.linearLayoutManager
        progress_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    private fun changeRecyclerViewVisibility() {
        if (orders.isEmpty()) {
            progress_recycler_view.visibility = View.GONE
            progress_empty_view.visibility = View.VISIBLE
        } else {
            progress_recycler_view.visibility = View.VISIBLE
            progress_empty_view.visibility = View.GONE
        }
    }
}
