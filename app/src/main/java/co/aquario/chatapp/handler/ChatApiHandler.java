package co.aquario.chatapp.handler;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.chatapp.event.request.BlockUserEvent;
import co.aquario.chatapp.event.request.ConversationGroupEvent;
import co.aquario.chatapp.event.request.ConversationOneToOneEvent;
import co.aquario.chatapp.event.request.GetChatInfoEvent;
import co.aquario.chatapp.event.request.HistoryEvent;
import co.aquario.chatapp.event.request.SearchUserEvent;
import co.aquario.chatapp.event.request.SomeEvent;
import co.aquario.chatapp.event.response.ChatInfo;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.FailedEvent;
import co.aquario.chatapp.event.response.GetChatInfoSuccess;
import co.aquario.chatapp.event.response.HistoryDataResponse;
import co.aquario.chatapp.event.response.HistoryEventSuccess;
import co.aquario.chatapp.event.response.SuccessEvent;
import co.aquario.chatapp.model.SomeData;
import co.aquario.chatapp.model.conversation.ConversationId;
import co.aquario.chatui.event_chat.response.SearchUserEventSuccess;
import co.aquario.chatui.event_chat.response.SearchUserNotFoundEvent;
import co.aquario.chatui.model.Block;
import co.aquario.chatui.model.FindFriends;
import co.aquario.socialkit.handler.ApiBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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


        api.getRandomImage2(options, new Callback<SomeData>() {
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

        Log.i("getHistory", ":" + event.cid);

        api.getHistory(event.cid, opt, new Callback<HistoryDataResponse>() {
            @Override
            public void success(HistoryDataResponse historyDataResponse, Response response) {
                //if(historyDataResponse.content != null)
                Log.i("historyContent", historyDataResponse.content.toString());
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
        opt.put("partnerId", event.partnerId);

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

        Log.e("event.liveUserId", event.liveUserId + "");

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

    @Subscribe public void onBlockUser(BlockUserEvent event) {
        api.getฺBlock("", new Callback<Block>() {
            @Override
            public void success(Block block, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void findFriend(SearchUserEvent event) {
        Map<String, String> options = new HashMap<String, String>();
        options.put("searchTerm", event.username);
        options.put("userId", event.userId);

        api.getFindFriends(options, new Callback<FindFriends>() {
            @Override
            public void success(FindFriends user, Response response) {
                if (user != null) ApiBus.getInstance().postQueue(new SearchUserEventSuccess(user));
                else ApiBus.getInstance().postQueue(new SearchUserNotFoundEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                ApiBus.getInstance().postQueue(new SearchUserNotFoundEvent());
            }
        });
    }

    @Subscribe public void getChatInfo(GetChatInfoEvent event) {
        api.getChatById(event.cid, new Callback<ChatInfo>() {
            @Override
            public void success(ChatInfo chatInfo, Response response) {
                if(chatInfo.getConversationMembers().size() != 0)
                    Log.e("conversationmembers",chatInfo.getConversationMembers().size()+ "");
                ApiBus.getInstance().postQueue(new GetChatInfoSuccess(chatInfo));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


}
