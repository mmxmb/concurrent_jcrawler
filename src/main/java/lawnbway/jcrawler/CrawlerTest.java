package lawnbway.jcrawler;

import java.io.IOException;

public class CrawlerTest {
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
        CrawlManager spiderManager = new CrawlManager();
        spiderManager.search("https://en.wikipedia.org/wiki/Main_Page","word");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time: " + totalTime);
    }
}
