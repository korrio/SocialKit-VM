package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Mac on 3/10/15.
 */
@Parcel
public class Clip extends BaseModel {
    @Expose
    public String id;
    @Expose
    public String url;
    @Expose
    @SerializedName("thumbnail")
    public String thumb;
}
