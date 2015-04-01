package co.aquario.socialkit.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 3/28/15.
 */
public class FollowUserResponse {

    @Expose
    public String status;
    @Expose
    public String message;
    @Expose
    @SerializedName("user_id")
    public String userId;
    @Expose
    @SerializedName("is_following")
    public boolean isFollowing;

}
