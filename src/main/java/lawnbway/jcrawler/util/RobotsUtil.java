package lawnbway.jcrawler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lawnbway.jcrawler.util.URLUtil;

/**
 * 
 * 
 * RobotsUtil reads robots.txt of a specified website. 
 * Creates a list of links that will not be visited during 
 * the crawl. This list can be added to a set of visitedLinks 
 * at the beginning of the crawl; this ensures that website 
 * areas specified in robots.txt are excluded from the scan.
 * 
 * This parser follows Robot Exclusion Standard only. That is,
 * regex and any non-standard directives in robots.txt are not
 * supported and are ignored by this parser and, consequently,
 * this web crawler.
 * 
 * For more information see: http://www.robotstxt.org/orig.html
 * 
 * @author lawnboymax
 *
 */
public final class RobotsUtil {
	
	private static List<String> robotExclusionLinks = new LinkedList<String>();
	
	// Suppresses default constructor, ensuring non-instantiability.
		private RobotsUtil() {}
	
	/**
	 * Creates a list of "areas" of a website, that are specified
	 * as off-limits for all user-agents in robots.txt.
	 * 
	 * @param url	the URL address of a website whose robots.txt needs to be parsed
	 */
	public static void parse(String url){
			
			// Prepare regex patterns for reading robots.txt
			String usrAgent = "User-agent: \\*";
			Pattern usrAgentRegex = Pattern.compile(usrAgent, Pattern.CASE_INSENSITIVE);
			// Regex in robots.txt is not supported by this crawler. Ignore '*'
			String disallow = "Disallow: (\\/*?[-a-zA-Z0-9@:%_\\\\+.~#?&//=]+)$";
			Pattern disallowRegex = Pattern.compile(disallow);
			
			String root = URLUtil.getRootUrl(url);
			try(BufferedReader in = new BufferedReader(
		            new InputStreamReader(new URL(root + "/robots.txt").openStream()))) { // Open robots.txt of chosen website
		        String line = "foo";
		        Matcher agentMatch = usrAgentRegex.matcher(line);
		        
		        // Read robots.txt until reach record for all robots
		        while((line = in.readLine()) != null && !agentMatch.find()) { 
		        	agentMatch = usrAgentRegex.matcher(line);
		        	}
		        
		        // Look for disallow fields that don't start with '*'
		        Matcher disMatch = disallowRegex.matcher(line);
		        System.out.print(String.format(
		        "The following links are excluded from search (see %s/robots.txt): \n\n", root));
		        while((line = in.readLine()) != null && !line.isEmpty()){
		        	disMatch = disallowRegex.matcher(line);
		        	
		        	// Add disallowed paths to visited pages set
		        	if (disMatch.find()) {
			        	String disallowedPath = disMatch.group(1);
			        	
			        	if(disallowedPath == "/") { // robots are excluded from entire server
			        		System.out.println("It looks like polite robots are not welcome on this server ;(");
			        		System.exit(0);
			        	}
			        	robotExclusionLinks.add(root + disallowedPath);
			        	System.out.println("Disallowed: " + root + disallowedPath);
		        	}
		        }
	        	System.out.println();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
	}
	
	/**
	 * Gets the list links that were found by this crawl job.
	 * May contain invalid links, since the links are not validated at this point.
	 * 
	 * @return list of URL links.
	 */
	public static List<String> getLinks(){
		return robotExclusionLinks;
	}

}
