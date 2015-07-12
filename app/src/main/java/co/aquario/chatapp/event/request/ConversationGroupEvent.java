package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 7/12/15.
 */
public class ConversationGroupEvent {
    public int liveUserId;

    public ConversationGroupEvent(int liveUserId) {
        this.liveUserId = liveUserId;
    }
}
