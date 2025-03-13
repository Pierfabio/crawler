package crawler;

import java.net.URI;

public class UrlUtils {
    public static String getDomain(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return "";
        }
    }
}
