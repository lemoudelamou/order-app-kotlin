package com.example.solubox.model

import android.os.Parcel
import android.os.Parcelable

class Menu protected constructor(`in`: Parcel) : Parcelable {
    var shop_id = 0
    var product_name: String?
    var price: Float
    var totalInCart: Int
    var image: String?
    var description: String?
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(product_name)
        dest.writeFloat(price)
        dest.writeString(image)
        dest.writeInt(totalInCart)
        dest.writeString(description)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Menu?> = object : Parcelable.Creator<Menu?> {
            override fun createFromParcel(`in`: Parcel): Menu {
                return Menu(`in`)
            }

            override fun newArray(size: Int): Array<Menu?> {
                return arrayOfNulls(size)
            }
        }
    }

    init {
        product_name = `in`.readString()
        price = `in`.readFloat()
        image = `in`.readString()
        totalInCart = `in`.readInt()
        description = `in`.readString()
    }
}