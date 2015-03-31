package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/4/15.
 */
public class RequestOtpEvent {
    public String mobile;
    public String message;

    public RequestOtpEvent(String mobile, String message) {
        this.mobile = mobile;
        this.message = message;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }
}
