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
import com.kleshchin.danil.ordermaker_chef.models.Order
import com.kleshchin.danil.ordermaker_chef.provider.DatabaseHelper
import com.kleshchin.danil.ordermaker_chef.provider.OrderMakerProvider
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.lang.ref.WeakReference


object OrderMakerRepository : LoaderManager.LoaderCallbacks<Cursor> {

    private val ORDER_CODE = 1
    private val TAG = "OrderMakerRepository"
    private val SERVER_ADDRESS = "http://192.168.0.102:3000"

    private var orderListener: OnReceiveOrderInformationListener? = null
    private var context: WeakReference<Context>? = null

    interface OnReceiveOrderInformationListener {
        fun onOrderReceive(mealList: ArrayList<Order>?)
    }

    fun setOnReceiveMealInformationListener(context: Context, listener: OnReceiveOrderInformationListener) {
        OrderMakerRepository.context = WeakReference(context)
        orderListener = listener
    }

    fun loadOrder() {
        val loader = InfoDownloader()
        loader.execute()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            ORDER_CODE -> return CursorLoader(context?.get(), OrderMakerProvider.createUrlForTable(DatabaseHelper.MEAL_TABLE), null, null, null, null)
            else -> throw IllegalArgumentException("no orderId handed")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        val loaderId = loader!!.id
        when (loaderId) {
            ORDER_CODE -> {
                orderListener!!.onOrderReceive(DatabaseHelper.createMealFromCursor(data!!))
            }
            else -> throw IllegalArgumentException("no loader orderId handled!")
        }
        data.close()
        (context?.get() as AppCompatActivity).loaderManager.destroyLoader(loaderId)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    fun sendOrderStatus(order: Order) {
        Thread {
            val client = OkHttpClient()
            val requestBody = FormBody.Builder()
                    .add("mealName", order.mealName)
                    .add("macAddress", order.mac)
                    .add("number", order.number.toString())
                    .add("status", order.status.toString())
                    .build()
            var request = Request.Builder()
                    .url(SERVER_ADDRESS + "/order/" + order.id.toString())
                    .put(requestBody)
                    .build()
            var response = client.newCall(request).execute()
            if (!response.isSuccessful()) {
                Log.e(TAG, response.toString())

                request = Request.Builder()
                        .url(SERVER_ADDRESS + "/order")
                        .post(requestBody)
                        .build()
                response = client.newCall(request).execute()
                if (!response.isSuccessful()) {
                    Log.e(TAG, response.toString())
                }
            }
        }.start()
    }

    private class InfoDownloader : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg p0: Void?): Void? {
            loadOrder()
            return null
        }

        override fun onPostExecute(result: Void?) {
            (context?.get() as AppCompatActivity).supportLoaderManager.restartLoader(ORDER_CODE, null, OrderMakerRepository)
            super.onPostExecute(result)
        }

        private fun loadOrder() {
            try {
                val url = SERVER_ADDRESS + "/order"
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
                val mealList: ArrayList<Order> = ArrayList()
                val jsonData = responses?.body()?.string()
                Log.i(TAG, jsonData)
                val jsonArray = JSONArray(jsonData)
                for (i in 0..(jsonArray.length() - 1)) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("mealName")
                    val macAddress = jsonObject.getString("macAddress")
                    val number = jsonObject.getLong("number")
                    val id = jsonObject.getInt("id")
                    mealList.add(Order(name, macAddress, number, Order.OrderStatus.Queue, id))
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
