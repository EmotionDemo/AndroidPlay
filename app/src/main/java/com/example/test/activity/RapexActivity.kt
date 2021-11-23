package com.example.test.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.alibaba.fastjson.JSON
import com.example.test.App
import com.example.test.R
import com.example.test.activity.model.Api
import com.example.test.activity.model.BaseInfoModel
import com.example.test.activity.model.ParameData
import com.example.test.activity.model.RapexDetailModel
import com.example.test.adaper.RapexDetailAdapter
import com.example.test.callback.CollectEventListener
import com.example.test.callback.MyCallBack
import com.example.test.util.ImgUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RapexActivity() : BaseActivity(){

    private var service: Api = App.getApi(Api::class.java)
    private lateinit var callRapexDetail: Call<RapexDetailModel>
    private var searchPage: Int = 0
    private lateinit var data: String
    private lateinit var rlBack: RelativeLayout
    private var rvRapexDetail: RecyclerView? = null
    private var mAdapter: RapexDetailAdapter? = null
    private var canLoadMore: Boolean = true
    private var contentId: Int = 0
    private var imgId: Int = 0
    private var myHandler: Handler? = null
    private var pbrNav: ProgressBar? = null
    private lateinit var callLove: Call<BaseInfoModel>
    private lateinit var callNoLove: Call<BaseInfoModel>
    private lateinit var executor: Executor
    private val mContext :Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rapex)
        myHandler = Handler(Looper.myLooper()!!, RapexDetailCallBack())
        pbrNav = findViewById(R.id.pbrNav)
        executor = Executors.newSingleThreadExecutor()
        initData()
    }

    /**
     * 初始化搜索列表
     */
    private fun initData() {
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
        pbrNav!!.visibility = VISIBLE
        //网络请求回调
        executor.execute {
            callRapexDetail.clone().enqueue(object : Callback<RapexDetailModel> {
                override fun onResponse(
                    call: Call<RapexDetailModel>,
                    response: Response<RapexDetailModel>
                ) {
                    pbrNav!!.visibility = View.GONE
                    if (response.code() != 200 || response.body() == null) {
                        Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val bodyStr = JSON.toJSONString(response.body())
                    val msg = myHandler?.obtainMessage()
                    val bundle = Bundle()
                    bundle.putString("rapexBean", bodyStr)
                    msg?.data = bundle
                    myHandler?.sendMessage(msg!!)
                }

                override fun onFailure(call: Call<RapexDetailModel>, t: Throwable) {
                    pbrNav!!.visibility = View.GONE
                    call.clone().cancel()
                    Toast.makeText(baseContext, "网络请求失败", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }


    /**
     * 加载更多
     */
    private fun loadMore() {
        rvRapexDetail?.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!rvRapexDetail?.canScrollVertically(1)!!) {
                    //向下滑动到底部
                    updateData()
                }
            }
        })
    }

    /**
     * 更新信息
     */
    fun updateData() {
        mAdapter?.setLoadMore(true)
        mAdapter?.setNoMore(false)
        callRapexDetail =
            service.showRapexDetail(if (canLoadMore) ++searchPage else searchPage, contentId)
        callRapexDetail.clone().enqueue(object : Callback<RapexDetailModel> {
            override fun onResponse(
                call: Call<RapexDetailModel>,
                response: Response<RapexDetailModel>
            ) {
                if (response.code() != 200) {
                    mAdapter?.setLoadMore(false)
                    mAdapter?.setNoMore(true)
                    canLoadMore = false
                    Toast.makeText(baseContext, "network fail!", Toast.LENGTH_SHORT).show()
                    return
                }
                val body = response.body()
                if (body?.data?.datas?.size!! < body.data?.size!!) {
                    mAdapter?.setLoadMore(false)
                    mAdapter?.setNoMore(true)
                } else {
                    mAdapter?.setLoadMore(true)
                    mAdapter?.setNoMore(false)
                    canLoadMore = true
                }
                mAdapter?.updateModel(body)
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

    /**
     * handler回调实现类
     */
    inner class RapexDetailCallBack : MyCallBack() {
        override fun handleMessage(msg: Message): Boolean {
            rvRapexDetail = findViewById(R.id.rvRapexDetail)
            mAdapter = RapexDetailAdapter(this@RapexActivity)
            val rapexStr = msg.data.getString("rapexBean")
            var body = JSON.parseObject(rapexStr, RapexDetailModel::class.java)
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
            mAdapter!!.setLoveListener { v,resId->
                callLove = service.collectArticle(resId)
                callNoLove = service.unCollectArticle(resId)
                //获取当前点赞状态
                imgId = ImgUtil.getImgResId(v)
                if (imgId == R.mipmap.ic_zaned) {
                    executor.execute {
                        callNoLove.clone().enqueue(object : Callback<BaseInfoModel> {
                            override fun onResponse( call: Call<BaseInfoModel>,  response: Response<BaseInfoModel>) {
                                setLove(response, v, R.mipmap.ic_unzan)
                            }

                            override fun onFailure(call: Call<BaseInfoModel>, t: Throwable) {
                                callNoLove.clone().cancel()
                            }

                        })
                    }
                } else {
                    executor.execute {
                        callLove.clone().enqueue(object : Callback<BaseInfoModel> {
                            override fun onResponse( call: Call<BaseInfoModel>, response: Response<BaseInfoModel>) {
                                setLove(response, v!!, R.mipmap.ic_zaned)
                            }

                            override fun onFailure(call: Call<BaseInfoModel>, t: Throwable) {
                                callNoLove.clone().cancel()
                            }
                        })
                    }
                }
            }
            loadMore()
            return false
        }
    }


    /**
     * 设置点赞
     */
    private fun setLove(response: Response<BaseInfoModel>, ivZan: ImageView, resId: Int) {
        if (response.body() == null || response.code() != 200) {
            Toast.makeText(baseContext, "network fail", Toast.LENGTH_SHORT).show()
            return
        }
        if (response.body()!!.errorCode == -1001) {
            Toast.makeText(baseContext, response.body()!!.errorMsg, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val errorCode = response.body()!!.errorCode
        if (errorCode == 0) {
            ivZan.setBackgroundResource(resId)
            imgId = ImgUtil.getImgResId(ivZan)
        }
    }

}