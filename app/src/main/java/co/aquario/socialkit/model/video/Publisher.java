package co.aquario.socialkit.model.video;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;


public class Publisher implements Parcelable{

    private static final String FIELD_MESSAGE_PRIVACY = "message_privacy";
    private static final String FIELD_ACTUAL_COVER_URL = "actual_cover_url";
    private static final String FIELD_CATEGORY_ID = "category_id";
    private static final String FIELD_GENDER = "gender";
    private static final String FIELD_AVATAR_ID = "avatar_id";
    private static final String FIELD_COVER = "cover";
    private static final String FIELD_COVER_ID = "cover_id";
    private static final String FIELD_LIVE_COVER_OFFLINE = "live_cover_offline";
    private static final String FIELD_LIVE_COVER = "live_cover";
    private static final String FIELD_AVATAR = "avatar";
    private static final String FIELD_THUMBNAIL_URL = "thumbnail_url";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ID = "id";
    private static final String FIELD_ONLINE = "online";
    private static final String FIELD_COMMENT_PRIVACY = "comment_privacy";
    private static final String FIELD_COVER_URL = "cover_url";
    private static final String FIELD_AVATAR_URL = "avatar_url";
    private static final String FIELD_FIRST_NAME = "first_name";
    private static final String FIELD_LAST_LOGGED = "last_logged";
    private static final String FIELD_CONFIRM_FOLLOWERS = "confirm_followers";
    private static final String FIELD_URL = "url";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_FOLLOW_PRIVACY = "follow_privacy";
    private static final String FIELD_LAST_NAME = "last_name";


