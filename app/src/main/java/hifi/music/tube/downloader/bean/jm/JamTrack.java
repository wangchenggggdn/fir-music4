package hifi.music.tube.downloader.bean.jm;

import android.text.TextUtils;

import jf.CA;
import jf.CC;
import es.BV;

public class JamTrack extends CC {

    public static final int DOWNLOAD_STATE_NOT_DOWNLOAD = 0;
    public static final int DOWNLOAD_STATE_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATE_DOWNLOADED = 2;

    public int downloadState = DOWNLOAD_STATE_NOT_DOWNLOAD;

    public long id;

    public long artistId;

    public String name;

    public int duration;

    public Stream stream;

    public Cover cover;

    public Stats stats;

    public Status status;

    private AudioInfo audioInfo;

    public String getStreamUrl() {
        return getAudioInfo().url;
    }

    public String getExt() {
        return getAudioInfo().ext;
    }

    private AudioInfo getAudioInfo() {
        if (audioInfo == null) {
            audioInfo = new AudioInfo();
            String ext = null;
            String url = null;
            if (!TextUtils.isEmpty(stream.mp3)) {
                url = stream.mp3;
                ext = ".mp3";
            }
//            if (!TextUtils.isEmpty(stream.ogg)) {
//                url = stream.ogg;
//                ext = ".ogg";
//            }
//            if (!TextUtils.isEmpty(stream.mp32)) {
//                url = stream.mp32;
//                ext = ".mp3";
//            }
//            if (!TextUtils.isEmpty(stream.mp33)) {
//                url = stream.mp33;
//                ext = ".mp3";
//            }

            audioInfo.ext = ext;
            audioInfo.url = url;

        }

        return audioInfo;
    }

    public static class AudioInfo {
        public String url;

        public String ext;
    }

    private String mediaId;

    public String getMediaId() {
        if (mediaId == null) {
            return (mediaId = BV.stringToMD5(id + name + duration + artistId));
        }
        return mediaId;
    }

    public static class Stream extends CA {
        public String mp3;
        public String ogg;
        public String mp32;
        public String mp33;

        public boolean isEmpty() {
            return TextUtils.isEmpty(mp3)
                    && TextUtils.isEmpty(ogg)
                    && TextUtils.isEmpty(mp32)
                    && TextUtils.isEmpty(mp33);
        }
    }

    public boolean isUnavailable() {
        return status == null || !status.available || stream == null || stream.isEmpty();
    }

    public String getCover() {
        if (cover == null || cover.small == null) {
            return "";
        }
        Cover.Small small = cover.small;

        if (!TextUtils.isEmpty(small.size130)) {
            return small.size130;
        }
        if (!TextUtils.isEmpty(small.size150)) {
            return small.size150;
        }
        if (!TextUtils.isEmpty(small.size175)) {
            return small.size175;
        }
        if (!TextUtils.isEmpty(small.size200)) {
            return small.size200;
        }
        if (!TextUtils.isEmpty(small.size300)) {
            return small.size300;
        }
        if (!TextUtils.isEmpty(small.size100)) {
            return small.size100;
        }
        if (!TextUtils.isEmpty(small.size600)) {
            return small.size600;
        }
        return "";
    }

    public static class Cover extends CA {

        public Small small;

        public static class Small extends CA {
            public String size100;
            public String size130;
            public String size150;
            public String size175;
            public String size200;
            public String size300;
            public String size600;
        }

    }

    public static class Status extends CA {
        public boolean available;
    }

    public static class Stats extends CA {

        public int downloadedAll;
        public int listenedAll;

    }

}
