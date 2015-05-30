package co.aquario.socialkit.search.soundcloud;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by kevin on 2/22/15.
 */
public interface SoundCloudService {

    String CLIENT_ID = "954240dcf4b7c0a46e59369a69db7215";

    @GET("/tracks?client_id=" + CLIENT_ID)
    void searchSongs(@Query("q") String query, Callback<List<Track>> cb);

    @GET("/tracks?client_id=" + CLIENT_ID)
    void getRecentSongs(@Query("created_at[from]") String date, Callback<List<Track>> cb);

    void songsAfter(@Query("created_at[to]") String date, Callback<List<Track>> cb);

    void bpmFrom(@Query("bpm[from]") String date, Callback<List<Track>> cb);
}
