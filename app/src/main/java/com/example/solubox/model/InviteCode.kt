package com.example.solubox.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@SuppressLint("NewApi")
class InviteCode {
    var id: Int? = 0
    var owner_id: Int? = 0
    var promo_code_id: Int? = 0
    var code: String? = null
    var code_type: String? = null
    var usage_limit: String? = null
    val active: Int = 0
    val amount: Int = 0


}