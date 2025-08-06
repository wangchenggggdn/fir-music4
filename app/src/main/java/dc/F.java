package dc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import hifi.music.tube.downloader.R;


public class F extends LinearLayout {
    public F(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public F(Context context) {
        super(context);
    }

    @SuppressLint({"NewApi"})
    public F(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setRating(int i) {
        removeAllViews();
        if (i == 0) {
            i = 5;
        }
        for (int i2 = 0; i2 < 5; i2++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(BE.dp2px(getContext(), 2.0f), 0, BE.dp2px(getContext(), 2.0f), 0);
            LayoutParams layoutParams = new LayoutParams(BE.dp2px(getContext(), 18.0f), BE.dp2px(getContext(), 18.0f));
            if (i2 < i) {
                imageView.setImageResource(R.drawable.recom_star_sel);
            } else {
                imageView.setImageResource(R.drawable.zl_recom_star_nor);
            }
            addView(imageView, layoutParams);
        }
    }
}
