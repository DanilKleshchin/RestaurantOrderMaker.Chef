package com.kleshchin.danil.ordermaker_chef.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.OrderMakerRepository
import com.kleshchin.danil.ordermaker_chef.OrderProcessor
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.adapters.MealAdapter
import com.kleshchin.danil.ordermaker_chef.models.Meal
import kotlinx.android.synthetic.main.fragment_queue.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class QueueFragment : Fragment(), MealAdapter.MealViewHolder.OnMealClickListener,
        OrderMakerRepository.OnReceiveMealInformationListener {

    private val KEY_SAVE_INSTANCE_BUNDLE = "KEY_SAVE_INSTANCE_BUNDLE"
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MealAdapter
    private var meals: ArrayList<Meal> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_queue, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val meals = OrderProcessor.getQueueMeals()
        if (meals == null || meals.isEmpty()) {
            loadMealInfo()
        } else {
            onMealReceive(meals)
        }
    }

    override fun onMealReceive(mealList: ArrayList<Meal>?) {
        if (mealList == null || mealList.isEmpty()) {
            return
        }
        meals = mealList
        OrderProcessor.setQueueMeals(meals)
        adapter = MealAdapter(meals)
        adapter.setOnMealClickListener(this)
        linearLayoutManager = LinearLayoutManager(context)
        queue_recycler_view.layoutManager = this.linearLayoutManager
        queue_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    override fun onMealClick(meal: Meal?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title)
        builder.setMessage(R.string.dialog_message_progress)
        builder.setPositiveButton(R.string.button_accept) { dialog, id ->
            if (meal != null) {
                OrderProcessor.changeQueueOrderStatus(meal)
                meals.remove(meal)
                (queue_recycler_view.adapter as MealAdapter).setMealList(meals)
                OrderProcessor.setQueueMeals(meals)
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
        repository.loadMeal()
    }

    private fun changeRecyclerViewVisibility() {
        if (queue_recycler_view.visibility == View.VISIBLE) {
            queue_recycler_view.visibility = View.GONE
            queue_empty_view.visibility = View.VISIBLE
        } else {
            queue_recycler_view.visibility = View.VISIBLE
            queue_empty_view.visibility = View.GONE
        }
    }
}
