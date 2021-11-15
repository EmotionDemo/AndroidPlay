package com.example.test.adaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.activity.model.RapexDetailModel

class RapexDetailAdapter(var mContext: Context) : RecyclerView.Adapter<RapexDetailAdapter.VH>() {

    private lateinit var model: RapexDetailModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        var view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_search_detail, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val datasInfo = model.data.datas.get(position)
        val user = datasInfo.shareUser
        val chapterName = datasInfo.chapterName
        val niceDate = datasInfo.niceDate
        val niceShareDate = datasInfo.niceShareDate
        val isLove = datasInfo.isCollect
        val title = datasInfo.title

        holder.tvAuthorDetail.text = user
        holder.tvArcTypeDetail.text = chapterName
        holder.tvTimeDetail.text = if (niceDate == null) niceShareDate else niceDate
        holder.ivLove.setBackgroundResource(if (isLove) R.mipmap.ic_zaned else R.mipmap.ic_unzan)
        holder.tv_artTitleDetail.text = title
    }

    override fun getItemCount(): Int {
        if (model.data == null) {
            return 0
        }
        return model.data.datas.size
    }


    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvAuthorDetail: TextView = itemView.findViewById(R.id.tvAuthorDetail)
        internal var tvTimeDetail: TextView = itemView.findViewById(R.id.tvTimeDetail)
        internal var tv_artTitleDetail: TextView = itemView.findViewById(R.id.tv_artTitleDetail)
        internal var tvArcTypeDetail: TextView = itemView.findViewById(R.id.tvArcTypeDetail)
        internal var rvLove: RelativeLayout = itemView.findViewById(R.id.rl_zan)
        internal var ivLove: ImageView = itemView.findViewById(R.id.ivZan)

    }

    public fun setModel(model: RapexDetailModel) {
        this.model = model
    }
}