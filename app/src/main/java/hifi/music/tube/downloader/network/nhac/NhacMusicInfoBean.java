package hifi.music.tube.downloader.network.nhac;

import java.io.Serializable;

import jf.CA;


public class NhacMusicInfoBean extends CA {
    private int errorCode;
    private String errorMsg;
    private DataDTO data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO extends CA implements Serializable {
        private String streaming_url;

        public String getStreaming_url() {
            return streaming_url == null ? "" : streaming_url;
        }

        public void setStreaming_url(String streaming_url) {
            this.streaming_url = streaming_url;
        }
    }
}
