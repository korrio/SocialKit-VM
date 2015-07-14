package co.aquario.socialkit;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.aquario.chatapp.handler.ChatApiHandler;
import co.aquario.chatapp.handler.ChatApiService;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.handler.ApiHandlerVM;
import co.aquario.socialkit.handler.ApiServiceVM;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.StorageUtils;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


public class VMApplication extends Application {

    public static final String ENDPOINT = "http://api.vdomax.com";
    public static final String CHAT_ENDPOINT = "https://chat.vdomax.com:1314/api";
    public static final String CHAT_SERVER = "https://chat.vdomax.com:1314";
    
    public static final String APP_ID = "391414774312517";
    public static final String APP_SECRET ="f486294a7603127e78833e54f17cbc51";
    public static final String APP_NAMESPACE = "vdomaxsocial";
    public static final String APP_PERMISSIONS = "email,public_profile,user_friends";

    public static PrefManager prefManager;
    public static String USER_TOKEN;


    private ApiHandlerVM loginApiHandler;
    private ChatApiHandler chatApiHandler;
    private static OkHttpClient sHttpClient;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static VMApplication get(Context context) {
        return (VMApplication) context.getApplicationContext();
    }

    private static Context sContext = null;

    public static Context getAppContext() {
        return sContext;
    }

    public static String appName() {
        return getAppContext().getString(R.string.app_name);
    }

    public static OkHttpClient getHttpClient() {
        if (sHttpClient == null) {
            sHttpClient = new OkHttpClient();

            int cacheSize = 10 * 1024 * 1024;
            File cacheLocation = new File(StorageUtils.getIdealCacheDirectory(VMApplication.getAppContext()).toString());
            cacheLocation.mkdirs();
            com.squareup.okhttp.Cache cache = new com.squareup.okhttp.Cache(cacheLocation, cacheSize);
            sHttpClient.setCache(cache);
        }
        return sHttpClient;
    }



    @Override public void onCreate() {
        super.onCreate();
        //LeakCanary.install(this);
        sContext = this;

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);


        //PushService.startServiceIfRequired(getApplicationContext());
        //PushService.setDefaultPushCallback(this, ManagePush.class);

        ParsePush.subscribeInBackground("EN", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }

        });
        ParseFacebookUtils.initialize(this);



        loginApiHandler = new ApiHandlerVM(this, buildLoginApi(),
                ApiBus.getInstance());
        loginApiHandler.registerForEvents();

        chatApiHandler = new ChatApiHandler(this, buildChatApi(),
                ApiBus.getInstance());
        chatApiHandler.registerForEvents();

        prefManager = new PrefManager(getSharedPreferences("App", MODE_PRIVATE));


    }

    public PrefManager getPrefManager() {
        return prefManager;
    }

    public static void logout() {
        prefManager.isLogin().put(false).commit();
        prefManager.clear().commit();
        boolean isLogin = prefManager.isLogin().getOr(false);
        ParsePush.unsubscribeInBackground("EN");
        Log.e("isLogin",":::"+isLogin);
    }

    public static void updateParseInstallation(int userId) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", userId);
        installation.saveInBackground();
    }

    //@Override
    //public void onLowMemory() {
        //super.onLowMemory();
    //}

    //@Override
    //public void onTerminate() {
        //super.onTerminate();
    //}

    ChatApiService buildChatApi() {
        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(CHAT_ENDPOINT)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })

                .build()
                .create(ChatApiService.class);
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

}
