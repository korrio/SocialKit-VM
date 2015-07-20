package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CommentStory {

    @Expose
    public String id;
    @Expose
    public String text;
    @Expose
    public User user;
    @Expose
    public String time;
    @Expose
    @SerializedName("love_count")
    public int loveCount;
    @Expose
    public String emoticonized;



}
