package co.aquario.socialkit.handler;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.event.FailedNetworkEvent;
import co.aquario.socialkit.event.FbAuthEvent;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.event.FollowUserResponse;
import co.aquario.socialkit.event.FollowUserSuccessEvent;
import co.aquario.socialkit.event.FriendListDataResponse;
import co.aquario.socialkit.event.GetStoryEvent;
import co.aquario.socialkit.event.GetStorySuccessEvent;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.LoadFriendListEvent;
import co.aquario.socialkit.event.LoadFriendListSuccessEvent;
import co.aquario.socialkit.event.LoadHashtagStoryEvent;
import co.aquario.socialkit.event.LoadPhotoListEvent;
import co.aquario.socialkit.event.LoadPhotoListSuccessEvent;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.LoginEvent;
import co.aquario.socialkit.event.LoginFailedAuthEvent;
import co.aquario.socialkit.event.LoginSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.event.PhotoListDataResponse;
import co.aquario.socialkit.event.PostCommentDataResponse;
import co.aquario.socialkit.event.PostCommentEvent;
import co.aquario.socialkit.event.PostCommentSuccessEvent;
import co.aquario.socialkit.event.PostLoveEvent;
import co.aquario.socialkit.event.PostLoveSuccessEvent;
import co.aquario.socialkit.event.PostShareEvent;
import co.aquario.socialkit.event.PostShareSuccessEvent;
import co.aquario.socialkit.event.RegisterEvent;
import co.aquario.socialkit.event.RegisterFailedEvent;
import co.aquario.socialkit.event.RegisterSuccessEvent;
import co.aquario.socialkit.event.RequestOtpEvent;
import co.aquario.socialkit.event.StoryDataResponse;
import co.aquario.socialkit.event.TimelineDataResponse;
import co.aquario.socialkit.event.UnfollowUserSuccessEvent;
import co.aquario.socialkit.event.UserProfileDataResponse;
import co.aquario.socialkit.model.LoginData;
import co.aquario.socialkit.model.RegisterData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ApiHandlerVM {

    private Context context;
    private ApiServiceVM api;
    private ApiBus apiBus;

    public ApiHandlerVM(Context context, ApiServiceVM api,
                        ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onLoginEvent(LoginEvent event) {
        Log.e("HEY2!","Login: " +event.getUsername() + " : " + event.getPassword());

        Map<String, String> options = new HashMap<String, String>();
        options.put("username", event.getUsername());
        options.put("password", event.getPassword());

        api.login(options, new Callback<LoginData>() {
            @Override
            public void success(LoginData loginData, Response response) {
                //Log.e("loginData",loginData.apiToken);
                Log.e("response", response.getBody().toString());

                if (loginData.status.equals("1"))
                    ApiBus.getInstance().post(new LoginSuccessEvent(loginData));
                else
                    ApiBus.getInstance().post(new LoginFailedAuthEvent());

                Log.e("POSTBACK", "post response back to LoginFragment");
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("response",error.getBody().toString());
                Log.e("failedNetwork", "failedNetworkEvent");
                apiBus.post(new FailedNetworkEvent());
            }
        });
    }


    @Subscribe public void onFbLoginEvent(FbAuthEvent event) {

        Map<String, String> options = new HashMap<String, String>();
        options.put("access_token", event.getFbToken());

        api.fbLogin(options, new Callback<LoginData>() {
            @Override
            public void success(LoginData loginData, Response response) {
                //Log.e("loginData",loginData.apiToken);
                Log.e("response", response.getBody().toString());

                if (loginData.status.equals("1"))
                    apiBus.post(new LoginSuccessEvent(loginData));
                else
                    apiBus.post(new LoginFailedAuthEvent());

                Log.e("POSTBACK", "post response back to LoginFragment");
            }

            @Override
            public void failure(RetrofitError error) {
                //Log.e("response",error.getBody().toString());
                apiBus.post(new FailedNetworkEvent());
            }
        });
    }

    @Subscribe public void onRegisterEvent(RegisterEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("name", event.getName());
        options.put("username", event.getUsername());
        options.put("password", event.getPassword());
        options.put("email", event.getEmail());
        options.put("gender", event.getGender());

        api.register(options, new Callback<RegisterData>() {
            @Override
            public void success(RegisterData registerData, Response response) {
                //Log.e("loginData",loginData.apiToken);
                Log.e("response", response.getBody().mimeType());

                if (registerData.status.equals("1")) {
                    apiBus.post(new RegisterSuccessEvent(registerData));
                } else {
                    apiBus.post(new RegisterFailedEvent(registerData.message));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("response", error.getBody().toString());
                apiBus.post(new FailedNetworkEvent());
            }
        });
    }

    @Subscribe public void onRequestOtp(RequestOtpEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("mobile", event.getMobile());
        options.put("message", event.getMessage());

        api.otp(options);
    }

    @Subscribe public void onGetStory(GetStoryEvent event) {

        api.getStory(Integer.parseInt(event.getPostId()), new Callback<StoryDataResponse>() {
            @Override
            public void success(StoryDataResponse storyDataResponse, Response response) {
                GetStorySuccessEvent event = new GetStorySuccessEvent(storyDataResponse.getPost());
                ApiBus.getInstance().post(event);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
            }

        });

    }

    @Subscribe public void onGetHashtagStory(LoadHashtagStoryEvent event) {

        Map<String, String> options = new HashMap<String, String>();

        options.put("q", event.getQ());

        api.getHashtagStory(options, new Callback<TimelineDataResponse>() {
            @Override
            public void success(TimelineDataResponse timelineDataResponse, Response response) {

                ApiBus.getInstance().post(new LoadTimelineSuccessEvent(timelineDataResponse));

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("error", error.toString());
            }

        });

    }

    @Subscribe public void onPostComment(PostCommentEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("timeline_id", event.getUserId());
        options.put("text", event.getPostText());

        api.postComment(Integer.parseInt(event.getPostId()), options, new Callback<PostCommentDataResponse>() {
            @Override
            public void success(PostCommentDataResponse postCommentDataResponse, Response response) {
                ApiBus.getInstance().post(new PostCommentSuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onPostShare(PostShareEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("timeline_id", event.getUserId());
        //options.put("text", event.getPostText());

        api.postShare(Integer.parseInt(event.getPostId()), options, new Callback<PostCommentDataResponse>() {
            @Override
            public void success(PostCommentDataResponse postCommentDataResponse, Response response) {
                ApiBus.getInstance().post(new PostShareSuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onPostLove(PostLoveEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("timeline_id", event.getUserId());
        //options.put("text", event.getPostText());

        api.postLove(Integer.parseInt(event.getPostId()), options, new Callback<PostCommentDataResponse>() {
            @Override
            public void success(PostCommentDataResponse postCommentDataResponse, Response response) {
                ApiBus.getInstance().post(new PostLoveSuccessEvent());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onFollowUser(FollowRegisterEvent event) {
        api.followUser(Integer.parseInt(event.getUserId()), new Callback<FollowUserResponse>() {
            @Override
            public void success(FollowUserResponse followUserResponse, Response response) {

                Log.v("FollowRegisterEvent", followUserResponse.message);

                if (followUserResponse.isFollowing)
                    ApiBus.getInstance().post(new FollowUserSuccessEvent(followUserResponse.userId));
                else
                    ApiBus.getInstance().post(new UnfollowUserSuccessEvent(followUserResponse.userId));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onGetUserProfile(GetUserProfileEvent event) {

        if(!event.getUserId().equals("")) {
            api.getProfile(Integer.parseInt(event.getUserId()), new Callback<UserProfileDataResponse>() {
                @Override
                public void success(UserProfileDataResponse userProfileDataResponse, Response response) {
                    GetUserProfileSuccessEvent event = new GetUserProfileSuccessEvent(userProfileDataResponse.user, userProfileDataResponse.count);
                    ApiBus.getInstance().post(event);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("error", error.toString());
                }

            });
        } else {
            api.getProfileUsername(event.getUsername(), new Callback<UserProfileDataResponse>() {
                @Override
                public void success(UserProfileDataResponse userProfileDataResponse, Response response) {
                    GetUserProfileSuccessEvent event = new GetUserProfileSuccessEvent(userProfileDataResponse.user, userProfileDataResponse.count);
                    ApiBus.getInstance().post(event);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("error", error.toString());
                }

            });
        }



    }

    @Subscribe public void onPhotoListRequestEvent(LoadPhotoListEvent event) {

        Map<String, String> options = new HashMap<String, String>();

        //api.vdomax.com/search/photo?sort=N&page=1&limit=50

        options.put("sort", event.getSortType());
        options.put("page", Integer.toString(event.getPage()));
        options.put("limit",Integer.toString(event.getPerPage()));

        api.getPhoto(options, new Callback<PhotoListDataResponse>() {
            @Override
            public void success(PhotoListDataResponse photoListDataResponse, Response response) {
                Log.e("photoListDataResponse", photoListDataResponse.status);
                if (photoListDataResponse.status.equals("1")) {
                    //Log.e("timelineDataResponse", response.getBody().toString());
                    if(photoListDataResponse != null)
                        ApiBus.getInstance().post(new LoadPhotoListSuccessEvent(photoListDataResponse, photoListDataResponse.sort));

                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onHomeTimelineRequestEvent(LoadTimelineEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        options.put("type", event.getType());
        options.put("page", Integer.toString(event.getPage()));
        options.put("per_page",Integer.toString(event.getPerPage()));

        if(event.getIsHome()) {
            api.getHomeTimeline(event.getUserId(),options,new Callback<TimelineDataResponse>() {
                @Override
                public void success(TimelineDataResponse timelineDataResponse, Response response) {
                    //Log.e("timelineDataResponse",timelineDataResponse.getStatus().toString());
                    //Log.e("posts",timelineDataResponse.getPosts().toArray().toString());
                    if(timelineDataResponse.getStatus().equals("1")) {
                        Log.e("timelineDataResponse",response.getBody().toString());
                        ApiBus.getInstance().post(new LoadTimelineSuccessEvent(timelineDataResponse));

                    } else {
                        //MainApplication.get(this).getPrefManager().isLogin().put(false);
                        //Log.e("LOGOUT!","LOG OUT LAEW");
                        ApiBus.getInstance().post(new LogoutEvent());
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("error",error.toString());
                }
            });
        } else {
            api.getUserTimeline(event.getUserId(),options,new Callback<TimelineDataResponse>() {
                @Override
                public void success(TimelineDataResponse timelineDataResponse, Response response) {
                    Log.e("timelineDataResponse",timelineDataResponse.getStatus().toString());
                    Log.e("posts", timelineDataResponse.getPosts().toArray().toString());
                    if(timelineDataResponse.getStatus().equals("1")) {
                        Log.e("timelineDataResponse",response.getBody().toString());

                        ApiBus.getInstance().post(new LoadTimelineSuccessEvent(timelineDataResponse));

                    } else {
                        //MainApplication.get(this).getPrefManager().isLogin().put(false);
                        Log.e("LOGOUT!","LOG OUT LAEW");
                        ApiBus.getInstance().post(new LogoutEvent());
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("error",error.toString());
                }
            });
        }


    }

    @Subscribe public void onLoadFriendListEvent(final LoadFriendListEvent event) {
        Map<String, String> options = new HashMap<String, String>();

        //options.put("type", event.getType());
        options.put("page", Integer.toString(event.getPage()));
        options.put("per_page",Integer.toString(event.getPerPage()));

        switch (event.getType()) {
            case "FOLLOWING":
                api.getFollowing(event.getUserId(), options, new Callback<FriendListDataResponse>() {
                    @Override
                    public void success(FriendListDataResponse friendListDataResponse, Response response) {
                        Log.e("friendListDataResponse", friendListDataResponse.status);
                        if (friendListDataResponse.status.equals("1")) {
                            //Log.e("timelineDataResponse", response.getBody().toString());
                            ApiBus.getInstance().post(new LoadFriendListSuccessEvent(friendListDataResponse, event.getType()));

                        } else {
                            //MainApplication.get(this).getPrefManager().isLogin().put(false);
                            Log.e("LOGOUT!", "LOG OUT LAEW");
                            ApiBus.getInstance().post(new LogoutEvent());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("error", error.toString());
                    }
                });
                break;
            case "FOLLOWER":
                api.getFollower(event.getUserId(), options, new Callback<FriendListDataResponse>() {
                    @Override
                    public void success(FriendListDataResponse friendListDataResponse, Response response) {
                        Log.e("friendListDataResponse", friendListDataResponse.status);
                        if (friendListDataResponse.status.equals("1")) {
                            //Log.e("timelineDataResponse", response.getBody().toString());
                            ApiBus.getInstance().post(new LoadFriendListSuccessEvent(friendListDataResponse, event.getType()));

                        } else {
                            //MainApplication.get(this).getPrefManager().isLogin().put(false);
                            Log.e("LOGOUT!", "LOG OUT LAEW");
                            ApiBus.getInstance().post(new LogoutEvent());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("error", error.toString());
                    }
                });
                break;
            case "FRIEND":
                api.getFriend(event.getUserId(), options, new Callback<FriendListDataResponse>() {
                    @Override
                    public void success(FriendListDataResponse friendListDataResponse, Response response) {
                        Log.e("friendListDataResponse", friendListDataResponse.status);
                        if (friendListDataResponse.status.equals("1")) {
                            //Log.e("timelineDataResponse", response.getBody().toString());
                            ApiBus.getInstance().post(new LoadFriendListSuccessEvent(friendListDataResponse, event.getType()));

                        } else {
                            //MainApplication.get(this).getPrefManager().isLogin().put(false);
                            Log.e("LOGOUT!", "LOG OUT LAEW");
                            ApiBus.getInstance().post(new LogoutEvent());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("error", error.toString());
                    }
                });
                break;
            case "PAGE":
                api.getPage(event.getUserId(), options, new Callback<FriendListDataResponse>() {
                    @Override
                    public void success(FriendListDataResponse friendListDataResponse, Response response) {
                        Log.e("friendListDataResponse", friendListDataResponse.status);
                        if (friendListDataResponse.status.equals("1")) {
                            //Log.e("timelineDataResponse", response.getBody().toString());
                            ApiBus.getInstance().post(new LoadFriendListSuccessEvent(friendListDataResponse, event.getType()));

                        } else {
                            //MainApplication.get(this).getPrefManager().isLogin().put(false);
                            Log.e("LOGOUT!", "LOG OUT LAEW");
                            ApiBus.getInstance().post(new LogoutEvent());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("error", error.toString());
                    }
                });
                break;

            // 1 -> 10 page 1
            // 11 -> 20 page 2

            default:
                options.put("sort",event.getType());

                api.getSocial( options, new Callback<FriendListDataResponse>() {
                    @Override
                    public void success(FriendListDataResponse friendListDataResponse, Response response) {
                        Log.e("friendListDataResponse", friendListDataResponse.status);
                        if (friendListDataResponse.status.equals("1")) {
                            //Log.e("timelineDataResponse", response.getBody().toString());
                            ApiBus.getInstance().post(new LoadFriendListSuccessEvent(friendListDataResponse, event.getType()));

                        } else {
                            //MainApplication.get(this).getPrefManager().isLogin().put(false);
                            Log.e("LOGOUT!", "LOG OUT LAEW");
                            ApiBus.getInstance().post(new LogoutEvent());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("error", error.toString());
                    }
                });
                break;

        }


    }




}
