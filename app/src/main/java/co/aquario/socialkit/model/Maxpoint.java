package co.aquario.socialkit.model;

/**
 * Created by root1 on 5/24/15.
 */
public class Maxpoint {


    String urlConis;
    String nameConis;
    String bath;

    public Maxpoint(String urlConis, String nameConis, String bath){
        this.urlConis = urlConis;
        this.nameConis = nameConis;
        this.bath = bath;
    }

    public String getUrlConis() {
        return urlConis;
    }

    public void setUrlConis(String urlConis) {
        this.urlConis = urlConis;
    }

    public String getNameConis() {
        return nameConis;
    }

    public void setNameConis(String nameConis) {
        this.nameConis = nameConis;
    }

    public String getBath() {
        return bath;
    }

    public void setBath(String bath) {
        this.bath = bath;
    }
}
