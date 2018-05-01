package com.kleshchin.danil.ordermaker_chef.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.OrderMakerRepository
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.adapters.MealAdapter
import com.kleshchin.danil.ordermaker_chef.models.Meal
import kotlinx.android.synthetic.main.fragment_progress.*

/**
 * Created by Danil Kleshchin on 01-May-18.
 */
class ProgressFragment : Fragment(), MealAdapter.MealViewHolder.OnMealClickListener {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MealAdapter
    private var meals: ArrayList<Meal> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_progress, container, false)
        return view
    }

    fun onMealReceive(mealList: ArrayList<Meal>?) {
        if (mealList == null || mealList.isEmpty()) {
            return
        }
        meals = mealList
        adapter = MealAdapter(meals)
        adapter.setOnMealClickListener(this)
        linearLayoutManager = LinearLayoutManager(context)
        progress_recycler_view.layoutManager = this.linearLayoutManager
        progress_recycler_view.adapter = adapter
        changeRecyclerViewVisibility()
    }

    override fun onMealClick(meal: Meal?) {
        TODO("Show confirmation dialog")
    }

    private fun changeRecyclerViewVisibility() {
        if (progress_recycler_view.visibility == View.VISIBLE) {
            progress_recycler_view.visibility = View.GONE
            progress_empty_view.visibility = View.VISIBLE
        } else {
            progress_recycler_view.visibility = View.VISIBLE
            progress_empty_view.visibility = View.GONE
        }
    }
}
