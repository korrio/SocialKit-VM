package co.aquario.chatapp.event.response;

import co.aquario.chatapp.model.conversation.RecentChatResponse;

/**
 * Created by Mac on 8/6/15.
 */
public class GetRecentChatSuccess {
    public RecentChatResponse response;

    public GetRecentChatSuccess(RecentChatResponse response) {
        this.response = response;
    }
}
