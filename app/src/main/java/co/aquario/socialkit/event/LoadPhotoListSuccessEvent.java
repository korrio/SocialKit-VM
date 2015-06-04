package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadPhotoListSuccessEvent {
    private String sort;
    private PhotoListDataResponse photoListData;

    public LoadPhotoListSuccessEvent(PhotoListDataResponse photoListData, String sort) {
        this.photoListData = photoListData;
        this.sort = sort;
    }

    public PhotoListDataResponse getPhotoListData() {
        return photoListData;
    }

    public String getSortType() {
        return sort;
    }
}

