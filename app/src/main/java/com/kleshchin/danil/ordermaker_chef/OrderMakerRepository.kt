package com.kleshchin.danil.ordermaker_chef

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.kleshchin.danil.ordermaker_chef.models.CategoryMeal
import com.kleshchin.danil.ordermaker_chef.models.Meal
import com.kleshchin.danil.ordermaker_chef.provider.DatabaseHelper
import com.kleshchin.danil.ordermaker_chef.provider.OrderMakerProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.lang.ref.WeakReference


object OrderMakerRepository : LoaderManager.LoaderCallbacks<Cursor> {

    private val CATEGORY_CODE = 0
    private val MEAL_CODE = 1
    private val TAG = "OrderMakerRepository"
    private val SERVER_ADDRESS = "http://192.168.0.102:3000"

    private var categoryListener: OnReceiveCategoryInformationListener? = null
    private var mealListener: OnReceiveMealInformationListener? = null
    private var context: WeakReference<Context>? = null

    interface OnReceiveCategoryInformationListener {
        fun onCategoryReceive(categoryList: ArrayList<CategoryMeal>?)
    }

    interface OnReceiveMealInformationListener {
        fun onMealReceive(mealList: ArrayList<Meal>?)
    }

    fun setOnReceiveMealInformationListener(context: Context, listener: OnReceiveMealInformationListener) {
        OrderMakerRepository.context = WeakReference(context)
        mealListener = listener
    }

    fun loadMeal() {
        val loader = InfoDownloader(InfoDownloader.Models.Meal)
        loader.execute()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            CATEGORY_CODE -> return CursorLoader(context?.get(), OrderMakerProvider.createUrlForTable(DatabaseHelper.CATEGORY_TABLE), null, null, null, null)
            MEAL_CODE -> return CursorLoader(context?.get(), OrderMakerProvider.createUrlForTable(DatabaseHelper.MEAL_TABLE), null, null, null, null)
            else -> throw IllegalArgumentException("no id handed")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        val loaderId = loader!!.id
        when (loaderId) {
            CATEGORY_CODE -> {
                categoryListener!!.onCategoryReceive(DatabaseHelper.createCategoryFromCursor(data!!))
            }
            MEAL_CODE -> {
                mealListener!!.onMealReceive(DatabaseHelper.createMealFromCursor(data!!))
            }
            else -> throw IllegalArgumentException("no loader id handled!")
        }
        data.close()
        (context?.get() as AppCompatActivity).loaderManager.destroyLoader(loaderId)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    private class InfoDownloader(var model : Models) : AsyncTask<Void, Void, Void>() {
        enum class Models {
            Meal, Category
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            when (model) {
                Models.Category -> {
                    loadCategory()
                }
                Models.Meal -> {
                    loadMeal()
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            var tableCode: Int = -1
            when (model) {
                Models.Meal -> {
                    tableCode = MEAL_CODE
                }
                Models.Category -> {
                    tableCode = CATEGORY_CODE
                }
            }
            (context?.get() as AppCompatActivity).supportLoaderManager.restartLoader(tableCode, null, OrderMakerRepository)
            super.onPostExecute(result)
        }

        private fun loadCategory() {
            try {
                val url = SERVER_ADDRESS +"/category"
                val client = OkHttpClient()
                val request = Request.Builder()
                        .url(url)
                        .build()
                var responses: Response? = null
                try {
                    responses = client.newCall(request).execute()
                    Log.i(TAG, "Load url " + url)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }
                val categoryList: ArrayList<CategoryMeal> = ArrayList()
                val jsonData = responses?.body()?.string()
                Log.i(TAG, jsonData)
                val jsonArray = JSONArray(jsonData)
                for (i in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val imageUrl = SERVER_ADDRESS + jsonObject.getString("imageUrl")
                    categoryList.add(CategoryMeal(name, imageUrl))
                }
                val resolver = context?.get()?.contentResolver
                if (resolver != null) {
                    DatabaseHelper.insertCategoryList(categoryList, resolver)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        private fun loadMeal() {
            try {
                val url = SERVER_ADDRESS + "/meal"
                val client = OkHttpClient()
                val request = Request.Builder()
                        .url(url)
                        .build()
                var responses: Response? = null
                try {
                    responses = client.newCall(request).execute()
                    Log.i(TAG, "Load url " + url)
                } catch (e: IOException) {
                    e.printStackTrace()
                    return
                }
                val mealList: ArrayList<Meal> = ArrayList()
                val jsonData = responses?.body()?.string()
                Log.i(TAG, jsonData)
                val jsonArray = JSONArray(jsonData)
                for (i in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val imageUrl = SERVER_ADDRESS + jsonObject.getString("imageUrl")
                    val price = jsonObject.getInt("price")
                    val description = jsonObject.getString("description")
                    mealList.add(Meal(name, imageUrl, price, description))
                }
                val resolver = context?.get()?.contentResolver
                if (resolver != null) {
                    DatabaseHelper.insertMealList(mealList, resolver)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
