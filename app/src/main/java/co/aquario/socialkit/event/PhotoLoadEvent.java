package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/12/15.
 */
public class PhotoLoadEvent {
    private String url;

    public PhotoLoadEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
