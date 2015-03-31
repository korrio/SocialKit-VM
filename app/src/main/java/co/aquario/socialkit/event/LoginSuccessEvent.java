package co.aquario.socialkit.event;

import co.aquario.socialkit.model.LoginData;

/**
 * Created by Mac on 3/2/15.
 */
public class LoginSuccessEvent {
    private LoginData loginData;

    public LoginSuccessEvent(LoginData loginData) {
        this.loginData = loginData;
    }

    public LoginData getLoginData() {
        return loginData;
    }
}
