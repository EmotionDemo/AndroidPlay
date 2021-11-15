package com.example.test.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.activity.SearchActivity;

public abstract class BaseFragment extends Fragment {
    protected View view;
    private  TextView tvTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_header, container, false);
        initToolbar();
        return view;
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        tvTitle = view.findViewById(R.id.tv_fg_title);
        tvTitle.setText(getTitle());
    }

    /**
     * 搜索
     */
    private void doSearch() {
        RelativeLayout rl_search = view.findViewById(R.id.rl_search);
        rl_search.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
    }


    /**
     * 初始化顶部搜索以及标题
     */
    void initToolbar() {
        doSearch();
        setTitle();
    }

    /**
     * 获取标题
     * 
     * @return
     */
    public abstract String getTitle();

    /**
     * 修改标题
     * @param newTitle
     */
    protected void changeTitle(String newTitle){
        tvTitle.setText(newTitle);
    }
}
