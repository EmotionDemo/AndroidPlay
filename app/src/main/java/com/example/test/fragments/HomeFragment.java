package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.AndroidActivity;
import com.example.test.activity.LoginActivity;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.BaseInfoModel;
import com.example.test.activity.model.HomeArticleModel;
import com.example.test.activity.model.HomeBannerBean;
import com.example.test.adaper.HomeArticleAdapter;
import com.example.test.callback.CollectEventListener;
import com.example.test.callback.EventRefresh;
import com.example.test.callback.MyCallBack;
import com.example.test.callback.TimeOutListener;
import com.example.test.common.TitleToDetailData;
import com.example.test.util.CollectUtil;
import com.example.test.util.ImgUtil;
import com.stx.xhb.androidx.XBanner;
import com.stx.xhb.androidx.entity.BaseBannerInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements TimeOutListener, CollectEventListener {
    private Api service;
    private Call<HomeBannerBean> callBanner;
    private Call<HomeArticleModel> callArticle;
    private Call<BaseInfoModel> callCollect;
    private Call<BaseInfoModel> callUnCollect;
    private XBanner banner;
    private HomeBannerBean homeBannerBean;
    private Executor executor = Executors.newSingleThreadExecutor();
    private HomeArticleAdapter articleAdapter;
    private RecyclerView rvArticle;
    private int defaultPage;
    private ProgressBar progressBar;
    private boolean canLoadMore = true;
    private long startTime;
    private long endTime;
    private SwipeRefreshLayout refreshLayout;
    private Handler myHandler;
    private MyCallBack myCallBack;
    private int resImgId;
    private NestedScrollView scrollView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        EventBus.getDefault().register(this);
        getService();
        callBannerInfo();
        reFreshData();
        initLoadMore();
        myCallBack = new MyHandlerCallBack();
        myHandler = new Handler(Looper.getMainLooper(), myCallBack);
        return view;
    }

    @Override
    public String getTitle() {
        return "首页";
    }

    /**
     * 加载更多
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initLoadMore() {
        // 判断加载到屏幕底部
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // scrollY是滑动的距离
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // 滑动到底部
                    if (!rvArticle.canScrollVertically(1)) {
                        if (articleAdapter != null) {
                            updateHomeArticle();
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取service
     */
    private void getService() {
        service = App.getApi(Api.class);
        callBanner = service.getHomeBanner();
        callArticle = service.getHomeArticle(defaultPage);
    }

    /**
     * 初始化views
     */
    private void initViews(View view) {
        banner = view.findViewById(R.id.iv_banner);
        scrollView = view.findViewById(R.id.nsvHome);
        rvArticle = view.findViewById(R.id.rl_article);
        progressBar = view.findViewById(R.id.progress_artc);
        refreshLayout = view.findViewById(R.id.srlRefresh);
        initToolbar();
        refreshFirst();
    }

    /**
     * 刷新
     */
    private void refreshFirst() {
        refreshLayout.post(() -> {
            refreshLayout.setRefreshing(true);
            myHandler.postDelayed(() -> {
                callArticleInfo();
                //刷新banner数据
                callBannerInfo();
            }, 1000);
        });
    }

    /**
     * 刷新数据
     */
    private void reFreshData() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 刷新当前界面数据
                        callArticleInfo();
                        callBannerInfo();
                    }
                }, 1000);
            }
        });
    }

    /***
     * 获取首页文章信息
     */
    private void callArticleInfo() {
        startTime = System.currentTimeMillis();
        endTime = startTime;
        callArticle = service.getHomeArticle(0);
        progressBar.setVisibility(View.VISIBLE);
        executor.execute(() -> callArticle.clone().enqueue(new Callback<HomeArticleModel>() {
            @Override
            public void onResponse(Call<HomeArticleModel> call, Response<HomeArticleModel> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        HomeArticleModel articleModels = response.body();
                        articleAdapter = new HomeArticleAdapter(getContext());
                        myHandler.sendEmptyMessage(1);
                        articleAdapter.setHomeArticleModels(articleModels, rvArticle);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                                false);
                        rvArticle.setLayoutManager(layoutManager);
                        rvArticle.setAdapter(articleAdapter);
                        rvArticle.setNestedScrollingEnabled(false);
                        refreshLayout.setRefreshing(false);
                        refreshLayout.post(() -> {
                            refreshLayout.setRefreshing(false);
                        });
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "更新数据完成！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeArticleModel> call, Throwable t) {
                call.clone().cancel();
                Toast.makeText(getContext(), "更新数据失败！", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }));
    }

    /**
     * 获取banner信息
     */
    private void callBannerInfo() {
        callBanner = service.getHomeBanner();
        executor.execute(() -> callBanner.enqueue(new Callback<HomeBannerBean>() {
            @Override
            public void onResponse(Call<HomeBannerBean> call, Response<HomeBannerBean> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        homeBannerBean = response.body();
                        setBannerData();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeBannerBean> call, Throwable t) {
                call.cancel();
                try {
                    throw new Exception(t.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * 设置banner数据
     */
    private void setBannerData() {
        List<BaseBannerInfo> models = new ArrayList<>();
        for (int i = 0; i < homeBannerBean.getData().size(); i++) {
            int finalI = i;
            models.add(new BaseBannerInfo() {
                @Override
                public Object getXBannerUrl() {
                    return homeBannerBean.getData().get(finalI).getImagePath();
                }

                @Override
                public String getXBannerTitle() {
                    return homeBannerBean.getData().get(finalI).getTitle();
                }
            });
        }
        banner.setBannerData(models);
        banner.loadImage((banner, model, view, position) -> Glide.with(Objects.requireNonNull(getActivity()))
                .load((homeBannerBean.getData().get(position).getImagePath())).centerCrop().into((ImageView) view));
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, Object model, View view, int position) {
                String link = homeBannerBean.getData().get(position).getUrl();
                String title = homeBannerBean.getData().get(position).getTitle();
                Intent intent = new Intent(getContext(), AndroidActivity.class);
                TitleToDetailData rapexToDetailData = new TitleToDetailData(title, link);
                String toJSONString = JSON.toJSONString(rapexToDetailData);
                intent.putExtra("articleInfo", toJSONString);
                Objects.requireNonNull(getContext()).startActivity(intent);
            }
        });
    }

    /**
     * 首页文章加载更多
     */
    private void updateHomeArticle() {
        callArticle = service.getHomeArticle(canLoadMore ? ++defaultPage : defaultPage);
        executor.execute(() -> callArticle.clone().enqueue(new Callback<HomeArticleModel>() {
            @Override
            public void onResponse(Call<HomeArticleModel> call, Response<HomeArticleModel> response) {
                if (response.code() == 200) {
                    List<HomeArticleModel.DataBean.DatasBean> datas = response.body().getData().getDatas();
                    articleAdapter.updateArticlesData(datas);
                    canLoadMore = true;
                }
            }

            @Override
            public void onFailure(Call<HomeArticleModel> call, Throwable t) {
                canLoadMore = false;
                call.cancel();
                Log.d("[updateHomeArticle]", "发生异常" + t.getMessage());
            }
        }));
    }

    @Override
    public void onTimeout(boolean isTimeout) {
        executor.execute(() -> {
            // while ((endTime - startTime) <)
        });
    }

    /**
     * 点赞事件处理
     *
     * @param ivZan
     * @param id
     */
    @SuppressLint("ResourceType")
    @Override
    public void onCollectEvent(ImageView ivZan, int id) {
        callCollect = service.collectArticle(id);
        callUnCollect = service.unCollectArticle(id);
        progressBar.setVisibility(View.VISIBLE);
        CollectUtil.Companion.setResImgId(ImgUtil.getImgResId(ivZan));
        // 在子线程中发起网络请求
        if (CollectUtil.Companion.getResImgId() == R.mipmap.ic_unzan) {
            executor.execute(() -> {
                callCollect.clone().enqueue(new Callback<BaseInfoModel>() {
                    @Override
                    public void onResponse(Call<BaseInfoModel> call, Response<BaseInfoModel> response) {
                        // 点赞
                        CollectUtil.Companion.setCollect(response, ivZan, R.mipmap.ic_zaned, getContext());
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<BaseInfoModel> call, Throwable t) {
                        callCollect.clone().cancel();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            });
        } else {
            // state=0为红心，需要发送取消点赞动作
            executor.execute(() -> {
                callUnCollect.clone().enqueue(new Callback<BaseInfoModel>() {
                    @Override
                    public void onResponse(Call<BaseInfoModel> call, Response<BaseInfoModel> response) {
                        // 取消点赞
                        CollectUtil.Companion.setCollect(response, ivZan, R.mipmap.ic_unzan, getContext());
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<BaseInfoModel> call, Throwable t) {
                        callUnCollect.clone().cancel();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            });
        }
    }


    /**
     * 由于adapter是在主线程中进行初始化，所以在主线程中设置注册
     */
    class MyHandlerCallBack extends MyCallBack {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                articleAdapter.setCollectEventListener(HomeFragment.this);
            } else if (msg.what == 2) {
            }
            return false;
        }
    }

    /**
     * EventBus，刷新事件处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFreshEvent(EventRefresh event) {
        // 刷新数据
        if (event.getEvent() == 1000) {
            refreshFirst();
            callBannerInfo();
        }
    }

    /**
     * 销毁事件中解除EventBus注册
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
