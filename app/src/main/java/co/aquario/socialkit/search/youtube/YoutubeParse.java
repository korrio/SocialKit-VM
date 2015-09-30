package co.aquario.socialkit.search.youtube;

import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mac on 9/30/15.
 */
public class YoutubeParse {
    // 常量
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String GDATA = "http://gdata.youtube.com/feeds/api/videos/%s?v=2";
    private static final String WATCHV = "http://www.youtube.com/watch?v=%s";
    private static final String VIDINFO = "http://www.youtube.com/get_video_info?video_id=%s&asv=3&el=detailpage&hl=en_US";
    private static final String PLAYLIST = "http://www.youtube.com/list_ajax?style=json&action_get_list=1&list=%s";
    private static final String USERAGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)";
    private static final String UEFSM = "url_encoded_fmt_stream_map";
    private static final String AF = "adaptive_fmts";
    private static final String jsplayer = "ytplayer\\.config\\s*=\\s*([^\\n]+);";

    public static HashMap<String, Resolution> Resolutions = new HashMap<String, Resolution>();
    static {
        Resolutions.put("5", new Resolution("320x240", "flv", "normal", ""));
        Resolutions.put("17", new Resolution("176x144", "3gp", "normal", ""));
        Resolutions.put("18", new Resolution("640x360", "mp4", "normal", ""));
        Resolutions.put("22", new Resolution("1280x720", "mp4", "normal", ""));
        Resolutions.put("34", new Resolution("640x360", "flv", "normal", ""));
        Resolutions.put("35", new Resolution("854x480", "flv", "normal", ""));
        Resolutions.put("36", new Resolution("320x240", "3gp", "normal", ""));
        Resolutions.put("37", new Resolution("1920x1080", "mp4", "normal", ""));
        Resolutions.put("38", new Resolution("4096x3072", "mp4", "normal", "4:3 hi-res"));
        Resolutions.put("43", new Resolution("640x360", "webm", "normal", ""));
        Resolutions.put("44", new Resolution("854x480", "webm", "normal", ""));
        Resolutions.put("45", new Resolution("1280x720", "webm", "normal", ""));
        Resolutions.put("46", new Resolution("1920x1080", "webm", "normal", ""));
        Resolutions.put("82", new Resolution("640x360-3D", "mp4", "normal", ""));
        Resolutions.put("83", new Resolution("640x480-3D", "mp4", "normal", ""));
        Resolutions.put("84", new Resolution("1280x720-3D", "mp4", "normal", ""));
        Resolutions.put("100", new Resolution("640x360-3D", "webm", "normal", ""));
        Resolutions.put("102", new Resolution("1280x720-3D", "webm", "normal", ""));
        Resolutions.put("133", new Resolution("426x240", "m4v", "video", ""));
        Resolutions.put("134", new Resolution("640x360", "m4v", "video", ""));
        Resolutions.put("135", new Resolution("854x480", "m4v", "video", ""));
        Resolutions.put("136", new Resolution("1280x720", "m4v", "video", ""));
        Resolutions.put("137", new Resolution("1920x1080", "m4v", "video", ""));
        Resolutions.put("138", new Resolution("4096x3072", "m4v", "video", ""));
        Resolutions.put("139", new Resolution("48k", "m4a", "audio", ""));
        Resolutions.put("140", new Resolution("128k", "m4a", "audio", ""));
        Resolutions.put("141", new Resolution("256k", "m4a", "audio", ""));
        Resolutions.put("160", new Resolution("256x144", "m4v", "video", ""));
        Resolutions.put("167", new Resolution("640x480", "webm", "video", ""));
        Resolutions.put("168", new Resolution("854x480", "webm", "video", ""));
        Resolutions.put("169", new Resolution("1280x720", "webm", "video", ""));
        Resolutions.put("170", new Resolution("1920x1080", "webm", "video", ""));
        Resolutions.put("171", new Resolution("128k", "ogg", "audio", ""));
        Resolutions.put("172", new Resolution("192k", "ogg", "audio", ""));
        Resolutions.put("242", new Resolution("360x240", "webm", "normal", ""));
        Resolutions.put("243", new Resolution("480x360", "webm", "normal", ""));
        Resolutions.put("244", new Resolution("640x480", "webm", "normal", ""));
        Resolutions.put("245", new Resolution("640x480", "webm", "normal", ""));
        Resolutions.put("246", new Resolution("640x480", "webm", "normal", ""));
        Resolutions.put("247", new Resolution("720x480", "webm", "normal", ""));
        Resolutions.put("248", new Resolution("1920x1080", "webm", "normal", ""));
        Resolutions.put("256", new Resolution("192k", "m4a", "audio", "6-channel"));
        Resolutions.put("258", new Resolution("320k", "m4a", "audio", "6-channel"));
        Resolutions.put("264", new Resolution("1920x1080", "m4v", "video", ""));
    }
    // 参数
    private boolean have_basic;
    private boolean have_gdata;
    private String description;
    private String category;
    private String published;
    List<FmtStreamMap> sm;
    List<FmtStreamMap> asm;
    private String jsurl;
    // streams
    // oggstreams
    // m4astreams
    // allstreams
    // videostreams
    // audiostreams
    private String title;
    private String thumb;
    private String rating;
    private long length;
    private String author;
    private String formats;
    private String videoid;
    private boolean ciphertag;
    private String duration;
    private String[] keywords;
    private String bigthumb;
    private int viewcount;
    private String bigthumbhd;

    private static class Resolution {
        public Resolution(String _resolution, String _format, String _type, String _notes) {
            resolution = _resolution;
            format = _format;
            type = _type;
            notes = _notes;
        }

        public String resolution;
        public String format;
        public String type;
        public String notes;
    }

    private static class FmtStreamMap {
        public String fallbackHost;
        public String s;
        public String itag;
        public String type;
        public String quality;
        public String url;
        public String sig;
    }

    public static class YoutubeStream {
        public String itag;
        public String threed;
        public String resolution;
        public String dimensions;
        public String vidformat;
        public String quality;
        public String extension;
        public String title;
        public boolean encrypted;
        public String parent;
        public String filename;
        public long fsize;
        public String bitrate;
        public String mediatype;
        public String notes;
        public String url;
        public String rawurl;
        public String sig;
        public String rawbitrate;
        public YoutubeParse mParent;

        public YoutubeStream(FmtStreamMap map, YoutubeParse parent) {
            itag = map.itag;
            // TODO:threed
            resolution = Resolutions.get(itag).resolution;
            // TODO:dimensions
            vidformat = map.type.split(";")[0];// TODO:异常处理
            quality = resolution;
            extension = Resolutions.get(itag).format;
            title = parent.title;
            encrypted = parent.ciphertag;
            mParent = parent;
            filename = title + "." + extension;
            // TODO:fsize
            // TODO:bitrate rawbitrate
            mediatype = Resolutions.get(itag).type;
            notes = Resolutions.get(itag).notes;
            // TODO:url
            rawurl = map.url;
            initUrl();
            sig = encrypted ? map.s : map.sig;
            if (TextUtils.equals("audio", mediatype)) {
                // TODO：音频处理
            }
        }

        /** 获取视频url包含解密 */
        private void initUrl() {
            if (!encrypted) {// 未加密
                url = makeUrl(rawurl, sig);
            } else {// 这货特么的加密了
                if (!TextUtils.isEmpty(mParent.jsurl)) {

                } else {// 额.....
                    String watchUrl = String.format(WATCHV, mParent.videoid);
                    // 获取页面内容，终于走到这一步了....
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet getData = new HttpGet(watchUrl);
                    getData.setHeader("User-Agent", USERAGENT);
                    try {
                        HttpResponse execute = client.execute(getData);
                        String pageContent = EntityUtils.toString(execute.getEntity(), "utf-8");
                        Pattern jsPattern = Pattern.compile(jsplayer, Pattern.MULTILINE);
                        Matcher matcher = jsPattern.matcher(pageContent);
                        // String jsResult = matcher.group();
                        // System.out.println(jsResult);
                        if (matcher.find()) {
                            System.out.println("============" + matcher.group(1));
                            try {
                                JSONObject ytplayerConfig = new JSONObject(matcher.group(1));
                                JSONObject args = ytplayerConfig.getJSONObject("args");
                                String fmtStream = args.getString("url_encoded_fmt_stream_map");
                                String[] fmtArray = fmtStream.split(",");
                                // 数据格式如下
                                // s=F6B648BDBB6A190BFE93FD2D28484C57E16760EF28.775F5A130A5932B78BCBF9380577606C66482FDF&type=video%2Fmp4%3B+codecs%3D%22avc1.64001F%2C+mp4a.40.2%22&itag=22&fallback_host=tc.v9.cache7.googlevideo.com&quality=hd720&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D22%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Cratebypass%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26ratebypass%3Dyes%26mt%3D1393643373
                                // s=04346DA13C859D87968FCCD4C866F04861B3A2C624.9E370B3331F0992C9209D5B143AE0C4252E85372&type=video%2Fwebm%3B+codecs%3D%22vp8.0%2C+vorbis%22&itag=43&fallback_host=tc.v2.cache7.googlevideo.com&quality=medium&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D43%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Cratebypass%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26ratebypass%3Dyes%26mt%3D1393643373
                                // s=A99948EE92641C8D21941BC7182A638CEBC72F09D6.86D7B03A783EAB8B0F9BD9D567F18F75C296B11C&type=video%2Fmp4%3B+codecs%3D%22avc1.42001E%2C+mp4a.40.2%22&itag=18&fallback_host=tc.v20.cache5.googlevideo.com&quality=medium&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D18%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Cratebypass%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26ratebypass%3Dyes%26mt%3D1393643373
                                // s=6F3FA1463C4CEA7B692E049042FB9C556AC62D6E49.F41281DDE47A90A11AEAE79D127502DA50D9AEEF&type=video%2Fx-flv&itag=5&fallback_host=tc.v16.cache6.googlevideo.com&quality=small&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D5%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26mt%3D1393643373
                                // s=297983C873C67E7D1EC06A7036389EC3A8C787E930.C6DF5B9561A571E18352B8252D8A6CEF57F26725&type=video%2F3gpp%3B+codecs%3D%22mp4v.20.3%2C+mp4a.40.2%22&itag=36&fallback_host=tc.v24.cache2.googlevideo.com&quality=small&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D36%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26mt%3D1393643373
                                // s=8898A3CB97C2E9ABBB916B931A22BED48960F2B5D8.10EBC794B834CDBB10F81CB9A811DB9E59054A1C&type=video%2F3gpp%3B+codecs%3D%22mp4v.20.3%2C+mp4a.40.2%22&itag=17&fallback_host=tc.v1.cache8.googlevideo.com&quality=small&url=http%3A%2F%2Fr4---sn-xoxgbt-nwje.googlevideo.com%2Fvideoplayback%3Fsource%3Dyoutube%26upn%3DMASRo0rj1UA%26gcr%3Dus%26itag%3D17%26ipbits%3D0%26fexp%3D906398%252C911429%252C940612%252C943103%252C916626%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26ip%3D173.252.252.202%26ms%3Dau%26expire%3D1393669755%26key%3Dyt5%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Csource%252Cupn%252Cexpire%26id%3D782b01f5511b174f%26mv%3Dm%26sver%3D3%26mt%3D1393643373

                                System.out.println(fmtArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private String makeUrl(String rawurl2, String sig2) {
            if (!rawurl2.toLowerCase().contains("ratebypass=")) {
                rawurl2 += "&ratebypass=yes";
            }
            if (rawurl2.toLowerCase().contains("signature=")) {
                rawurl2 += "&signature=" + sig2;
            }
            return rawurl2;
        }
    }

    public YoutubeParse() {
        // 构造http

    }

    public String extractVideoId(String url) {
        Pattern p = Pattern.compile("(?:^|[^\\w-]+)([\\w-]{11})(?:[^\\w-]+|$)");
        Matcher matcher = p.matcher(url);
        // for (int i = 0; i < groupCount; i++) {
        String group = matcher.group(1);
        System.out.println(group);
        // }
        return group;
    }

    public void fetchBasic(String videoId) {
        HashMap<String, String> videoInfoMap = getVideoInfo(videoId);
        title = videoInfoMap.get("title").replace("/", "-");
        author = videoInfoMap.get("author");
        videoid = videoInfoMap.get("video_id");
        rating = videoInfoMap.get("avg_rating");
        length = Long.parseLong(videoInfoMap.get("length_seconds"));
        viewcount = Integer.parseInt(videoInfoMap.get("view_count"));
        try {
            thumb = URLDecoder.decode(videoInfoMap.get("thumbnail_url"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // duration=//TODO:时长
        // TODO:解析视频格式,这货后面几个值是个啥
        // fmt_list=
        // 22/1280x720/9/0/115,
        // 43/640x360/99/0/0,
        // 18/640x360/9/0/115,
        // 5/320x240/7/0/0,
        // 36/320x240/99/1/0,
        // 17/176x144/99/1/0
        String fmtList = videoInfoMap.get("fmt_list");
        String[] fmtArray = fmtList.split(",");
        for (String fmt : fmtArray) {
            String[] format = fmt.split("/");
        }
        keywords = videoInfoMap.get("keywords").split(",");
        bigthumb = videoInfoMap.get("iurlsd");
        bigthumbhd = videoInfoMap.get("iurlsdmaxres");
        // ciphertag//TODO:这货标识是否使用密码签名 'use_cipher_signature': ['True']
        ciphertag = TextUtils.equals(videoInfoMap.get("use_cipher_signature"), "True");
        // 解析流列表(这货又是个啥)
        sm = extractStreamMap(UEFSM, videoInfoMap, TextUtils.isEmpty(jsurl));
        asm = extractStreamMap(AF, videoInfoMap, TextUtils.isEmpty(jsurl));
        // TODO:expiry链接超时时间
        have_basic = true;
        processStream();
    }

    public YoutubeStream processStream() {

        FmtStreamMap mapmap;
        // TODO:如果没有获取到基本信息那么重新获取
        // If you do not get the basic information so reacquired
        if (sm != null) {
            for (FmtStreamMap m : sm) {

                return new YoutubeStream(m, this);
            }
        }
        return null;
    }

    /** @param uefsm2
     * @param videoInfoMap
     * @param empty
     *            //这货是做啥用的 */
    private List<FmtStreamMap> extractStreamMap(String uefsm2, HashMap<String, String> videoInfoMap, boolean empty) {
        List<FmtStreamMap> streamMaps = new ArrayList<FmtStreamMap>();
        if (videoInfoMap != null && videoInfoMap.containsKey(uefsm2)) {
            String uefms2 = videoInfoMap.get(uefsm2);
            String[] uefms2s = uefms2.split(",");
            for (String s : uefms2s) {
                FmtStreamMap streamMap = parseFmtStreamMap(new Scanner(s), "utf-8");
                streamMaps.add(streamMap);
            }
        }
        return streamMaps;
    }

    /** 获取视频基本信息
     *
     * @param videoId */
    public HashMap<String, String> getVideoInfo(String videoId) {
        String url = String.format(VIDINFO, videoId);
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getData = new HttpGet(url);
        getData.setHeader("User-Agent", USERAGENT);
        HashMap<String, String> valueData = new HashMap<String, String>();
        try {
            HttpResponse execute = client.execute(getData);
            HttpEntity entity = execute.getEntity();
            String data = EntityUtils.toString(entity);
            // System.out.println(data);
            // String decode = URLDecoder.decode(data, "utf-8");
            // System.out.println(decode);
            parse(valueData, new Scanner(data), "utf-8");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valueData;
    }

    /************************** 工具类 *****************************/
    public static void parse(final HashMap<String, String> parameters, final Scanner scanner, final String encoding) {
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            final String[] nameValue = scanner.next().split(NAME_VALUE_SEPARATOR);
            if (nameValue.length == 0 || nameValue.length > 2)
                throw new IllegalArgumentException("bad parameter");

            final String name = decode(nameValue[0], encoding);
            String value = null;
            if (nameValue.length == 2)
                value = decode(nameValue[1], encoding);
            parameters.put(name, value);
        }
    }

    private FmtStreamMap parseFmtStreamMap(final Scanner scanner, final String encoding) {
        FmtStreamMap streamMap = new FmtStreamMap();
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            final String[] nameValue = scanner.next().split(NAME_VALUE_SEPARATOR);
            if (nameValue.length == 0 || nameValue.length > 2)
                throw new IllegalArgumentException("bad parameter");

            final String name = decode(nameValue[0], encoding);
            String value = null;
            if (nameValue.length == 2)
                value = decode(nameValue[1], encoding);

            // fallback_host=tc.v1.cache8.googlevideo.com&
            // s=9E89E8DE8FF59D59BA5F96D9A220724C1A304F634B2C19.55E8C8A3A7C02C3FBF4D274A85A41F5F55F0401B&
            // itag=17&
            // type=video%2F3gpp%3B+codecs%3D%22mp4v.20.3%2C+mp4a.40.2%22&
            // quality=small&
            // url=http%3A%2F%2Fr20---sn-a5m7lne6.googlevideo.com%2Fvideoplayback%3Fkey%3Dyt5%26ip%3D173.254.202.174%26mt%3D1393571459%26fexp%3D936112%252C937417%252C937416%252C913434%252C936910%252C936913%252C902907%26itag%3D17%26source%3Dyoutube%26sver%3D3%26mv%3Dm%26ms%3Dau%26sparams%3Dgcr%252Cid%252Cip%252Cipbits%252Citag%252Csource%252Cupn%252Cexpire%26ipbits%3D0%26expire%3D1393597755%26gcr%3Dus%26upn%3Du-4gaUCuZCM%26id%3D782b01f5511b174f

            if (TextUtils.equals("fallback_host", name)) {
                streamMap.fallbackHost = value;
            }
            if (TextUtils.equals("s", name)) {
                streamMap.s = value;
            }
            if (TextUtils.equals("itag", name)) {
                streamMap.itag = value;
            }
            if (TextUtils.equals("type", name)) {
                streamMap.type = value;
            }
            if (TextUtils.equals("quality", name)) {
                streamMap.quality = value;
            }
            if (TextUtils.equals("url", name)) {
                streamMap.url = value;
            }
            if (TextUtils.equals("sig", name)) {
                streamMap.sig = value;
            }
        }
        return streamMap;
    }

    private static String decode(final String content, final String encoding) {
        try {
            return URLDecoder.decode(content, encoding != null ? encoding : HTTP.DEFAULT_CONTENT_CHARSET);
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }
}
