package co.aquario.socialkit.model.video;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;


public class Comment implements Parcelable{

    private static final String FIELD_ID = "id";
    private static final String FIELD_TIMELINE_ID = "timeline_id";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_PUBLISHER_BOX = "publisher_box";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_POST_ID = "post_id";
    private static final String FIELD_TYPE1 = "type1";
    private static final String FIELD_TYPE2 = "type2";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_PUBLISHER = "publisher";


    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_TIMELINE_ID)
    private int mTimelineId;
    @SerializedName(FIELD_TEXT)
    private String mText;
    @SerializedName(FIELD_PUBLISHER_BOX)
    private boolean mPublisherBox;
    @SerializedName(FIELD_ACTIVE)
    private int mActive;
    @SerializedName(FIELD_POST_ID)
    private int mPostId;
    @SerializedName(FIELD_TYPE1)
    private String mType1;
    @SerializedName(FIELD_TYPE2)
    private String mType2;
    @SerializedName(FIELD_TIME)
    private long mTime;
    @SerializedName(FIELD_PUBLISHER)
    private Publisher mPublisher;


    public Comment(){

    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setTimelineId(int timelineId) {
        mTimelineId = timelineId;
    }

    public int getTimelineId() {
        return mTimelineId;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setPublisherBox(boolean publisherBox) {
        mPublisherBox = publisherBox;
    }

    public boolean isPublisherBox() {
        return mPublisherBox;
    }

    public void setActive(int active) {
        mActive = active;
    }

    public int getActive() {
        return mActive;
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public int getPostId() {
        return mPostId;
    }

    public void setType1(String type1) {
        mType1 = type1;
    }

    public String getType1() {
        return mType1;
    }

    public void setType2(String type2) {
        mType2 = type2;
    }

    public String getType2() {
        return mType2;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getTime() {
        return mTime;
    }

    public void setPublisher(Publisher publisher) {
        mPublisher = publisher;
    }

    public Publisher getPublisher() {
        return mPublisher;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Comment){
            return ((Comment) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public Comment(Parcel in) {
        mId = in.readLong();
        mTimelineId = in.readInt();
        mText = in.readString();
        mPublisherBox = in.readInt() == 1 ? true: false;
        mActive = in.readInt();
        mPostId = in.readInt();
        mType1 = in.readString();
        mType2 = in.readString();
        mTime = in.readLong();
        mPublisher = in.readParcelable(Publisher.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
        return new Comment[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeInt(mTimelineId);
        dest.writeString(mText);
        dest.writeInt(mPublisherBox ? 1 : 0);
        dest.writeInt(mActive);
        dest.writeInt(mPostId);
        dest.writeString(mType1);
        dest.writeString(mType2);
        dest.writeLong(mTime);
        dest.writeParcelable(mPublisher, flags);
    }

    @Override
    public String toString(){
        return "id = " + mId + ", timelineId = " + mTimelineId + ", text = " + mText + ", publisherBox = " + mPublisherBox + ", active = " + mActive + ", postId = " + mPostId + ", type1 = " + mType1 + ", type2 = " + mType2 + ", time = " + mTime + ", publisher = " + mPublisher;
    }


}