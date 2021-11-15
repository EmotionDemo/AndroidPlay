package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.LoginActivity;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.BaseInfoModel;
import com.example.test.activity.model.UserInfoModel;
import com.example.test.callback.Event;
import com.example.test.util.SpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.test.util.SpUtil.getUser;

public class MineFragment extends Fragment {
    private TextView tvUserNickName, tvLevel, tvRank, tvCoin;
    private Call<UserInfoModel> infoModelCall;
    private Call<BaseInfoModel> logOutCall;
    private Api service;
    private Executor executor = Executors.newSingleThreadExecutor();
    private LinearLayout llOut;
    private boolean isNotLogin;

    public MineFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = App.getApi(Api.class);
        infoModelCall = service.getUserInfo();
        logOutCall = service.logOut();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 注册EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        tvUserNickName = view.findViewById(R.id.tvUserNickName);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvRank = view.findViewById(R.id.tvRank);
        tvCoin = view.findViewById(R.id.tvCoin);
        llOut = view.findViewById(R.id.llOut);
        //退出登录
        logOut();
        setUserLocalData();
        tvUserNickName.setOnClickListener(v -> {
            executor.execute(() -> {
                infoModelCall.clone().enqueue(new Callback<UserInfoModel>() {
                    @Override
                    public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getContext(), "请求失败!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (response.body().getErrorCode() == -1001) {
                            Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getContext(), LoginActivity.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfoModel> call, Throwable t) {
                        infoModelCall.clone().cancel();
                    }
                });
            });
        });
        return view;
    }


    /**
     * 退出登录
     */
    private void logOut() {
        llOut.setOnClickListener(v -> {
            //警告对话框
            AlertDialog.Builder diaLog = new AlertDialog.Builder(getContext()).setMessage("确认退出登录？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        executor.execute(() -> {
                            //判断是否处于登录状态
                            infoModelCall.clone().enqueue(new Callback<UserInfoModel>() {
                                @Override
                                public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                                    if (response.code() != 200) {
                                        Toast.makeText(getContext(), "请求失败!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (response.body().getErrorCode() == -1001) {
                                        Toast.makeText(getContext(), "未处于登录状态!", Toast.LENGTH_SHORT).show();
                                        isNotLogin = true;
                                        return;
                                    }
                                    //能获取到个人信息，说明此时处于登录状态
                                    if (response.body().getErrorCode() == 0) {
                                        isNotLogin = false;
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserInfoModel> call, Throwable t) {
                                    infoModelCall.clone().cancel();
                                }
                            });

                            logOutCall.clone().enqueue(new Callback<BaseInfoModel>() {
                                @Override
                                public void onResponse(Call<BaseInfoModel> call, Response<BaseInfoModel> response) {
                                    //判断是否处于登录状态
                                    if (isNotLogin) {
                                        return;
                                    }
                                    if (response.body() == null) {
                                        Toast.makeText(getContext(), "请求失败!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (response.body().getErrorCode() != 0) {
                                        Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Toast.makeText(getContext(), "已退出登录~", Toast.LENGTH_SHORT).show();
                                    tvUserNickName.setText("点击登录");
                                    tvLevel.setText("- -");
                                    tvRank.setText("积分排名：- -");
                                    tvCoin.setText("积分总数：- -");
                                    isNotLogin = true;
                                    //清除Cookie
                                    SpUtil.clearCookie();
                                }

                                @Override
                                public void onFailure(Call<BaseInfoModel> call, Throwable t) {
                                    logOutCall.clone().cancel();
                                }
                            });
                        });
                    });
            // 取消不做任何处理，所以不用添加事件
            diaLog.setNegativeButton("取消", null);
            diaLog.show();
        });
    }

    /**
     * 持久化，设置用户信息
     */
    @SuppressLint("SetTextI18n")
    private void setUserLocalData() {
        String userInfo = getUser();
        UserInfoModel model = JSON.parseObject(userInfo, UserInfoModel.class);
        putUserInfo(model);
    }

    /**
     * EventBus事件通知
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(Event event) {
        UserInfoModel model = event.getModel();
        putUserInfo(model);
        String modelStr = JSON.toJSONString(model);
        SpUtil.putUserInfo(modelStr);
    }

    /**
     * 设置用户信息
     *
     * @param model
     */
    @SuppressLint("SetTextI18n")
    private void putUserInfo(UserInfoModel model) {
        if (model == null) {
            return;
        }
        UserInfoModel.DataBean.CoinInfoBean coinInfo = model.getData().getCoinInfo();
        UserInfoModel.DataBean.UserInfoBean userInfo = model.getData().getUserInfo();
        // 积分总数
        int coinCount = coinInfo.getCoinCount();
        // 账号等级
        int level = coinInfo.getLevel();
        // 全站排名
        String rank = coinInfo.getRank();
        // 账号id
        int id = userInfo.getId();
        // 昵称
        String nickname = userInfo.getNickname();
        // email
        String email = userInfo.getEmail();
        // userName
        String userName = userInfo.getUsername();
        tvUserNickName.setText(nickname);
        tvLevel.setText(String.valueOf(level));
        tvRank.setText("积分排名：" + rank);
        tvCoin.setText("积分总数：" + coinCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
