package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadHashtagStoryEvent {


    private int userId;
    private String type;
    private int page;
    private int perPage;
    private boolean isHome;

    private String q;

    public LoadHashtagStoryEvent(int userId, String type, int page, int perPage, boolean isHome, String q) {
        this.userId = userId;
        this.type = type;
        this.page = page;
        this.perPage = perPage;
        this.isHome = isHome;
        this.q = q;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

    public boolean getIsHome() {
        return isHome;
    }

    public String getQ() { return q; }
}