    @SerializedName(FIELD_MESSAGE_PRIVACY)
    private String mMessagePrivacy;
    @SerializedName(FIELD_ACTUAL_COVER_URL)
    private String mActualCoverUrl;
    @SerializedName(FIELD_CATEGORY_ID)
    private int mCategoryId;
    @SerializedName(FIELD_GENDER)
    private String mGender;
    @SerializedName(FIELD_AVATAR_ID)
    private int mAvatarId;
    @SerializedName(FIELD_COVER)
    private Cover mCover;
    @SerializedName(FIELD_COVER_ID)
    private int mCoverId;
    @SerializedName(FIELD_LIVE_COVER_OFFLINE)
    private String mLiveCoverOffline;
    @SerializedName(FIELD_LIVE_COVER)
    private String mLiveCover;
    @SerializedName(FIELD_AVATAR)
    private Avatar mAvatar;
    @SerializedName(FIELD_THUMBNAIL_URL)
    private String mThumbnailUrl;
    @SerializedName(FIELD_USERNAME)
    private String mUsername;
    @SerializedName(FIELD_TYPE)
    private String mType;
    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_ONLINE)
    private boolean mOnline;
    @SerializedName(FIELD_COMMENT_PRIVACY)
    private String mCommentPrivacy;
    @SerializedName(FIELD_COVER_URL)
    private String mCoverUrl;
    @SerializedName(FIELD_AVATAR_URL)
    private String mAvatarUrl;
    @SerializedName(FIELD_FIRST_NAME)
    private String mFirstName;
    @SerializedName(FIELD_LAST_LOGGED)
    private long mLastLogged;
    @SerializedName(FIELD_CONFIRM_FOLLOWERS)
    private int mConfirmFollower;
    @SerializedName(FIELD_URL)
    private String mUrl;
    @SerializedName(FIELD_NAME)
    private String mName;
    @SerializedName(FIELD_FOLLOW_PRIVACY)
    private String mFollowPrivacy;
    @SerializedName(FIELD_LAST_NAME)
    private String mLastName;


    public Publisher(){

    }

    public void setMessagePrivacy(String messagePrivacy) {
        mMessagePrivacy = messagePrivacy;
    }

    public String getMessagePrivacy() {
        return mMessagePrivacy;
    }

    public void setActualCoverUrl(String actualCoverUrl) {
        mActualCoverUrl = actualCoverUrl;
    }

    public String getActualCoverUrl() {
        return mActualCoverUrl;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getGender() {
        return mGender;
    }

    public void setAvatarId(int avatarId) {
        mAvatarId = avatarId;
    }

    public int getAvatarId() {
        return mAvatarId;
    }

    public void setCover(Cover cover) {
        mCover = cover;
    }

    public Cover getCover() {
        return mCover;
    }

    public void setCoverId(int coverId) {
        mCoverId = coverId;
    }

    public int getCoverId() {
        return mCoverId;
    }

    public void setLiveCoverOffline(String liveCoverOffline) {
        mLiveCoverOffline = liveCoverOffline;
    }

    public String getLiveCoverOffline() {
        return mLiveCoverOffline;
    }

    public void setLiveCover(String liveCover) {
        mLiveCover = liveCover;
    }

    public String getLiveCover() {
        return mLiveCover;
    }

    public void setAvatar(Avatar avatar) {
        mAvatar = avatar;
    }

    public Avatar getAvatar() {
        return mAvatar;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
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

    public void setOnline(boolean online) {
        mOnline = online;
    }

    public boolean isOnline() {
        return mOnline;
    }

    public void setCommentPrivacy(String commentPrivacy) {
        mCommentPrivacy = commentPrivacy;
    }

    public String getCommentPrivacy() {
        return mCommentPrivacy;
    }

    public void setCoverUrl(String coverUrl) {
        mCoverUrl = coverUrl;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setLastLogged(long lastLogged) {
        mLastLogged = lastLogged;
    }

    public long getLastLogged() {
        return mLastLogged;
    }

    public void setConfirmFollower(int confirmFollower) {
        mConfirmFollower = confirmFollower;
    }

    public int getConfirmFollower() {
        return mConfirmFollower;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setFollowPrivacy(String followPrivacy) {
        mFollowPrivacy = followPrivacy;
    }

    public String getFollowPrivacy() {
        return mFollowPrivacy;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getLastName() {
        return mLastName;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Publisher){
            return ((Publisher) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public Publisher(Parcel in) {
        mMessagePrivacy = in.readString();
        mActualCoverUrl = in.readString();
        mCategoryId = in.readInt();
        mGender = in.readString();
        mAvatarId = in.readInt();
        mCover = in.readParcelable(Cover.class.getClassLoader());
        mCoverId = in.readInt();
        mLiveCoverOffline = in.readString();
        mLiveCover = in.readString();
        mAvatar = in.readParcelable(Avatar.class.getClassLoader());
        mThumbnailUrl = in.readString();
        mUsername = in.readString();
        mType = in.readString();
        mId = in.readLong();
        mOnline = in.readInt() == 1 ? true: false;
        mCommentPrivacy = in.readString();
        mCoverUrl = in.readString();
        mAvatarUrl = in.readString();
        mFirstName = in.readString();
        mLastLogged = in.readLong();
        mConfirmFollower = in.readInt();
        mUrl = in.readString();
        mName = in.readString();
        mFollowPrivacy = in.readString();
        mLastName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Publisher> CREATOR = new Parcelable.Creator<Publisher>() {
        public Publisher createFromParcel(Parcel in) {
            return new Publisher(in);
        }

        public Publisher[] newArray(int size) {
        return new Publisher[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMessagePrivacy);
        dest.writeString(mActualCoverUrl);
        dest.writeInt(mCategoryId);
        dest.writeString(mGender);
        dest.writeInt(mAvatarId);
        dest.writeParcelable(mCover, flags);
        dest.writeInt(mCoverId);
        dest.writeString(mLiveCoverOffline);
        dest.writeString(mLiveCover);
        dest.writeParcelable(mAvatar, flags);
        dest.writeString(mThumbnailUrl);
        dest.writeString(mUsername);
        dest.writeString(mType);
        dest.writeLong(mId);
        dest.writeInt(mOnline ? 1 : 0);
        dest.writeString(mCommentPrivacy);
        dest.writeString(mCoverUrl);
        dest.writeString(mAvatarUrl);
        dest.writeString(mFirstName);
        dest.writeLong(mLastLogged);
        dest.writeInt(mConfirmFollower);
        dest.writeString(mUrl);
        dest.writeString(mName);
        dest.writeString(mFollowPrivacy);
        dest.writeString(mLastName);
    }

    @Override
    public String toString(){
        return "messagePrivacy = " + mMessagePrivacy + ", actualCoverUrl = " + mActualCoverUrl + ", categoryId = " + mCategoryId + ", gender = " + mGender + ", avatarId = " + mAvatarId + ", cover = " + mCover + ", coverId = " + mCoverId + ", liveCoverOffline = " + mLiveCoverOffline + ", liveCover = " + mLiveCover + ", avatar = " + mAvatar + ", thumbnailUrl = " + mThumbnailUrl + ", username = " + mUsername + ", type = " + mType + ", id = " + mId + ", online = " + mOnline + ", commentPrivacy = " + mCommentPrivacy + ", coverUrl = " + mCoverUrl + ", avatarUrl = " + mAvatarUrl + ", firstName = " + mFirstName + ", lastLogged = " + mLastLogged + ", confirmFollower = " + mConfirmFollower + ", url = " + mUrl + ", name = " + mName + ", followPrivacy = " + mFollowPrivacy + ", lastName = " + mLastName;
    }


}