package com.kleshchin.danil.ordermaker_chef.models
import java.io.Serializable

data class CategoryMeal(var categoryName: String, var categoryImageUrl: String) : Serializable {
    var name: String = categoryName
    var image: String = categoryImageUrl
}
