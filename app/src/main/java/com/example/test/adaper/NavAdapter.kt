package com.example.test.adaper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.alibaba.fastjson.JSON
import com.example.test.R
import com.example.test.activity.AndroidActivity
import com.example.test.activity.model.NavModel
import com.example.test.common.TitleToDetailData
import com.yang.flowlayoutlibrary.FlowLayout

class NavAdapter(var mContext: Context?) : Adapter<NavAdapter.VH>() {
    private lateinit var model: NavModel
    private var titles: MutableList<String> = ArrayList()
    private var navs: MutableList<List<String>> = ArrayList()
    private var nameMap:HashMap<String,String> = HashMap()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_ground, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = model.data
        if (data.isEmpty()) {
            return
        }
        holder.tvTitle.text = titles.get(position)
        holder.fllGroundType.setViews(navs.get(position), object : FlowLayout.OnItemClickListener {
            override fun onItemClick(content: String?) {
                var intent = Intent(mContext as Activity, AndroidActivity::class.java)
                val rapexToDetailData = TitleToDetailData(content!!, nameMap.get(content)!!);
                val toJSONString = JSON.toJSONString(rapexToDetailData)
                intent.putExtra("articleInfo", toJSONString)
                mContext!!.startActivity(intent)
            }
        })
    }

    /**
     * 获取行数
     */
    override fun getItemCount(): Int {
        model.data.ifEmpty {
            return 0
        }
        return model.data.size
    }

    /**
     * 设置数据源
     */
    public fun setModel(model: NavModel) {
        this.model = model
        //设置title
        for (i in 0..model.data.size - 1) {
            val name = model.data[i].name
            titles.add(name)
            val contentNames: MutableList<String> = ArrayList()
            val articles = model.data[i].articles
            for (j in 0..articles.size - 1) {
                contentNames.add(articles[j].title)
                nameMap.put(articles[j].title,articles[j].link)
            }
            //添加实体
            navs.add(contentNames)
        }
    }

    /**
     * 创建ViewHolder
     */
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fllGroundType = itemView.findViewById<FlowLayout>(R.id.fllGroundType)
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    }
}