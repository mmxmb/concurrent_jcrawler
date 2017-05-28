package lawnbway.jcrawler;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import lawnbway.jcrawler.util.*;

/**
 * CrawlManager a class that stores and updates a set
 * of visited links and a queue of links that are not yet visited.
 * Its main function is to send CrawlJobs to individual webpages 
 * and, if the webpage is an HTML document, look for a specified word
 * on that webpage. Under normal conditions (when no exceptions were thrown),
 * the search terminates when one of the following conditions it true:
 * 
 * 1) size of pagesVisited set is bigger than MAX_PAGES_TO_SEARCH
 * 2) specified word is found by a dispatched CrawlJob
 */
public class CrawlManager {
	
	private static final int MAX_PAGES_TO_SEARCH = 500;
	protected Set<String> pagesVisited = new HashSet<String>();
	protected Deque<String> pagesToVisit = new LinkedList<String>();
	protected StringBuffer words;
	

	/**
	 * Gets an oldest URL address from the queue of links that
	 * were not visited.
	 * 
	 * Since the links are added to pagesToVisit without verifying
	 * whether they were visited in the past, this method ensures
	 * that only the unvisited links are given to the new CrawlJobs.
	 * 
	 * @return next unvisited url from the queue of pages to visit
	 */
	protected String nextUrl() {
		
		String nextUrl = this.pagesToVisit.removeFirst();
		// look for a valid url that has not been visited
		while (URLUtil.isInvalidUrl(nextUrl) || AuthorityCheckerSingleton.INSTANCE.isNotSameAuthority(nextUrl)
				|| pagesVisited.contains(nextUrl)) {
			nextUrl = this.pagesToVisit.removeFirst();
		} 
		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	/**
	 * This method is a starting point of the crawl. The crawl starts
	 * at the provided URL address, where all the hyperlinks are collected.
	 * A CrawlJob is dispatched to every hyperlink in order to collect
	 * all the hyperlinks from that webpage and look for a search word.
	 * 
	 * The search terminates if the maximum number of pages was visited
	 * or if the search word was found on one of the pages.
	 * 
	 * @see CrawlJob
	 * 
	 * @param url	the starting point of the search
	 * @param searchWord	the word that the crawler is looking for
	 */
	public void search(String url, String searchWord) {
		
		if(URLUtil.isInvalidUrl(url)) {
			System.out.println(String.format("ERROR! %s is not a valid URL address", url));
			System.exit(1);
		}
		bePolite(url);	// exclude links from robots.txt from the search
		AuthorityCheckerSingleton.INSTANCE.setAuthorityUrl(url);
		
		JobResult overallResult = new WordSearchResult(false, false, url);
		
        while (overallResult.isUnsuccessful() && this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
        	overallResult = dispatchSearchCrawler(url, searchWord);       
        }
        // close file handles in UserAgent
        UserAgentManagerSingleton.INSTANCE.cleanup();
        if(overallResult.isSuccessful()) {
            System.out.println(String.format("**Success** Word %s found at %s", searchWord, overallResult.getCurrentUrl())); //current URL was here
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
	
	/**
	 * Dispatches a single CrawlJob to a chosen URL address to collect
	 * all the hyperlinks and look for the search word. Since the main aim
	 * of each CrawlJob and the crawl overall is to find the search word,
	 * the JobResult will be unsuccessful unless the word is found.
	 * 
	 * @param url	only used by the very first CrawlJob
	 * @param searchWord	the word that the crawler is looking for
	 * @return result of the call to searchForWord method in CrawlJob class
	 */
	protected JobResult dispatchSearchCrawler(String url, String searchWord) {
		
		String currentUrl;   
        // first search only
        if(this.pagesToVisit.isEmpty()) {
            currentUrl = url;
            this.pagesVisited.add(url);
        }
        else
            currentUrl = this.nextUrl();
        
        SearchCrawlJob spider = new SearchCrawlJob();
        CrawlResult crawlResult = spider.crawl(currentUrl);
        WordSearchResult wordSearchResult = new WordSearchResult(false, false, currentUrl); 
        
        if(crawlResult.isSuccessful())
        	wordSearchResult = spider.searchForWord(searchWord);
        this.pagesToVisit.addAll(spider.getLinks());
        
        return wordSearchResult;
	}
	
	public void createWordCloud(String url){
		if(URLUtil.isInvalidUrl(url)) {
			System.out.println(String.format("ERROR! %s is not a valid URL address", url));
			System.exit(1);
		}
		bePolite(url);	// exclude links from robots.txt from the search
		AuthorityCheckerSingleton.INSTANCE.setAuthorityUrl(url);
		words = new StringBuffer(); // all collected bodyText from crawled pages will be stored here
		
		while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
        	dispatchWordCloudCrawler(url);       
        }
		writeWordsToFile(words.toString());
		makeWordCloud();
        // close file handles in UserAgent
        UserAgentManagerSingleton.INSTANCE.cleanup();
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));		
	}
	
	protected JobResult dispatchWordCloudCrawler(String url) {
		String currentUrl;   
        // first search only
        if(this.pagesToVisit.isEmpty()) {
            currentUrl = url;
            this.pagesVisited.add(url);
        }
        else
            currentUrl = this.nextUrl();
        
        WordCloudCrawlJob spider = new WordCloudCrawlJob();
        CrawlResult crawlResult = spider.crawl(currentUrl);
        WordCollectResult wordCollectResult = new WordCollectResult(false, false, currentUrl); 
        
        if (crawlResult.isSuccessful())
        	wordCollectResult = spider.collectWords();
        if (wordCollectResult.isSuccessful())
        	words.append(spider.getBodyText());
        this.pagesToVisit.addAll(spider.getLinks());
        
        return wordCollectResult;
	}
	
	protected void writeWordsToFile(String words){
		try( PrintWriter out = new PrintWriter("tmp")){
		    out.println(words);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR! Can't create output text file.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	protected void makeWordCloud() {
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		final List<WordFrequency> wordFrequencies;
		try {
			wordFrequencies = frequencyAnalyzer.load("tmp");
			final Dimension dimension = new Dimension(600, 600);
			final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
			wordCloud.setPadding(2);
			wordCloud.setBackground(new CircleBackground(300));
			wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
			wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
			wordCloud.build(wordFrequencies);
			wordCloud.writeToFile("datarank_wordcloud_circle_sqrt_font.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses robots.txt, gets list of links that should
	 * be excluded from the search and adds them to the
	 * set of visited pages.
	 * 
	 * @param url	address of website where this crawler will be polite
	 * 
	 * @see RobotsUtil
	 */
	protected void bePolite(String url) {
		
		RobotsUtil.parse(url);
		this.pagesVisited.addAll(RobotsUtil.getLinks());
	}
	
}
