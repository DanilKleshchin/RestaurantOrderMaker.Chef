package com.kleshchin.danil.ordermaker_chef.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.OrderProcessor
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.adapters.MealAdapter
import com.kleshchin.danil.ordermaker_chef.models.Meal
import kotlinx.android.synthetic.main.fragment_done.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class DoneFragment: Fragment(), OrderProcessor.OnProgressOrderStatusChangedListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MealAdapter
    private var meals: ArrayList<Meal> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        OrderProcessor.setOnProgressOrderStatusChangedListener(this)
        val view = inflater?.inflate(R.layout.fragment_done, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        val meals = OrderProcessor.getDoneMeals()
        if (meals != null && !meals.isEmpty()) {
            onMealReceive(meals)
        }
    }

    override fun onProgressOrderStatusChanged(meal: Meal) {
        meals.add(meal)
        OrderProcessor.setDoneMeals(meals)
        adapter = MealAdapter(meals)
        linearLayoutManager = LinearLayoutManager(context)
        done_recycler_view.layoutManager = this.linearLayoutManager
        done_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    private fun onMealReceive(mealList: ArrayList<Meal>?) {
        if (mealList == null || mealList.isEmpty()) {
            return
        }
        meals = mealList
        OrderProcessor.setDoneMeals(meals)
        adapter = MealAdapter(meals)
        linearLayoutManager = LinearLayoutManager(context)
        done_recycler_view.layoutManager = this.linearLayoutManager
        done_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    private fun changeRecyclerViewVisibility() {
        if (meals.isEmpty()) {
            done_recycler_view.visibility = View.GONE
            done_empty_view.visibility = View.VISIBLE
        } else {
            done_recycler_view.visibility = View.VISIBLE
            done_empty_view.visibility = View.GONE
        }
    }
}
