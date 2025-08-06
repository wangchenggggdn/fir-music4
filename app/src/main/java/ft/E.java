package ft;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import hifi.music.tube.downloader.admax.MaxSearchNative;

public
class E extends FrameLayout {
    public E(@NonNull Context context) {
        super(context);
    }

    public E(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public E(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadAd() {
        new MaxSearchNative(getContext()).createNativeAd(this);
    }


}
