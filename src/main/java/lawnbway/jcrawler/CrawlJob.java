package lawnbway.jcrawler;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import lawnbway.jcrawler.util.TextCleaningUtil;

/**
 * CrawlJob represents a single unit of crawling activity performed on a specified HTML document.
 * Its main functions are to gather every hyperlink from the provided HTML document
 * and search for a specified key word (searchWord) in that document. It also stores
 * the collected hyperlinks in a list, ready to provide them to a CrawlManager.
 * 
 * @see CrawlManager
 */
public class CrawlJob {
	
	private static final int USER_AGENT_SWITCH_PROBABILITY = 30;
	protected String usrAgent;
	protected List<String> links = new LinkedList<String>(); // list of URLs that are gathered from the current URL
	protected Document htmlDocument; 
	protected String url;
	protected String searchWord;
	volatile protected CrawlResult crawlResult;
	volatile protected WordSearchResult wordSearchResult;
	
	/**
	 * Sends a HTTP GET request to a specified url.
	 * In case the requested document is HTML, collects all the 
	 * links from the page (regardless of reply status code) and
	 * saves them to a list.
	 * 
	 * @param url	the address of the document to be crawled
	 * @return result of the crawl job; conatins status (successful or unsuccessful) and URL of the document
	 */
	public CrawlResult crawl(String url) {
		
		setUrl(url);
		crawlResult = new CrawlResult(false, false, url);  // crawl result is false by default, no error occured
		
        try {
        	switchUsrAgent(); // changes user agent every once in a while
            Connection connection = Jsoup.connect(getUrl()).userAgent(getUsrAgent());
            
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            
            // if status code is other than 200, the page is unlikely to contain any links,
            // so it is safe to proceed regardless
            if(connection.response().statusCode() == 200) {
            	System.out.println("**Visiting** Received web page at " + getUrl());
            }
            
            // abort the crawl with negative (unsuccessful) result if document is not in HTML format
            if(!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return crawlResult;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");    // find all html links on the page
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));    // add the links to the list of links from the page
            }
            crawlResult.setSuccess(true);
            return crawlResult;
            // when the method's attempt to get the document at specified URL address is unsuccessful
        } catch(IOException ioe) {
        	crawlResult.setErrorStatus(true);
            System.out.println("ERROR! Error in HTTP request. " + ioe);
            return crawlResult;
        }
    }
	
	
	/**
	 * Method should only be called after successful crawl!
	 * Looks for a specified word on the current webpage.
	 * When the webpage body text is read, it is converted to all lowercase.
	 * Therefore, the search is case-insensetive.
	 * 
	 * @param searchWord	the String to search for on the current page
	 * @return true if the searchWord is found in the current htmlDocument, false if not found
	 */
	protected WordSearchResult searchForWord(String searchWord){
		
		setSearchWord(searchWord);
		wordSearchResult = new WordSearchResult(false, false, getUrl()); // word search result is false by default, no error occured
		if(this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return wordSearchResult;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        boolean containsSearchWord = bodyText.toLowerCase().contains(getSearchWord().toLowerCase());
        wordSearchResult.setSuccess(containsSearchWord);
        return wordSearchResult;
    }
	
	/**
	 * Gets the list links that were found by this crawl job.
	 * May contain invalid links, since the links are not validated at this point.
	 * 
	 * @return list of URL links.
	 */
	public List<String> getLinks(){
		return this.links;
	}
	
	/**
	 * Switches current user agent to another random user agent
	 * with a certain probability, specified by USER_AGENT_SPOOF_PROBABILITY.
	 * 
	 * @see UserAgentManager
	 */
	protected void switchUsrAgent() {
		UserAgentManagerSingleton.INSTANCE.changeUserAgent(USER_AGENT_SWITCH_PROBABILITY);
    	setUsrAgent(UserAgentManagerSingleton.INSTANCE.getName());
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
	
	/**
	 * Sets the search word assigned of this crawl job.
	 * 
	 * @param searchWord	Search word assigned to this job
	 */
	protected void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	
	/**
	 * Gets the search word assigned of this crawl job.
	 * 
	 * @return search word assigned to this job
	 */
	protected String getSearchWord() {
		return searchWord;
	}
	
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
	 * Gets the word search result of this crawl job.
	 * 
	 * @return reference to WordSearchResult object
	 */
	protected WordSearchResult getWordSearchResult() {
		return wordSearchResult;
	}
}