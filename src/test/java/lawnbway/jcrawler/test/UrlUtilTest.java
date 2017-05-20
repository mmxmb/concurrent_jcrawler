package lawnbway.jcrawler.test;

import static org.junit.Assert.*;

import org.junit.Test;
import lawnbway.jcrawler.util.URLUtil;

public class UrlUtilTest {

        @Test
        public void getRootUrlShouldReturnRoot() {

                assertEquals("Root of https://www.google.com/humans.txt must be https://www.google.com", 
                		"https://www.google.com", URLUtil.getRootUrl("https://www.google.com/humans.txt"));
                assertEquals("Root of https://news.ycombinator.com/newsfaq.html must be https://news.ycombinator.com", 
                		"https://news.ycombinator.com", URLUtil.getRootUrl("https://news.ycombinator.com/newsfaq.html")); //no wwww
                assertEquals("Root of http://www.reddit.com/wiki/reddiquette must be http://reddit.com", 
                		"http://www.reddit.com", URLUtil.getRootUrl("http://www.reddit.com/wiki/reddiquette")); // no 's' in https
                assertEquals("Root of http://www.5z8.info/worm_g9m4gh_refinance-now must be http://www.5z8.info", 
                		"http://www.5z8.info", URLUtil.getRootUrl("http://www.5z8.info/worm_g9m4gh_refinance-now"));
                assertEquals("Root of http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM must be http://www.amazon.com", 
                		"http://www.amazon.com", URLUtil.getRootUrl("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM")); //long get request     
        }
        
        @Test
        public void getAuthorityShouldReturnHostName() {
        	
            assertEquals("Hostname of https://www.google.com/humans.txt must be google.com", 
            		"google.com", URLUtil.getAuthority("https://www.google.com/humans.txt"));
            assertEquals("Hostname of https://news.ycombinator.com/newsfaq.html must be news.ycombinator.com", 
            		"news.ycombinator.com", URLUtil.getAuthority("https://news.ycombinator.com/newsfaq.html")); //no wwww
            assertEquals("Hostname of http://www.reddit.com/wiki/reddiquette must be reddit.com", 
            		"reddit.com", URLUtil.getAuthority("http://www.reddit.com/wiki/reddiquette")); // no 's' in https
            assertEquals("Hostname of http://www.5z8.info/worm_g9m4gh_refinance-now must be 5z8.info", 
            		"5z8.info", URLUtil.getAuthority("http://www.5z8.info/worm_g9m4gh_refinance-now"));
            assertEquals("Hostname of http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM must be amazon.com", 
            		"amazon.com", URLUtil.getAuthority("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM")); //long get request 
        }
        
        @Test
        public void validUrlShouldBeValid() {
        	
            assertTrue("https://www.google.com/humans.txt must be google.com is a valid URL", 
            		URLUtil.isValidUrl("https://www.google.com/humans.txt"));
            assertTrue("https://news.ycombinator.com/newsfaq.html is a valid URL", 
            		URLUtil.isValidUrl("https://news.ycombinator.com/newsfaq.html"));
            assertTrue("http://www.reddit.com/wiki/reddiquette is a valid URL", 
            		URLUtil.isValidUrl("http://www.reddit.com/wiki/reddiquette"));
            assertTrue("http://www.5z8.info/worm_g9m4gh_refinance-now is a valid URL", 
            		URLUtil.isValidUrl("http://www.5z8.info/worm_g9m4gh_refinance-now"));
            assertTrue("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM is a valid URL", 
            		URLUtil.isValidUrl("http://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM"));
            
        }
        
        @Test
        public void invalidUrlShouldBeInvalid() {
        	assertTrue("httpss://www.google.com/humans.txt must be google.com is an invalid URL", 
            		URLUtil.isInvalidUrl("httpss://www.google.com/humans.txt")); // scheme is wrong
        	assertTrue("www.news.ycombinator.com/newsfaq.html is an invalid URL", 
            		URLUtil.isInvalidUrl("ww.news.ycombinator.com/newsfaq.html")); // no scheme
        	assertTrue("http://www.reddΩt.com/wiki/reddiquette is an ivalid URL", 
            		URLUtil.isInvalidUrl("http://www.reddΩt.com/wiki/reddiquette")); // 'Ω' is not allowed
        	assertTrue("http://www.5z8.info/worm_g9m4gh_refinance-now is a valid URL", 
            		URLUtil.isInvalidUrl("http://www5z8info/worm_g9m4gh_refinance-now")); // no dots
        	assertTrue("http://www.\"\".com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM is an invalid URL", 
            		URLUtil.isInvalidUrl("http://www.\"\".com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=windows+10+OEM")); // '"' is not allowed
        }
}