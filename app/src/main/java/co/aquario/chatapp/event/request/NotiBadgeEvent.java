package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 7/19/15.
 */
public class NotiBadgeEvent {
    public String method;
    public String userId;

    public NotiBadgeEvent(String userId) {
        this.method = "badge";
        this.userId = userId;
    }
}
