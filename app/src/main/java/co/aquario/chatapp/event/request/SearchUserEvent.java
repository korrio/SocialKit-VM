package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 7/28/15.
 */
public class SearchUserEvent {
    public String username;
    public String userId;

    public SearchUserEvent(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }
}
