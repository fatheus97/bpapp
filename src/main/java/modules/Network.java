package modules;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class Network {
    private static int count = 0;
    private static final Properties props = new Properties();
    static {
        try {
            props.load(Network.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String APIKEY = props.getProperty("api.key");
    private static final Bucket riotBucket = Bucket.builder()
            .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(1))))
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofSeconds(135))))
            .build();
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
            queryArray[i] = queryArray[i].substring(0, queryArray[i].indexOf("=") + 1) + URLEncoder.encode(queryArray[i].substring(queryArray[i].indexOf("=") + 1).replaceAll("(?<==)'([^']*)'([^']*)'","'$1\\\\'$2'"));
        }
        query = String.join("&", queryArray);
        System.out.println(baseURL + query);
        return baseURL + query;
    }

    public static Document getDocumentFromURLString(String urlString) throws IOException {
        Connection.Response response = Jsoup.connect(encodeURLString(urlString)).execute();
        if (response.statusCode() == 504)
            throw new IOException(urlString + " is down!");
        else if (response.statusCode() != 200)
            throw new IOException("Failed : HTTP Error code : " +
                    response.statusCode() + "\n" +
                    urlString);

        return response.parse();
    }

    public static String getJSONFromURLString(String urlString) throws URISyntaxException, IOException, InterruptedException {

        URI uri = new URI(encodeURLString(urlString));
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();
        if(urlString.contains("api.riotgames.com")){
            httpRequest = HttpRequest.newBuilder(uri)
                    .header("X-Riot-Token", APIKEY)
                    .build();
            riotBucket.asBlocking().consume(1);
            System.out.println(count++ + "  " + urlString);
        }
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 403)
            throw new IOException("You don't have access to " + uri.getHost());
        else if (httpResponse.statusCode() != 200)
            throw new IOException("Failed : HTTP Error code : " +
                    httpResponse.statusCode() + "\n" +
                    urlString);

        return httpResponse.body();
    }
}
