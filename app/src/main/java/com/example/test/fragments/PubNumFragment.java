package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.PubNumHisModel;
import com.example.test.activity.model.PublisherModel;
import com.example.test.activity.model.base.DataBean;
import com.example.test.adaper.SearchAndPubAdapter;
import com.example.test.callback.CollectEventListener;
import com.example.test.callback.MyCallBack;
import com.example.test.util.ViewTransUtil;
import com.yang.flowlayoutlibrary.FlowLayout;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PubNumFragment extends BaseFragment implements CollectEventListener {
    private RecyclerView rvPubNum;
    private SearchAndPubAdapter adapter;
    private Call<PubNumHisModel> pubNumHisModelCall;
    private Call<PublisherModel> publisherModelCall;
    private Api service;
    private Executor executor = Executors.newSingleThreadExecutor();
    private boolean canLoadMore = true;
    private int showPage;
    private boolean isInitLoadMorePub;
    private ProgressBar pgbPubNum;
    private RelativeLayout rlPubType;
    private FlowLayout flvPublisher;
    private int mPublisherId;
    private LinearLayout llPublisher;
    private List<Integer> publisherIds = new LinkedList<>();
    private List<String> publisherNames = new LinkedList<>();
    private Map<String, Integer> publisherMap = new HashMap<>();
    private Handler myHandler;

    public PubNumFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pub_num, container, false);
        initToolbar();
        rvPubNum = view.findViewById(R.id.rvPub);
        pgbPubNum = view.findViewById(R.id.pgbPubNum);
        rlPubType = view.findViewById(R.id.rl_cids);
        flvPublisher = view.findViewById(R.id.flvPublisher);
        llPublisher = view.findViewById(R.id.llPublisher);
        rlPubType.setVisibility(View.VISIBLE);
        service = App.getApi(Api.class);
        // 获取所有公众号
        getPublisher();
        myHandler = new Handler(Looper.getMainLooper(), new PublisherIdCallBack());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadMore();
        }
        return view;
    }

    /**
     * 获取所有公众号主
     */
    private void getPublisher() {
        publisherModelCall = service.showPublishers();
        executor.execute(() -> {
            publisherModelCall.clone().enqueue(new Callback<PublisherModel>() {
                @Override
                public void onResponse(Call<PublisherModel> call, Response<PublisherModel> response) {
                    if (response.body() == null && response.code() != 200) {
                        Toast.makeText(getContext(), "请求失败，请检查网络！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<PublisherModel.DataBean> datas = response.body().getData();
                    if (datas.isEmpty()) {
                        Toast.makeText(getContext(), "请求失败，请求数据为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < datas.size(); i++) {
                        publisherIds.add(datas.get(i).getId());
                        publisherNames.add(datas.get(i).getName());
                    }
                    for (int i = 0; i < datas.size(); i++) {
                        publisherMap.put(datas.get(i).getName(), datas.get(i).getId());
                    }
                    Message message = myHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putInt("publisherId", publisherIds.get(0));
                    message.setData(bundle);
                    myHandler.sendMessage(message);
                }

                @Override
                public void onFailure(Call<PublisherModel> call, Throwable t) {
                    call.clone().cancel();
                }
            });
        });
    }

    /**
     * 获取公众号历史消息
     */
    private void getPubNumHisData(int publisherId) {
        this.showPage = 0;
        pubNumHisModelCall = service.getPubNumHisData(publisherId, showPage);
        pgbPubNum.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            pubNumHisModelCall.clone().enqueue(new Callback<PubNumHisModel>() {
                @Override
                public void onResponse(Call<PubNumHisModel> call, Response<PubNumHisModel> response) {
                    if (response.body() == null || response.code() != 200) {
                        Toast.makeText(getContext(), "请求出现异常！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DataBean data = response.body().getData();
                    adapter = new SearchAndPubAdapter(getContext(), response.body(), PubNumFragment.this);
                    if (data.getDatas().size() < data.getSize()) {
                        adapter.setLoadMore(false);
                        adapter.setItemNoMore(true);
                        isInitLoadMorePub = false;
                    } else {
                        adapter.setLoadMore(true);
                        adapter.setItemNoMore(false);
                        isInitLoadMorePub = true;
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                    rvPubNum.setLayoutManager(manager);
                    rvPubNum.setAdapter(adapter);
                    pgbPubNum.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<PubNumHisModel> call, Throwable t) {
                    pubNumHisModelCall.clone().cancel();
                    pgbPubNum.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "请求出现异常！", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * 加载更多
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadMore() {
        rvPubNum.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    // 滑动到底部
                    if (adapter != null) {
                        adapter.setLoadMore(true);
                        adapter.setItemNoMore(false);
                        updateData();
                    }
                }
            }
        });
    }

    /**
     * 更新加载更多
     */
    private void updateData() {
        // 使用项目分类首个字段
        pubNumHisModelCall = service.getPubNumHisData(mPublisherId, (canLoadMore ? ++showPage : showPage));
        executor.execute(() -> pubNumHisModelCall.clone().enqueue(new Callback<PubNumHisModel>() {
            @Override
            public void onResponse(Call<PubNumHisModel> call, Response<PubNumHisModel> response) {
                if (response.code() == 200) {
                    PubNumHisModel model = response.body();
                    List<DataBean.DatasBean> datas = model.getData().getDatas();
                    if (datas.size() == 0 || !isInitLoadMorePub) {
                        adapter.setItemNoMore(true);
                        adapter.setLoadMore(false);
                    } else {
                        adapter.updateSearchDetail(model);
                        adapter.setLoadMore(true);
                        adapter.setItemNoMore(false);
                        canLoadMore = true;
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    canLoadMore = false;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PubNumHisModel> call, Throwable t) {
                canLoadMore = false;
                call.cancel();
                Log.d("[updateHomeArticle]", "发生异常" + t.getMessage());
            }
        }));
    }

    @Override
    public String getTitle() {
        return "公众号文章";
    }

    @Override
    public void onCollectEvent(ImageView view, int id) {
    }

    /**
     * 在主线程中更新
     */
    class PublisherIdCallBack extends MyCallBack {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            PubNumFragment.this.mPublisherId = msg.getData().getInt("publisherId");
            // 获取微信公众号信息
            getPubNumHisData(PubNumFragment.this.mPublisherId);
            rlPubType.setOnClickListener(v -> {
                if (llPublisher.getVisibility() == View.VISIBLE) {
                    llPublisher.setVisibility(View.GONE);
                    ViewTransUtil.setGoneTrans(llPublisher);
                    rvPubNum.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                } else {
                    llPublisher.setVisibility(View.VISIBLE);
                    ViewTransUtil.setVisibleTrans(llPublisher);
                    rvPubNum.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
            });
            flvPublisher.addViews(publisherNames, content -> {
                PubNumFragment.this.mPublisherId = publisherMap.get(content);
                getPubNumHisData(PubNumFragment.this.mPublisherId);
                llPublisher.setVisibility(View.GONE);
                ViewTransUtil.setGoneTrans(llPublisher);
                // 修改标题
                changeTitle(content);
                rvPubNum.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            });

            return false;
        }
    }

}
