package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 6/8/15.
 */

public class Hashtag {
    @Expose
    public String id;
    @Expose
    public String hash;
    @Expose
    public String tag;

    public Hashtag(String id, String hash, String tag) {
        this.id = id;
        this.hash = hash;
        this.tag = tag;
    }
}
