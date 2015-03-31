package co.aquario.socialkit.model;

/**
 * Created by root1 on 2/22/15.
 */
public class Comment {

    private String titleImage;
    private String name;
    private String time;
    private String loveCount;
    private String comment;
    private String id;

    public Comment( String titleImage,String name,String time,String loveCount,String comment, String id){
        this.titleImage = titleImage;
        this.name = name;
        this.time = time;
        this.loveCount = loveCount;
        this.comment = comment;
        this.id = id;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public String getName() {
        return name;
    }

    public String getLoveCount() {
        return loveCount;
    }

    public String getComment() {
        return comment;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLoveCount(String loveCount) {
        this.loveCount = loveCount;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
