package com.example.test.adaper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.example.test.R
import com.example.test.activity.AndroidActivity
import com.example.test.activity.model.RapexDetailModel
import com.example.test.common.TitleToDetailData

class RapexDetailAdapter(var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var model: RapexDetailModel
    private val ITEM_NORMAL: Int = 0
    private val ITEM_LOAD_MORE: Int = 1
    private val ITEM_NOMORE: Int = 2
    private var itemNoMore: Boolean = false
    private var itemLoadMore: Boolean = false
    private var itemType: Int = -1
    private lateinit var loveListener: (ImageView, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewNormal =
            LayoutInflater.from(mContext).inflate(R.layout.layout_item_search_detail, parent, false)
        val viewLoadMore =
            LayoutInflater.from(mContext).inflate(R.layout.layout_item_load_more, parent, false)
        val viewNoMore =
            LayoutInflater.from(mContext).inflate(R.layout.layout_item_no_more, parent, false)
        return when (itemType) {
            ITEM_NOMORE -> VHNoMore(viewNoMore)
            ITEM_LOAD_MORE -> VHLoadMore(viewLoadMore)
            else -> VH(viewNormal)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (itemType) {
            ITEM_NORMAL -> {
                holder as VH
                val datasInfo = model.data.datas.get(position)
                val user = datasInfo.shareUser
                val chapterName = datasInfo.chapterName
                val niceDate = datasInfo.niceDate
                val niceShareDate = datasInfo.niceShareDate
                val isLove = datasInfo.isCollect
                val title = datasInfo.title
                val id = datasInfo.id
                val link = datasInfo.link
                holder.tvAuthorDetail.text = user
                holder.tvArcTypeDetail.text = chapterName
                holder.tvTimeDetail.text = niceDate ?: niceShareDate
                holder.ivLove.setBackgroundResource(if (isLove) R.mipmap.ic_zaned else R.mipmap.ic_unzan)
                holder.rlZan.setOnClickListener {
                    loveListener.invoke(holder.ivLove, id)
                }
                holder.tv_artTitleDetail.text = title
                holder.rlItemContent.setOnClickListener {
                    var intent: Intent = Intent(mContext as Activity, AndroidActivity::class.java)
                    val rapexToDetailData = TitleToDetailData(title, link)
                    val toJSONString = JSON.toJSONString(rapexToDetailData)
                    intent.putExtra("articleInfo", toJSONString)
                    mContext.startActivity(intent)
                }
            }
            ITEM_LOAD_MORE -> {
                holder as VHLoadMore
                holder.vLoadMore.visibility = View.VISIBLE
            }
            ITEM_NOMORE -> {
                holder as VHNoMore
                holder.vNoMore.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int {
        if (model.data == null) {
            return 0
        }
        return model.data.datas.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position + 1 == itemCount) {
            if (this.itemLoadMore) {
                itemType = ITEM_LOAD_MORE
            }
            if (this.itemNoMore) {
                itemType = ITEM_NOMORE
            }
        } else {
            itemType = ITEM_NORMAL
        }
        return itemType
    }

    /**
     * 常规ViewHolder
     */
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvAuthorDetail: TextView = itemView.findViewById(R.id.tvAuthorDetail)
        internal var tvTimeDetail: TextView = itemView.findViewById(R.id.tvTimeDetail)
        internal var tv_artTitleDetail: TextView = itemView.findViewById(R.id.tv_artTitleDetail)
        internal var tvArcTypeDetail: TextView = itemView.findViewById(R.id.tvArcTypeDetail)
        internal var ivLove: ImageView = itemView.findViewById(R.id.ivZan)
        internal var rlZan: RelativeLayout = itemView.findViewById(R.id.rl_zan)
        internal var rlItemContent: RelativeLayout = itemView.findViewById(R.id.rlItemContent)
    }

    inner class VHLoadMore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var vLoadMore: LinearLayout = itemView.findViewById(R.id.ll_load_more)
    }

    inner class VHNoMore(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var vNoMore: LinearLayout = itemView.findViewById(R.id.ll_no_more)
    }

    fun setModel(model: RapexDetailModel) {
        this.model = model
    }

    fun setNoMore(isNoMore: Boolean) {
        this.itemNoMore = isNoMore
    }

    fun setLoadMore(loadMore: Boolean) {
        this.itemLoadMore = loadMore
    }

    /**
     *更新界面布局
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateModel(datas: RapexDetailModel) {
        val newData = datas.data.datas
        model.data.datas.addAll(newData)
        this.notifyDataSetChanged()
    }

    /**
     * 设置点赞监听
     */
    fun setLoveListener(listener: (ImageView, Int) -> Unit) {
        this.loveListener = listener
    }

}