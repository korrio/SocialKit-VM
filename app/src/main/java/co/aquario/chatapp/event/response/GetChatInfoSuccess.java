package co.aquario.chatapp.event.response;

/**
 * Created by Mac on 8/3/15.
 */
public class GetChatInfoSuccess {
    public ChatInfo info;

    public GetChatInfoSuccess(ChatInfo info) {
        this.info = info;
    }
}
