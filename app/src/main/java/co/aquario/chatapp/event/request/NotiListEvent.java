package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 7/19/15.
 */
public class NotiListEvent {

    public String method;
    public int userId;
    public int page;

    public NotiListEvent(int userId, int page) {
        this.method = "list";
        this.userId = userId;
        this.page = page;
    }
}
