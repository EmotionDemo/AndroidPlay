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
     * ??????????????????
     */
    private void callHotInfo() {
        executor.execute(() -> {
            hotSearchModelCall.enqueue(new Callback<HotSearchModel>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<HotSearchModel> call, Response<HotSearchModel> response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            hotSearchModel = response.body();
                            // ?????????????????????UI
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
     * ?????????????????????????????????
     */
    private void getService() {
        Api service = App.getApi(Api.class);
        hotSearchModelCall = service.getHotSearchModel();
    }

    /**
     * ??????????????????
     */
    private void initDataBase() {
        SearchHisModelBase searchHisModelBase = Room
                .databaseBuilder(getApplicationContext(), SearchHisModelBase.class, "search_his").build();
        searchHisDao = searchHisModelBase.getSearchDao();
    }

    /**
     * ?????????Views
     */
    private void initViews() {
        RelativeLayout rlBack = findViewById(R.id.rl_back);
        rlBack.setOnClickListener((v) -> {
            finish();
        });
        //????????????????????????
        ll_clearAll = findViewById(R.id.ll_clearAll);
        //???????????????
        svSearch = findViewById(R.id.sv_search);
        //???????????????????????????
        svSearch.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        svSearch.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        //??????rv
        rv_hot = findViewById(R.id.rv_hot);
        //?????????????????????????????????????????????
        rvSearHis = findViewById(R.id.rv_sear_his);
        rvSearHis.setMaxHeight(widthAndHeight[1] / 2);
        rvSearHis.setVisibility(View.VISIBLE);
        if (rvSearHis.getVisibility() == View.VISIBLE){
            ll_clearAll.setVisibility(View.VISIBLE);
        }else {
            ll_clearAll.setVisibility(View.GONE);
        }
        hisModels = new ArrayList<>();
        //??????????????????????????????????????????????????????
        executor.execute(() -> {
            List<SearchHisModel> allHisInfo = searchHisDao.getAllHisInfo();
            if (allHisInfo != null && allHisInfo.size() != 0) {
                for (SearchHisModel searchHisModel : allHisInfo) {
                    hisModels.add(searchHisModel.getHisInfo());
                }
            }
        });
        //???????????????
        searchAdapter = new SearchAdapter(this, hisModels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        rvSearHis.setLayoutManager(linearLayoutManager);
        rvSearHis.setAdapter(searchAdapter);
        // ???????????????
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
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
        // ????????????item
        searchAdapter.setOnItemClearClick((v, position) -> {
            executor.execute(() -> {
                searchHisDao.deleteHisModel(hisModels.get(position));
                hisModels.remove(position);
                // ?????????????????????adapter?????????Item
                runOnUiThread(() -> {
                    searchAdapter.notifyDataSetChanged();
                });
            });
            setClearAllShow();
        });
        // ??????????????????item
        ll_clearAll.setOnClickListener(v -> {
            // ??????????????????????????????
            AlertDialog.Builder diaLog = new AlertDialog.Builder(mContext).setMessage("????????????????????????????????????")
                    .setPositiveButton("??????", (dialog, which) -> {
                        executor.execute(() -> {
                            hisModels.clear();
                            searchHisDao.deleteAll();
                        });
                        searchAdapter.notifyDataSetChanged();
                        ll_clearAll.setVisibility(View.GONE);
                    });
            // ???????????????????????????????????????????????????
            diaLog.setNegativeButton("??????", null);
            diaLog.show();
        });
        // ?????????????????????????????????
        getHisDataAndSearch();
    }

    /**
     * ?????????????????????????????????
     */
    private void getHisDataAndSearch() {
        if (searchAdapter != null) {
            searchAdapter.setOnItemTextClickListener((query, position) -> {
                svSearch.setQuery(query, true);
                //???????????????????????????????????????????????????????????????
                rvSearHis.setVisibility(View.VISIBLE);
            });
        }
    }

    /**
     * ??????????????????????????????
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
        // ????????????????????????
        svSearch.setOnQueryTextFocusChangeListener((v, position) -> {
            rvSearHis.setVisibility(View.VISIBLE);
            setClearAllShow();
        });
        //????????????
        svSearch.clearFocus();
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void setClearAllShow() {
        isDbDataEmpty();
    }

    /**
     * ??????????????????????????????????????????????????????
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
     * ????????????????????????Handler????????????????????????????????????????????????????????????UI?????????
     */
    @SuppressLint("HandlerLeak")
    private final Handler myHandler = new Handler(Looper.getMainLooper()) {
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