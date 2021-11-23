package com.example.test.util

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetWorkUtil<T> {

   companion object{
//        private lateinit var netBackListener: IDataBackListener<T>
        fun <T> setNetBackListener(netBackListener: IDataBackListener<T>):Unit {
//            NetWorkUtil.netBackListener = netBackListener
            //TODO
        }

        fun <T> startRequest(call: Call<T>, context: Context) {
            call.clone().enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {

                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    call.clone().cancel()
                }
            })
        }
    }

    interface IDataBackListener<T> {
        fun onSuccess(call: Call<T>, response: Response<T>)
        fun onError(call: Call<T>, t: Throwable)
    }

}