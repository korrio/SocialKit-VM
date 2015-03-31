package co.aquario.socialkit.event;

import co.aquario.socialkit.model.UserProfile;

/**
 * Created by Mac on 3/3/15.
 */
public class UpdateProfileEvent {

    private UserProfile user;

    public UpdateProfileEvent(UserProfile user) {
        this.user = user;
    }

    public UserProfile getUserProfile() {
        return user;
    }


}
