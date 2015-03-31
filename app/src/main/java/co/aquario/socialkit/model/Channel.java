package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by root1 on 2/6/15.
 */
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
    @SerializedName("live_cover")
    @Expose
    public String liveCover;
    @Expose
    public String gender;
    @Expose
    public boolean online;

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
        return EndpointManager.getPath(avatar);
    }

    public String getCoverUrl() {
        return EndpointManager.getPath(cover);
    }
}
