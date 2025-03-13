package crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class WebCrawler {
    private final String domain;
    private final String originalUrl;
    private final LinkedHashSet<String> collectedLinks = new LinkedHashSet<>(); // Keeps order & uniqueness

    public WebCrawler(String startUrl) {
        this.originalUrl = normalizeUrl(startUrl); // Store normalized original URL
        this.domain = UrlUtils.getDomain(startUrl);
        crawl(startUrl);
    }

    public void crawl(String url) {
        String html = HtmlFetcher.fetch(url);
        if (html != null) {
            processPage(html);
        }
    }

    public void processPage(String html) {
        // Extract and normalize links from ONLY this page
        Set<String> newLinks = new HashSet<>();
        for (String link : LinkExtractor.extractLinks(html, domain)) {
            newLinks.add(normalizeUrl(link)); // Normalize before adding
        }

        collectedLinks.addAll(newLinks);
    }

    public void start() {
        // Convert Set to List to sort it properly
        List<String> sortedLinks = new ArrayList<>(collectedLinks);

        Collections.sort(sortedLinks); // Sort alphabetically

        // Print the sorted links
        for (String link : sortedLinks) {
            System.out.println(link);
        }
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) return "";

        try {
            // Convert to URI to normalize properly
            URI uri = new URI(url).normalize();

            String scheme = (uri.getScheme() == null) ? "https" : uri.getScheme().toLowerCase(); // Default to HTTPS
            String host = (uri.getHost() == null) ? "" : uri.getHost().toLowerCase(); // Normalize case
            String path = (uri.getPath() == null) ? "" : uri.getPath();

            // Remove trailing slash
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }

            // Rebuild the URL (strict normalization)
            return new URI(scheme, host, path, null).toASCIIString();
        } catch (URISyntaxException e) {
            return url.trim().toLowerCase(); // Fallback if URL parsing fails
        }
    }
}