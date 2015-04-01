package co.aquario.socialkit.model.video;

import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import android.os.Parcel;


public class Medium implements Parcelable{

    private static final String FIELD_ID = "id";


    @SerializedName(FIELD_ID)
    private String mId;


    public Medium(){

    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Medium){
            return ((Medium) obj).getId() == mId;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return mId.hashCode();
    }

    public Medium(Parcel in) {
        mId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Medium> CREATOR = new Parcelable.Creator<Medium>() {
        public Medium createFromParcel(Parcel in) {
            return new Medium(in);
        }

        public Medium[] newArray(int size) {
        return new Medium[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
    }

    @Override
    public String toString(){
        return "id = " + mId;
    }


}