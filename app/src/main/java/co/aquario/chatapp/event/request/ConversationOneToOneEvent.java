package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 3/2/15.
 */
public class ConversationOneToOneEvent {
    public int userId;
    public int partnerId;

    public ConversationOneToOneEvent(int userId, int partnerId) {
        this.userId = userId;
        this.partnerId = partnerId;
    }

}
