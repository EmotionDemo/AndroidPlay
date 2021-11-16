package com.example.test.fragments.GoundFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.RapexModel;
import com.example.test.adaper.RapexAdapter;
import com.example.test.callback.MyCallBack;
import com.example.test.fragments.BaseFragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 体系
 */
public class RapexFragment extends BaseFragment {
    private Api service;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private ProgressBar pgbRapex;

    public RapexFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rapex, container, false);
        pgbRapex = view.findViewById(R.id.pgbRapex);
        return view;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = App.getApi(Api.class);
        mHandler = new Handler(Looper.getMainLooper(), new RapexDataCallBack());
        initData();
    }

    /**
     * 初始化Rv
     * 
     * @param
     */
    private void initData() {
        executor.execute(() -> {
            Call<RapexModel> rapexModelCall = service.showRapexs();
            rapexModelCall.clone().enqueue(new Callback<RapexModel>() {
                @Override
                public void onResponse(Call<RapexModel> call, Response<RapexModel> response) {
                    if (response.code() != 200) {
                        Toast.makeText(getContext(), "请求失败，请检查网络配置", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.body() == null) {
                        Toast.makeText(getContext(), "请求失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String rapexBean = JSON.toJSONString(response.body());
                    Message message = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("rapexBean", rapexBean);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    pgbRapex.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<RapexModel> call, Throwable t) {
                    call.clone().cancel();
                    pgbRapex.setVisibility(View.GONE);
                }
            });
        });
    }

    class RapexDataCallBack extends MyCallBack {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            RecyclerView rlRapex = view.findViewById(R.id.rlRapex);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                    Boolean.FALSE);
            rlRapex.setLayoutManager(linearLayoutManager);
            RapexAdapter adapter = new RapexAdapter(getContext());
            String rapexBean = msg.getData().getString("rapexBean");
            RapexModel model = JSON.parseObject(rapexBean, RapexModel.class);
            adapter.setModel(model);
            rlRapex.setAdapter(adapter);
            return false;
        }
    }
}
