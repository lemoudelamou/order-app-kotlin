package com.example.solubox.Utils



import com.example.solubox.entities.ApiError
import com.example.solubox.network.RetrofitBuilder.retrofit
import okhttp3.ResponseBody
import java.io.IOException

object Utils {
    @JvmStatic
    fun converErrors(response: ResponseBody): ApiError? {
        val converter = retrofit.responseBodyConverter<ApiError>(ApiError::class.java, arrayOfNulls(0))
        var apiError = null
        try {
            converter.convert(response).also { apiError = it as Nothing? }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return apiError
    }
}