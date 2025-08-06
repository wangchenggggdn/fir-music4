package hifi.music.tube.downloader.network;

public interface CallBack<T>  {
    void onFail();
    void onSuccess(T t);
}
