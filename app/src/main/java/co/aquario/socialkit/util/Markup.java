package co.aquario.socialkit.util;

import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class Markup {
    public static String getLink(String text) throws UnsupportedEncodingException {
        String link_search = "\\[a(=(.*?))?\\](.*?)\\[\\/a\\]";

        String[] matches = Pcre.preg_match_all(link_search, text);
        if (matches != null) {

            for (String match : matches) {

                String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

                String match_decode = URLDecoder.decode(match, "UTF-8");
                String match_url = match_decode;
                if (Pcre.preg_match(regex, match_decode, true) != null) {
                    match_url = "http://" + match_url;
                }

                text = text.replace("[a]" + match + "[/a]", "<a href=\"" + Jsoup.parse(match_url).text() + "\" >" + match_decode + "</a>");
            }
        }

    return text;

    }
}
