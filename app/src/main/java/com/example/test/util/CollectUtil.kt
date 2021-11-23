package com.example.test.util

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.test.activity.LoginActivity
import com.example.test.activity.model.BaseInfoModel
import retrofit2.Response

class CollectUtil {
    companion object{
         var resImgId:Int = 0
        /**
         * 设置点赞
         *
         * @param response
         * @param ivZan
         */
         fun setCollect(response: Response<BaseInfoModel?>, ivZan: ImageView,
                        resId: Int,context :Context) {
            if (response.code() != 200 || response.body() == null) {
                Toast.makeText(context, "请求失败！", Toast.LENGTH_SHORT).show()
                return
            }
            if (response.body()!!.errorCode == -1001) {
                Toast.makeText(
                    context,
                    response.body()!!.errorMsg,
                    Toast.LENGTH_SHORT
                ).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
            val errorCode = response.body()!!.errorCode
            if (errorCode == 0) {
                ivZan.setBackgroundResource(resId)
                resImgId = resId
            }
        }
    }
}