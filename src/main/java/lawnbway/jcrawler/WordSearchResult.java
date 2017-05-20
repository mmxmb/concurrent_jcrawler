package lawnbway.jcrawler;

/**
 * WordSearchResult acts as a 3-tuple that is used by the CrawlJob and CrawlManager classes.
 * Represents the state of the current word search (searchForWord method in CrawlJob class)
 */
public class WordSearchResult extends JobResult{
	/**
	 * Creates an instance of the WordSearchResult class.
	 * 
	 * @param result	the result of the current word search
	 * @param errorStatus    the error status of the job
	 * @param url	the URL address of the current word search
	 */
	public WordSearchResult(boolean result, boolean errorStatus, String url) {
		super(result, errorStatus, url);
	}
}