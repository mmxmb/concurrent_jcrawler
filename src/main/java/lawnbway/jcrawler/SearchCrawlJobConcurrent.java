package lawnbway.jcrawler;
import java.util.concurrent.CountDownLatch;



/**
 * SearchCrawlJobConcurrent represents a single unit of crawling 
 * activity performed on a specified HTML document. Each crawl job 
 * is performed by one of the threads from the FixedThreadPool that 
 * is instantiated by the CrawlManagerConcurrentObject.
 * 
 * In order to support multithreading, the class was changed
 * substantially compared to its parent - CrawlJob.
 * 
 * @see CrawlJob
 */
public class SearchCrawlJobConcurrent extends SearchCrawlJob implements Runnable{
	
	public Thread thread;
	public String threadName;
	private CountDownLatch crawlJobLatch;
	
	/**
	 * Creates an instance of CrawlJoboncurrent class. Sets
	 * the name of the thread that processes the crawl job.
	 * Instantiates false CrawlResult and CountDownResult
	 * in case exception happens half-way through processing
	 * the job and the thread never reaches the areas of code
	 * that actually set these values to what they should be.
	 * Creates a CountDownLatch that prevents the thread running
	 * the CrawlManagerConcurrent from using the CrawlJobResult
	 * before the crawl job is completed.
	 * 
	 * @param threadName	the name of the executing thread
	 * @param url    the address of the document to be crawled
	 * @param searchWord    the String to search for on the current page
	 */
	SearchCrawlJobConcurrent(String threadName, String url, String searchWord) {
		this.threadName = threadName;
		setUrl(url);
		setSearchWord(searchWord);
		crawlResult = new CrawlResult(false, false, url);  // crawl result is false by default, no error occured
		wordSearchResult = new WordSearchResult(false, false, url); // word search result is false by default, no error occured
		this.crawlJobLatch = new CountDownLatch(1);
	}
	
	/**
	 * Starts a new thread that processes the crawl jobs.
	 */
	public void start() {
	      System.out.println("Starting " +  threadName );
	      if (thread == null) {
	         thread = new Thread(threadName);
	         thread.start();
	      }
	}
	
	/**
	 * The processing task for a thread consists of performing 
	 * a crawl and, if crawl is successful, a word search.
	 */
	public void run(){
       crawlResult = crawl(getUrl());
       if(crawlResult.isSuccessful()) {
    	   wordSearchResult = searchForWord(getSearchWord());
       }
       else {
    	    crawlJobLatch.countDown();
       }
    }
	
	
	/**
	 * The only difference from the overriden method in the parent class
	 * is the added countDown method call that relesaes the waiting thread in
	 * the CrwalJobConcurrent instance.
	 * 
	 * @see CrawlJob
	 * 
	 * @param searchWord	the String to search for on the current page
	 * @return WordSearchResult    the state of the current word search
	 */
	@Override
	protected WordSearchResult searchForWord(String searchWord) {
	
		if(this.htmlDocument == null) {
	        System.out.println("ERROR! Call crawl() before performing analysis on the document");
	        return wordSearchResult;
	    }
	    System.out.println("Searching for the word " + searchWord + "...");
	    String bodyText = this.htmlDocument.body().text();
	    boolean containsSearchWord = bodyText.toLowerCase().contains(getSearchWord().toLowerCase());
	    crawlJobLatch.countDown();
	    wordSearchResult.setSuccess(containsSearchWord);
	    return wordSearchResult;
	}
	
	/**
	 * Gets crawlJobLatch.
	 * 
	 * @return CountDownLatch    prevents thread running the CrawlJobManager from accessing CrawlJob result before it's done.
	 */
	public CountDownLatch getCrawlJobLatch(){
		return crawlJobLatch;
	}
}