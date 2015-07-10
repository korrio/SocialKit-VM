package co.aquario.socialkit.handler;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.event.SomeEvent;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ApiHandler {

    private Context context;
    private ApiService api;
    private ApiBus apiBus;

    public ApiHandler(Context context, ApiService api,
                      ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onSomeEvent(SomeEvent event) {
        Log.e("HEY2!","SomeEvent");

        Map<String, String> options = new HashMap<String, String>();
        options.put("key1", event.getVar1());
        options.put("key2", Integer.toString(event.getVar2()));

    /*
        api.getRandomImage2(options,toolbar Callback<SomeData>() {
            @Override
            public void success(SomeData randomImage, Response response) {
                apiBus.post(toolbar SuccessEvent(randomImage));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(toolbar FailedEvent());
            }
        });
        */
    }
}
