package co.aquario.socialkit;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.handler.ApiHandlerVM;
import co.aquario.socialkit.handler.ApiServiceVM;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.PrefManager;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Mac on 3/2/15.
 */
public class MainApplication extends Application {

    //private static final String ENDPOINT = "http://wallsplash.lanora.io";

    private static final String ENDPOINT = "http://api.vdomax.com";
    
    public static final String APP_ID = "391414774312517";
    public static final String APP_SECRET ="f486294a7603127e78833e54f17cbc51";
    public static final String APP_NAMESPACE = "vdomaxsocial";
    public static final String APP_PERMISSIONS = "read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_about_me,friends_about_me,user_activities,friends_activities,user_checkins,friends_checkins,user_events,friends_events,user_groups,friends_groups,user_interests,friends_interests,user_likes,friends_likes,user_notes,friends_notes,user_photos,friends_photos,user_status,friends_status,user_videos,friends_videos";

    public static PrefManager prefManager;
    public static String USER_TOKEN;
    public static UserProfile user;

    private ApiHandlerVM loginApiHandler;

    public static MainApplication get(Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    private static Context sContext = null;

    public static Context getAppContext() {
        return sContext;
    }

    public static String appName() {
        return getAppContext().getString(R.string.app_name);
    }

    @Override public void onCreate() {
        super.onCreate();
        sContext = this;

        loginApiHandler = new ApiHandlerVM(this, buildLoginApi(),
                ApiBus.getInstance());
        loginApiHandler.registerForEvents();

        prefManager = new PrefManager(getSharedPreferences("App", MODE_PRIVATE));
    }

    public PrefManager getPrefManager() {
        return prefManager;
    }

    public static void logout() {
        prefManager.isLogin().put(false).commit();
        prefManager.clear().commit();
        boolean isLogin = prefManager.isLogin().getOr(false);
        Log.e("isLogin",":::"+isLogin);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }




    ApiServiceVM buildLoginApi() {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    private DateFormat format = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss Z");

                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        String s = json.getAsJsonPrimitive().getAsString();
                        try {
                            return format.parse(s);
                        } catch (ParseException ignore) {
                        }

                        return null;
                    }
                })
                .create();

        Log.e("HEY!","called after post");

        return new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json;versions=1");
                        request.addHeader("X-Auth-Token",prefManager.token().getOr(""));
                        if(!prefManager.token().getOr("").equals("")){
                            //request.addHeader("X-Auth-Token",prefManager.token().getOr(""));
                        }
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(ApiServiceVM.class);
    }

    /*
    ApiService buildRandomUnsplashImageApi() {

        Log.e("HEY!","after post");

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ENDPOINT)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })

                .build()
                .create(ApiService.class);
    }
    */



}
