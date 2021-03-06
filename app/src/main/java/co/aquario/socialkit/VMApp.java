package co.aquario.socialkit;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.MenuItem;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AjaxStatus;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.squareup.okhttp.OkHttpClient;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import co.aquario.chatapp.handler.ChatApiHandler;
import co.aquario.chatapp.handler.ChatApiService;
import co.aquario.chatapp.handler.NotiApiHandler;
import co.aquario.chatapp.handler.NotiApiService;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.handler.ApiHandlerVM;
import co.aquario.socialkit.handler.ApiServiceVM;
import co.aquario.socialkit.handler.PostUploadHandler;
import co.aquario.socialkit.handler.PostUploadService;
import co.aquario.socialkit.push.PushManage;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.StorageUtils;
import io.fabric.sdk.android.Fabric;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@ReportsCrashes (
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://chat.vdomax.com:5984/acra-vm/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "user",
        formUriBasicAuthPassword = "12345"
)
public class VMApp extends Application implements ActivityLifecycleCallbacks {

    public static String VERSION;

    public static final String IMAGE_ENDPOINT = "https://www.vdomax.com/";
    public static final String ENDPOINT = "http://api.vdomax.com";
    public static final String NOTI_ENDPOINT = "http://chat.vdomax.com/noti";
    public static final String CHAT_ENDPOINT = "https://chat.vdomax.com:1314/api";
    public static final String CHAT_SERVER = "https://chat.vdomax.com:1314";
    
    public static final String APP_ID = "391414774312517";
    public static final String APP_SECRET ="f486294a7603127e78833e54f17cbc51";
    public static final String APP_NAMESPACE = "vdomaxsocial";
    public static final String APP_PERMISSIONS = "email,public_profile,user_friends";

    public static PrefManager mPref;
    public static String USER_TOKEN;


    private ApiHandlerVM loginApiHandler;
    private ChatApiHandler chatApiHandler;
    private NotiApiHandler notiApiHandler;
    private PostUploadHandler uploadApiHandler;
    private static OkHttpClient sHttpClient;

    private static FacebookHandle handle;
    private static Activity mFbHandleActivity;

    public static int notiBadge = 0;

    public static int getNotiBadge() {
        return notiBadge;
    }

    public static MenuItem badgeItem;

    public static void clearBadge(Context context,MenuItem item) {
        String userId = VMApp.mPref.userId().getOr("0");
        badgeItem = item;
        if(!userId.equals("0")) {
            long unixTime = System.currentTimeMillis() / 1000L;
            String url = "http://chat.vdomax.com/noti/index.php?a=readAll&user_id="+userId+"&timestamp="+unixTime;
            AQuery aq = new AQuery(context);
            HashMap<String,Object> params = new HashMap<>();

            aq.ajax(url, JSONObject.class, context,
                    "clearBadgeCb");
        }

    }

