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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.test.R;
import com.example.test.activity.AndroidActivity;
import com.example.test.activity.model.ProjectModel;
import com.example.test.callback.CollectEventListener;
import com.example.test.common.TitleToDetailData;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ProjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ProjectModel model;
    private CollectEventListener<ImageView> listener;
    // 没有更多
    private static final int ITEM_NO_MORE = 2;
    // 加载更多
    private static final int ITEM_LOAD_MORE = 1;
    // 常规条目
    private static final int ITEM_NORMAL = 0;
    private boolean isLoadMore = false;
    private boolean itemNoMore = false;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_NORMAL) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_project, parent, false);
            return new VH(view);
        } else if (viewType == ITEM_LOAD_MORE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_load_more, parent, false);
            return new LoadMoreVH(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_no_more, parent, false);
            return new NoMoreVH(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (model.getData().getDatas() == null) {
            return;
        }
        if (holder instanceof VH) {
            ProjectModel.DataBean.DatasBean datasBean = model.getData().getDatas().get(position);
            boolean isZaned = datasBean.isCollect();
            String title = datasBean.getTitle();
            String link = datasBean.getLink();
            Glide.with(mContext)
                    .load(datasBean.getEnvelopePic())
                    .error(R.mipmap.ic_error)
                    .placeholder(R.mipmap.ic_pjo_loading)
                    .centerCrop()
                    .transition(withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((VH) holder).ivDes);
            ((VH) holder).tvAuthor.setText(datasBean.getAuthor());
            ((VH) holder).tvTime
                    .setText(datasBean.getNiceDate() == null ? datasBean.getNiceShareDate() : datasBean.getNiceDate());
            ((VH) holder).tvTitle.setText(datasBean.getTitle());
            ((VH) holder).tvDes.setText(datasBean.getDesc());
            ((VH) holder).rlPjoContent.setOnClickListener((v) -> {
                Intent intent = new Intent(mContext, AndroidActivity.class);
                TitleToDetailData rapexToDetailData = new TitleToDetailData(title, link);
                String toJSONString = JSON.toJSONString(rapexToDetailData);
                intent.putExtra("articleInfo", toJSONString);
                mContext.startActivity(intent);
            });
            ((VH) holder).ivLove.setBackgroundResource(isZaned ? R.mipmap.ic_zaned : R.mipmap.ic_unzan);
            ((VH) holder).rlPjoLove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCollectEvent(((VH) holder).ivLove, datasBean.getId());
                }
            });

        } else if (holder instanceof LoadMoreVH) {
            ((LoadMoreVH) holder).ll_load_more.setVisibility(View.VISIBLE);
        } else if (holder instanceof NoMoreVH) {
            ((NoMoreVH) holder).ll_no_more.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (model == null) {
            return 0;
        }
        ProjectModel.DataBean data = model.getData();
        if (isLoadMore || itemNoMore) {
            return data.getDatas().size() + 1;
        }
        return data.getDatas().size();
    }

    class VH extends RecyclerView.ViewHolder {
        private ImageView ivDes, ivLove;
        private TextView tvTitle, tvTime, tvDes, tvAuthor;
        private CardView cardPjo;
        private LinearLayout rlPjoContent;
        private RelativeLayout rlPjoLove;
        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDes = itemView.findViewById(R.id.tvDes);
            tvAuthor = itemView.findViewById(R.id.tvAutherName);
            ivDes = itemView.findViewById(R.id.ivPjo);
            ivLove = itemView.findViewById(R.id.ivLove);
            cardPjo = itemView.findViewById(R.id.cardPjo);
            rlPjoContent = itemView.findViewById(R.id.rlPjoContent);
            rlPjoLove = itemView.findViewById(R.id.rlPjoLove);
        }
    }

    /***
     * 加载更多
     */
    class LoadMoreVH extends RecyclerView.ViewHolder {
        private LinearLayout ll_load_more;

        public LoadMoreVH(@NonNull View itemView) {
            super(itemView);
            ll_load_more = itemView.findViewById(R.id.ll_load_more);
        }
    }

    /**
     * 没有更多
     */
    class NoMoreVH extends RecyclerView.ViewHolder {
        private LinearLayout ll_no_more;

        public NoMoreVH(@NonNull View itemView) {
            super(itemView);
            ll_no_more = itemView.findViewById(R.id.ll_no_more);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            if (this.isLoadMore) {
                return ITEM_LOAD_MORE;
            }
            if (this.itemNoMore) {
                return ITEM_NO_MORE;
            }
        }
        return ITEM_NORMAL;
    }

    public void setModel(ProjectModel model) {
        this.model = model;
    }

    public ProjectsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
    }

    public void setItemNoMore(boolean itemNoMore) {
        this.itemNoMore = itemNoMore;
    }

    /**
     * 更新添加后的数据
     *
     * @param
     */
    public void updatePjoData(ProjectModel.DataBean datasBean) {
        model.getData().getDatas().addAll(datasBean.getDatas());
        this.notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof VH) {
            ImageView imageView = ((VH) holder).ivDes;
            if (imageView != null) {
                Glide.with(mContext)
                        .clear(imageView);
            }
        }
    }

    /**
     * 设置点赞监听器
     * @param listener
     */
    public void setCollectListener(CollectEventListener<ImageView> listener){
        this.listener = listener;
    }
}
