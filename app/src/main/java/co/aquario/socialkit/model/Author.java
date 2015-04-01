package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by root1 on 2/22/15.
 */
public class Author {

    @Expose
    public String id;
    @Expose
    public String username;
    @Expose
    public String name;
    @Expose
    @SerializedName("avatar")
    public String avatarPath;
    @Expose
    @SerializedName("cover")
    public String coverPath;
    @Expose
    @SerializedName("live_cover")
    public String liveCover;
    @Expose
    @SerializedName("live_url")
    public String liveUrl;

    public String getAvatarPath() {
        return EndpointManager.getPath(avatarPath);
    }
    public String getCoverPath() {
        return EndpointManager.getPath(coverPath);
    }

}
