package co.aquario.chatapp.handler;

import java.util.ArrayList;
import java.util.Map;

import co.aquario.chatapp.event.response.ChatInfo;
import co.aquario.chatapp.event.response.HistoryDataResponse;
import co.aquario.chatapp.model.SomeData;
import co.aquario.chatapp.model.conversation.ConversationId;
import co.aquario.chatapp.model.conversation.RecentChatResponse;
import co.aquario.chatui.model.Block;
import co.aquario.chatui.model.CreateGroup;
import co.aquario.chatui.model.FindFriends;
import co.aquario.chatui.model.Invite;
import co.aquario.chatui.model.ShotGroup;
import co.aquario.chatui.model.Status;
import co.aquario.chatui.model.UpdateGroup;
import co.aquario.chatui.model_chat.ConversationChat;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.EncodedQuery;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
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

    @GET("/chat/{id}/android")
    public void getChatById(@Path("id") int id,Callback<ChatInfo> responseJson);

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

    @POST("/chat/block")
    public void getฺBlock(@Body String body, Callback<Block> responseJson);

    @POST("/chat/unblock")
    public void getUnฺBlock(@Body String body, Callback<Block> responseJson);

    @POST("/chat/notification/on")
    public void getOn(@Body String body, Callback<Status> responseJson);

    @POST("/chat/notification/off")
    public void getOff(@Body String body, Callback<Status> responseJson);

    @POST("/chat/group/create")
    public void getCreateGroup(@Body String body, Callback<CreateGroup> responseJson);



    @POST("/chat/group/{id}/invite")
    public void getInvite(@Body String body, @Path("id") int id, Callback<Invite> responseJson);

    @GET("/chat/find_friends")
    public void getFindFriends(@QueryMap Map<String, String> options, Callback<FindFriends> responseJson);

    @PUT("/chat/group/{id}/name")
    public void updateGroupName(@Body String body, @Path("id") int id,Callback<UpdateGroup> responseJson);

    @PUT("/chat/group/{id}/avatar")
    public void updateGroupAvatar(@Body String body, @Path("id") int id,Callback<UpdateGroup> responseJson);



//    @GET("/api/chat/{id}/history/android")
//    public void getHistory( @Path("id") int id, @QueryMap Map<String, Integer> options,Callback<co.aquario.chatui.event_chat.response.HistoryDataResponse> responseJson);
//
//    @GET("/api/chat/list/{id}")
//    public void getConversationList(@Body String body, @Path("id") int id, Callback<ConversationChat> responseJson);
//
//    @FormUrlEncoded
//    @POST("/chat/individual/create")
//    public void getConversation
//            (@Field("userId") int userId,
//             @Field("partnerId") int partnerId
//             //,@Header("Content-Type") String contentType
//                    , Callback<ConversationOneToOne> responseJson);

    @GET("/chat/individual/{id}")
    public void getIndividualList(@Body String body, @Path("id") int id, Callback<ConversationChat> responseJson);

    @GET("/chat/group/{id}")
    public void getGroupList(@Body String body, @Path("id") int id,Callback<ShotGroup> callback);

    @GET("/chat/recent/user/{id}/android")
    public void getRecentChat(@Path("id") int id, Callback<RecentChatResponse> callback);



}
