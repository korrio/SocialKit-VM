package co.aquario.socialkit.model.video;

import java.util.ArrayList;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import android.os.Parcel;


public class Video implements Parcelable{

    private static final String FIELD_STATUS = "status";
    private static final String FIELD_COUNT = "count";
    private static final String FIELD_RESULT = "result";


    @SerializedName(FIELD_STATUS)
    private int mStatus;
    @SerializedName(FIELD_COUNT)
    private int mCount;
    @SerializedName(FIELD_RESULT)
    private List<Result> mResults;


    public Video(){

    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public int getCount() {
        return mCount;
    }

    public void setResults(List<Result> results) {
        mResults = results;
    }

    public List<Result> getResults() {
        return mResults;
    }

    public Video(Parcel in) {
        mStatus = in.readInt();
        mCount = in.readInt();
        mResults = new ArrayList<Result>();
        in.readTypedList(mResults, Result.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
        return new Video[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStatus);
        dest.writeInt(mCount);
        dest.writeTypedList(mResults);
    }

    @Override
    public String toString(){
        return "status = " + mStatus + ", count = " + mCount + ", results = " + mResults;
    }


}