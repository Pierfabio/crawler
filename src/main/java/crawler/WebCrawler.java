package crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class WebCrawler {
    private final String domain;
    private final String originalUrl;
    private static final Integer MAX_THREADS = 10;
    private final LinkedHashSet<String> collectedLinks = new LinkedHashSet<>(); // Keeps order & uniqueness
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS); // Adjust the thread pool size as needed


    public WebCrawler(String startUrl) {
        this.originalUrl = normalizeUrl(startUrl); // Store normalized original URL
        this.domain = UrlUtils.getDomain(startUrl);
        crawl(startUrl);
    }

    public void crawl(String url) {
        // Submit a task to the executor service for concurrent processing
        executorService.submit(() -> {
            String html = HtmlFetcher.fetch(url);
            if (html != null) {
                processPage(html);
            }
        });
    }

    private void processPage(String html) {
        // Extract and normalize links from ONLY this page
        Set<String> newLinks = new HashSet<>();
        for (String link : LinkExtractor.extractLinks(html, domain)) {
            newLinks.add(normalizeUrl(link)); // Normalize before adding
        }

        collectedLinks.addAll(newLinks);
        sortLinks();
    }
    private void sortLinks() {
        List<String> sortedLinks = new ArrayList<>(collectedLinks);
        Collections.sort(sortedLinks); // Sort alphabetically
        printLinks(sortedLinks, 0); // Start recursive printing from index 0
    }

    private void printLinks(List<String> sortedLinks, int index) {
        if (index >= sortedLinks.size()) {
            return; // Stop when all links are printed
        }

        System.out.println(sortedLinks.get(index)); // Print current link
        printLinks(sortedLinks, index + 1); // Recursive call for next link
    }

    public void shutdown() {
        try {
            // Initiate graceful shutdown
            executorService.shutdown();
            // Wait for all tasks to finish
            if (!executorService.awaitTermination(90, TimeUnit.SECONDS)) {
                // If tasks did not finish within 60 seconds, force shutdown
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // If interrupted while waiting for termination, force shutdown
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
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