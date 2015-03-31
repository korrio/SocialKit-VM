package co.aquario.socialkit.model.video;

import java.util.ArrayList;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import android.os.Parcel;


public class Result implements Parcelable{

    private static final String FIELD_TEXT = "text";
    private static final String FIELD_YOUTUBE_DESCRIPTION = "youtube_description";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_RECIPIENT_EXISTS = "recipient_exists";
    private static final String FIELD_RECIPIENT_ID = "recipient_id";
    private static final String FIELD_ID = "id";
    private static final String FIELD_YOUTUBE_VIDEO_ID = "youtube_video_id";
    private static final String FIELD_MEDIA = "media";
    private static final String FIELD_CLIP_ID = "clip_id";
    private static final String FIELD_SOUNDCLOUD_URI = "soundcloud_uri";
    private static final String FIELD_VIEW = "view";
    private static final String FIELD_LINK_TITLE = "link_title";
    private static final String FIELD_MEDIA_EXISTS = "media_exists";
    private static final String FIELD_MEDIA_ID = "media_id";
    private static final String FIELD_VIA_TYPE = "via_type";
    private static final String FIELD_YOUTUBE_TITLE = "youtube_title";
    private static final String FIELD_ADMIN = "admin";
    private static final String FIELD_POST_ID = "post_id";
    private static final String FIELD_COMMENTS = "comments";
    private static final String FIELD_RECIPIENT = "recipient";
    private static final String FIELD_LINK_URL = "link_url";
    private static final String FIELD_VIEW_ALL_COMMENTS = "view_all_comments";
    private static final String FIELD_GOOGLE_MAP_NAME = "google_map_name";
    private static final String FIELD_TIMELINE_ID = "timeline_id";
    private static final String FIELD_MEDIA_TYPE = "media_type";
    private static final String FIELD_LOCATION_EXISTS = "location_exists";
    private static final String FIELD_SOUNDCLOUD_TITLE = "soundcloud_title";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_TYPE1 = "type1";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_TYPE2 = "type2";
    private static final String FIELD_HIDDEN = "hidden";
    private static final String FIELD_PUBLISHER = "publisher";


