package com.example.test.adaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> {
    private Context mContext;
    private List<String> searchHisModels;
    private OnItemClearClickListener onItemClearClickListener;
    private ItemTextClickListener itemTextClickListener;

    public SearchAdapter(Context mContext, List<String> searchHisModels) {
        this.mContext = mContext;
        this.searchHisModels = searchHisModels;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemNormal = LayoutInflater.from(mContext).inflate(R.layout.layout_item_search_his, parent, false);
        return new VH(itemNormal);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tvHisInfo.setText(searchHisModels.get(position));
        holder.tvHisInfo.setOnClickListener(v -> {
            if (itemTextClickListener != null) {
                itemTextClickListener.onItemTextClick(holder.tvHisInfo.getText().toString(), position);
            }
        });
        holder.lvClearHisInfo.setOnClickListener(v -> {
            if (onItemClearClickListener != null) {
                onItemClearClickListener.onItemClearClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (searchHisModels != null) {
            return searchHisModels.size();
        }
        return 0;
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tvHisInfo;
        private RelativeLayout lvClearHisInfo;

        public VH(@NonNull View itemView) {
            super(itemView);
            lvClearHisInfo = itemView.findViewById(R.id.rl_his_clear_info);
            tvHisInfo = itemView.findViewById(R.id.tv_his_info);
        }
    }



    /**
     * 删除按钮设置监听器
     * 
     * @param onItemClearClick
     */
    public void setOnItemClearClick(OnItemClearClickListener onItemClearClick) {
        this.onItemClearClickListener = onItemClearClick;
    }

    /**
     * 删除键监听
     */
    public interface OnItemClearClickListener {
        void onItemClearClick(View v, int position);
    }

    /**
     * RecyclerView item监听器
     */
    public interface ItemTextClickListener {
        void onItemTextClick(String v, int position);
    }

    /**
     * 设置Item监听器
     * 
     * @param itemTextClickListener
     */
    public void setOnItemTextClickListener(ItemTextClickListener itemTextClickListener) {
        this.itemTextClickListener = itemTextClickListener;
    }
}
