package co.aquario.chatapp.handler;

import android.content.Context;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.chatapp.event.request.NotiBadgeEvent;
import co.aquario.chatapp.event.request.NotiListEvent;
import co.aquario.chatapp.event.request.NotiReadEvent;
import co.aquario.chatapp.event.response.FailedEvent;
import co.aquario.chatapp.event.response.NotiBadgeDataResponse;
import co.aquario.chatapp.event.response.NotiBadgeEventSuccess;
import co.aquario.chatapp.event.response.NotiListDataResponse;
import co.aquario.chatapp.event.response.NotiListEventSuccess;
import co.aquario.socialkit.handler.ApiBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class NotiApiHandler {

    private Context context;
    private NotiApiService api;
    private ApiBus apiBus;

    public NotiApiHandler(Context context, NotiApiService api,
                          ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onGetNotiList(NotiListEvent event) {

        Map<String, String> options = new HashMap<String, String>();
        options.put("a", event.method);
        options.put("user_id", event.userId + "");
        options.put("page", event.page + "");

        api.getNotiList(options, new Callback<NotiListDataResponse>() {
            @Override
            public void success(NotiListDataResponse data, Response response) {
                ApiBus.getInstance().post(new NotiListEventSuccess(data));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(new FailedEvent());
            }
        });
    }

    @Subscribe public void onReadNoti(NotiReadEvent event) {
        Map<String, String> options = new HashMap<String, String>();
        options.put("a", event.method);
        options.put("noti_id", event.notiId + "");

        api.readNoti(options);
    }

    @Subscribe public void onGetBadge(NotiBadgeEvent event) {
        Map<String, String> options = new HashMap<String, String>();
        options.put("a", event.method);
        options.put("user_id", event.userId + "");

        api.getNotiBadge(options, new Callback<NotiBadgeDataResponse>() {
            @Override
            public void success(NotiBadgeDataResponse data, Response response) {
                ApiBus.getInstance().post(new NotiBadgeEventSuccess(data));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(new FailedEvent());
            }
        });
    }




}
