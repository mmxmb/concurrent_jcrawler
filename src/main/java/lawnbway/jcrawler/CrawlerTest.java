package lawnbway.jcrawler;

import lawnbway.jcrawler.util.TextCleaningUtil;

public class CrawlerTest {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
        CrawlManager spiderManager = new CrawlManagerConcurrent();
        spiderManager.search("https://en.wikipedia.org", "plank");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time: " + totalTime);
		//System.out.println(TextCleaningUtil.removeNonLetterChars(test));
		//System.out.println(TextCleaningUtil.removeArticles(test));
    }
}
