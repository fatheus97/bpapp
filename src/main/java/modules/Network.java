package modules;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class Network {

    private static final String APIKEY = "RGAPI-39f6b2d8-b23a-4864-bdc2-a6bf2fa5990c";

    public static String encodeURLString(String urlString) {

        int endIndex;
        if (urlString.contains("?"))
            endIndex = urlString.substring(0, urlString.indexOf("?")).lastIndexOf("/") + 1;
        else
            endIndex = urlString.lastIndexOf("/") + 1;
        String baseURL = urlString.substring(0, endIndex);
        String query = urlString.substring(endIndex);

        String[] queryArray = query.split("&");

        for (int i = 0; i < queryArray.length; i++) {
            queryArray[i] = queryArray[i].substring(0, queryArray[i].indexOf("=") + 1) + URLEncoder.encode(queryArray[i].substring(queryArray[i].indexOf("=") + 1));
        }
        query = String.join("&", queryArray);
        return baseURL + query;
    }

    public static Document getDocumentFromURLString(String urlString) throws IOException {
        return Jsoup.connect(encodeURLString(urlString)).header("X-Riot-Token", APIKEY).get();
    }

    public static String getJSONFromURLString(String urlString) throws URISyntaxException, IOException, InterruptedException {
        URI uri = new URI(encodeURLString(urlString));
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).header("X-Riot-Token", APIKEY).build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


        if (httpResponse.statusCode() != 200)
            throw new RuntimeException("Failed : HTTP Error code : " + httpResponse.statusCode());

        return httpResponse.body();
    }
}
