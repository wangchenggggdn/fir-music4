package dc;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import hifi.music.tube.downloader.R;

public class G extends FrameLayout {
    private ImageView iconSiv;
    private ImageView closeIv;
    private Activity activity;
    private View view;
    private BC bean;

    public G(Context context) {
        super(context);
    }

    public G(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public G(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* Access modifiers changed, original: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.iconSiv = (ImageView) findViewById(R.id.recommend_icon);
        this.closeIv = (ImageView) findViewById(R.id.close_icon);
        this.view = findViewById(R.id.recommend_view);
    }

    /* Access modifiers changed, original: protected */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.view.clearAnimation();
        this.activity = null;
    }

    public static G newInstance(Activity activity) {
        G smallRecommend = (G) LayoutInflater.from(activity).inflate(R.layout.small_recommend_layout, null);
        smallRecommend.activity = activity;
        return smallRecommend;
    }

    public void removeView() {
        try {
            if (!(this.activity == null || getParent() == null)) {
                ((ViewGroup) this.activity.findViewById(android.R.id.content)).removeView(this);
            }
            setVisibility(GONE);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void addView() {
        try {
            if (this.activity != null) {
                ((ViewGroup) this.activity.findViewById(android.R.id.content)).addView(this);
            }
            if (BD.getInstance().getRecomEventListener() != null) {
                BD.getInstance().getRecomEventListener().showed(1, this.bean);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public G build(final BC recommendBean) {
        this.bean = recommendBean;
//        this.iconSiv.setImageResource(recommendBean.getImgId());
        if (!TextUtils.isEmpty(recommendBean.getImgUrl())) {
            Glide.with(this)
                    .load(recommendBean.getImgUrl())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            iconSiv.setImageDrawable(resource);
                        }
                    });
        }
        this.iconSiv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BD.getInstance().getProvider().remove(recommendBean);
                BE.gotoGP(recommendBean.getPackageIdWithRecom());
                G.this.removeView();
                if (BD.getInstance().getRecomEventListener() != null) {
                    BD.getInstance().getRecomEventListener().clicked(1, recommendBean);
                }
            }
        });
        this.closeIv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                G.this.removeView();
                BE.setCount(recommendBean.getPackageId(), BE.getMaxShowCount());
                if (BD.getInstance().getRecomEventListener() != null) {
                    BD.getInstance().getRecomEventListener().closed(1, recommendBean);
                }
            }
        });
        this.view.startAnimation(BE.rotateIcon(7));
        return this;
    }
}
