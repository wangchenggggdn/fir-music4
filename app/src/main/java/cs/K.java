package cs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;

import hifi.music.tube.downloader.adapter.AMusicAdapter;
import ft.U;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dc.BC;
import dc.BD;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import s.H;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.network.ApiConstants;
import jf.CI;
import jf.CB;
import jf.CD;
import jf.BY;
import jf.CE;
import es.BU;
import es.BV;
import hifi.music.tube.downloader.admax.MaxBackInterstitial;
import hifi.music.tube.downloader.admax.MaxDownloadInterstitial;
import hifi.music.tube.downloader.admax.MaxOpenInterstitial;
import hifi.music.tube.downloader.admax.MaxRewardedAds;
import cj.M;
import cj.BT;
import cj.BR;
import cj.BQ;
import hifi.music.tube.downloader.ztools.ImageHelper;
import hifi.music.tube.downloader.ztools.LogUtil;
import hifi.music.tube.downloader.ztools.PermissionUtils;
import hifi.music.tube.downloader.ztools.vPrefsUtils;
import hifi.music.tube.downloader.ztools.ShareUtils;
import hifi.music.tube.downloader.ztools.Timeutils;
import hifi.music.tube.downloader.ztools.ToastUtils;
import hifi.music.tube.downloader.ztools.Utils;


public class K extends BM implements BT.Callback, FolderChooserDialog.FolderCallback {

    private static final String TAG = "AlMainActivity";


    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {
    }

    //@BindView(R.id.viewpager)
    ViewPager mainViewpager;
    //@BindView(R.id.navigation)
    BottomNavigationView navigation;
    //@BindView(R.id.image_iv)
    ImageView imageIv;
    //@BindView(R.id.title_tv)
    TextView titleTv;
    //@BindView(R.id.progress_tv)
    TextView textViewProgress;
    //@BindView(R.id.seek_bar)
    AppCompatSeekBar seekBarProgress;
    //@BindView(R.id.duration_tv)
    TextView textViewDuration;
    //@BindView(R.id.play_mode_toggle)
    ImageView playModeToggle;
    //@BindView(R.id.play_or_pause_iv)
    ImageView playOrPauseIv;
    //@BindView(R.id.loading_v)
    MaterialProgressBar loadingV;
    //@BindView(R.id.last_iv)
    ImageView lastIv;
    //@BindView(R.id.next_iv)
    ImageView nextIv;
    //@BindView(R.id.download_iv)
    ImageView downloadIv, downloadListIv;
    //@BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdpter;

    public static boolean sIsInActivity;
    public static Activity MA;
    private int mOpenCount;//启动次数

    private boolean isSearched = false;
    private AsyncTask mSearchTask;
    private MenuItem menuItem;

    private BT mPlayer;
    private int mIndex;
    private BR mPlayList;
    private Handler mHandler = new Handler();
    ProgressDialog scanDialog;


