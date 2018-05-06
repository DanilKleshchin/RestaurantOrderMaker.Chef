package com.kleshchin.danil.ordermaker_chef.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kleshchin.danil.ordermaker_chef.models.Order


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "OrderMakerChef"

        val KEY_ID = "_id"

        val MEAL_TABLE = "Meal"
        val KEY_MEAL_NAME = "Meal_Name"
        val KEY_MEAL_MAC_ADDRESS = "Mac_Address"
        val KEY_ORDER_NUMBER = "Order_Number"
        val KEY_ORDER_ID = "Order_Id"

        fun createMealContentValues(categories: ArrayList<Order>): Array<ContentValues> {
            val contentValuesArray: Array<ContentValues> = Array(categories.size, { ContentValues() })
            for (i in categories.indices) {
                val contentValues = ContentValues()
                contentValues.put(KEY_MEAL_NAME, categories[i].mealName)
                contentValues.put(KEY_MEAL_MAC_ADDRESS, categories[i].mac)
                contentValues.put(KEY_ORDER_NUMBER, categories[i].number)
                contentValues.put(KEY_ORDER_ID, categories[i].id)
                contentValuesArray[i] = contentValues
            }
            return contentValuesArray
        }

        fun createMealFromCursor(data: Cursor): ArrayList<Order>? {
            val competitions = ArrayList<Order>()
            if (data.moveToFirst()) {
                do {
                    val name = data.getString(data.getColumnIndex(KEY_MEAL_NAME))
                    val orderNumber = data.getLong(data.getColumnIndex(KEY_ORDER_NUMBER))
                    val macAddress = data.getString(data.getColumnIndex(KEY_MEAL_MAC_ADDRESS))
                    val id = data.getInt(data.getColumnIndex(KEY_ORDER_ID))
                    val meal = Order(name, macAddress , orderNumber, Order.OrderStatus.Queue, id)
                    competitions.add(meal)
                } while (data.moveToNext())
                return competitions
            }
            return null
        }

        fun insertMealList(mealList: ArrayList<Order>, resolver: ContentResolver) {
            val uri = OrderMakerProvider.createUrlForTable(MEAL_TABLE)
            resolver.delete(uri, null, null)
            resolver.bulkInsert(uri, createMealContentValues(mealList))
        }
    }

    private val TEXT_TYPE = " TEXT"
    private val INT_TYPE = " INTEGER"
    private val COMMA_SEP = ","
    private val BRACKET_RIGHT_SEP = ")"
    private val BRACKET_LEFT_SEP = "("

    private val CREATE_MEAL_TABLE = "CREATE TABLE " + MEAL_TABLE + BRACKET_LEFT_SEP +
            KEY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            KEY_MEAL_MAC_ADDRESS + TEXT_TYPE + COMMA_SEP +
            KEY_MEAL_NAME + TEXT_TYPE + COMMA_SEP +
            KEY_ORDER_ID + INT_TYPE + COMMA_SEP +
            KEY_ORDER_NUMBER + INT_TYPE + BRACKET_RIGHT_SEP

    private val DROP_MEAL_TABLE = "DROP TABLE IF EXISTS " + MEAL_TABLE

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_MEAL_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DROP_MEAL_TABLE)
    }
}
