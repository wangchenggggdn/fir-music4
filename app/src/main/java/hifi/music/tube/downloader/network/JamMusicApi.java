package hifi.music.tube.downloader.network;


import hifi.music.tube.downloader.bean.jm.JamArtist;
import hifi.music.tube.downloader.bean.jm.JamTag;
import hifi.music.tube.downloader.bean.jm.JamTrack;
import hifi.music.tube.downloader.bean.jm.JamUp;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JamMusicApi {

    @GET("/api/update")
    Observable<List<JamUp>> update();

    @GET("/api/tags?order=featureRank&limit=40&lang=en&category[]=genre")
    Observable<List<JamTag>> tags();

    @GET("/api/tracks")
    Call<List<JamTrack>> getTracksByTag(@Query("order") String order, @Query("tagId") long tagId,
                                        @Query("limit") int limit, @Query("offset") int offset);

//    @GET("/api/search?identities=www&type=track")
    @GET("/api/search")
    Call<List<JamTrack>> search(@Query("query") String query, @Query("type") String type, @Query("limit") int limit,
                                @Query("identities") String identities);

    @GET("/api/tracks")
    Call<List<JamTrack>> getPopular(@Query("order") String order, @Query("limit") int limit,
                                    @Query("offset") int offset);

    @GET("/api/artists")
    Call<List<JamArtist>> artist(@Query("id[]") List<Long> ids);

}
