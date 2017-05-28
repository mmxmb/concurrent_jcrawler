package lawnbway.jcrawler;

import java.io.IOException;

public class CrawlerTest {
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
        CrawlManager spiderManager = new CrawlManager();
        spiderManager.createWordCloud("https://en.wikipedia.org/wiki/Main_Page");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time: " + totalTime);
        
		
		
		
		//System.out.println(TextCleaningUtil.removeNonLetterChars(test));
		//System.out.println(TextCleaningUtil.removeArticles(test));
    }
}
