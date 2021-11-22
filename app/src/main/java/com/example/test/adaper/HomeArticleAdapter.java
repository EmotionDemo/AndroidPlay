package com.example.test.adaper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.example.test.activity.model.HomeArticleModel;
import com.example.test.callback.CollectEventListener;
import com.example.test.common.TitleToDetailData;

import java.util.List;

public class HomeArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private HomeArticleModel homeArticleModels;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADER = 1;
    // 点赞
    private CollectEventListener collectEventListener;
    public void setHomeArticleModels(HomeArticleModel homeArticleModels, RecyclerView recyclerArticle) {
        this.homeArticleModels = homeArticleModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewData = LayoutInflater.from(mContext).inflate(R.layout.layout_item_article, parent, false);
        View viewLoadMore = LayoutInflater.from(mContext).inflate(R.layout.layout_item_load_more, parent, false);
        if (viewType == VIEW_TYPE_DATA) {
            return new VH(viewData);
        } else {
            return new LoadMoreViewHolder(viewLoadMore);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (homeArticleModels == null) {
            return;
        }
        if (holder instanceof VH) {
            HomeArticleModel.DataBean.DatasBean datasBean = homeArticleModels.getData().getDatas().get(position);
            int articleId = datasBean.getId();
            boolean isCollect = datasBean.isCollect();
            String title = datasBean.getTitle();
            String link = datasBean.getLink();
            ((VH) holder).tvType.setText(datasBean.getSuperChapterName());
            ((VH) holder).tvTitle.setText(datasBean.getTitle());
            ((VH) holder).tvTime.setText(datasBean.getNiceDate());
            ((VH) holder).tvAuthor.setText(
                    !TextUtils.isEmpty(datasBean.getShareUser()) ? datasBean.getShareUser() : datasBean.getAuthor());
            ((VH) holder).ivHomeZan.setBackgroundResource(isCollect ? R.mipmap.ic_zaned : R.mipmap.ic_unzan);
            ((VH) holder).rlZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (collectEventListener != null) {
                        collectEventListener.onCollectEvent(((VH) holder).ivHomeZan, articleId);
                    }
                }
            });
            ((VH) holder).rlContent.setOnClickListener((v)->{
                Intent intent = new Intent(mContext, AndroidActivity.class);
                TitleToDetailData rapexToDetailData = new TitleToDetailData(title, link);
                String toJSONString = JSON.toJSONString(rapexToDetailData);
                intent.putExtra("articleInfo", toJSONString);
                mContext.startActivity(intent);
            });
        } else if (holder instanceof LoadMoreViewHolder) {
            ((LoadMoreViewHolder) holder).ll_load_more.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (homeArticleModels.getData().getDatas().size() == position) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DATA;
        }
    }

    @Override
    public int getItemCount() {
        if (homeArticleModels != null) {
            return homeArticleModels.getData().getDatas().size() + 1;
        }
        return 0;
    }

    public HomeArticleAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateArticlesData(List<HomeArticleModel.DataBean.DatasBean> datas) {
        homeArticleModels.getData().getDatas().addAll(datas);
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tvAuthor, tvTime, tvTitle, tvType;
        private RelativeLayout rlZan,rlContent;
        private ImageView ivHomeZan;

        VH(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTitle = itemView.findViewById(R.id.tv_artTitle);
            tvType = itemView.findViewById(R.id.tvArcType);
            rlZan = itemView.findViewById(R.id.rl_home_zan);
            ivHomeZan = itemView.findViewById(R.id.ivHomeZan);
            rlContent = itemView.findViewById(R.id.rlContent);
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_load_more;

        LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_load_more = itemView.findViewById(R.id.ll_load_more);
        }
    }

    /**
     * 点赞监听器
     *
     * @param collectEventListener
     */
    public void setCollectEventListener(CollectEventListener collectEventListener) {
        this.collectEventListener = collectEventListener;
    }
}
