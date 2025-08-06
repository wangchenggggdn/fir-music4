package cs;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MediatorLiveData;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkSettings;
import encrypt.pck.JiaMi;

import s.H;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.admax.MaxOpenInterstitial;
import es.BU;
import hifi.music.tube.downloader.network.CountryApi;
import hifi.music.tube.downloader.network.IApiService;
import hifi.music.tube.downloader.network.NetworkManager;
import es.BV;
import hifi.music.tube.downloader.referrer.ReferrerUtil;
import hifi.music.tube.downloader.ztools.Config;
import hifi.music.tube.downloader.ztools.vPrefsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class I extends AppCompatActivity {
    //@BindView(R.id.name_ll)
    LinearLayout nameLl;
    //@BindView(R.id.logo_iv)
    ImageView logoIv;

    boolean isGoMain = false;
    boolean configDone = false;
    boolean openAdDone = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        if (K.sIsInActivity) {
            startMainActivity();
            return;
        }
        setContentView(R.layout.activity_splash);

        initViews();
        initDatas();
    }


    protected void initViews() {
        nameLl = findViewById(R.id.name_ll);
        logoIv = findViewById(R.id.logo_iv);
    }

    protected void initDatas() {
//        loadOpen();
        //load config
        Call<Config> mCall = NetworkManager.createConfigService(IApiService.class).getConfig();
        mCall.enqueue(new Callback<Config>() {
            @Override
            public void onResponse(Call<Config> call, Response<Config> response) {
                Config config = response.body();
                if (config != null) {
                    if (config.isBanDeal)
                        config.ban = JiaMi.ban;
                    vPrefsUtils.setConfig(config);
                    H.postConfig(config);
                    ReferrerUtil.preloadImage();
                    //首次加载配置成功时 setCountryFlag
                    int cfgCount = vPrefsUtils.getConfigCount();
                    vPrefsUtils.setConfigCount(cfgCount + 1);
                    if (cfgCount == 0) {
                        BV.setUacInstall(H.sContext, config.devToken, config.linkId);
                        BV.setCountryFlag(H.sContext);
                        if (config.isIpDeal) {
                            loadIpCountry();
                        }
                    }
                }
                //进入mainActivity
                configEnterMain();
            }

            @Override
            public void onFailure(Call<Config> call, Throwable t) {
                //进入mainActivity
                configEnterMain();
            }
        });

        //开屏动画
        nameLl.post(new Runnable() {
            @Override
            public void run() {
                anim();
            }
        });
        nameLl.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 5000);
    }

    private final CountryApi mCountryApi = new CountryApi();

    private void loadIpCountry() {
        if (BV.isBanUser()) {
            return;
        }
        mCountryApi.checkBlock(new CountryApi.BlockCallback() {
            @Override
            public void onBlockResult(boolean block) {
                BU.ipBlock(block, BV.isBanUser());
                if (block) {
                    BV.setBanUser();
                }
            }
        });
    }

    private void configEnterMain() {
        configDone = true;
        tryStartMainActivity();
    }

    private void adEnterMain() {
        openAdDone = true;
        tryStartMainActivity();
    }

    //防止back键退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    private void anim() {
        ValueAnimator alpha = ObjectAnimator.ofFloat(logoIv, "alpha", 0.0f, 1.0f);
        alpha.setDuration(1500);
        ValueAnimator alphaN = ObjectAnimator.ofFloat(nameLl, "alpha", 0.0f, 1.0f);
        ValueAnimator tranY = ObjectAnimator.ofFloat(logoIv, "translationY", -logoIv.getHeight() / 3, 0);
        tranY.setDuration(1500);
        ValueAnimator wait = ObjectAnimator.ofInt(0, 100);
        wait.setDuration(1500);
        wait.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (isGoMain) {
//                            startMainActivity();
//                        }
//                    }
//                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new LinearInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logoIv.setVisibility(View.VISIBLE);
                nameLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.play(alpha).with(alphaN).with(tranY).before(wait);
        set.start();
    }

    private void tryStartMainActivity() {
        if (openAdDone && configDone) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        if (!isGoMain) {
            isGoMain = true;
            K.launch(this);
            finish();
        }
    }
}