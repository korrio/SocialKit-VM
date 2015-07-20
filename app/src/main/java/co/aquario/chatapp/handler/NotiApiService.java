package co.aquario.chatapp.handler;

import java.util.Map;

import co.aquario.chatapp.event.response.NotiBadgeDataResponse;
import co.aquario.chatapp.event.response.NotiListDataResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

;

/**
 * Created by matthewlogan on 9/3/14.
 */
public interface NotiApiService {
    @GET("/index.php")
    public void getNotiList(@QueryMap Map<String, String> options,
                                Callback<NotiListDataResponse> responseJson);

    @GET("/index.php")
    public void getNotiBadge(@QueryMap Map<String, String> options,
                            Callback<NotiBadgeDataResponse> responseJson);

    @GET("/index.php")
    public void readNoti(@QueryMap Map<String, String> options);


}
