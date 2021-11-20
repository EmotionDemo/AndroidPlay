package com.example.test.activity

import android.os.Bundle
import android.widget.RelativeLayout
import com.example.test.R.id
import com.example.test.R.layout
import com.just.agentweb.AgentWeb
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AndroidActivity : BaseActivity() {
    private lateinit var rlContent : RelativeLayout;
    private lateinit var executor: Executor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_android)
        rlContent = findViewById(id.rlContent)
        executor = Executors.newSingleThreadExecutor()
    }

    override fun onResume() {
        super.onResume()
        executor.execute {
            /*AgentWeb awvContentView = AgentWeb.with(this)
                .setAgentWebParent(rlContent,RelativeLayout.LayoutParams(-1,1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go();*/
            /*var awvContentView:AgentWeb = AgentWeb.with(this)
                .setAgentWebParent(rlContent,RelativeLayout.LayoutParams(-1,1))*/
//            var awvContentView: AgentWeb = AgentWeb.with(this)




        }
    }
}