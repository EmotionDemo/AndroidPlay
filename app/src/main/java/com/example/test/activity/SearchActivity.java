package com.example.test.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.HotSearchModel;
import com.example.test.activity.model.SearchHisModel;
import com.example.test.adaper.HotSearchAdapter;
import com.example.test.adaper.SearchAdapter;
import com.example.test.dao.SearchHisDao;
import com.example.test.dao.SearchHisModelBase;
import com.example.test.view.MyRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    private SearchView svSearch;
    private MyRecyclerView rvSearHis;
    private List<String> hisModels;
    private SearchAdapter searchAdapter;
    private SearchHisDao searchHisDao;
    private LinearLayout ll_clearAll;
    private HotSearchAdapter hotSearchAdapter;
    private RecyclerView rv_hot;
    private Executor executor = Executors.newSingleThreadExecutor();
    private HotSearchModel hotSearchModel;
    private Call<HotSearchModel> hotSearchModelCall;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        getService();
        initDataBase();
        initViews();
        callHotInfo();
    }

    /***
     * 调用热门搜索
     */
    private void callHotInfo() {
        executor.execute(() -> {
            hotSearchModelCall.enqueue(new Callback<HotSearchModel>() {
                @Override
                public void onResponse(Call<HotSearchModel> call, Response<HotSearchModel> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            hotSearchModel = response.body();
                            // 在主线程中更新UI
                            runOnUiThread(() -> {
                                hotSearchAdapter = new HotSearchAdapter(mContext, hotSearchModel);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
                                rv_hot.setLayoutManager(gridLayoutManager);
                                rv_hot.setAdapter(hotSearchAdapter);
                                hotSearchAdapter.notifyDataSetChanged();
                                getHotDataAndSearch();
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<HotSearchModel> call, Throwable t) {
                    call.cancel();
                    try {
                        throw new Exception(t.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    /**
     * 初始化热门搜索网络服务
     */
    private void getService() {
        Api service = App.getApi(Api.class);
        hotSearchModelCall = service.getHotSearchModel();
    }

    /**
     * 初始化数据库
     */
    private void initDataBase() {
        SearchHisModelBase searchHisModelBase = Room
                .databaseBuilder(getApplicationContext(), SearchHisModelBase.class, "search_his").build();
        searchHisDao = searchHisModelBase.getSearchDao();
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        RelativeLayout rlBack = findViewById(R.id.rl_back);
        rlBack.setOnClickListener((v) -> {
            finish();
        });
        ll_clearAll = findViewById(R.id.ll_clearAll);
        svSearch = findViewById(R.id.sv_search);
        svSearch.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        svSearch.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        rv_hot = findViewById(R.id.rv_hot);
        rvSearHis = findViewById(R.id.rv_sear_his);
        rvSearHis.setMaxHeight(widthAndHeight[1] / 2);
        rvSearHis.setVisibility(View.GONE);
        ll_clearAll.setVisibility(View.GONE);
        hisModels = new ArrayList<>();
        executor.execute(() -> {
            List<SearchHisModel> allHisInfo = searchHisDao.getAllHisInfo();
            if (allHisInfo != null && allHisInfo.size() != 0) {
                for (SearchHisModel searchHisModel : allHisInfo) {
                    hisModels.add(searchHisModel.getHisInfo());
                }
            }
        });
        searchAdapter = new SearchAdapter(this, hisModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        rvSearHis.setLayoutManager(linearLayoutManager);
        rvSearHis.setAdapter(searchAdapter);
        // 搜索框输入
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    return false;
                }
                Intent intent = new Intent(SearchActivity.this, SearchDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("queryInfo", query);
                intent.putExtras(bundle);
                startActivity(intent);
                if (!hisModels.contains(query)) {
                    SearchHisModel model = new SearchHisModel();
                    model.setHisInfo(query);
                    executor.execute(() -> {
                        searchHisDao.addHisModel(model);
                    });
                    hisModels.add(query);
                } else {
                    hisModels.remove(query);
                    hisModels.add(query);
                }
                setClearAllShow();
                searchAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // 删除单个item
        searchAdapter.setOnItemClearClick((v, position) -> {
            executor.execute(() -> {
                searchHisDao.deleteHisModel(hisModels.get(position));
                hisModels.remove(position);
                // 在主线程中通知adapter去更新Item
                runOnUiThread(() -> {
                    searchAdapter.notifyDataSetChanged();
                });
            });
            setClearAllShow();
        });
        // 删除全部缓存item
        ll_clearAll.setOnClickListener(v -> {
            // 警告对话框，防止误删
            AlertDialog.Builder diaLog = new AlertDialog.Builder(mContext).setMessage("确认清除全部历史记录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        executor.execute(() -> {
                            hisModels.clear();
                            searchHisDao.deleteAll();
                        });
                        searchAdapter.notifyDataSetChanged();
                        ll_clearAll.setVisibility(View.GONE);
                    });
            // 取消不做任何处理，所以不用添加事件
            diaLog.setNegativeButton("取消", null);
            diaLog.show();
        });
        // 拿到历史记录并进行搜索
        getHisDataAndSearch();
    }

    /**
     * 拿到历史记录并进行搜索
     */
    private void getHisDataAndSearch() {
        if (searchAdapter != null) {
            searchAdapter.setOnItemTextClickListener((query, position) -> {
                svSearch.setQuery(query, true);
                rvSearHis.setVisibility(View.VISIBLE);
            });
        }
    }

    /**
     * 拿到搜索热词进行搜索
     */
    private void getHotDataAndSearch() {
        if (hotSearchAdapter != null) {
            hotSearchAdapter.setItemClickListener((query, position) -> {
                svSearch.setQuery(query, true);
                rvSearHis.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 搜索输入框监听器
        svSearch.setOnQueryTextFocusChangeListener((v, position) -> {
            rvSearHis.setVisibility(View.VISIBLE);
            setClearAllShow();
        });
        svSearch.clearFocus();
    }

    /**
     * 根据数据库中是否有值来显示清除按钮
     */
    private void setClearAllShow() {
        isDbDataEmpty();
    }

    /**
     * 判断数据库是否为空，并将数据传递出去
     * 
     * @return
     */
    private void isDbDataEmpty() {
        executor.execute(() -> {
            List<SearchHisModel> allHisInfo = searchHisDao.getAllHisInfo();
            Message message = myHandler.obtainMessage();
            Bundle bundle = new Bundle();
            if (allHisInfo.size() == 0) {
                bundle.putBoolean("dataIsNull", true);
                message.setData(bundle);
                myHandler.sendMessage(message);
            } else {
                bundle.putBoolean("dataIsNull", false);
                message.setData(bundle);
                myHandler.sendMessage(message);
            }
        });
    }

    /**
     * 使用主线程注册的Handler将子线程的执行结果拿到，然后在主线程中做UI的更新
     */
    @SuppressLint("HandlerLeak")
    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            boolean dbDataEmpty = msg.getData().getBoolean("dataIsNull");
            if (dbDataEmpty) {
                ll_clearAll.setVisibility(View.GONE);
            } else {
                ll_clearAll.setVisibility(View.VISIBLE);
            }
        }
    };
}