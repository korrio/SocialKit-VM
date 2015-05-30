package co.aquario.socialkit.connections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import co.aquario.socialkit.model.ImageBean;

public class ParserLargeImage {


    public static ImageBean parser(String html) {
        ImageBean imageBean = new ImageBean();
        if (html != null) {
            Document document = Jsoup.parse(html);

            Element element = document.getElementsByClass("post-media").first().select("img[src]").first();


            String src = element.attr("src");
            String width = element.attr("width");
            String height = element.attr("height");
            String alt = element.attr("alt");

            imageBean.setAlt(alt);
            imageBean.setWidth(width);
            imageBean.setHeight(height);
            imageBean.setImgurl(src);
        }

        return imageBean;
    }
}
