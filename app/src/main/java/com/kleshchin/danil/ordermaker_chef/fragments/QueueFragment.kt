package com.kleshchin.danil.ordermaker_chef.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.OrderMakerRepository
import com.kleshchin.danil.ordermaker_chef.OrderProcessor
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.adapters.OrderAdapter
import com.kleshchin.danil.ordermaker_chef.models.Order
import kotlinx.android.synthetic.main.fragment_queue.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class QueueFragment : Fragment(), OrderAdapter.MealViewHolder.OnOrderClickListener,
        OrderMakerRepository.OnReceiveOrderInformationListener {

    private val KEY_CHECKED_ID = "KEY_CHECKED_ID"
    private val KEY_CHECKED_ORDERS = "KEY_CHECKED_ORDERS"
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val HANDLER_DELAYED = 30000L
    private lateinit var adapter: OrderAdapter
    private var orders: ArrayList<Order> = ArrayList()
    private var checkedOrdersNumbers: ArrayList<Long> = ArrayList()
    val handler: Handler = Handler()
    val runnable = object : Runnable {
        override fun run() {
            loadMealInfo()
            handler.postDelayed(this, HANDLER_DELAYED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_queue, container, false)
        view?.findViewById<SwipeRefreshLayout>(R.id.pull_to_refresh)?.setOnRefreshListener {
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        changeRecyclerViewVisibility()
        handler.post(runnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(KEY_CHECKED_ID, checkedOrdersNumbers)
        outState?.putSerializable(KEY_CHECKED_ORDERS, orders)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            this.checkedOrdersNumbers = savedInstanceState.getSerializable(KEY_CHECKED_ID) as ArrayList<Long>
            this.orders = savedInstanceState.getSerializable(KEY_CHECKED_ORDERS) as ArrayList<Order>
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onOrderReceive(mealList: ArrayList<Order>?) {
        pull_to_refresh.isRefreshing = false

        if (mealList == null || mealList.isEmpty()) {
            return
        }
        val newMeals = getNewOrderList(mealList)
        if (newMeals.isEmpty()) {
            updateOrderRecyclerView()
            return
        }
        orders.addAll(mealList)
        updateOrderRecyclerView()
        changeRecyclerViewVisibility()
    }

    override fun onOrderClick(order: Order?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message_progress)
        builder.setPositiveButton(R.string.button_accept) { dialog, id ->
            if (order != null) {
                order.status = Order.OrderStatus.Progress
                OrderProcessor.changeQueueOrderStatus(order)
                orders.remove(order)
                (queue_recycler_view.adapter as OrderAdapter).setOrderList(orders)
            }
        }
        builder.setNegativeButton(R.string.button_cancel) { dialog, id ->
            // No need action
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun loadMealInfo() {
        val repository = OrderMakerRepository
        repository.setOnReceiveMealInformationListener(activity, this)
        repository.loadOrder()
    }

    private fun updateOrderRecyclerView() {
        adapter = OrderAdapter(orders)
        adapter.setOnMealClickListener(this)
        linearLayoutManager = LinearLayoutManager(context)
        queue_recycler_view.layoutManager = this.linearLayoutManager
        queue_recycler_view.adapter = adapter
    }

    private fun changeRecyclerViewVisibility() {
        if (this.orders.isEmpty()) {
            pull_to_refresh.visibility = View.GONE
            queue_empty_view.visibility = View.VISIBLE
        } else {
            pull_to_refresh.visibility = View.VISIBLE
            queue_empty_view.visibility = View.GONE
        }
    }

    private fun getNewOrderList(mealList: ArrayList<Order>): ArrayList<Order> {
        if (checkedOrdersNumbers.isEmpty()) {
            addCheckedId(mealList)
            return mealList
        }
        val meals = getNewOrders(mealList)
        addCheckedId(meals)
        return meals
    }

    private fun addCheckedId(mealList: ArrayList<Order>) {
        for (order in mealList) {
            checkedOrdersNumbers.add(order.number)
        }
    }

    private fun getNewOrders(orderList: ArrayList<Order>): ArrayList<Order> {
        val meals: ArrayList<Order> = ArrayList()
        for (order in orderList) {
            if (!checkedOrdersNumbers.contains(order.number)) {
                meals.add(order)
            }
        }
        return meals
    }
}
