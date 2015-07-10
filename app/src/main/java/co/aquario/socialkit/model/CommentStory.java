package co.aquario.socialkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mac on 3/10/15.
 */
public class CommentStory implements Parcelable {
    /*
    @Expose
    public String id;
    @Expose
    public String text;
    @Expose
    public User user;
    @Expose
    public String timestamp;
    @Expose
    @SerializedName("love_count")
    public int loveCount;
    */

    private static final String FIELD_ID = "id";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_EMOTICONIZED_TEXT = "emoticonized";
    private static final String FIELD_USER = "user";
    private static final String FIELD_LOVE = "love";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_LOVE_COUNT = "love_count";


    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_TEXT)
    private String mText;
    @SerializedName(FIELD_USER)
    private User mUser;
    @SerializedName(FIELD_LOVE)
    private List<Love> mLoves;
    @SerializedName(FIELD_TIMESTAMP)
    private String mTimestamp;
    @SerializedName(FIELD_LOVE_COUNT)
    private int mLoveCount;
    @SerializedName(FIELD_EMOTICONIZED_TEXT)
    private String mEmoticonizedText;


    public String getmEmoticonizedText() {
        return mEmoticonizedText;
    }

    public void setmEmoticonizedText(String mEmoticonizedText) {
        this.mEmoticonizedText = mEmoticonizedText;
    }

    public CommentStory(){

    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    public void setLoves(List<Love> loves) {
        mLoves = loves;
    }

    public List<Love> getLoves() {
        return mLoves;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setLoveCount(int loveCount) {
        mLoveCount = loveCount;
    }

    public int getLoveCount() {
        return mLoveCount;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Comment){
            return ((CommentStory) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public CommentStory(Parcel in) {
        mId = in.readLong();
        mText = in.readString();
        mEmoticonizedText = in.readString();
        mUser = in.readParcelable(User.class.getClassLoader());
        mLoves = new ArrayList<Love>();
        in.readTypedList(mLoves, Love.CREATOR);
        mTimestamp = in.readString();
        mLoveCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CommentStory> CREATOR = new Parcelable.Creator<CommentStory>() {
        public CommentStory createFromParcel(Parcel in) {
            return new CommentStory(in);
        }

        public CommentStory[] newArray(int size) {
            return new CommentStory[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mText);
        dest.writeString(mEmoticonizedText);
        //dest.writeParcelable(mUser, flags);
        dest.writeTypedList(mLoves);
        dest.writeString(mTimestamp);
        dest.writeInt(mLoveCount);
    }

    @Override
    public String toString(){
        return "id = " + mId + ", text = " + mText + ", user = " + mUser + ", loves = " + mLoves + ", timestamp = " + mTimestamp + ", loveCount = " + mLoveCount;
    }
}
