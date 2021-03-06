package com.example.test.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.test.App;
import com.example.test.R;
import com.example.test.activity.model.Api;
import com.example.test.activity.model.LoginModel;
import com.example.test.activity.model.RegisterModel;
import com.example.test.activity.model.UserInfoModel;
import com.example.test.callback.Event;
import com.example.test.callback.EventRefresh;
import com.example.test.callback.OnUserCallBackListener;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements OnUserCallBackListener {
    private EditText etPwd, etUserName;
    private boolean isShow;
    private Button btnLogin, btnRegister;
    private String userName, userPwd;
    private ImageView ivBack;
    private Call<LoginModel> callLogin;
    private Call<UserInfoModel> callUserInfo;
    private Call<RegisterModel> callRegister;
    private Api service = App.getApi(Api.class);
    private Executor executor = Executors.newSingleThreadExecutor();
    private OnUserCallBackListener listener;
    private static final String TAG = "LoginActivity";
    private ProgressBar loginProBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setOnUserCallBack(this);
        initPwdEyes();
        initViews();
    }

    /**
     * ??????
     */
    private void doLogin() {
        loginProBar.setVisibility(View.VISIBLE);
        if (checkParamer()) {
            return;
        }
        callLogin = service.login(userName, userPwd);
        executor.execute(() -> {
            callLogin.clone().enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    //???????????????(??????????????????????????????)
                    if (response.code() != 200 || response.body() == null) {
                        Toast.makeText(LoginActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        loginProBar.setVisibility(View.GONE);
                        return;
                    }
                    //????????????
                    if (response.body().getErrorCode() != 0) {
                        Toast.makeText(LoginActivity.this, response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        loginProBar.setVisibility(View.GONE);
                        return;
                    }
                    // ????????????????????????????????????
                    Toast.makeText(LoginActivity.this, response.body().getData().getNickname(), Toast.LENGTH_SHORT)
                            .show();
                    //?????????????????????????????????????????????
                    if (listener != null) {
                        listener.onUserInfoCallBack();
                    }
                    Toast.makeText(LoginActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                    loginProBar.setVisibility(View.GONE);
                    //??????????????????????????????1000
                    EventBus.getDefault().post(new EventRefresh(1000));
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    callLogin.clone().cancel();
                }
            });
        });
    }

    /**
     * ???????????????
     */
    private void initViews() {
        etUserName = findViewById(R.id.etUserName);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        ivBack = findViewById(R.id.ivBack);
        loginProBar = findViewById(R.id.loginProBar);
        ivBack.setOnClickListener((v) -> {
            finish();
        });
        btnLogin.setOnClickListener(v -> {
            doLogin();
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
    }

    /**
     * ???????????????
     */
    private void doRegister() {
        //??????????????????????????????
        loginProBar.setVisibility(View.VISIBLE);
        if (checkParamer()) {
            return;
        }
        callRegister = service.register(userName, userPwd, userPwd);
        loginProBar.setVisibility(View.VISIBLE);
        executor.execute(() -> {
            callRegister.clone().enqueue(new Callback<RegisterModel>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                    if (response.body() == null) {
                        Toast.makeText(LoginActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                        loginProBar.setVisibility(View.GONE);
                        return;
                    }
                    //????????????
                    if (response.body().getErrorCode() == -1) {
                        Toast.makeText(LoginActivity.this, response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        loginProBar.setVisibility(View.GONE);
                        return;
                    }
                    //???????????????????????????????????????
                    Toast.makeText(LoginActivity.this, "????????????~?????????", Toast.LENGTH_SHORT).show();
                    loginProBar.setVisibility(View.GONE);
                    btnRegister.setClickable(false);
                    btnRegister.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                }

                @Override
                public void onFailure(Call<RegisterModel> call, Throwable t) {
                    callRegister.clone().cancel();
                    loginProBar.setVisibility(View.GONE);
                }
            });
        });

    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private boolean checkParamer() {
        userName = etUserName.getText().toString();
        userPwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
            loginProBar.setVisibility(View.GONE);
            Toast.makeText(this, "???????????????????????????~", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * ??????????????????
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initPwdEyes() {
        etPwd = findViewById(R.id.etPwd);
        Drawable etEyeClose = getResources().getDrawable(R.mipmap.ic_pwd_noshow);
        Drawable etEyeOpen = getResources().getDrawable(R.mipmap.ic_pwd_show);
        etEyeOpen.setBounds(0, 0, 70, 70);
        etEyeClose.setBounds(0, 0, 70, 70);
        etPwd.setCompoundDrawables(null, null, etEyeClose, null);
        Drawable[] compoundDrawables = etPwd.getCompoundDrawables();
        int eyeWidth = compoundDrawables[2].getBounds().width();
        etPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float minX = v.getWidth() - eyeWidth - etPwd.getPaddingRight();
                    float maxX = v.getWidth();
                    float minY = 0;
                    float maxY = v.getHeight();
                    float x = event.getX();
                    float y = event.getY();
                    if (x < maxX && x > minX && y < maxY && y > minY) {
                        isShow = !isShow;
                        if (isShow) {
                            etPwd.setCompoundDrawables(null, null, etEyeClose, null);
                            etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            etPwd.setCompoundDrawables(null, null, etEyeOpen, null);
                            etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onUserInfoCallBack() {
        callUserInfo = service.getUserInfo();
        executor.execute(() -> {
            // ??????????????????
            callUserInfo.enqueue(new Callback<UserInfoModel>() {
                @Override
                public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                    UserInfoModel body = response.body();
                    if (body == null || response.code() != 200) {
                        Toast.makeText(LoginActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (body.getErrorCode() != 0) {
                        Toast.makeText(LoginActivity.this, body.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // ???????????????????????????????????????????????????
                    runOnUiThread(() -> {
                        Event event = new Event(body);
                        EventBus.getDefault().postSticky(event);
                    });
                    finish();
                }

                @Override
                public void onFailure(Call<UserInfoModel> call, Throwable t) {
                }
            });
        });
    }

    /**
     * ????????????????????????
     *
     * @param listener
     */
    public void setOnUserCallBack(OnUserCallBackListener listener) {
        this.listener = listener;
    }

}
