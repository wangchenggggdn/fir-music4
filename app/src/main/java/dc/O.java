package dc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import s.H;
import hifi.music.tube.downloader.R;
import hifi.music.tube.downloader.ztools.ShareUtils;


public class O extends Activity {
    private BC recommendBean;
    private OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View view) {
            BD.getInstance().getProvider().remove(O.this.recommendBean);
            String weburl = recommendBean.getWebappurl();
            if (!TextUtils.isEmpty(weburl)) {
                ShareUtils.openBrowser(O.this, H.config.webappurl);
            } else {
                BE.gotoGP(O.this.recommendBean.getPackageIdWithRecom());
            }
            O.this.finish();
            if (BD.getInstance().getRecomEventListener() != null) {
                BD.getInstance().getRecomEventListener().clicked(2, O.this.recommendBean);
            }
        }
    };

    public static void show(Context context, BC recommendBean) {
        try {
            Intent intent = new Intent(context, O.class);
            intent.putExtra("recommendBean", recommendBean);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.recommendBean != null) {
            bundle.putParcelable("recommendBean", this.recommendBean);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (BD.getInstance().getRecomEventListener() != null) {
            BD.getInstance().getRecomEventListener().closed(2, this.recommendBean);
        }
    }

    public static void setStyle(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            activity.getWindow().getDecorView().setSystemUiVisibility(1280);
            activity.getWindow().setStatusBarColor(0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setStyle((Activity) this);
            setContentView(R.layout.recommend_dialog_activity);
            if (bundle == null) {
                this.recommendBean = (BC) getIntent().getParcelableExtra("recommendBean");
            } else {
                this.recommendBean = (BC) bundle.getParcelable("recommendBean");
            }
            findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    BE.setCount(O.this.recommendBean.getPackageId(), BE.getMaxShowCount());
                    O.this.finish();
                    if (BD.getInstance().getRecomEventListener() != null) {
                        BD.getInstance().getRecomEventListener().closed(2, O.this.recommendBean);
                    }
                }
            });
            ImageView imageView = (ImageView) findViewById(R.id.recom_app_icon);
//            imageView.setImageResource(this.recommendBean.getImgId());
            if (!TextUtils.isEmpty(this.recommendBean.getImgUrl())) {
                Glide.with(this)
                        .load(this.recommendBean.getImgUrl())
                        .into(imageView);
            }
            imageView.setOnClickListener(this.onClickListener);
            ((TextView) findViewById(R.id.iv_app_name)).setText(this.recommendBean.getTitle());
            ((F) findViewById(R.id.recom_app_star)).setRating(5);
            ((TextView) findViewById(R.id.recom_app_desc)).setText(this.recommendBean.getDesc());
            findViewById(R.id.recom_cta_tv).setOnClickListener(this.onClickListener);
            if (BD.getInstance().getRecomEventListener() != null) {
                BD.getInstance().getRecomEventListener().showed(2, this.recommendBean);
            }
        } catch (Throwable th) {
            th.printStackTrace();
            finish();
        }
    }
}
