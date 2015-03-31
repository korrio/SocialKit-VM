package co.aquario.socialkit.event;

import com.google.gson.annotations.Expose;

import co.aquario.socialkit.model.BadgeCount;
import co.aquario.socialkit.model.User;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class UserProfileDataResponse {

    @Expose
    public String status;
    @Expose
    public User user;
    @Expose
    public BadgeCount count;

}
