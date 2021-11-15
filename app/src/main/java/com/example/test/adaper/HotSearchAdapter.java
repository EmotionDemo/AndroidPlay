package com.example.test.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.activity.model.HotSearchModel;

public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.VH> {
    private Context mContext;
    private HotSearchModel hotSearchModel;
    private ItemClickListener itemClickListener;

    public HotSearchAdapter(Context mContext, HotSearchModel hotSearchModel) {
        this.mContext = mContext;
        this.hotSearchModel = hotSearchModel;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_item_hot, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        int hotInfo = hotSearchModel.getData().get(position).getOrder();
        if (hotInfo <= 2) {
            // 热门
            holder.ivHot.setImageResource(R.mipmap.ic_hot_font);
        } else if (hotInfo < 6) {
            // 推荐
            holder.ivHot.setImageResource(R.mipmap.ic_straw_font);
        } else {
            holder.ivHot.setVisibility(View.GONE);
        }
        holder.tvContent.setText(hotSearchModel.getData().get(position).getName());
        // 拿到当前Item后设置监听
        holder.iv_hot_card.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(holder.tvContent.getText().toString(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (hotSearchModel != null) {
            return hotSearchModel.getData().size();
        }
        return 0;
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tvContent;
        private ImageView ivHot;
        private CardView iv_hot_card;

        VH(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            ivHot = itemView.findViewById(R.id.iv_hot_font);
            iv_hot_card = itemView.findViewById(R.id.iv_hot_card);
        }
    }


    /**
     * item监听器
     */
    public interface ItemClickListener {
        void onItemClick(String queryInfo, int position);
    }

    /**
     * 设置监听器
     * 
     * @param listener
     */
    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }
}
