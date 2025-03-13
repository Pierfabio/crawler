package crawler;

public class CrawlTask implements Runnable {
    private final String url;
    private final WebCrawler crawler;

    public CrawlTask(String url, WebCrawler crawler) {
        this.url = url;
        this.crawler = crawler;
    }

    @Override
    public void run() {
        crawler.crawl(url);
    }
}
