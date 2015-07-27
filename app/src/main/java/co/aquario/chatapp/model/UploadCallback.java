package co.aquario.chatapp.model;

/**
 * Created by Mac on 7/21/15.
 */
public class UploadCallback {


    /**
     * id : 116039
     * fileType : image/png
     * fileName : ScreenShot2015-07-26at10.25.53AM.png
     * active : 1
     * full_path : http://chat.vdomax.com/photos/2015/07/ynBan_116039_266961938e316427507db4857da1be71.png
     * thumb : http://chat.vdomax.com/photos/2015/07/ynBan_116039_266961938e316427507db4857da1be71_thumb.png
     * url : photos/2015/07/ynBan_116039_266961938e316427507db4857da1be71.png
     */
    private int id;
    private String fileType;
    private String fileName;
    private int active;
    private String full_path;
    private String thumb;
    private String url;

    public void setId(int id) {
        this.id = id;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setFull_path(String full_path) {
        this.full_path = full_path;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public int getActive() {
        return active;
    }

    public String getFull_path() {
        return full_path;
    }

    public String getThumb() {
        return thumb;
    }

    public String getUrl() {
        return url;
    }
}
