package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadTimelineEvent {
    private int userId;
    private String type;
    private int page;
    private int perPage;
    private boolean isHome;

    public LoadTimelineEvent(int userId, String type, int page, int perPage,boolean isHome) {
        this.userId = userId;
        this.type = type;
        this.page = page;
        this.perPage = perPage;
        this.isHome = isHome;
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
}
