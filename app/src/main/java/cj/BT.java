package cj;

import androidx.annotation.Nullable;

import jf.CI;


public interface BT {

    void setPlayList(BR list);

    boolean play();

    boolean play(BR list);

    boolean play(BR list, int startIndex);

    boolean play(CI song);

    boolean playLast();

    boolean playNext();

    boolean pause();

    boolean isPlaying();

    long getProgress();

    long getDuration();

    CI getPlayingSong();

    boolean seekTo(int progress);

    void setPlayMode(BQ playMode);

    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void removeCallbacks();

    void releasePlayer();

    interface Callback {

        void onSwitchLast(@Nullable CI last);

        void onSwitchNext(@Nullable CI next);

        void onComplete(@Nullable CI next);

        void onPlayStatusChanged(boolean isPlaying);

        void onLoading(boolean isLoading);
    }
}
