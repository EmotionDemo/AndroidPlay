package com.example.test.adaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.test.R;
import com.example.test.activity.AndroidActivity;
import com.example.test.activity.model.base.BaseModel;
import com.example.test.activity.model.base.DataBean;
import com.example.test.callback.CollectEventListener;
import com.example.test.common.TitleToDetailData;

import java.util.List;

public class SearchAndPubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private BaseModel model;
    private List<DataBean.DatasBean> datasBeans;
    //点赞
    private CollectEventListener collectEventListener;
    // 常规数据
    private static final int ITEM_TYPE_NORMAL = 0;
    // 显示加载更多
    private static final int ITEM_TYPE_LOAD_MORE = 1;
    // 没有更多
    private static final int ITEM_TYPE_NO_MORE = 2;
    // 底部显示没有更多
    private boolean itemNoMore = false;
    // 底部显示加载更多
    private boolean isLoadMore = false;


    public SearchAndPubAdapter(Context mContext, BaseModel model, CollectEventListener collectEventListener) {
        this.mContext = mContext;
        this.model = model;
        datasBeans = model.getData().getDatas();
        this.collectEventListener = collectEventListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View normal = LayoutInflater.from(mContext).inflate(R.layout.layout_item_search_detail, parent, false);
        View loader = LayoutInflater.from(mContext).inflate(R.layout.layout_item_load_more, parent, false);
        View noMore = LayoutInflater.from(mContext).inflate(R.layout.layout_item_no_more, parent, false);
        if (viewType == ITEM_TYPE_NORMAL) {
            return new VH(normal);
        } else if (viewType == ITEM_TYPE_LOAD_MORE) {
            return new LoadMoreViewHolder(loader);
        } else {
            return new NoMoreViewHolder(noMore);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        datasBeans = model.getData().getDatas();
        if (datasBeans.size() == 0) {
            return;
        }
        if (holder instanceof VH) {
            boolean isCollect = datasBeans.get(position).isCollect();
            String artUser = datasBeans.get(position).getAuthor();
            String artShare = datasBeans.get(position).getShareUser();
            String niceDate = datasBeans.get(position).getNiceDate();
            String niceShareDate = datasBeans.get(position).getNiceShareDate();
            int id = datasBeans.get(position).getId();
            String title = datasBeans.get(position).getTitle();
            String  link = datasBeans.get(position).getLink();
            // 设置文章标题
            ((VH) holder).tv_artTitleDetail.setText(title);
            // 设置文章类型
            ((VH) holder).tvArcTypeDetail.setText(datasBeans.get(position).getChapterName());
            // 设置文章作者或者分享人
            ((VH) holder).tvAuthorDetail.setText(!artUser.equals("") ? artUser : artShare);
            // 设置发布事件或者分享时间
            ((VH) holder).tvTimeDetail.setText(!niceDate.equals("") ? niceDate : niceShareDate);
            ((VH) holder).ivZan.setBackgroundResource(isCollect ? R.mipmap.ic_zaned : R.mipmap.ic_unzan);
            ((VH) holder).rlZan.setOnClickListener(v -> {
                collectEventListener.onCollectEvent(((VH) holder).ivZan, id);
            });
            ((VH) holder).rlItemContent.setOnClickListener((v)->{
                Intent intent = new Intent((Activity)mContext  , AndroidActivity.class);
                TitleToDetailData rapexToDetailData = new TitleToDetailData(title, link);
                String toJSONString = JSON.toJSONString(rapexToDetailData);
                intent.putExtra("articleInfo", toJSONString);
                mContext.startActivity(intent);
            });
        } else if (holder instanceof LoadMoreViewHolder) {
            ((LoadMoreViewHolder) holder).ll_load_more.setVisibility(View.VISIBLE);
        } else if (holder instanceof NoMoreViewHolder) {
            ((NoMoreViewHolder) holder).ll_no_more.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新添加后的数据
     *
     * @param model
     */
    public void updateSearchDetail(BaseModel model) {
        datasBeans.addAll(model.getData().getDatas());
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            if (this.isLoadMore) {
                return ITEM_TYPE_LOAD_MORE;
            }
            if (this.itemNoMore) {
                return ITEM_TYPE_NO_MORE;
            }
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (datasBeans == null) {
            return 0;
        }
        if (isLoadMore || itemNoMore) {
            return datasBeans.size() + 1;
        }
        return datasBeans.size();
    }

    public void setItemNoMore(boolean noMore) {
        this.itemNoMore = noMore;
    }


    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    /**
     * 常规数据Holder
     */
    class VH extends RecyclerView.ViewHolder {
        private TextView tvAuthorDetail, tvTimeDetail, tv_artTitleDetail, tvArcTypeDetail;
        private RelativeLayout rlZan;
        private ImageView ivZan;
        private RelativeLayout rlItemContent;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTimeDetail = itemView.findViewById(R.id.tvTimeDetail);
            tvAuthorDetail = itemView.findViewById(R.id.tvAuthorDetail);
            tv_artTitleDetail = itemView.findViewById(R.id.tv_artTitleDetail);
            tvArcTypeDetail = itemView.findViewById(R.id.tvArcTypeDetail);
            rlZan = itemView.findViewById(R.id.rl_zan);
            ivZan = itemView.findViewById(R.id.ivZan);
            rlItemContent = itemView.findViewById(R.id.rlItemContent);
        }
    }

    /**
     * 加载更多Holder
     */
    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_load_more;

        LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_load_more = itemView.findViewById(R.id.ll_load_more);
        }
    }

    /**
     * 没有更多数据Holder
     */
    class NoMoreViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_no_more;

        NoMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_no_more = itemView.findViewById(R.id.ll_no_more);
        }
    }

    /**
     * 设置点赞监听器
     */

    public void setCollectEventListener(CollectEventListener listener) {
        this.collectEventListener = listener;
    }
}
