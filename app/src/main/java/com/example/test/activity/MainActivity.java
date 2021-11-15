package com.example.test.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.test.R;
import com.example.test.callback.OnUserInfoBackListener;
import com.example.test.fragments.HomeFragment;
import com.example.test.fragments.MineFragment;
import com.example.test.fragments.PjoFragment;
import com.example.test.fragments.PubNumFragment;
import com.example.test.fragments.SquareFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity {
    private BottomNavigationView navigationView;
    private int backDownCount;
    private Fragment homeFrag;
    private Fragment mineFrag;
    private Fragment pjoFrag;
    private Fragment pubNumFrag;
    private Fragment squareFrag;
    private Fragment currentFragment;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNav();
        initFrag();
    }

    /**
     * 初始化fragment
     */
    private void initFrag() {
        homeFrag = new HomeFragment();
        mineFrag = new MineFragment();
        pjoFrag = new PjoFragment();
        pubNumFrag = new PubNumFragment();
        squareFrag = new SquareFragment();
        currentFragment = homeFrag;
        // 添加默认fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.rl_fragment_main, currentFragment);
        fragmentTransaction.commit();
    }

    /**
     * 初始化底部栏
     */
    private void initBottomNav() {
        navigationView = findViewById(R.id.bottomNav);
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.item_home).setIcon(R.mipmap.ic_home_pressed);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setDefaultIcon();
                switch (item.getItemId()) {
                    case R.id.item_home:
                        item.setIcon(R.mipmap.ic_home_pressed);
                        switchFragment(homeFrag);
                        return true;
                    case R.id.item_ground:
                        item.setIcon(R.mipmap.ic_ground_pressed);
                        switchFragment(squareFrag);
                        return true;
                    case R.id.item_mine:
                        item.setIcon(R.mipmap.ic_mine_pressed);
                        switchFragment(mineFrag);
                        return true;
                    case R.id.item_project:
                        item.setIcon(R.mipmap.ic_pro_pressed);
                        switchFragment(pjoFrag);
                        return true;
                    case R.id.item_pub_num:
                        item.setIcon(R.mipmap.ic_pub_pressed);
                        switchFragment(pubNumFrag);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * 设置默认图标
     */
    private void setDefaultIcon() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.item_ground).setIcon(R.mipmap.ic_ground);
        menu.findItem(R.id.item_home).setIcon(R.mipmap.ic_home);
        menu.findItem(R.id.item_mine).setIcon(R.mipmap.ic_mine);
        menu.findItem(R.id.item_pub_num).setIcon(R.mipmap.ic_pub_unpressed);
        menu.findItem(R.id.item_project).setIcon(R.mipmap.ic_pro_un_pressed);
    }

    @SuppressLint("MissingSuperCall")
    public void onSaveInstanceState(Bundle outState) {
        // 注释掉以防view重叠
        // super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * 按下返回键时不销毁界面而是退回到Home
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backDownCount++;
            if (backDownCount == 1) {
                Toast.makeText(this, "请再点击一次返回按钮回到桌面", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent home = new Intent(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            backDownCount = 0;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 切换Fragment
     *
     */
    private void switchFragment(Fragment targetFragment) {
        transaction = fragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.hide(currentFragment).add(R.id.rl_fragment_main, targetFragment).commit();
        } else {
            transaction.hide(currentFragment).show(targetFragment).commit();
        }
        currentFragment = targetFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册
        EventBus.getDefault().unregister(this);
    }
}
