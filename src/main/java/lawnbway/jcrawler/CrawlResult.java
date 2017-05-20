package lawnbway.jcrawler;

/**
 * CrawlResult acts as a 3-tuple that is used by the CrawlJob and CrawlManager classes.
 * Represents the state of the current crawl.
 */
public class CrawlResult extends JobResult{

	/**
	 * Creates an instance of the CrawlResult class.
	 * 
	 * @param result	the result of the current crawl
	 * @param errorStatus    the error status of the crawl
	 * @param url	the URL address of the current crawl
	 */
	public CrawlResult(boolean result, boolean errorStatus, String url) {
		super(result, errorStatus, url);
	}
}
