package co.aquario.socialkit.handler;

import java.util.Map;

import co.aquario.chatui.model.UserMe;
import co.aquario.chatui.model.follow_suggestion_model.FollowSuggestionModel;
import co.aquario.chatui.model.followersmodel.FollowersModel;
import co.aquario.chatui.model.friendmodel.FriendsModel;
import co.aquario.socialkit.activity.search.SearchResult;
import co.aquario.socialkit.event.FollowUserResponse;
import co.aquario.socialkit.event.FriendListDataResponse;
import co.aquario.socialkit.event.PhotoListDataResponse;
import co.aquario.socialkit.event.PostCommentDataResponse;
import co.aquario.socialkit.event.StoryDataResponse;
import co.aquario.socialkit.event.TimelineDataResponse;
import co.aquario.socialkit.event.UserProfileDataResponse;
import co.aquario.socialkit.model.LoginData;
import co.aquario.socialkit.model.RegisterData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface ApiServiceVM {
    @POST("/1.0/auth")
    public void login(@QueryMap Map<String, String> options,
                                Callback<LoginData> responseJson);

    @POST("/1.0/fbAuth")
    public void fbLogin(@QueryMap Map<String, String> options,
                      Callback<LoginData> responseJson);

    @POST("/user/register")
    public void register(@QueryMap Map<String, String> options,
                      Callback<RegisterData> responseJson);

    @GET("/user/otp")
    public void otp(@QueryMap Map<String, String> options);

    @POST("/1.0/posts/home_timeline/{id}")
    public void getHomeTimeline(@Path("id") int id,@QueryMap Map<String, String> options,
                        Callback<TimelineDataResponse> responseJson);

    @POST("/1.0/posts/user_timeline/{id}")
    public void getUserTimeline(@Path("id") int id,@QueryMap Map<String, String> options,
                                Callback<TimelineDataResponse> responseJson);

    @GET("/story/{id}")
    public void getStory(@Path("id") int id,
                         Callback<StoryDataResponse> responseJson);

    @GET("/search/result")
    public void getSearch(@QueryMap Map<String, String> options,
                                Callback<SearchResult> responseJson);

    @GET("/search/hashtag")
    public void getHashtagStory(@QueryMap Map<String, String> options,
                         Callback<TimelineDataResponse> responseJson);

    @POST("/1.0/follow/{id}")
    public void followUser(@Path("id") int id,Callback<FollowUserResponse> responseJson);


    @POST("/1.0/followers/{id}")
    public void getFollower(@Path("id") int id,@QueryMap Map<String, String> options,
                            Callback<FriendListDataResponse> responseJson);

    @POST("/1.0/followings/{id}")
    public void getFollowing(@Path("id") int id,@QueryMap Map<String, String> options,
                            Callback<FriendListDataResponse> responseJson);

    @POST("/1.0/friends/{id}")
    public void getFriend(@Path("id") int id,@QueryMap Map<String, String> options,
                            Callback<FriendListDataResponse> responseJson);

    @POST("/1.0/pages/{id}")
    public void getPage(@Path("id") int id,@QueryMap Map<String, String> options,
                          Callback<FriendListDataResponse> responseJson);

    //http://api.vdomax.com/search/social/a?from=0&limit=10&sort=A&user_id=3082
    @GET("/search/social")
    public void getSocial(@QueryMap Map<String, String> options,
                        Callback<FriendListDataResponse> responseJson);

    @GET("/search/photo")
    public void getPhoto(@QueryMap Map<String, String> options,
                          Callback<PhotoListDataResponse> responseJson);



    @GET("/1.0/user/{id}")
    public void getProfile(@Path("id") int id,
                           Callback<UserProfileDataResponse> responseJson);

    @GET("/username/{username}")
    public void getProfileUsername(@Path("username") String username,
                           Callback<UserProfileDataResponse> responseJson);

    @POST("/1.0/posts/{id}/comment")
    public void postComment(@Path("id") int postId,@QueryMap Map<String, String> options,Callback<PostCommentDataResponse> responseCallback);

    @POST("/1.0/posts/{id}/love")
    public void postLove(@Path("id") int postId,@QueryMap Map<String, String> options,Callback<PostCommentDataResponse> responseCallback);

    @POST("/1.0/posts/{id}/share")
    public void postShare(@Path("id") int postId,@QueryMap Map<String, String> options,Callback<PostCommentDataResponse> responseCallback);



    /*
    @Multipart
    @POST("https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac")
    void uploadImage(@Part("File") TypedFile file,
                     Callback<Response> callback);
                     */

    // some social api used in chat:ui
    @POST("/1.0/friends/{id}")
    public void getFriends(@Body String body,@Path("id") int id, Callback<FriendsModel> responseJson);

    @POST("/1.0/followings/{id}")
    public void getFollowings(@Body String body, @Path("id") int id, Callback<FollowersModel> responseJson);

    @POST("/1.0/followers/{id}")
    public void getFollowers(@Body String body, @Path("id") int id,Callback<FollowersModel> responseJson);

    @POST("/1.0/follow_suggestion")
    public void getFollowSuggestion(@Body String body, Callback<FollowSuggestionModel> responseJson);

    @GET("/user/{id}")
    public void getUser(@Body String body, @Path("id") int id,Callback<UserMe> responseJson);


}
