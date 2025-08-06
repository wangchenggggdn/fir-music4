package hifi.music.tube.downloader.admax;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import hifi.music.tube.downloader.R;


/**
 * Created by LiJiaZhi on 16/12/31.
 * 插屏 ad loading对话框
 */

public class AdLoadingDialog extends Dialog {

    public AdLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public AdLoadingDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ad_loading);
    }
}
