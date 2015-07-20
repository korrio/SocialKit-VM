package co.aquario.chatapp.handler;

import java.util.ArrayList;
import java.util.Map;

import co.aquario.chatapp.event.response.HistoryDataResponse;
import co.aquario.chatapp.model.conversation.ConversationId;
import co.aquario.chatapp.model.SomeData;
import retrofit.Callback;
import retrofit.http.EncodedQuery;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

;

/**
 * Created by matthewlogan on 9/3/14.
 */
public interface ChatApiService {
    @GET("/random/")
    public void getRandomImage2(@QueryMap Map<String, String> options,
                                Callback<SomeData> responseJson);
    @GET("/random/")
    public void getRandomImage(
            Callback<SomeData> responseJson);

    @GET("/chat/{id}/history/android")
    public void getHistory(@Path("id") int id, @QueryMap Map<String, Integer> options,
                           Callback<HistoryDataResponse> responseJson);

    @FormUrlEncoded
    @POST("/chat/group/create")
    public void getConversationGroup
            (@Field("name") String name,
             @EncodedQuery("inviteUserIds[]") ArrayList<Integer> inviteUserIds,
             @Field("createdBy") int createdBy
             //,@Header("Content-Type") String contentType
                    , Callback<ConversationId> responseJson);

    @FormUrlEncoded
    @POST("/chat/individual/create")
    public void getConversation
            (@Field("userId") int userId,
             @Field("partnerId") int partnerId
             //,@Header("Content-Type") String contentType
                    , Callback<ConversationId> responseJson);

    @GET("/chat/public/channel/{id}")
    public void getConversationGroupPublic
            (@Path("id") int id
                    , Callback<ConversationId> responseJson);

}
