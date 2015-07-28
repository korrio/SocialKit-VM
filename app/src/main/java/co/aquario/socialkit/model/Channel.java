package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by root1 on 2/6/15.
 */
@Parcel
public class Channel extends BaseModel {

    @Expose
    public String id;
    @Expose
    public String name;
    @Expose
    public String username;
    @SerializedName("cover_url")
    @Expose
    public String cover;
    @SerializedName("avatar_url")
    @Expose
    public String avatar;

    @Expose
    @SerializedName("is_following")
    public boolean isFollowing = false;

    @Expose
    @SerializedName("total_follower")
    public String totalFollower;
    @Expose
    @SerializedName("live_cover")
    public String liveCover;
    @Expose
    public String gender;
    @Expose
    public boolean online;

    @ParcelConstructor
    public Channel(String id, String name, String username, String cover, String avatar, String liveCover, String gender, boolean online) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.cover = cover;
        this.avatar = avatar;
        this.liveCover = liveCover;
        this.gender = gender;
        this.online = online;
    }

    public String getAvatarUrl() {
        return EndpointManager.getAvatarPath(avatar);
    }

    public String getCoverUrl() {
        return EndpointManager.getAvatarPath(cover);
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}
