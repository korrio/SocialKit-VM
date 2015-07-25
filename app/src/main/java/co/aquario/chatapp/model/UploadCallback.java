package co.aquario.chatapp.model;

/**
 * Created by Mac on 7/21/15.
 */
public class UploadCallback {

    /**
     * extension : png
     * id : 115147
     * mimetype : image/png
     * name : bk_item_pressed.png
     * path : photos/2015/07/RMgku_115147_1276d43cb604e89c276793ae2667ccf5.png
     * active : 1
     * full_path : http://chat.vdomax.com/photos/2015/07/RMgku_115147_1276d43cb604e89c276793ae2667ccf5.png
     * thumb : http://chat.vdomax.com/photos/2015/07/RMgku_115147_1276d43cb604e89c276793ae2667ccf5_thumb.png
     * url : photos/2015/07/RMgku_115147_1276d43cb604e89c276793ae2667ccf5
     */
    private String extension;
    private int id;
    private String mimetype;
    private String name;
    private String path;
    private int active;
    private String full_path;
    private String thumb;
    private String url;

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getExtension() {
        return extension;
    }

    public int getId() {
        return id;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
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

    @Override
    public String toString() {
        return "UploadCallback{" +
                "extension='" + extension + '\'' +
                ", id=" + id +
                ", mimetype='" + mimetype + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", active=" + active +
                ", full_path='" + full_path + '\'' +
                ", thumb='" + thumb + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
