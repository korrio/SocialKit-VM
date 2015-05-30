package co.aquario.socialkit.search.soundcloud;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kevin on 2/22/15.
 */
public class Track {
    @SerializedName("title")
    public String mTitle;

    @SerializedName("stream_url")
    public String mStreamURL;

    @SerializedName("id")
    public int mID;

    @SerializedName("artwork_url")
    public String artworkURL;

    public String uri;

    public SCUser user;

    public int duration;

    @SerializedName("created_at")
    public String ago;

    @SerializedName("playback_count")
    public String playbackCount;

    public String getArtworkURL() {
        //if (artworkURL != null){
        //  return artworkURL.replace("large","tiny");
        //} else {
        return artworkURL;
        //}

    }

    public class SCUser {

        public String username;

        @SerializedName("avatar_url")
        public String avatarUrl;

        public String permalink;
    }
}
