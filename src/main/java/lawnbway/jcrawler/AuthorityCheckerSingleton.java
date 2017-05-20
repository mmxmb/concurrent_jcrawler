package lawnbway.jcrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lawnbway.jcrawler.util.URLUtil;

/**
 * AuthorityCheckerSingleton is a singleton that stores a compiled pattern of authority URL
 * for regex matching. It is used to verify if a certain URL address is hosted by the
 * same network as the one specified by <tt>authorityUrl</tt>.
 * 
 * 
 * enum type fields are compile time constants and are constructed when the type is referenced for the
 * first time. Therefore multiple instantiations are not possible.
 */
public enum AuthorityCheckerSingleton {
	INSTANCE;
	
	private volatile String authorityUrl;
	private static volatile Pattern hostPattern;
	
	/**
	 * Returns <tt>true</tt> if the specified URL is hosted by the same <tt>authorityUrl</tt>
	 * 
	 * @param urlToCheck
	 * 		  URL address that will be compared against the authority pattern.
	 * @return <tt>true</tt> if the specified URL is hosted by the same <tt>authorityUrl</tt>
	 */
	public synchronized boolean isSameAuthority(String urlToCheck) {
		if (URLUtil.isValidUrl(urlToCheck)) {
			Matcher m = hostPattern.matcher(urlToCheck);
			return m.find();
		}
		else {
			return false;
		}		
	}
	
	/**
	 * Returns <tt>true</tt> if the specified URL is not hosted by the same <tt>authorityUrl</tt>.
	 * 
	 * @param urlToCheck
	 * 		  URL address that will be compared against the authority pattern.
	 * @return <tt>true</tt> if the specified URL is not hosted by the same <tt>authorityUrl</tt>
	 */
	public synchronized boolean isNotSameAuthority(String urlToCheck) {
		return !isSameAuthority(urlToCheck);
	}
	
	/**
	 * Compiles regex pattern from <tt>authorityUrl</tt>. 
	 */
	private void compilePattern() {
		StringBuilder sb = new StringBuilder();
		String patternStart = "https?:\\/\\/(www\\.)?";
		String patternEnd = "\\b[-a-zA-Z0-9@:%_\\+.~#?&//=]*";
		sb.append(patternStart);
		sb.append(authorityUrl);
		sb.append(patternEnd);
		String regex = sb.toString();
		hostPattern = Pattern.compile(regex);
	}
	
	/**
	 * Sets <tt>authorityUrl</tt> to a provided <tt>url</tt>, given that
	 * it is a valid URL. Calls <tt>compilePattern()</tt>.
	 * 
	 * @param url
	 * 		  authority URL that is will be compared against other addresses.
	 */
	public synchronized void setAuthorityUrl(String url) {
		if (URLUtil.isValidUrl(url)) {
			authorityUrl = URLUtil.getAuthority(url);
			compilePattern();
		}
		else {
			System.out.println("ERROR! Provided authority URL is invalid.");
			System.exit(1);
		}
	}
	
	/**
	 * Returns <tt>authorityUrl</tt>.
	 * 
	 * @return <tt>authorityUrl</tt>
	 */
	public String getAuthorityUrl() {
		if(authorityUrl != null) {
			return authorityUrl;
		}
		System.out.println("ERROR! Set the authority before using AuthorityChecker.");
		return null;
	}
}