    public void clearBadgeCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        if (jo != null) {
            if(VMApp.getNotiBadge() > 0)
                ActionItemBadge.update(badgeItem, VMApp.getNotiBadge());
            else
                ActionItemBadge.hide(badgeItem);
        }
    }

    public static void fetchBadge(Context context) {
        String userId = VMApp.mPref.userId().getOr("0");
        if(!userId.equals("0")) {
            String url = "http://chat.vdomax.com/noti/index.php?a=badge&user_id=" + userId;
            AQuery aq = new AQuery(context);
            HashMap<String,Object> params = new HashMap<>();

            aq.ajax(url, JSONObject.class, context,
                    "updateBadgeCb");
        }

    }

    public void updateBadgeCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        if (jo != null) {
            notiBadge = jo.optJSONObject("count").optInt("noti_unread");
            ActionItemBadge.update(badgeItem, VMApp.getNotiBadge());
            //Utils.showToast("Badge:" + notiBadge);

        }
    }



            @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static VMApp get(Context context) {
        return (VMApp) context.getApplicationContext();
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
            File cacheLocation = new File(StorageUtils.getIdealCacheDirectory(VMApp.getAppContext()).toString());
            cacheLocation.mkdirs();
            com.squareup.okhttp.Cache cache = new com.squareup.okhttp.Cache(cacheLocation, cacheSize);
            sHttpClient.setCache(cache);
        }
        return sHttpClient;
    }


    public static FacebookHandle getFacebookHandle(Activity activity) {
        mFbHandleActivity = activity;
        handle = new FacebookHandle(activity, APP_ID, APP_PERMISSIONS);
        return handle;
    }

    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);


        //LeakCanary.install(this);
        sContext = this;

        PackageInfo pInfo;
        try {
            pInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
            VERSION = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Fabric.with(this, new Crashlytics());
        ParseCrashReporting.enable(this);
        ACRA.init(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);

        String applicationID = "j6DTfeUL6JvI9PunllRInpQbUg3dJLCVNTvaAOfY";
        String clientKey = "VLESF9CjbpiRJ97A1XVllHZuBgO0TJrRJyNA3OL8";

        Parse.initialize(this, applicationID, clientKey);
        PushService.setDefaultPushCallback(this,
                PushManage.class);
        PushService
                .subscribe(this, "EN", PushManage.class);

        saveInstallation(0);

        //APA91bHWnClTagZ9PD8sqM3Xf2EHb1Y14mPeeSMJV1YYcwCtpGxDQVPNBIe144KLRzIgS6LajHebBqRmnzPs4oeB2xM_feChMBPK72lf5HVCEZuC-yTAZfUgYyi7GHc4gfenwmd8x2fZ


        loginApiHandler = new ApiHandlerVM(this, buildLoginApi(),
                ApiBus.getInstance());
        loginApiHandler.registerForEvents();

        chatApiHandler = new ChatApiHandler(this, buildChatApi(),
                ApiBus.getInstance());
        chatApiHandler.registerForEvents();

        notiApiHandler = new NotiApiHandler(this, buildNotiApi(),
                ApiBus.getInstance());
        notiApiHandler.registerForEvents();

        uploadApiHandler = new PostUploadHandler(this, buildUploadApi(),
                ApiBus.getInstance());
        uploadApiHandler.registerForEvents();

        mPref = new PrefManager(getSharedPreferences("App", MODE_PRIVATE));
        mPref.isNoti().put(true).commit();


        // test crash
        //throw new RuntimeException("Test Exception!");

    }

    public static void saveInstallation(int userId) {
        final ParseInstallation installation = ParseInstallation
                .getCurrentInstallation();

        installation.put("user_id", userId);
        installation.saveInBackground(new SaveCallback() {
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    System.out.println("ok");
                    //deviceToken = installation.get("deviceToken").toString();
                    //System.out.println(deviceToken);
                } else {
                    System.out.println("not ok" + e.getLocalizedMessage());
                }
            }
        });
    }

    public static void removeInstallation(int userId) {
        final ParseInstallation installation = ParseInstallation
                .getCurrentInstallation();

        installation.deleteInBackground();
    }

    public PrefManager getPrefManager() {
        return mPref;
    }

    public static void logout(Context context) {
        mPref.isLogin().put(false).commit();
        mPref.clear().commit();
        boolean isLogin = mPref.isLogin().getOr(false);
        PushService
                .unsubscribe(getAppContext(), "EN");
        VMApp.removeInstallation(Integer.parseInt(mPref.userId().getOr("0")));
        if (mFbHandleActivity != null)
            getFacebookHandle(mFbHandleActivity).unauth();
//        ParsePush.unsubscribeInBackground("EN");
        Log.e("isLogin",":::"+isLogin);
    }

//    public static void updateParseInstallation(int userId) {
//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        //installation.put("badge", userId);
//        installation.put("user_id", userId);
//        installation.saveInBackground();
//    }

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
                .setLogLevel(RestAdapter.LogLevel.FULL)
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

    NotiApiService buildNotiApi() {
        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(NOTI_ENDPOINT)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })

                .build()
                .create(NotiApiService.class);
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
                        request.addHeader("X-Auth-Token", mPref.token().getOr(""));
                        if(!mPref.token().getOr("").equals("")){
                            //request.addHeader("X-Auth-Token",mPref.token().getOr(""));
                        }
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(ApiServiceVM.class);
    }

    PostUploadService buildUploadApi() {
        String BASE_URL = "https://www.vdomax.com";

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })
                .build()
                .create(PostUploadService.class);
    }



    public static boolean applicationOnPause = false;
    public static Activity currentActivity;

    @Override
    public void onActivityCreated(Activity arg0, Bundle arg1) {
        currentActivity = arg0;
        Log.e("VMVMVM","onActivityCreated");

    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.e("VMVMVM","onActivityDestroyed ");

    }
    @Override
    public void onActivityPaused(Activity activity) {
        applicationOnPause = true;
        Log.e("VMVMVM","onActivityPaused "+activity.getClass());

    }
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        applicationOnPause = false;
        Log.e("VMVMVM","onActivityResumed "+activity.getClass());

    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.e("VMVMVM","onActivitySaveInstanceState");

    }
    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
        Log.e("VMVMVM","onActivityStarted");

    }
    @Override
    public void onActivityStopped(Activity activity) {
        Log.e("VMVMVM","onActivityStopped");

    }
}
