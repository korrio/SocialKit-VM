package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostStory extends BaseModel {

    @Expose
    public String id;
    @Expose
    public String active;
    @Expose
    public Author author;
    @Expose
    public String google_map_name;
    @SerializedName("post_id")
    @Expose
    public String postId;
    @Expose
    public String recipient_id;
    @Expose
    public String seen;
    @Expose
    public String text;
    @Expose
    public String time;
    @Expose
    public String timeline_id;
    @Expose
    public String timestamp;
    @Expose
    public String type1;
    @Expose
    public String type2;
    @Expose
    public String view;
    @SerializedName("follow_count")
    @Expose
    public int followCount;
    @Expose
    public List<Follow> follow;
    @Expose
    public int loveCount;
    @Expose
    public List<Love> love;
    @SerializedName("comment_count")
    @Expose
    public int commentCount;
    @Expose
    public ArrayList<CommentStory> comment;
    @SerializedName("share_count")
    @Expose
    public int shareCount;
    @Expose
    public List<Share> share;
    @SerializedName("post_type")
    @Expose
    public String type;
    @Expose
    public Media media;
    @Expose
    public Clip clip;
    @SerializedName("soundcloud")
    @Expose
    public SoundCloud soundCloud;
    @Expose
    public Youtube youtube;
    @Expose
    @SerializedName("is_loved")
    public boolean isLoved;
    @Expose
    @SerializedName("is_commented")
    public boolean isCommented;
    @Expose
    @SerializedName("is_shared")
    public boolean isShared;

    public String getId() {
        return id;
    }

    public String getAgoText() {
        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(time);
        Date timeAgo = new java.util.Date(agoLong * 1000);
        return p.format(timeAgo);
    }




}
