package hifi.music.tube.downloader.network;


import hifi.music.tube.downloader.ztools.Config;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IApiService {
    @GET("fir4music.json")
    Call<Config> getConfig();

}
