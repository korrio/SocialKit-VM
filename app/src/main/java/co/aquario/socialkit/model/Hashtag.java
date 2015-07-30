package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Mac on 6/8/15.
 */

@Parcel
public class Hashtag {
    @Expose
    public String id;
    @Expose
    public String hash;
    @Expose
    public String tag;
    @Expose
    @SerializedName("trend_use_num")
    public String num;
    @Expose
    @SerializedName("last_trend_time")
    public String ago;

    @ParcelConstructor
    public Hashtag(String id, String hash, String tag, String num, String ago) {
        this.id = id;
        this.hash = hash;
        this.tag = tag;
        this.num = num;
        this.ago = ago;
    }
}
