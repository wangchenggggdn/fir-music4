package cs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import s.H;
import hifi.music.tube.downloader.R;
import jf.CI;
import hifi.music.tube.downloader.databinding.DialogPlayMusicViaBinding;
import es.BU;
import cj.BS;
import dc.BE;
import hifi.music.tube.downloader.referrer.ReferrerStream;
import hifi.music.tube.downloader.ztools.ViaDialogCountUtil;

public class BO extends Dialog implements View.OnClickListener {
    private DialogPlayMusicViaBinding binding;
    private ArrayList<CI> mArrayList;
    private int mIndex;

    public BO(Context context, ArrayList<CI> arrayList, int index) {
        // 在构造方法里, 传入主题
        super(context, R.style.BottomDialogStyle);
        mArrayList = arrayList;
        mIndex = index;
        // 拿到Dialog的Window, 修改Window的属性
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        attributes.width = width / 5 * 4;
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_play_music_via, null, false);
        setContentView(view);
        binding = DataBindingUtil.bind(view);
        binding.externalPlay.setOnClickListener(this);
        binding.builtInPlay.setOnClickListener(this);
        ReferrerStream referrerStream = H.config.referrer;
        String icon = referrerStream.player_feature.icon;
        String title = referrerStream.player_feature.title;
        binding.externalText.setText(title);
        Glide.with(binding.externalIcon).load(icon).into(binding.externalIcon);
        setOnShowListener(dialog -> {
            BU.viaDialogShow();
            ViaDialogCountUtil.reduceCount();
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.external_play) {
            ReferrerStream referrerStream = H.config.referrer;
            String pkg = referrerStream.player_feature.getPkg(true);
            BE.gotoGP(pkg);
            BU.goGp("way1");
            dismiss();
        }
        if (id == R.id.built_in_play) {
            try {
                BS.playList(mArrayList, mIndex);
                BU.built_in("way1");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            dismiss();
        }
    }


}
