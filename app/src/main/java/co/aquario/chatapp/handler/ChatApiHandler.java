package co.aquario.chatapp.handler;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.chatapp.event.request.ConversationGroupEvent;
import co.aquario.chatapp.event.request.ConversationOneToOneEvent;
import co.aquario.chatapp.event.request.HistoryEvent;
import co.aquario.chatapp.event.request.SomeEvent;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.FailedEvent;
import co.aquario.chatapp.event.response.HistoryDataResponse;
import co.aquario.chatapp.event.response.HistoryEventSuccess;
import co.aquario.chatapp.event.response.SuccessEvent;
import co.aquario.chatapp.model.SomeData;
import co.aquario.chatapp.model.conversation.ConversationId;
import co.aquario.socialkit.handler.ApiBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class ChatApiHandler {

    private Context context;
    private ChatApiService api;
    private ApiBus apiBus;

    public ChatApiHandler(Context context, ChatApiService api,
                          ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onSomeEvent(SomeEvent event) {
        Log.e("HEY2!", "SomeEvent");

        Map<String, String> options = new HashMap<String, String>();
        options.put("key1", event.getVar1());
        options.put("key2", Integer.toString(event.getVar2()));


        api.getRandomImage2(options,new Callback<SomeData>() {
            @Override
            public void success(SomeData randomImage, Response response) {
                ApiBus.getInstance().post(new SuccessEvent(randomImage));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(new FailedEvent());
            }
        });
    }

    @Subscribe public void onGetHistory(HistoryEvent event) {
        Map<String,Integer> opt = new HashMap<>();
        opt.put("page",event.page);
        opt.put("size",event.size);

        api.getHistory(event.cid,opt, new Callback<HistoryDataResponse>() {
            @Override
            public void success(HistoryDataResponse historyDataResponse, Response response) {
                //if(historyDataResponse.content != null)
                ApiBus.getInstance().post(new HistoryEventSuccess(historyDataResponse.content));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onGetConversation(ConversationOneToOneEvent event) {

        Map<String,Integer> opt = new HashMap<>();
        opt.put("userId",event.userId);
        opt.put("partnerId",event.partnerId);

        String contentType = "application/json";


        api.getConversation(event.userId, event.partnerId, new Callback<ConversationId>() {
            @Override
            public void success(ConversationId conversationId, Response response) {
                Log.e("conversationOneToOne", conversationId.id + "");
                ApiBus.getInstance().post(new ConversationEventSuccess(conversationId.id));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    @Subscribe public void onGetConversationGroup(ConversationGroupEvent event) {
        api.getConversationGroupPublic(event.liveUserId, new Callback<ConversationId>() {
            @Override
            public void success(ConversationId conversationId, Response response) {
                Log.e("conversationGroupPublic", conversationId.id + "");
                ApiBus.getInstance().post(new ConversationEventSuccess(conversationId.id));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}
