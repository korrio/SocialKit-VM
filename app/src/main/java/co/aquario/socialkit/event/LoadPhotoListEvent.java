package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadPhotoListEvent {
    private int userId;
    private String sortType;
    private int page;
    private int perPage;

    public LoadPhotoListEvent(int userId, String sortType, int page, int perPage) {
        this.userId = userId;
        this.sortType = sortType;
        this.page = page;
        this.perPage = perPage;
    }

    public int getUserId() {
        return userId;
    }

    public String getSortType() {
        return sortType;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return perPage;
    }

}