    public static void launch(Context context) {
        Intent intent = new Intent(context, K.class);
        if (context instanceof Activity) {
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        MA = this;

        mainViewpager = findViewById(R.id.viewpager);
        navigation = findViewById(R.id.navigation);
        imageIv = findViewById(R.id.image_iv);
        titleTv = findViewById(R.id.title_tv);
        textViewProgress = findViewById(R.id.progress_tv);
        seekBarProgress = findViewById(R.id.seek_bar);
        textViewDuration = findViewById(R.id.duration_tv);
        playModeToggle = findViewById(R.id.play_mode_toggle);
        playOrPauseIv = findViewById(R.id.play_or_pause_iv);
        loadingV = findViewById(R.id.loading_v);
        lastIv = findViewById(R.id.last_iv);
        nextIv = findViewById(R.id.next_iv);
        downloadListIv = findViewById(R.id.download_list);
        downloadIv = findViewById(R.id.download_iv);
        mSearchView = findViewById(R.id.floating_search_view);

//        if (TextUtils.isEmpty(App.config.promId)) {
        mSearchView.inflateOverflowMenu(R.menu.zl_search_menu);
//        } else {
//            mSearchView.inflateOverflowMenu(R.menu.search_menu2);
//        }

        MaxOpenInterstitial.getInstance().showInterstitial(this);

//        if (BuildConfig.DEBUG) {
//            MyApp.appLovinSdk.showMediationDebugger();
//        }
    }

    @Override
    protected void initListeners() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayer != null) {
                    CI currentSong = mPlayer.getPlayingSong();
                    if (null != currentSong) {
                        updateProgressTextWithProgress(progress);
                    } else {
                        seekBarProgress.setProgress(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (null != mPlayer) {
                    CI currentSong = mPlayer.getPlayingSong();
                    if (null != currentSong && null != mPlayer) {
                        seekTo(getDuration(seekBar.getProgress()));
                        if (mPlayer.isPlaying()) {
                            mHandler.removeCallbacks(mProgressCallback);
                            mHandler.post(mProgressCallback);
                        }
                    } else {
                        seekBarProgress.setProgress(0);
                    }
                }
            }
        });
        playModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;
                BQ current = vPrefsUtils.lastPlayMode();
                BQ newMode = BQ.switchNextMode(current);
                vPrefsUtils.setPlayMode(newMode);
                mPlayer.setPlayMode(newMode);
                updatePlayModeView(newMode);
            }
        });
        playOrPauseIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) return;

                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.play();
                }
            }
        });
        lastIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null)
                    return;
                if (!mPlayer.playLast()) {
                    ToastUtils.showShortToast("No Previous Song");
                }
            }
        });
        nextIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null)
                    return;
                if (!mPlayer.playNext()) {
                    ToastUtils.showShortToast("No Next Song");
                }
            }
        });
        downloadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer == null) {
                    return;
                }
                CI bean = mPlayer.getPlayingSong();
                if (bean == null) {
                    return;
                }
                AMusicAdapter.tryDownload(K.this, bean);
            }
        });

        downloadListIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainViewpager.getCurrentItem() == 0) {
                    mainViewpager.setCurrentItem(1);
                    downloadListIv.setImageResource(R.drawable.icon_download_list);
                } else {
                    mainViewpager.setCurrentItem(0);
                    downloadListIv.setImageResource(R.drawable.icon_hot);
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                onSearchAction(searchSuggestion.getBody());
                mSearchView.clearSearchFocus();
                mSearchView.setSearchText(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                LogUtil.e(TAG, "onSearchAction>>");
                isSearched = true;
                mSearchView.clearSuggestions();
                if (mSearchTask != null) {
                    mSearchTask.cancel(true);
                }
                mSearchView.hideProgress();
                if (TextUtils.isEmpty(currentQuery)) {
                    return;
                }
                P.launch(K.this, currentQuery);
            }
        });
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageResource(R.drawable.ic_search_6060_24dp);
                textView.setText(item.getBody());
            }
        });
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                LogUtil.e(TAG, ">> isSearched " + isSearched);
                if (isSearched) {
                    isSearched = false;
                    return;
                }
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    searchSuggestions(newQuery);
                }
            }
        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_share) {
                    try {
                        Intent textIntent = new Intent(Intent.ACTION_SEND);
                        textIntent.setType("text/plain");
                        textIntent.putExtra(Intent.EXTRA_TEXT,
                                String.format(getString(R.string.share_content), getPackageName()));
                        startActivity(Intent.createChooser(textIntent, getString(R.string.share_text)));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }else if( R.id.action_recommend == item.getItemId()) {
                    final BC recom = BD.getInstance().getMainRecommend();
                    ShareUtils.gotoRecommend(K.this, recom != null ? recom.getPackageIdWithRecom() : "");
                }else if( R.id.action_more_apps  == item.getItemId()){
                    ShareUtils.gotoMoreApps(K.this, H.config.moreApps);
                }
            }
        });
        //默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
        U.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                menuItem = item;
                mainViewpager.setCurrentItem(item.getOrder());
                return true;
            }
        });
        mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updatePlayModeView(BQ playMode) {
        if (playMode == null) {
            playMode = BQ.getDefault();
        }
        switch (playMode) {
            case LOOP:
                playModeToggle.setImageResource(R.drawable.al_play_loop);
                break;
            case SHUFFLE:
                playModeToggle.setImageResource(R.drawable.play_shuffle);
                break;
            case SINGLE:
                playModeToggle.setImageResource(R.drawable.play_single);
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void searchSuggestions(String newText) {
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }

        mSearchTask = new AsyncTask<String, Void, List<CB>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mSearchView != null) {
                    mSearchView.showProgress();
                }
            }

            @Override
            protected List<CB> doInBackground(String... strings) {
                try {
                    LogUtil.v(TAG, "doInBackground suggistion");
                    String query = strings[0];
                    URL url = new URL("http://suggestqueries.google.com/complete/search?client=firefox&hl=fr&q=" + query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        baos.close();
                        is.close();
                        byte[] byteArray = baos.toByteArray();
                        String content = new String(byteArray);
                        LogUtil.v(TAG, "searchSuggestions content::" + content);
                        if (!TextUtils.isEmpty(content)) {
                            JSONArray jsonArray = new JSONArray(content);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONArray jsonArray1 = jsonArray.optJSONArray(i);
                                if (jsonArray1 != null) {
                                    ArrayList<CB> list = new ArrayList<>();
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String str = jsonArray1.getString(j);
                                        list.add(new CB(str));
                                    }
                                    return list;
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                mSearchView.hideProgress();
            }

            @Override
            protected void onPostExecute(List<CB> list) {
                super.onPostExecute(list);
                if (list != null && !isFinishing()) {
                    mSearchView.swapSuggestions(list);
                }
                mSearchView.hideProgress();
            }
        }.executeOnExecutor(Utils.sExecutorService, newText);
    }

    @Override
    protected void initDatas() {
        sIsInActivity = true;
        PermissionUtils.checkStoragePermissions(this, 10001);

        //判断是否弹出 update对话框
        try {
            String str = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            String version = H.config.uVer;
            if (version.compareTo(str) > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            new BI().showDialog(K.this, H.config.uId, H.config.uForce, H.config.uInfo);
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadAd();

        initViewPages();
        bindService(new Intent(this, M.class), mConnection, Context.BIND_AUTO_CREATE);

        //初始化 user
        mOpenCount = vPrefsUtils.getOpenCount();
        mOpenCount++;
        vPrefsUtils.setOpenCount(mOpenCount);
        ApiConstants.sDownloadQuota = 0;
        if (mOpenCount == 1) {
            Timeutils.checkTime();
            ApiConstants.sDownloadQuota = 3;
        }
        if (!vPrefsUtils.getRefererDone()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vPrefsUtils.setReferDone(true);
                }
            }, 3000);
        }
//        AdManager.getInstance().tryShowOpenAd(AlMainActivity.this);

        //放在Timeutils.checkTime()之后
        if (!BV.isBanUser()) {
            BD.getInstance().setProvider(H.config.promId);
        }
    }

    private void loadAd() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MaxBackInterstitial.getInstance().loadBackInterstitialAd(K.this);
                MaxRewardedAds.getInstance().createRewardedAd(K.this);
            }
        }, 1500);
    }

