package com.example.test.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.example.test.R;
import com.example.test.fragments.GoundFragment.NavFragment;
import com.example.test.fragments.GoundFragment.RapexFragment;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 广场
 */
public class SquareFragment extends BaseFragment {
    private TabLayout tabType;
    private ViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_square, container, false);
        tabType = view.findViewById(R.id.tabType);
        viewPager = view.findViewById(R.id.vpGround);
        titles.add("体系");
        titles.add("导航");
        fragments.add(new RapexFragment());
        fragments.add( new NavFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        viewPager.setOffscreenPageLimit(fragments.size());
        tabType.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public String getTitle() {
        return "广场";
    }
}
