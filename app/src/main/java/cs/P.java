package cs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import s.H;
import hifi.music.tube.downloader.BuildConfig;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.network.ApiConstants;
import jf.CE;
import es.BV;
import hifi.music.tube.downloader.admax.MaxBackInterstitial;
import hifi.music.tube.downloader.ztools.ToastUtils;

import dc.BD;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class P extends BM {
    private static final String Q = "q";
    //@BindView(R.id.toolbar)
    Toolbar mToolbar;
    //@BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    //@BindView(R.id.viewpager)
    ViewPager mViewPager;


    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private static final List<String> mAllTitles = new ArrayList<String>() {{
        add("Server1");
        add("Server2");
        add("Server3");
        add("Server4");
        add("Server5");
        add("Server6");
        add("Server7");
        add("Server8");
        add("Server9");
        add("Server10");
        add("Server11");
        add("Server12");
        add("Server13");
    }};
    private FragmentPagerAdapter mAdpter;

    private String mQuery;


    public static void launch(Context context, String query) {
        Intent launcher = new Intent(context, P.class);
        launcher.putExtra(Q, query);

        if (context instanceof Activity) {
        } else {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(launcher);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initBundleExtra(Bundle savedInstanceState) {
        super.initBundleExtra(savedInstanceState);
        mQuery = getIntent().getStringExtra(Q);
    }

    @Override
    protected void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
    }

    @Override
    protected void initDatas() {
        mToolbar.setTitle("Search: " + mQuery);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViewPages();
        initTab();
//        if (BuildConfig.DEBUG) {
//            H.appLovinSdk.showMediationDebugger();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTab() {
        if (null != mFragments && mFragments.size() <= 4) {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        mTabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.dark_gray), ContextCompat.getColor(this, R.color.white));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        ViewCompat.setElevation(mTabLayout, 0);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    private void addSuper() {

        if (H.config.yt) {
            mFragments.add(BG.newInstance(BG.TYPE_YT, mQuery));
        }


//        if (H.config.wyy) {
//            mFragments.add(BG.newInstance(BG.TYPE_WYY, mQuery));
//        }
        if (H.config.nhac) {
            mFragments.add(BG.newInstance(BG.TYPE_NHAC, mQuery));
        }

        mFragments.add(BG.newInstance(BG.TYPE_JAMENDO, mQuery));
    }

    private void addNormal() {
        if (H.config.jm) {
            mFragments.add(BG.newInstance(BG.TYPE_JAMENDO, mQuery));
        } else if (H.config.xm) {
            mFragments.add(BG.newInstance(BG.TYPE_XM, mQuery));
        } else if (H.config.sc) {
            mFragments.add(BG.newInstance(BG.TYPE_SOUND, mQuery));
        } else if (H.config.yt) {
            mFragments.add(BG.newInstance(BG.TYPE_YT, mQuery));
        }
    }

    private void initViewPages() {
        if (BV.isBanUser()) {
            addNormal();
        } else if (BV.isSuper()) {
            addSuper();
        } else if (ApiConstants.onlist) {
            addNormal();
        } else if (BV.isCnUser()) {
            addSuper();
        } else {
            addNormal();
        }

        if (H.openAbsoluteShow == 10) {
            mFragments.clear();
            mFragments.add(BG.newInstance(BG.TYPE_YT, mQuery));
            mFragments.add(BG.newInstance(BG.TYPE_JAMENDO, mQuery));
//            mFragments.add(BG.newInstance(BG.TYPE_WYY, mQuery));
            mFragments.add(BG.newInstance(BG.TYPE_NHAC, mQuery));
        }

        if (BuildConfig.DEBUG) {
            mFragments.clear();
            mFragments.add(BG.newInstance(BG.TYPE_YT, mQuery));
            mFragments.add(BG.newInstance(BG.TYPE_JAMENDO, mQuery));
//            mFragments.add(BG.newInstance(BG.TYPE_WYY, mQuery));
            mFragments.add(BG.newInstance(BG.TYPE_NHAC, mQuery));
        }


        //Jamendo保底
        if (mFragments.size() < 1) {
            mFragments.add(BG.newInstance(BG.TYPE_JAMENDO, mQuery));
        }

        mTitles = mAllTitles.subList(0, mFragments.size());
        // 初始化Adapter这里使用FragmentPagerAdapter
        mAdpter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        };
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mAdpter);
        if (mFragments.size() < 2) {
            mTabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MaxBackInterstitial.getInstance().isReady()) {
            MaxBackInterstitial.getInstance().showDialog(K.MA);
            MaxBackInterstitial.getInstance().show();
        } else if (H.normalUser) {
            BD.getInstance().showRecommend(H.sContext);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10001:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().postSticky(new CE(CE.SCAN_START));
                } else {
                    ToastUtils.showLongToast("For download music, please give us storage permission");
                }
                break;
        }
    }
}