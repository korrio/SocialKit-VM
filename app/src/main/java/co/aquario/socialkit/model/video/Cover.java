package co.aquario.socialkit.model.video;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;


public class Cover implements Parcelable{

    private static final String FIELD_EXTENSION = "extension";
    private static final String FIELD_TEMP = "temp";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ID = "id";
    private static final String FIELD_TIMELINE_ID = "timeline_id";
    private static final String FIELD_ALBUM_ID = "album_id";
    private static final String FIELD_URL = "url";
    private static final String FIELD_POST_URL = "post_url";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_POST_ID = "post_id";
    private static final String FIELD_COMPLETE_URL = "complete_url";


    @SerializedName(FIELD_EXTENSION)
    private String mExtension;
    @SerializedName(FIELD_TEMP)
    private int mTemp;
    @SerializedName(FIELD_TYPE)
    private String mType;
    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_TIMELINE_ID)
    private int mTimelineId;
    @SerializedName(FIELD_ALBUM_ID)
    private int mAlbumId;
    @SerializedName(FIELD_URL)
    private String mUrl;
    @SerializedName(FIELD_POST_URL)
    private String mPostUrl;
    @SerializedName(FIELD_NAME)
    private String mName;
    @SerializedName(FIELD_ACTIVE)
    private int mActive;
    @SerializedName(FIELD_POST_ID)
    private int mPostId;
    @SerializedName(FIELD_COMPLETE_URL)
    private String mCompleteUrl;


    public Cover(){

    }

    public void setExtension(String extension) {
        mExtension = extension;
    }

    public String getExtension() {
        return mExtension;
    }

    public void setTemp(int temp) {
        mTemp = temp;
    }

    public int getTemp() {
        return mTemp;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
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

    public void setAlbumId(int albumId) {
        mAlbumId = albumId;
    }

    public int getAlbumId() {
        return mAlbumId;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setPostUrl(String postUrl) {
        mPostUrl = postUrl;
    }

    public String getPostUrl() {
        return mPostUrl;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
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

    public void setCompleteUrl(String completeUrl) {
        mCompleteUrl = completeUrl;
    }

    public String getCompleteUrl() {
        return mCompleteUrl;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Cover){
            return ((Cover) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public Cover(Parcel in) {
        mExtension = in.readString();
        mTemp = in.readInt();
        mType = in.readString();
        mId = in.readLong();
        mTimelineId = in.readInt();
        mAlbumId = in.readInt();
        mUrl = in.readString();
        mPostUrl = in.readString();
        mName = in.readString();
        mActive = in.readInt();
        mPostId = in.readInt();
        mCompleteUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Cover> CREATOR = new Parcelable.Creator<Cover>() {
        public Cover createFromParcel(Parcel in) {
            return new Cover(in);
        }

        public Cover[] newArray(int size) {
        return new Cover[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mExtension);
        dest.writeInt(mTemp);
        dest.writeString(mType);
        dest.writeLong(mId);
        dest.writeInt(mTimelineId);
        dest.writeInt(mAlbumId);
        dest.writeString(mUrl);
        dest.writeString(mPostUrl);
        dest.writeString(mName);
        dest.writeInt(mActive);
        dest.writeInt(mPostId);
        dest.writeString(mCompleteUrl);
    }

    @Override
    public String toString(){
        return "extension = " + mExtension + ", temp = " + mTemp + ", type = " + mType + ", id = " + mId + ", timelineId = " + mTimelineId + ", albumId = " + mAlbumId + ", url = " + mUrl + ", postUrl = " + mPostUrl + ", name = " + mName + ", active = " + mActive + ", postId = " + mPostId + ", completeUrl = " + mCompleteUrl;
    }


}