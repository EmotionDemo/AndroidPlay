package com.example.test.fragments;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.PjoTypeModel;
import com.example.test.activity.model.ProjectModel;
import com.example.test.adaper.CidsAdapter;
import com.example.test.adaper.ProjectsAdapter;
import com.example.test.callback.CollectEventListener;
import com.example.test.callback.MyCallBack;
import com.example.test.util.ViewTransUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PjoFragment extends BaseFragment implements CollectEventListener {
    private RecyclerView rvPjo, rvCids;
    private ProjectsAdapter adapter;
    private CidsAdapter cidsAdapter;
    private Executor executor = Executors.newFixedThreadPool(5);
    private Call<ProjectModel> callPjo;
    private Call<PjoTypeModel> callPjoType;
    private Api service;
    private int showPage;
    private LinearLayout llCids;
    private boolean canLoadMore = true;
    private TextView tvCidTitle;
    // 项目分类
    private List<Integer> cids;
    // 分类名称
    private List<String> cidStr;
    // 分类id
    private int cid;
    private Handler myHandler;
    private ProgressBar probarPjo;
    private boolean isInitLoadMore;
    private static final String TAG = "PjoFragment";
    private List<String> cidPan = new ArrayList<>();
    public PjoFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pjo, container, false);
        initToolbar();
        service = App.getApi(Api.class);
        initViews();
        return view;
    }

    /**
     * 加载View
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews() {
        rvPjo = view.findViewById(R.id.rvProjects);
        rvCids = view.findViewById(R.id.rvCids);
        tvCidTitle = view.findViewById(R.id.tvCidTitle);
        probarPjo = view.findViewById(R.id.probarPjo);
        llCids = view.findViewById(R.id.llCids);
        probarPjo.setVisibility(View.VISIBLE);
        RelativeLayout rl_cids = view.findViewById(R.id.rl_cids);
        adapter = new ProjectsAdapter(this.getContext());
        adapter.setListener(this);
        cidsAdapter = new CidsAdapter(this.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPjo.setLayoutManager(linearLayoutManager);
        rvPjo.setAdapter(adapter);
        // 构建Handler并处理消息
        myHandler = new Handler(Looper.getMainLooper(), new CidCallBack());
        initCids();
        rl_cids.setVisibility(View.VISIBLE);
        llCids.setVisibility(View.GONE);
        rl_cids.setOnClickListener(v -> {
            if (llCids.getVisibility() == View.GONE) {
                llCids.setVisibility(View.VISIBLE);
                ViewTransUtil.setVisibleTrans(llCids);
            } else {
                llCids.setVisibility(View.GONE);
                ViewTransUtil.setGoneTrans(llCids);
            }
        });
        cidsAdapter.setOnCidClickListener((cidName, position) -> {
            cid = cids.get(position);
            tvCidTitle.setText(cidName);
            getCurrentTypePjo(cid);
            changeTitle(cidName);
            llCids.setVisibility(View.GONE);
            ViewTransUtil.setGoneTrans(llCids);
            if (!cidPan.isEmpty() &&!cidPan.get(0).equals(cidName)){
                //回到顶部
                rvPjo.scrollToPosition(0);
            }
            if (!cidPan.contains(cidName)){
                cidPan.add(cidName);
            }else {
                cidPan.remove(cidName);
                cidPan.add(cidName);
            }
        });
        loadMore();
    }

    /**
     * 加载所有项目分类
     * 
     * @return
     */
    private void initCids() {
        callPjoType = service.showAllPjoTypes();
        cids = new ArrayList<>();
        cidStr = new ArrayList<>();
        executor.execute(() -> {
            callPjoType.clone().enqueue(new Callback<PjoTypeModel>() {
                @Override
                public void onResponse(Call<PjoTypeModel> call, Response<PjoTypeModel> response) {
                    if (response.code() != 200 || response.body() == null) {
                        Toast.makeText(getContext(), "网络请求失败，请检查网络配置!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<PjoTypeModel.DataBean> data = response.body().getData();
                    for (int i = 0; i < data.size(); i++) {
                        cids.add(data.get(i).getId());
                        cidStr.add(data.get(i).getName());
                    }
                    myHandler.sendEmptyMessage(100);
                }

                @Override
                public void onFailure(Call<PjoTypeModel> call, Throwable t) {
                    callPjoType.clone().cancel();
                }
            });
        });
    }

    /**
     * 加载更多
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loadMore() {
        rvPjo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "direction 1: true");
                } else {
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
        callPjo = service.showPjoArticle((canLoadMore ? ++showPage : showPage), cid);
        executor.execute(() -> callPjo.clone().enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                if (response.code() == 200) {
                    ProjectModel model = response.body();
                    List<ProjectModel.DataBean.DatasBean> datas = model.getData().getDatas();
                    if (datas.size() == 0 || !isInitLoadMore ) {
                        adapter.setItemNoMore(true);
                        adapter.setLoadMore(false);
                    } else {
                        adapter.updatePjoData(model.getData());
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
            public void onFailure(Call<ProjectModel> call, Throwable t) {
                canLoadMore = false;
                call.cancel();
                Log.d("[updateHomeArticle]", "发生异常" + t.getMessage());
            }
        }));
    }

    /**
     * 子线程获取所有的项目分类后，把消息通过Handler发送到主线程
     */
    class CidCallBack extends MyCallBack {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 100) {
                cid = cids.get(0);
                if (cid == 0) {
                    return false;
                }
                if (cidStr.size() > 0) {
                    cidsAdapter.setCids(cidStr.toArray(new String[] {}));
                    LinearLayoutManager cidManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                            false);
                    rvCids.setLayoutManager(cidManager);
                    rvCids.setAdapter(cidsAdapter);
                } else {
                    Toast.makeText(getContext(), "未请求到分类！", Toast.LENGTH_SHORT).show();
                }
                // 获取当前类型下的项目
                getCurrentTypePjo(cid);
            }
            return false;
        }
    }

    /**
     * 获取当前type类型的项目
     * 
     * @param cidHeader
     */
    private void getCurrentTypePjo(int cidHeader) {
        this.showPage = 0;
        callPjo = service.showPjoArticle(showPage, cidHeader);
        executor.execute(() -> {
            callPjo.clone().enqueue(new Callback<ProjectModel>() {
                @Override
                public void onResponse(Call<ProjectModel> call, Response<ProjectModel> response) {
                    if (response.body() == null || response.code() != 200) {
                        Toast.makeText(getContext(), "网络请求错误！", Toast.LENGTH_SHORT).show();
                        probarPjo.setVisibility(View.GONE);
                        return;
                    }
                    ProjectModel body = response.body();
                    if (body.getErrorCode() != 0) {
                        Toast.makeText(getContext(), body.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        probarPjo.setVisibility(View.GONE);
                        return;
                    }
                    adapter.setModel(body);
                    if (body.getData().getDatas().size() < body.getData().getSize()) {
                        adapter.setLoadMore(false);
                        adapter.setItemNoMore(true);
                        isInitLoadMore = false;
                    } else {
                        adapter.setLoadMore(true);
                        adapter.setItemNoMore(false);
                        isInitLoadMore = true;
                    }
                    adapter.notifyDataSetChanged();
                    probarPjo.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ProjectModel> call, Throwable t) {
                    callPjo.clone().cancel();
                    probarPjo.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public String getTitle() {
        return "项目";
    }

    /**
     * 红心点击事件回调
     * 
     * @param view
     * @param id
     */
    @Override
    public void onCollectEvent(ImageView view, int id) {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            llCids.setVisibility(View.GONE);
        }
    }
}
