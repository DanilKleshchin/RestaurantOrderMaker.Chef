package com.kleshchin.danil.ordermaker_chef.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.kleshchin.danil.ordermaker_chef.R
import com.kleshchin.danil.ordermaker_chef.models.Meal
import com.kleshchin.danil.ordermaker_chef.utils.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_meal_recycler_view.view.*

class MealAdapter(private val mealList: ArrayList<Meal>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {


    private var listener: MealViewHolder.OnMealClickListener? = null

    override fun getItemCount() = mealList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val inflatedView = parent.inflate(R.layout.item_meal_recycler_view, false)
        return MealViewHolder(inflatedView, listener!!)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val itemMeal = mealList[position]
        holder.bindMeal(itemMeal)
    }

    fun setOnMealClickListener(listener: MealViewHolder.OnMealClickListener) {
        this.listener = listener
    }

    class MealViewHolder(v: View, listener: OnMealClickListener) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var meal: Meal? = null
        private var listener: OnMealClickListener? = listener

        interface OnMealClickListener {
            fun onMealClick(meal: Meal?)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.meal_name -> {
                    listener?.onMealClick(meal)
                }
            }
        }

        fun bindMeal(meal: Meal) {
            this.meal = meal
            view.meal_name.text = meal.mealName
            Picasso.with(view.context).load(meal.mealIconUrl).into(view.meal_icon)
        }
    }
}
