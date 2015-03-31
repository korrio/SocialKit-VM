package co.aquario.socialkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root1 on 2/22/15.
 */
public class Love implements Parcelable{

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";


    @SerializedName(FIELD_ID)
    private long mId;
    @SerializedName(FIELD_NAME)
    private String mName;


    public Love(){

    }

    public void setId(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Love){
            return ((Love) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ((Long)mId).hashCode();
    }

    public Love(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Love> CREATOR = new Parcelable.Creator<Love>() {
        public Love createFromParcel(Parcel in) {
            return new Love(in);
        }

        public Love[] newArray(int size) {
            return new Love[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
    }

    @Override
    public String toString(){
        return "id = " + mId + ", name = " + mName;
    }

}
