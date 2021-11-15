package com.example.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.BaseInfoModel;
import com.example.test.activity.model.SearchDetailModel;
import com.example.test.activity.model.base.DataBean;
import com.example.test.adaper.SearchAndPubAdapter;
import com.example.test.callback.CollectEventListener;
import com.example.test.view.MyToast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDetailActivity extends BaseActivity implements CollectEventListener<ImageView> {
    private Call<SearchDetailModel> searchDetailCall;
    private Call<BaseInfoModel> collectArtCall;
    private SearchAndPubAdapter searchDetailAdapter;
    private Api service = App.getApi(Api.class);
    private int defaultPage;
    private RecyclerView rvSearchDetail;
    private Context mContext;
    private SearchView sv_search_detail;
    private String query;
    private ImageView iv_noMsg;
    private boolean canLoadMore = true;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ProgressBar mProgressBar;
    private static final String TAG = "SearchDetailActivity";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_detail);
        mContext = this;
        initViews();
        initSearchData();
        if (searchDetailAdapter != null) {
            searchDetailAdapter.setCollectEventListener(this);
        }
        loadMore(this.query);
    }

    /**
     * 加载更多
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadMore(String content) {
        rvSearchDetail.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!rvSearchDetail.canScrollVertically(1)) {
                    if (searchDetailAdapter != null) {
                        searchDetailAdapter.setLoadMore(true);
                        searchDetailAdapter.setItemNoMore(false);
                        SearchDetailActivity.this.updateDetailMore(content);
                    }
                }
            }
        });
    }

    /**
     * 更新加载更多
     *
     * @param content
     */
    private void updateDetailMore(String content) {
        searchDetailCall = service.getSearchDetail(canLoadMore ? ++defaultPage : defaultPage, content);
        executor.execute(() -> searchDetailCall.clone().enqueue(new Callback<SearchDetailModel>() {
            @Override
            public void onResponse(Call<SearchDetailModel> call, Response<SearchDetailModel> response) {
                int responseCode = response.code();
                if (responseCode == 200) {
                    List<DataBean.DatasBean> datas = response.body().getData().getDatas();
                    SearchDetailModel body = response.body();
                    if (datas.size() == 0) {
                        searchDetailAdapter.setItemNoMore(true);
                        searchDetailAdapter.setLoadMore(false);
                    } else {
                        searchDetailAdapter.updateSearchDetail(body);
                        searchDetailAdapter.setLoadMore(true);
                        searchDetailAdapter.setItemNoMore(false);
                        canLoadMore = true;
                    }
                } else if (responseCode == 500 || responseCode == 404) {
                    canLoadMore = false;
                    searchDetailAdapter.setItemNoMore(true);
                    searchDetailAdapter.setLoadMore(false);
                }
                searchDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchDetailModel> call, Throwable t) {
                canLoadMore = false;
                call.cancel();
                Log.d("[updateHomeArticle]", "发生异常" + t.getMessage());
            }
        }));
    }

    /**
     * 初始化搜索框内数据并完成搜索
     */
    private void initSearchData() {
        Intent intent = this.getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.query = extras.getString("queryInfo");
        }
        getSearchDetailInfo();
    }

    /**
     * 获取搜索页列表
     */
    private void getSearchDetailInfo() {
        getData();
    }

    private void getData() {
        searchDetailCall = service.getSearchDetail(defaultPage, query);
        searchDetailCall.clone().enqueue(new Callback<SearchDetailModel>() {
            @Override
            public void onResponse(Call<SearchDetailModel> call, Response<SearchDetailModel> response) {
                if (response.code() == 200) {
                    DataBean data = response.body().getData();
                    sv_search_detail.setQuery(query, false);
                    int size = response.body().getData().getDatas().size();
                    if (size <= 0) {
                        iv_noMsg.setVisibility(View.VISIBLE);
                        return;
                    }
                    /*
                     * if (size < 16) { updateDetailMore(query); }
                     */
                    searchDetailAdapter = new SearchAndPubAdapter(mContext, response.body(), SearchDetailActivity.this);
                    if (data.getDatas().size() < data.getSize()-1) {
                        searchDetailAdapter.setLoadMore(false);
                        searchDetailAdapter.setItemNoMore(true);
                    } else {
                        searchDetailAdapter.setLoadMore(true);
                        searchDetailAdapter.setItemNoMore(false);
                    }
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    layoutManager.setOrientation(RecyclerView.VERTICAL);
                    rvSearchDetail.setLayoutManager(layoutManager);
                    rvSearchDetail.setAdapter(searchDetailAdapter);
                    searchDetailAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SearchDetailModel> call, Throwable t) {
                searchDetailCall.clone().cancel();
            }
        });
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        sv_search_detail = findViewById(R.id.sv_search_detail);
        sv_search_detail.findViewById(androidx.appcompat.R.id.search_plate).setBackground(null);
        sv_search_detail.findViewById(androidx.appcompat.R.id.submit_area).setBackground(null);
        rvSearchDetail = findViewById(R.id.rv_search_detail);
        mProgressBar = findViewById(R.id.prgBar);
        iv_noMsg = findViewById(R.id.iv_noMsg);
        // 获取搜索框的关闭按钮
        ImageView clearButton = sv_search_detail.findViewById(androidx.appcompat.R.id.search_close_btn);
        SearchView.SearchAutoComplete searchEditText = sv_search_detail
                .findViewById(androidx.appcompat.R.id.search_src_text);
        clearButton.setEnabled(true);
        // 禁用文本编辑框，文本会变为灰色
        searchEditText.setEnabled(false);
        // 将搜索字体修改为黑色
        searchEditText.setTextColor(getResources().getColor(R.color.black));
        sv_search_detail.setSubmitButtonEnabled(false);
        clearButton.setOnClickListener((v) -> finish());
        Class clazz = MyToast.class;
        try {
            Method method = clazz.getDeclaredMethod("setDuration", Integer.class);
            method.invoke(200);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCollectEvent(ImageView view, int id) {
        collectArtCall = service.collectArticle(id);
        mProgressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            collectArtCall.clone().enqueue(new Callback<BaseInfoModel>() {
                @Override
                public void onResponse(Call<BaseInfoModel> call, Response<BaseInfoModel> response) {
                    // 未请求成功
                    if (response.code() != 200 || response.body() == null) {
                        return;
                    }
                    // 返回值为-1001说明未进行登录
                    if (response.body().getErrorCode() == -1001) {
                        mProgressBar.setVisibility(View.GONE);
                        MyToast.makeText(mContext, response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                    int collectErrorcode = response.body().getErrorCode();
                    runOnUiThread(() -> {
                        if (collectErrorcode == 0) {
                            view.setImageResource(R.mipmap.ic_zaned);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onFailure(Call<BaseInfoModel> call, Throwable t) {
                    collectArtCall.clone().cancel();
                }
            });
        });
    }
}
