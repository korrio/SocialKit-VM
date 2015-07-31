package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Mac on 3/24/15.
 */
@Parcel
public class BadgeCount {
    @Expose
    public int post;
    @Expose
    public int follower;
    @Expose
    public int following;
    @Expose
    public int friend;
    @Expose
    public int love;
    @Expose
    public  int group;

    @ParcelConstructor
    public BadgeCount(int post, int follower, int following, int friend, int love, int group) {
        this.post = post;
        this.follower = follower;
        this.following = following;
        this.friend = friend;
        this.love = love;
        this.group = group;
    }
}
