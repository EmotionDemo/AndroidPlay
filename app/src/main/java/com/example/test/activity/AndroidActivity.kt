package com.example.test.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.alibaba.fastjson.JSON
import com.example.test.R.id
import com.example.test.R.layout
import com.example.test.common.TitleToDetailData
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AndroidActivity : BaseActivity() {
    private lateinit var rlContent: RelativeLayout;
    private lateinit var executor: Executor
    private lateinit var mAgentWeb: AgentWeb
    private var rlWebBack: RelativeLayout? = null
    private var tvWebTitle: TextView? = null
    private lateinit var articleInfo: String
    private lateinit var rapexData: TitleToDetailData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_android)
        articleInfo = intent.getStringExtra("articleInfo").toString()
        if (TextUtils.isEmpty(articleInfo)){
            throw IllegalArgumentException("非法的参数异常,服务器返回的地址链接异常!")
        }
        rapexData = JSON.parseObject(articleInfo, TitleToDetailData::class.java)
        rlContent = findViewById(id.rlContent)
        executor = Executors.newSingleThreadExecutor()
        rlWebBack = findViewById(id.rlWebBack)
        tvWebTitle = findViewById(id.tvWebTitle)
        tvWebTitle?.text = rapexData.tvWebTitle
        rlWebBack!!.setOnClickListener {
            finish()
        }
        initWebView()
    }

    private fun initWebView() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                rlContent,
                -1,
                RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )//传入AgentWeb的父控件。
            .useDefaultIndicator(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
            .setMainFrameErrorView(
                layout.agentweb_error_page,
                -1
            )
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
            .interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
            .createAgentWeb()//创建AgentWeb。
            .ready()//设置 WebSettings。
            .go(rapexData.link) //WebView载入
    }

    override fun onResume() {
        super.onResume()
        mAgentWeb.webLifeCycle.onResume()
    }

    override fun onPause() {
        super.onPause()
        mAgentWeb.webLifeCycle.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAgentWeb.webLifeCycle.onDestroy()
    }

}

