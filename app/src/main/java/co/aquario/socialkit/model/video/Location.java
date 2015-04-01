package co.aquario.socialkit.model.video;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;


public class Location implements Parcelable{

    private static final String FIELD_NAME = "name";


    @SerializedName(FIELD_NAME)
    private String mName;


    public Location(){

    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public Location(Parcel in) {
        mName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
        return new Location[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
    }

    @Override
    public String toString(){
        return "name = " + mName;
    }


}