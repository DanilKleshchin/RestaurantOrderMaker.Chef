package com.kleshchin.danil.ordermaker_chef.models

import java.io.Serializable

/**
 * Created by Danil Kleshchin on 05-May-18.
 */
public data class Order(var mealName: String, var mac: String, var number: Long, var status: OrderStatus, var id: Int) : Serializable {
    public enum class OrderStatus {
        Queue, Progress, Done;
    }
}
