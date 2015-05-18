package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadFriendListEventWithQuery {
    private String q;
    private String type;
    private int userId;
    private int page;
    private int perPage;



    public LoadFriendListEventWithQuery(String q, String type, int userId, int page, int perPage) {
        this.q = q;
        this.type = type;
        this.userId = userId;
        this.page = page;
        this.perPage = perPage;
    }

    public String getQuery() {
        return q;
    }

    public String getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }
}
