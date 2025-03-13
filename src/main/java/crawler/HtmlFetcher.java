package crawler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HtmlFetcher {
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)  // Follow redirects
            .build();

    public static String fetch(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("User-Agent", "Mozilla/5.0")
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("FETCHED: " + url + " | Status: " + response.statusCode());

            if (response.statusCode() == 301 || response.statusCode() == 302) {
                System.out.println("REDIRECT DETECTED! ");
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR fetching: " + url);
            return null;
        }
    }
}
