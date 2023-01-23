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

    private static final String APIKEY = "RGAPI-523832c5-45cd-497b-8488-8c1130c4e038";

    public static String encodeString(String s) {
        try {
            return new String(URLEncoder.encode(s, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document getDocumentFromURL(String url) {
        try {
            return Jsoup.connect(url).header("X-Riot-Token", APIKEY).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJSONFromURL(String url) {
        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).header("X-Riot-Token", APIKEY).build();
        HttpResponse<String> httpResponse = null;

        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (httpResponse.statusCode() != 200)
            throw new RuntimeException("Failed : HTTP Error code : " + httpResponse.statusCode());

        return httpResponse.body();
    }

    public static void main(String[] args) {

    }

    public static long getTimestampInSeconds() {
        Date date = new Date();
        return date.getTime()/1000-2592000;
    }
}
