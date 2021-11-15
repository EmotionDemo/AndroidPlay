package com.example.test.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.test.App
import com.example.test.R
import com.example.test.activity.model.Api
import com.example.test.activity.model.ParameData
import com.example.test.activity.model.RapexDetailModel
import com.example.test.adaper.RapexDetailAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RapexActivity : BaseActivity() {

    private var service: Api = App.getApi(Api::class.java)
    private lateinit var callRapexDetail: Call<RapexDetailModel>
    private var searchPage: Int = 0
    private lateinit var data: String
    private lateinit var rlBack: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapex)
    }

    override fun onResume() {
        super.onResume()
        data = intent.getStringExtra("artId").toString()
        val dataParam = JSON.parseObject(data, ParameData::class.java)
        val tvTitle: TextView = findViewById(R.id.tvRapexTitle)
        rlBack = findViewById(R.id.rlBack)
        rlBack.setOnClickListener {
            finish()
        }
        tvTitle.text = dataParam.content
        callRapexDetail = service.showRapexDetail(searchPage, dataParam.rapeid)
        callRapexDetail.clone().enqueue(object : Callback<RapexDetailModel> {
            override fun onResponse(call: Call<RapexDetailModel>, response: Response<RapexDetailModel>) {
                if (response.code() != 200) {
                    Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
                    return
                }
                val rvRapexDetail: RecyclerView = findViewById(R.id.rvRapexDetail)
                val adapter = RapexDetailAdapter(baseContext)
                val linearManager = LinearLayoutManager(baseContext, RecyclerView.VERTICAL, false)
                rvRapexDetail.layoutManager = linearManager
                adapter.setModel(response.body()!!)
                rvRapexDetail.adapter = adapter
            }

            override fun onFailure(call: Call<RapexDetailModel>, t: Throwable) {
                call.clone().cancel()
                Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
            }
        })
    }


}