package lawnbway.jcrawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
	

	
	
	protected void writeWordsToFile(String words){
		try( PrintWriter out = new PrintWriter("tmp")){
		    out.println(words);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR! Can't create output text file.");
			e.printStackTrace();
			System.exit(1);
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
