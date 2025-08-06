package hifi.music.tube.downloader.bean.jm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import jf.CA;

public class JamTag extends CA {

    public long id;

    public String name;

    public String lang;

    public String idstr;

    public int featuredRank;

    public Cover cover;

    public static class Cover extends CA {

        @SerializedName("tile-xs")
        public String tileXs;

        @SerializedName("tile-sm")
        public String tileSm;

        public String getCover() {
            if (TextUtils.isEmpty(tileSm)) {
                return TextUtils.isEmpty(tileXs) ? "" : tileXs;
            }
            return tileSm;
        }
    }
}
