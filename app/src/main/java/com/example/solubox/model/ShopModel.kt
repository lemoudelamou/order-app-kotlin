package com.example.solubox.model

import android.os.Parcel
import android.os.Parcelable


class ShopModel(`in`: Parcel) : Parcelable {
    var id = 0
    var shop_name: String?
    var address: String?
    var image: String?
    var lat: Double
    var lon: Double
    var hours: String?
    var menus: List<Menu?>?
    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(shop_name)
        dest.writeString(address)
        dest.writeDouble(lat)
        dest.writeDouble(lon)
        dest.writeString(image)
        dest.writeString(hours)
        dest.writeTypedList(menus)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<ShopModel?> =
            object : Parcelable.Creator<ShopModel?> {
                override fun createFromParcel(`in`: Parcel): ShopModel {
                    return ShopModel(`in`)
                }

                override fun newArray(size: Int): Array<ShopModel?> {
                    return arrayOfNulls(size)
                }
            }
    }

    init {
        shop_name = `in`.readString()
        address = `in`.readString()
        lat = `in`.readDouble()
        lon = `in`.readDouble()
        image = `in`.readString()
        hours = `in`.readString()
        menus = `in`.createTypedArrayList(Menu.CREATOR)
    }

}