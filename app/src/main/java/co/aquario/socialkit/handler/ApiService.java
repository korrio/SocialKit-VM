package co.aquario.socialkit.handler;

import java.util.Map;

import co.aquario.socialkit.model.ImageData;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by matthewlogan on 9/3/14.
 */
public interface ApiService {
    @GET("/random/")
    public void getRandomImage2(@QueryMap Map<String, String> options,
                          Callback<ImageData> responseJson);
    @GET("/random/")
    public void getRandomImage(
                          Callback<ImageData> responseJson);
}