    @SerializedName(FIELD_TEXT)
    private String mText;
    @SerializedName(FIELD_YOUTUBE_DESCRIPTION)
    private String mYoutubeDescription;
    @SerializedName(FIELD_COMMENT)
    private Comment mComment;
    @SerializedName(FIELD_LOCATION)
    private Location mLocation;
    @SerializedName(FIELD_RECIPIENT_EXISTS)
    private boolean mRecipientExist;
    @SerializedName(FIELD_RECIPIENT_ID)
    private int mRecipientId;
    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_YOUTUBE_VIDEO_ID)
    private String mYoutubeVideoId;
    @SerializedName(FIELD_MEDIA)
    private Medium mMedium;
    @SerializedName(FIELD_CLIP_ID)
    private int mClipId;
    @SerializedName(FIELD_SOUNDCLOUD_URI)
    private String mSoundcloudUri;
    @SerializedName(FIELD_VIEW)
    private int mView;
    @SerializedName(FIELD_LINK_TITLE)
    private String mLinkTitle;
    @SerializedName(FIELD_MEDIA_EXISTS)
    private boolean mMediaExist;
    @SerializedName(FIELD_MEDIA_ID)
    private int mMediaId;
    @SerializedName(FIELD_VIA_TYPE)
    private String mViaType;
    @SerializedName(FIELD_YOUTUBE_TITLE)
    private String mYoutubeTitle;
    @SerializedName(FIELD_ADMIN)
    private boolean mAdmin;
    @SerializedName(FIELD_POST_ID)
    private int mPostId;
    @SerializedName(FIELD_COMMENTS)
    private List<Comment> mComments;
    @SerializedName(FIELD_RECIPIENT)
    private String mRecipient;
    @SerializedName(FIELD_LINK_URL)
    private String mLinkUrl;
    @SerializedName(FIELD_VIEW_ALL_COMMENTS)
    private boolean mViewAllComment;
    @SerializedName(FIELD_GOOGLE_MAP_NAME)
    private String mGoogleMapName;
    @SerializedName(FIELD_TIMELINE_ID)
    private int mTimelineId;
    @SerializedName(FIELD_MEDIA_TYPE)
    private String mMediaType;
    @SerializedName(FIELD_LOCATION_EXISTS)
    private boolean mLocationExist;
    @SerializedName(FIELD_SOUNDCLOUD_TITLE)
    private String mSoundcloudTitle;
    @SerializedName(FIELD_ACTIVE)
    private int mActive;
    @SerializedName(FIELD_TYPE1)
    private String mType1;
    @SerializedName(FIELD_TIME)
    private long mTime;
    @SerializedName(FIELD_TYPE2)
    private String mType2;
    @SerializedName(FIELD_HIDDEN)
    private int mHidden;
    @SerializedName(FIELD_PUBLISHER)
    private Publisher mPublisher;


    public Result(){

    }

    public void setText(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setYoutubeDescription(String youtubeDescription) {
        mYoutubeDescription = youtubeDescription;
    }

    public String getYoutubeDescription() {
        return mYoutubeDescription;
    }

    public void setComment(Comment comment) {
        mComment = comment;
    }

    public Comment getComment() {
        return mComment;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setRecipientExist(boolean recipientExist) {
        mRecipientExist = recipientExist;
    }

    public boolean isRecipientExist() {
        return mRecipientExist;
    }

    public void setRecipientId(int recipientId) {
        mRecipientId = recipientId;
    }

    public int getRecipientId() {
        return mRecipientId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setYoutubeVideoId(String youtubeVideoId) {
        mYoutubeVideoId = youtubeVideoId;
    }

    public String getYoutubeVideoId() {
        return mYoutubeVideoId;
    }

    public void setMedium(Medium medium) {
        mMedium = medium;
    }

    public Medium getMedium() {
        return mMedium;
    }

    public void setClipId(int clipId) {
        mClipId = clipId;
    }

    public int getClipId() {
        return mClipId;
    }

    public void setSoundcloudUri(String soundcloudUri) {
        mSoundcloudUri = soundcloudUri;
    }

    public String getSoundcloudUri() {
        return mSoundcloudUri;
    }

    public void setView(int view) {
        mView = view;
    }

    public int getView() {
        return mView;
    }

    public void setLinkTitle(String linkTitle) {
        mLinkTitle = linkTitle;
    }

    public String getLinkTitle() {
        return mLinkTitle;
    }

    public void setMediaExist(boolean mediaExist) {
        mMediaExist = mediaExist;
    }

    public boolean isMediaExist() {
        return mMediaExist;
    }

    public void setMediaId(int mediaId) {
        mMediaId = mediaId;
    }

    public int getMediaId() {
        return mMediaId;
    }

    public void setViaType(String viaType) {
        mViaType = viaType;
    }

    public String getViaType() {
        return mViaType;
    }

    public void setYoutubeTitle(String youtubeTitle) {
        mYoutubeTitle = youtubeTitle;
    }

    public String getYoutubeTitle() {
        return mYoutubeTitle;
    }

    public void setAdmin(boolean admin) {
        mAdmin = admin;
    }

    public boolean isAdmin() {
        return mAdmin;
    }

    public void setPostId(int postId) {
        mPostId = postId;
    }

    public int getPostId() {
        return mPostId;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setRecipient(String recipient) {
        mRecipient = recipient;
    }

    public String getRecipient() {
        return mRecipient;
    }

    public void setLinkUrl(String linkUrl) {
        mLinkUrl = linkUrl;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

    public void setViewAllComment(boolean viewAllComment) {
        mViewAllComment = viewAllComment;
    }

    public boolean isViewAllComment() {
        return mViewAllComment;
    }

    public void setGoogleMapName(String googleMapName) {
        mGoogleMapName = googleMapName;
    }

    public String getGoogleMapName() {
        return mGoogleMapName;
    }

    public void setTimelineId(int timelineId) {
        mTimelineId = timelineId;
    }

    public int getTimelineId() {
        return mTimelineId;
    }

    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
    }

    public String getMediaType() {
        return mMediaType;
    }

    public void setLocationExist(boolean locationExist) {
        mLocationExist = locationExist;
    }

    public boolean isLocationExist() {
        return mLocationExist;
    }

    public void setSoundcloudTitle(String soundcloudTitle) {
        mSoundcloudTitle = soundcloudTitle;
    }

    public String getSoundcloudTitle() {
        return mSoundcloudTitle;
    }

    public void setActive(int active) {
        mActive = active;
    }

    public int getActive() {
        return mActive;
    }

    public void setType1(String type1) {
        mType1 = type1;
    }

    public String getType1() {
        return mType1;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getTime() {
        return mTime;
    }

    public void setType2(String type2) {
        mType2 = type2;
    }

    public String getType2() {
        return mType2;
    }

    public void setHidden(int hidden) {
        mHidden = hidden;
    }

    public int getHidden() {
        return mHidden;
    }

    public void setPublisher(Publisher publisher) {
        mPublisher = publisher;
    }

    public Publisher getPublisher() {
        return mPublisher;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Result){
            return ((Result) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public Result(Parcel in) {
        mText = in.readString();
        mYoutubeDescription = in.readString();
        mComment = in.readParcelable(Comment.class.getClassLoader());
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mRecipientExist = in.readInt() == 1 ? true: false;
        mRecipientId = in.readInt();
        mId = in.readLong();
        mYoutubeVideoId = in.readString();
        mMedium = in.readParcelable(Medium.class.getClassLoader());
        mClipId = in.readInt();
        mSoundcloudUri = in.readString();
        mView = in.readInt();
        mLinkTitle = in.readString();
        mMediaExist = in.readInt() == 1 ? true: false;
        mMediaId = in.readInt();
        mViaType = in.readString();
        mYoutubeTitle = in.readString();
        mAdmin = in.readInt() == 1 ? true: false;
        mPostId = in.readInt();
        mComments = new ArrayList<Comment>();
        in.readTypedList(mComments, Comment.CREATOR);
        mRecipient = in.readString();
        mLinkUrl = in.readString();
        mViewAllComment = in.readInt() == 1 ? true: false;
        mGoogleMapName = in.readString();
        mTimelineId = in.readInt();
        mMediaType = in.readString();
        mLocationExist = in.readInt() == 1 ? true: false;
        mSoundcloudTitle = in.readString();
        mActive = in.readInt();
        mType1 = in.readString();
        mTime = in.readLong();
        mType2 = in.readString();
        mHidden = in.readInt();
        mPublisher = in.readParcelable(Publisher.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
        return new Result[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mText);
        dest.writeString(mYoutubeDescription);
        dest.writeParcelable(mComment, flags);
        dest.writeParcelable(mLocation, flags);
        dest.writeInt(mRecipientExist ? 1 : 0);
        dest.writeInt(mRecipientId);
        dest.writeLong(mId);
        dest.writeString(mYoutubeVideoId);
        dest.writeParcelable(mMedium, flags);
        dest.writeInt(mClipId);
        dest.writeString(mSoundcloudUri);
        dest.writeInt(mView);
        dest.writeString(mLinkTitle);
        dest.writeInt(mMediaExist ? 1 : 0);
        dest.writeInt(mMediaId);
        dest.writeString(mViaType);
        dest.writeString(mYoutubeTitle);
        dest.writeInt(mAdmin ? 1 : 0);
        dest.writeInt(mPostId);
        dest.writeTypedList(mComments);
        dest.writeString(mRecipient);
        dest.writeString(mLinkUrl);
        dest.writeInt(mViewAllComment ? 1 : 0);
        dest.writeString(mGoogleMapName);
        dest.writeInt(mTimelineId);
        dest.writeString(mMediaType);
        dest.writeInt(mLocationExist ? 1 : 0);
        dest.writeString(mSoundcloudTitle);
        dest.writeInt(mActive);
        dest.writeString(mType1);
        dest.writeLong(mTime);
        dest.writeString(mType2);
        dest.writeInt(mHidden);
        dest.writeParcelable(mPublisher, flags);
    }

    @Override
    public String toString(){
        return "text = " + mText + ", youtubeDescription = " + mYoutubeDescription + ", comment = " + mComment + ", location = " + mLocation + ", recipientExist = " + mRecipientExist + ", recipientId = " + mRecipientId + ", id = " + mId + ", youtubeVideoId = " + mYoutubeVideoId + ", medium = " + mMedium + ", clipId = " + mClipId + ", soundcloudUri = " + mSoundcloudUri + ", view = " + mView + ", linkTitle = " + mLinkTitle + ", mediaExist = " + mMediaExist + ", mediaId = " + mMediaId + ", viaType = " + mViaType + ", youtubeTitle = " + mYoutubeTitle + ", admin = " + mAdmin + ", postId = " + mPostId + ", comments = " + mComments + ", recipient = " + mRecipient + ", linkUrl = " + mLinkUrl + ", viewAllComment = " + mViewAllComment + ", googleMapName = " + mGoogleMapName + ", timelineId = " + mTimelineId + ", mediaType = " + mMediaType + ", locationExist = " + mLocationExist + ", soundcloudTitle = " + mSoundcloudTitle + ", active = " + mActive + ", type1 = " + mType1 + ", time = " + mTime + ", type2 = " + mType2 + ", hidden = " + mHidden + ", publisher = " + mPublisher;
    }


}