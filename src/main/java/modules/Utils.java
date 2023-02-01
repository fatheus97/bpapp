package modules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

public class Utils {

    private static final String APIKEY = "RGAPI-9c2c4b81-ca1f-46ad-bf56-8eec15d398e8";

    public static String encodeString(String s) {
        // TODO: 25/01/2023 change to whole URL splitting, encoding and joining again function 
        try {
            return new String(URLEncoder.encode(s, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document getDocumentFromURL(String url) throws IOException {
        return Jsoup.connect(url).header("X-Riot-Token", APIKEY).get();
    }

    public static String getJSONFromURL(String url) throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(url);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).header("X-Riot-Token", APIKEY).build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


        if (httpResponse.statusCode() != 200)
            throw new RuntimeException("Failed : HTTP Error code : " + httpResponse.statusCode());

        return httpResponse.body();
    }

    public static long getTimestampInSeconds(int days) {
        Date date = new Date();
        return date.getTime()/1000-(days*24*60*60);
    }
}
