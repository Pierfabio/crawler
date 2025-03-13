package crawler;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a\\s+[^>]*href=[\"']([^\"'#]+)[\"']", Pattern.CASE_INSENSITIVE);

    public static Set<String> extractLinks(String html, String domain) {
        Set<String> links = new HashSet<>();
        Matcher matcher = LINK_PATTERN.matcher(html);
        while (matcher.find()) {
            String link = matcher.group(1);

            if (link.startsWith("/")) {  // Convert relative to absolute
                link = "https://" + domain + link;
            }

            if (link.contains(domain)) { // Ensure it's an internal link
                links.add(link);
            }
        }

        System.out.println("Extracted Links: " + links.size());

        return links;
    }
}
