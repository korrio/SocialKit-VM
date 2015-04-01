package co.aquario.socialkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by Mac on 3/10/15.
 */
public class User extends BaseModel implements Parcelable {

    /*
    @Expose
    public String id;
    @Expose
    public String username;
    @Expose
    public String about;
    @Expose
    public String active;
    @Expose
    public String email;
    @Expose
    public String language;
    @SerializedName("last_logged")
    @Expose
    public String lastLogged;
    @Expose
    public String name;
    @Expose
    public String time;
    @Expose
    public String timestamp;
    @Expose
    public String timezone;
    @Expose
    public String type;
    @Expose
    public String verified;
    @Expose
    public String avatar;
    @Expose
    public String cover;

    */

    private static final String FIELD_AVATAR_ID = "avatar_id";
    private static final String FIELD_VERIFIED = "verified";
    private static final String FIELD_COVER = "cover";
    private static final String FIELD_COVER_ID = "cover_id";
    private static final String FIELD_LANGUAGE = "language";
    private static final String FIELD_ABOUT = "about";
    private static final String FIELD_AVATAR = "avatar";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_COVER_POSITION = "cover_position";
    private static final String FIELD_EMAIL_VERIFICATION_KEY = "email_verification_key";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_ID = "id";
    private static final String FIELD_LAST_LOGGED = "last_logged";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_EMAIL_VERIFIED = "email_verified";
    private static final String FIELD_ACTIVE = "active";
    private static final String FIELD_TIMEZONE = "timezone";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_FOLLOWING = "is_following";


    @SerializedName(FIELD_AVATAR_ID)
    private int mAvatarId;
    @SerializedName(FIELD_VERIFIED)
    private int mVerified;
    @SerializedName(FIELD_COVER)
    private String mCover;
    @SerializedName(FIELD_COVER_ID)
    private int mCoverId;
    @SerializedName(FIELD_LANGUAGE)
    private String mLanguage;
    @SerializedName(FIELD_ABOUT)
    private String mAbout;
    @SerializedName(FIELD_AVATAR)
    private String mAvatar;
    @SerializedName(FIELD_TIMESTAMP)
    private String mTimestamp;
    @SerializedName(FIELD_COVER_POSITION)
    private int mCoverPosition;
    @SerializedName(FIELD_EMAIL_VERIFICATION_KEY)
    private String mEmailVerificationKey;
    @SerializedName(FIELD_USERNAME)
    private String mUsername;
    @SerializedName(FIELD_TYPE)
    private String mType;
    @SerializedName(FIELD_ID)
    private String mId;
    @SerializedName(FIELD_LAST_LOGGED)
    private long mLastLogged;
    @SerializedName(FIELD_PASSWORD)
    private String mPassword;
    @SerializedName(FIELD_EMAIL_VERIFIED)
    private int mEmailVerified;
    @SerializedName(FIELD_ACTIVE)
    private int mActive;
    @SerializedName(FIELD_TIMEZONE)
    private String mTimezone;
    @SerializedName(FIELD_NAME)
    private String mName;
    @SerializedName(FIELD_TIME)
    private long mTime;
    @SerializedName(FIELD_EMAIL)
    private String mEmail;
    @SerializedName(FIELD_FOLLOWING)
    private boolean mIsFollowing;


    public User(){

    }

    public void setAvatarId(int avatarId) {
        mAvatarId = avatarId;
    }

    public int getAvatarId() {
        return mAvatarId;
    }

    public void setVerified(int verified) {
        mVerified = verified;
    }

    public int getVerified() {
        return mVerified;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public String getCover() {
        return mCover;
    }

    public void setCoverId(int coverId) {
        mCoverId = coverId;
    }

    public int getCoverId() {
        return mCoverId;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setAbout(String about) {
        mAbout = about;
    }

    public String getAbout() {
        return mAbout;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setCoverPosition(int coverPosition) {
        mCoverPosition = coverPosition;
    }

    public int getCoverPosition() {
        return mCoverPosition;
    }

    public void setEmailVerificationKey(String emailVerificationKey) {
        mEmailVerificationKey = emailVerificationKey;
    }

    public String getEmailVerificationKey() {
        return mEmailVerificationKey;
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

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public void setLastLogged(long lastLogged) {
        mLastLogged = lastLogged;
    }

    public long getLastLogged() {
        return mLastLogged;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setEmailVerified(int emailVerified) {
        mEmailVerified = emailVerified;
    }

    public int getEmailVerified() {
        return mEmailVerified;
    }

    public void setActive(int active) {
        mActive = active;
    }

    public int getActive() {
        return mActive;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public long getTime() {
        return mTime;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setIsFollowing(boolean isFollowing) {
        mIsFollowing = isFollowing;
    }

    public boolean getIsFollowing() {
        return mIsFollowing;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof User){
            return ((User) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Integer.parseInt(mId);
    }

    public User(Parcel in) {
        mAvatarId = in.readInt();
        mVerified = in.readInt();
        mCover = in.readString();
        mCoverId = in.readInt();
        mLanguage = in.readString();
        mAbout = in.readString();
        mAvatar = in.readString();
        mTimestamp = in.readString();
        mCoverPosition = in.readInt();
        mEmailVerificationKey = in.readString();
        mUsername = in.readString();
        mType = in.readString();
        mId = in.readString();
        mLastLogged = in.readLong();
        mPassword = in.readString();
        mEmailVerified = in.readInt();
        mActive = in.readInt();
        mTimezone = in.readString();
        mName = in.readString();
        mTime = in.readLong();
        mEmail = in.readString();
        mIsFollowing = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mAvatarId);
        dest.writeInt(mVerified);
        dest.writeString(mCover);
        dest.writeInt(mCoverId);
        dest.writeString(mLanguage);
        dest.writeString(mAbout);
        dest.writeString(mAvatar);
        dest.writeString(mTimestamp);
        dest.writeInt(mCoverPosition);
        dest.writeString(mEmailVerificationKey);
        dest.writeString(mUsername);
        dest.writeString(mType);
        dest.writeString(mId);
        dest.writeLong(mLastLogged);
        dest.writeString(mPassword);
        dest.writeInt(mEmailVerified);
        dest.writeInt(mActive);
        dest.writeString(mTimezone);
        dest.writeString(mName);
        dest.writeLong(mTime);
        dest.writeString(mEmail);
        dest.writeByte((byte) (mIsFollowing ? 1 : 0));
    }

    @Override
    public String toString(){
        return "isFollowing = " + mIsFollowing + ", avatarId = " + mAvatarId + ", verified = " + mVerified + ", cover = " + mCover + ", coverId = " + mCoverId + ", language = " + mLanguage + ", about = " + mAbout + ", avatar = " + mAvatar + ", timestamp = " + mTimestamp + ", coverPosition = " + mCoverPosition + ", emailVerificationKey = " + mEmailVerificationKey + ", username = " + mUsername + ", type = " + mType + ", id = " + mId + ", lastLogged = " + mLastLogged + ", password = " + mPassword + ", emailVerified = " + mEmailVerified + ", active = " + mActive + ", timezone = " + mTimezone + ", name = " + mName + ", time = " + mTime + ", email = " + mEmail;
    }

    public String getAvatarUrl() {
        return EndpointManager.getPath(mAvatar);
    }

    public String getCoverUrl() {
        return EndpointManager.getPath(mCover);
    }
}
