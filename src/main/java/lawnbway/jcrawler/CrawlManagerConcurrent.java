package lawnbway.jcrawler;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lawnbway.jcrawler.util.URLUtil;

/**
 * CrawlManagerConcurrent is a version of CrawlManager that supports multithreading.
 * This class uses FixedThreadPool, that creates and reuses fixed number of threads.
 * Only N_THREAD threads exist at any point of time.
 * 
 * @see CrawlManager
 */
public class CrawlManagerConcurrent extends CrawlManager {

	private static final int MAX_PAGES_TO_SEARCH = 1000;
	private static final int N_THREAD = 5;
	private ConcurrentLinkedQueue<String> pagesToVisit = new ConcurrentLinkedQueue<String>();
	private JobResult overallResult;
	
	/**
	 * Almost identical to the nextUrl method in the parent class.
	 * An override was needed since this class uses ConcurrentLinkedQueue
	 * rather than LinkedList.
	 * 
	 * @see CrawlManager
	 * 
	 * @return next unvisited url from the queue of pages to visit
	 */
	protected String nextUrl(){
		String nextUrl = this.pagesToVisit.poll();

		// look for a valid url under same hostname that has not been visited
		while (URLUtil.isInvalidUrl(nextUrl) || AuthorityCheckerSingleton.INSTANCE.isNotSameAuthority(nextUrl) 
				|| super.pagesVisited.contains(nextUrl)) {
			nextUrl = this.pagesToVisit.poll();	
		}
		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	/**
	 * This method is a starting point of the crawl. It is similar to
	 * CrawlManager, but uses multiple threads to dispatch and process
	 * crawl jobs simultaneously. 
	 * 
	 * <p>Since FixedThreadPool pool service is used to dispatch and process 
	 * the jobs, only a limited number of threads is alive at any given time. 
	 * The exact number of threads to use is specified by N_THREAD.</p>
	 * 
	 * The search terminates if the maximum number of pages was visited
	 * or if the search word was found on one of the pages.
	 * 
	 * @see CrawlManager
	 * 
	 * @param url	
	 * The starting point of the search
	 * @param searchWord	The word that the crawler is looking for
	 */
	@Override
	public void search(String url, String searchWord) {
		
		if(URLUtil.isInvalidUrl(url)) {
			System.out.println(String.format("ERROR! %s is not a valid URL address", url));
			System.exit(1);
		}
		bePolite(url);	// exclude links from robots.txt from the search
		AuthorityCheckerSingleton.INSTANCE.setAuthorityUrl(url);
		
		ExecutorService pool = Executors.newFixedThreadPool(N_THREAD);
		
		overallResult = new WordSearchResult(false, false, url);
		
        while (overallResult.isUnsuccessful() && this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
        	overallResult = dispatchSearchCrawler(url, searchWord, pool);
        }
        
        // stops execution of all running/waiting threads
        shutdownAndAwaitTermination(pool);
        // close file handles in UserAgent
        UserAgentManagerSingleton.INSTANCE.cleanup();
        
        if(overallResult.isSuccessful()) {
            System.out.println(String.format("**Success** Word %s found at %s", searchWord, overallResult.getCurrentUrl())); //current URL was here
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
	
	/**
	 * Dispatches a thread that starts CrawlJobConcurrent at a chosen URL, 
	 * collects all the hyperlinks and looks for the search word. 
	 * 
	 * @see CrawlManager
	 * @see CrawlJobConcurrent
	 * 
	 * @param url	only used by the very first CrawlJob
	 * @param searchWord	the word that the crawler is looking for
	 * @param pool    reference to the fixed thread pool executor service
	 * @return result of the call to searchForWord method in CrawlJob class
	 */
	protected JobResult dispatchSearchCrawler(String url, String searchWord, ExecutorService pool) {
		
		String currentUrl;
		
        // first search only
        if(this.pagesToVisit.isEmpty()) {
            currentUrl = url;
            this.pagesVisited.add(url);
        }
        else
            currentUrl = this.nextUrl();
        
        SearchCrawlJobConcurrent spider = new SearchCrawlJobConcurrent("Thread-" +  + this.pagesVisited.size(), currentUrl, searchWord);
        pool.execute(spider); // runs search method in CrawlJob concurrent, and if successful, runs searchForWord
        
    	// wait until the thread finishes the crawlJob before 
        try {
			spider.getCrawlJobLatch().await();
		} catch (InterruptedException e) {
			System.out.println("Waiting thread " + spider.threadName + " was interrupted.");
			e.printStackTrace();
		}
        
        this.pagesToVisit.addAll(spider.getLinks());
        return spider.getWordSearchResult();
	}
	
	/**
	 * Shuts down FixedThreadPool without waiting for threads to finish execution.
	 * This is done to avoid overwrite to overallResult. If any thread is still 
	 * running 10 seconds after the method call, display error message.
	 * 
	 * Taken from: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
	 * 
	 * @param pool    reference to the fixed thread pool executor service
	 */
	protected void shutdownAndAwaitTermination(ExecutorService pool) {
		  pool.shutdownNow();// Disable new tasks from being submitted
		   try {
		       if (!pool.awaitTermination(10, TimeUnit.SECONDS))
		           System.err.println("ERROR! Fixed Thread Pool did not terminate.");
		   } catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
		     pool.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
		 }
}