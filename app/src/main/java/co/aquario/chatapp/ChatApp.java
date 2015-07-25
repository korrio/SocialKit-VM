package co.aquario.chatapp;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.PushService;

import co.aquario.chatapp.handler.ChatApiHandler;
import co.aquario.chatapp.handler.ChatApiService;
import co.aquario.chatapp.push.PushManage;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.handler.ApiBus;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


public class ChatApp extends Application {
    private static final String CHAT_ENDPOINT = "https://chat.vdomax.com:1314/api";
    public static String USER_TOKEN;

    //private static final String ENDPOINT = "http://wallsplash.lanora.io";
    private ChatApiHandler chatApiHandler;
    public static PrefManager prefManager;

    public static ChatApp get(Context context) {
        return (ChatApp) context.getApplicationContext();
    }

    public PrefManager getPrefManager() {
        return prefManager;
    }

    @Override public void onCreate() {
        super.onCreate();
        chatApiHandler = new ChatApiHandler(this, buildApi(),
                ApiBus.getInstance());
        chatApiHandler.registerForEvents();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        PushService.startServiceIfRequired(getApplicationContext());
        PushService.setDefaultPushCallback(this, PushManage.class);

        ParseFacebookUtils.initialize(this);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        prefManager = new PrefManager(getSharedPreferences("App", MODE_PRIVATE));
    }

    ChatApiService buildApi() {
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
}
