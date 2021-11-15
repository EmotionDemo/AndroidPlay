package com.example.test.fragments.GoundFragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.test.App
import com.example.test.R
import com.example.test.activity.model.Api
import com.example.test.activity.model.NavModel
import com.example.test.adaper.NavAdapter
import com.example.test.callback.MyCallBack
import com.example.test.fragments.BaseFragment
import retrofit2.Response
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * 导航
 */
class NavFragment() : BaseFragment() {
    private lateinit var service: Api
    private lateinit var executor: Executor
    private lateinit var callNavData: retrofit2.Call<NavModel>
    private lateinit var pbrNav: ProgressBar
    private lateinit var myHandler: Handler
    private lateinit var navAdapter: NavAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        service = App.getApi(Api::class.java)
        executor = Executors.newSingleThreadExecutor()
        callNavData = service.showNavData()
        navAdapter = NavAdapter(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_nav, container, false)
        pbrNav = view.findViewById(R.id.pbrNav)
        return view
    }

    override fun onResume() {
        super.onResume()
        myHandler = Handler(Looper.myLooper()!!, NavCallBack())
        showNavData()

    }

    /**
     * 获取导航信息
     */
    private fun showNavData() {
        executor.execute {
            callNavData.clone().enqueue(object : retrofit2.Callback<NavModel> {
                override fun onResponse(call: retrofit2.Call<NavModel>, response: Response<NavModel>) {
                    if (response.code() != 200) {
                        Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val bundle = Bundle()
                    val msg = myHandler.obtainMessage()
                    val NavModelStr = JSON.toJSONString(response.body())
                    bundle.putString("NavModelStr", NavModelStr)
                    msg.data = bundle
                    myHandler.sendMessage(msg)
                    pbrNav.visibility = View.GONE
                }

                override fun onFailure(call: retrofit2.Call<NavModel>, t: Throwable) {
                    call.clone().cancel()
                    pbrNav.visibility = View.GONE
                }
            })
        }
    }

    override fun getTitle(): String? {
        return null
    }

    inner class NavCallBack : MyCallBack() {
        override fun handleMessage(msg: Message): Boolean {
            val rvNav = view.findViewById<RecyclerView>(R.id.rvNav)
            rvNav.layoutManager = LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false)
            val navModelStr = msg.data.getString("NavModelStr")
            val model = JSON.parseObject(navModelStr, NavModel::class.java)
            navAdapter.setModel(model)
            rvNav.adapter = navAdapter
            return false
        }
    }
}





