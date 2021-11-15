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
import com.example.test.callback.OnCidClickListener;

public class CidsAdapter extends RecyclerView.Adapter<CidsAdapter.VH> {
    private Context mContext;
    private String[] cids;
    // 标题点击事件
    public OnCidClickListener onCidClickListener;

    public CidsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_cid_dialog, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (cids == null) {
            return;
        }
        holder.tvCid.setText(cids[position]);
        holder.rlCidItem.setOnClickListener(v -> {
            if (onCidClickListener != null) {
                onCidClickListener.onCidClick(cids[position],position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cids.length;
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tvCid;
        private RelativeLayout rlCidItem;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvCid = itemView.findViewById(R.id.tvCidName);
            rlCidItem = itemView.findViewById(R.id.rlCidItem);
        }
    }

    public void setCids(String[] cids) {
        this.cids = cids;
    }

    /**
     * 注册cid监听器
     * 
     * @param listener
     */
    public void setOnCidClickListener(OnCidClickListener listener) {
        this.onCidClickListener = listener;
    }
}
