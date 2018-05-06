package com.kleshchin.danil.ordermaker_chef.models

import android.os.Parcel
import android.os.Parcelable

data class Meal(var orderId: Int, var name: String, var iconUrl: String, var price: Int,
                var info: String) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeInt(this.orderId)
        p0?.writeString(this.name)
        p0?.writeString(this.iconUrl)
        p0?.writeInt(this.price)
        p0?.writeString(this.info)
    }

    companion object CREATOR : Parcelable.Creator<Meal> {
        override fun createFromParcel(parcel: Parcel): Meal {
            return Meal(parcel)
        }

        override fun newArray(size: Int): Array<Meal?> {
            return arrayOfNulls(size)
        }
    }
}
