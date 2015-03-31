package co.aquario.socialkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.services.youtube.YouTube;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostStory extends BaseModel implements Parcelable{

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

    public String getId() {
        return id;
    }

    public String getAgoText() {
        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(time);
        Date timeAgo = new java.util.Date(agoLong * 1000);
        return p.format(timeAgo);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof PostStory){
            return ((PostStory) obj).getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt(id);
    }

    public PostStory(Parcel in) {

        id = in.readString();
        active = in.readString();
        author = (Author) in.readParcelable(Author.class.getClassLoader());

        google_map_name = in.readString();
        postId = in.readString();
        recipient_id = in.readString();
        seen = in.readString();

        text = in.readString();
        time = in.readString();
        timeline_id = in.readString();
        timestamp = in.readString();
        type1 = in.readString();
        type2 = in.readString();
        view = in.readString();
        followCount = in.readInt();
        loveCount = in.readInt();
        commentCount = in.readInt();
        shareCount = in.readInt();

        in.readList(follow, Follow.class.getClassLoader());
        in.readList(comment, Comment.class.getClassLoader());
        in.readList(share, Share.class.getClassLoader());

        type = in.readString();
        media = (Media) in.readParcelable(Media.class.getClassLoader());
        clip = (Clip) in.readParcelable(Clip.class.getClassLoader());
        soundCloud = (SoundCloud) in.readParcelable(SoundCloud.class.getClassLoader());
        youtube = (Youtube) in.readParcelable(YouTube.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<PostStory> CREATOR = new Parcelable.Creator<PostStory>() {
        public PostStory createFromParcel(Parcel in) {
            return new PostStory(in);
        }

        public PostStory[] newArray(int size) {
            return new PostStory[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {


    }


}
