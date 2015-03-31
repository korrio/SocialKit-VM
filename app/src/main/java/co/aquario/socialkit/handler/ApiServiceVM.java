package co.aquario.socialkit.handler;

import java.util.Map;

import co.aquario.socialkit.event.FollowUserResponse;
import co.aquario.socialkit.event.FriendListDataResponse;
import co.aquario.socialkit.event.PostCommentDataResponse;
import co.aquario.socialkit.event.StoryDataResponse;
import co.aquario.socialkit.event.TimelineDataResponse;
import co.aquario.socialkit.event.UserProfileDataResponse;
import co.aquario.socialkit.model.LoginData;
import co.aquario.socialkit.model.RegisterData;
import retrofit.Callback;
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

    @GET("/story/{id}")
    public void getStory(@Path("id") int id,
                         Callback<StoryDataResponse> responseJson);

    @GET("/1.0/user/{id}")
    public void getProfile(@Path("id") int id,
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


}
