package lawnbway.jcrawler;

import java.io.IOException;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordCloudCrawlJob extends CrawlJob{
	
	private static final int USER_AGENT_SWITCH_PROBABILITY = 30;
	protected Document htmlDocument;
	protected WordCollectResult wordCollectResult;
	private String bodyText;
	
	
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
	
	public WordCollectResult collectWords(){
		wordCollectResult = new WordCollectResult(false, false, getUrl()); // word search result is false by default, no error occured
		if(this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return wordCollectResult;
        }
        setBodyText(this.htmlDocument.body().text().toLowerCase());
        wordCollectResult.setSuccess(true);
		return wordCollectResult;
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
	 * Gets the list links that were found by this crawl job.
	 * May contain invalid links, since the links are not validated at this point.
	 * 
	 * @return list of URL links.
	 */
	public List<String> getLinks(){
		return this.links;
	}
	
	public String getBodyText() {
		return bodyText;
	}
	
	public void setBodyText(String text) {
		bodyText = text;
	}

}
