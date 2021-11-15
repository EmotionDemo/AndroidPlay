package com.example.test.adaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.test.R;
import com.example.test.activity.RapexActivity;
import com.example.test.activity.model.ParameData;
import com.example.test.activity.model.RapexModel;
import com.yang.flowlayoutlibrary.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RapexAdapter extends RecyclerView.Adapter<RapexAdapter.VH> {
    private RapexModel model;
    private Context mContext;
    private List<String> titles = new ArrayList<>();
    private List<List<String>> rapxInfos = new ArrayList<>();
    private Map<String,Integer> rapIdMap = new HashMap<>();
    public RapexAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_ground, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tvTitle.setText(titles.get(position));
        List<String> rapxs = rapxInfos.get(position);
        holder.fllRapex.setViews(rapxs, (content) -> {
            Intent toRapexIntent = new Intent();
            toRapexIntent.setClass(mContext, RapexActivity.class);
            ParameData data = new ParameData();
            data.setContent(content);
            data.setRapeid(rapIdMap.get(content));
            String paramDataStr = JSON.toJSONString(data);
            toRapexIntent.putExtra("artId",paramDataStr);
            mContext.startActivity(toRapexIntent);
            Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        List<RapexModel.DataBean> data = model.getData();
        if (data.size() != 0) {
            return data.size();
        }
        return 0;
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private FlowLayout fllRapex;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            fllRapex = itemView.findViewById(R.id.fllGroundType);
        }
    }

    public RapexModel getModel() {
        return model;
    }

    public void setModel(RapexModel model) {
        this.model = model;
        for (int i = 0; i < model.getData().size(); i++) {
            String outName = model.getData().get(i).getName();
            List<String> childrens = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            titles.add(outName);
            // 得到Childrens
            List<RapexModel.DataBean.ChildrenBean> childrenBeans = model.getData().get(i).getChildren();
            for (int j = 0; j < childrenBeans.size(); j++) {
                String name = childrenBeans.get(j).getName();
                int id = childrenBeans.get(j).getId();
                childrens.add(name);
                ids.add(id);
                rapIdMap.put(name,id);
            }
            rapxInfos.add(childrens);
        }
    }

}
