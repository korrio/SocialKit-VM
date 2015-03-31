package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/4/15.
 */
public class FbAuthEvent {
    private String fbToken;

    public FbAuthEvent(String fbToken) {
        this.fbToken = fbToken;
    }

    public String getFbToken() {
        return fbToken;
    }
}
