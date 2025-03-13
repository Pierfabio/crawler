
import crawler.WebCrawler;

public class Main {

    public static void main(String[] args) {
        String startUrl = "https://www.ecosio.com";
        WebCrawler crawler = new WebCrawler(startUrl);
        crawler.start();
    }

}
