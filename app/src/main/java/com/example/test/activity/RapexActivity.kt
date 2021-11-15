package com.example.test.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
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
    private var rvRapexDetail: RecyclerView? = null
    private var mAdapter: RapexDetailAdapter? = null
    private var canLoadMore: Boolean = true
    private var contentId: Int = 0
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
        //顶部标题
        tvTitle.text = dataParam.content
        contentId = dataParam.rapeid
        //创建一个网络请求
        callRapexDetail = service.showRapexDetail(searchPage, contentId)
        //网络请求回调
        callRapexDetail.clone().enqueue(object : Callback<RapexDetailModel> {
            override fun onResponse(call: Call<RapexDetailModel>, response: Response<RapexDetailModel>) {
                if (response.code() != 200 || response.body() == null) {
                    Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
                    return
                }
                val body = response.body()
                rvRapexDetail = findViewById(R.id.rvRapexDetail)
                mAdapter = RapexDetailAdapter(baseContext)
                if (body?.data?.datas?.size!! < body.data.size) {
                    mAdapter?.setNoMore(true)
                    mAdapter?.setLoadMore(false)
                } else {
                    mAdapter?.setNoMore(false)
                    mAdapter?.setLoadMore(true)
                }
                val linearManager = LinearLayoutManager(baseContext, RecyclerView.VERTICAL, false)
                rvRapexDetail?.layoutManager = linearManager
                mAdapter?.setModel(body)
                rvRapexDetail?.adapter = mAdapter
            }

            override fun onFailure(call: Call<RapexDetailModel>, t: Throwable) {
                call.clone().cancel()
                Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
            }
        })
        loadMore()
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        with(rvRapexDetail) {
            this?.addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!canScrollVertically(1)) {
                        //向下滑动到底部
                        updateData()
                    }
                }
            })
        }
    }

    /**
     * 更新信息
     */
    fun updateData() {
        mAdapter?.setLoadMore(true)
        mAdapter?.setNoMore(false)
        callRapexDetail = service.showRapexDetail(if (canLoadMore) ++searchPage else searchPage, contentId)
        callRapexDetail.clone().enqueue(object : Callback<RapexDetailModel> {
            override fun onResponse(call: Call<RapexDetailModel>, response: Response<RapexDetailModel>) {
                if (response.code() != 200) {
                    mAdapter?.setLoadMore(false)
                    mAdapter?.setNoMore(true)
                    canLoadMore = false
                    Toast.makeText(baseContext, "network fail!", Toast.LENGTH_SHORT).show()
                    return
                }
                val body = response.body()
                if (body?.data?.size == 0) {
                    mAdapter?.setLoadMore(false)
                    mAdapter?.setNoMore(true)
                } else {
                    mAdapter?.setLoadMore(true)
                    mAdapter?.setNoMore(false)
                    canLoadMore = true
                }
                mAdapter?.updateModel(body!!)
            }

            override fun onFailure(call: Call<RapexDetailModel>, t: Throwable) {
                Toast.makeText(baseContext, "network fail!", Toast.LENGTH_SHORT).show()
                mAdapter?.setLoadMore(false)
                mAdapter?.setNoMore(true)
                canLoadMore = false
                callRapexDetail.clone().cancel()
            }
        })
    }
}