//    private static boolean gotReward() {
//        return App.config.rewardpop <= 0 ? ApiConstants.rewarded : (ApiConstants.sDownloadQuota >= App.config.rewardpop);
//    }

    public static boolean notRewardOrRunOut() {
        //      return true;
        return H.config.rewardpop <= 0 ? !ApiConstants.rewarded : (ApiConstants.sDownloadQuota <= 0);
    }
//    public static boolean notRewardOrRunOut() {
//        //      return true;
//        return ApiConstants.sDownloadQuota <= 0;
//    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayer = ((M.LocalBinder) service).getService();
            mPlayer.registerCallback(K.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.unregisterCallback(K.this);
            mPlayer = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MA = null;
        unbindService(mConnection);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(CD event) {
        if (null == mPlayList) {
            mPlayList = new BR();
        }
        mPlayList.getSongs().clear();
        mPlayList.songs.clear();
        mPlayList.songs.addAll(event.list);
        mPlayList.playingIndex = event.index;
        mIndex = event.index;

        if (null != mPlayer) {
            playOrPauseIv.setImageResource(R.drawable.al_icon_play_white);
            seekBarProgress.setProgress(0);
            textViewProgress.setText("00:00");
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            playSong(mPlayList, mIndex);
        }
//        AdManager.getInstance().tryShowDownloadWithRate();
        MaxDownloadInterstitial.getInstance().show();
        try {
            BU.logListen(event.list.get(event.index).channel);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void playSong(BR playList, int playIndex) {
        if (playList == null) return;

        playList.setPlayMode(vPrefsUtils.lastPlayMode());
        // boolean result =
        mPlayer.play(playList, playIndex);

        CI song = playList.getCurrentSong();
        if (null != song) {
            onSongUpdated(song);
        }
    }

    private void initViewPages() {
        mFragments.clear();
        mFragments.add(new BP());
        mFragments.add(new BJ());
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
                return "";
            }
        };
        mainViewpager.setAdapter(mAdpter);
        mainViewpager.setOffscreenPageLimit(mAdpter.getCount());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayer != null && mPlayer.isPlaying()) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(Timeutils.formatDuration(targetDuration));
    }

    private void updateProgressTextWithDuration(long progress, long duration) {
        textViewProgress.setText(Timeutils.formatDuration(progress));
        textViewDuration.setText(Timeutils.formatDuration(duration));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seekBarProgress.getMax()));
    }

    private long getCurrentSongDuration() {
        CI currentSong = mPlayer.getPlayingSong();
        if (null == currentSong) {
            return 0;
        }
        long duration = 0;
        if (currentSong != null) {
            duration = currentSong.realduration;
        }
        return duration;
    }

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (mPlayer.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress(), mPlayer.getDuration());
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true);
                    } else {
                        seekBarProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    @Override
    public void onSwitchLast(@Nullable CI last) {
        onSongUpdated(last);
    }

    @Override
    public void onSwitchNext(@Nullable CI next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(@Nullable CI next) {

    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        playOrPauseIv.setImageResource(isPlaying ? R.drawable.icon_pause_white : R.drawable.al_icon_play_white);
        if (isPlaying) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (isLoading) {
            loadingV.setVisibility(View.VISIBLE);
        } else {
            loadingV.setVisibility(View.GONE);
        }
    }

    public void onSongUpdated(@Nullable CI song) {
        if (song == null) {
            playOrPauseIv.setImageResource(R.drawable.al_icon_play_white);
            seekBarProgress.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }
        titleTv.setText(song.getTitle());
        textViewDuration.setText(Timeutils.formatDuration(song.realduration));
        if (!TextUtils.isEmpty(song.getImage())) {
            ImageHelper.loadMusic(imageIv, song.getImage(), K.this, 50, 50);
        }
        mHandler.removeCallbacks(mProgressCallback);
        if (mPlayer.isPlaying()) {
            mHandler.post(mProgressCallback);
            playOrPauseIv.setImageResource(R.drawable.icon_pause_white);
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mainViewpager && mainViewpager.getCurrentItem() == 1) {
            mainViewpager.setCurrentItem(0);
            return;
        }
        exitApp();
    }

    private long[] mHits = new long[2];

    //定义一个所需的数组
    private void exitApp() {
//         数组向左移位操作
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {// 2000代表设定的间隔时间
            vPrefsUtils.sDownloadSuccessCount = 0;
            MaxOpenInterstitial.getInstance().destroy();
            MaxBackInterstitial.getInstance().destroy();
            MaxDownloadInterstitial.getInstance().destroy();
            finish();
        } else {
            ToastUtils.showShortToast("Press again to exit!");
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(CE event) {
        if (event.getType() == CE.SCAN_START) {
            loadMusic(new File(BL.getInstance().getDownloadPath()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BY event) {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull final File folder) {
        //scan folder music
        loadMusic(folder);
    }

    private void loadMusic(final File folder) {
        new AsyncTask<Void, Void, List<CI>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (scanDialog != null) {
                    if (scanDialog.isShowing()) {
                        scanDialog.dismiss();
                    }
                    scanDialog = null;
                }
                scanDialog = new ProgressDialog(K.this);
                scanDialog.setTitle("Import mp3");
                scanDialog.setMessage("scan " + folder.getAbsolutePath());
                scanDialog.setCancelable(false);
                scanDialog.show();
            }

            @Override
            protected List<CI> doInBackground(Void... voids) {
                return Utils.scanDir(folder.getAbsolutePath());
            }

            @Override
            protected void onPostExecute(List<CI> musicList) {
                super.onPostExecute(musicList);
                if (scanDialog != null) {
                    if (scanDialog.isShowing()) {
                        scanDialog.dismiss();
                    }
                    scanDialog = null;
                }
                //与现有的music list 合并
                BL.getInstance().updateMuiscList(musicList);
            }
        }.executeOnExecutor(Utils.sExecutorService);
    }

}