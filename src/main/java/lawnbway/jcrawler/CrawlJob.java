package lawnbway.jcrawler;

import java.util.LinkedList;
import java.util.List;

/**
 * Crawljob is an abstract class that represents the state
 * of an arbitrary job performed at a specified URL address.
 */
public abstract class CrawlJob {
	
	protected String url;
	volatile protected CrawlResult crawlResult;
	protected List<String> links = new LinkedList<String>(); // list of URLs that are gathered from the current URL
	protected String usrAgent;

	
	public abstract CrawlResult crawl(String url);
	
	/**
	 * Sets the URL address assigned of this crawl job.
	 * 
	 * @param searchWord	URL address assigned to this job
	 */
	protected void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Gets the URL address assigned of this crawl job.
	 * 
	 * @return URL address assigned to this job
	 */
	protected String getUrl() {
		return url;
	}
	
	/**
	 * Gets the crawl result of this crawl job.
	 * 
	 * @return reference to CrawlResult object
	 */
	protected CrawlResult getCrawlResult() {
		return crawlResult;
	}
	
	
	/**
	 * Sets the user agent for this crawl job.
	 * 
	 * @param name    name of the current user agent
	 */
	protected void setUsrAgent(String name) {
		usrAgent = name;
	}
	
	/**
	 * Gets  the user agent of this crawl job.
	 * 
	 * @return name of the current user agent
	 */
	protected String getUsrAgent() {
		return usrAgent;
	}
	
	protected abstract void switchUsrAgent();
	public abstract List<String> getLinks();

}
