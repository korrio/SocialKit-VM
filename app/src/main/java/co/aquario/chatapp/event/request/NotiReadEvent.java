package co.aquario.chatapp.event.request;

/**
 * Created by Mac on 7/19/15.
 */
public class NotiReadEvent {

    public String method;
    public int notiId;

    public NotiReadEvent(int notiId) {
        this.method = "read";
        this.notiId = notiId;
    }
}
