package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class RegisterFailedEvent {
    public String msg;

    public RegisterFailedEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
