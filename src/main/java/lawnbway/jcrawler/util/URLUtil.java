package lawnbway.jcrawler.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * URLUtil is a utiltiy class that is used to verify if a ceratin URL address is valid.
 */
public final class URLUtil {
	
	/**
	 * The following regex does not match URLs with invalid (long) TLD 
	 * (that is any length is allowed, although I am certain there should be a limit)
	 * 
	 * <p>The following grouping is used to capture some subexpressions:
	 * <ul>
	 * 	<li>group(1) - matches root path</li>
	 * 	<li>group(2) - matches www (if exists)</li>
	 * 	<li>group(3) - matches authority</li>
	 * </ul>
	 * This pattern is taken from: 
	 * <blockquote>http://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url</blockquote></p>
	 */
	private static final String HTTP_REGEX = "(https?:\\/\\/(www\\.)?([-a-zA-Z0-9@:%._\\\\+~#=]{2,256}\\.[a-z]{2,6}))\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
	private static final Pattern HTTP_PATTERN = Pattern.compile(HTTP_REGEX);
	
	// Suppresses default constructor, ensuring non-instantiability.
	private URLUtil() {}
	
	/**
	 * Captures the subexpression that represents the root of the URL using the full address.
	 * Returns the root.
	 * 
	 * <p>Validate URL before passing to this method.</p>
	 * 
	 * @param url    
	 *        A valid URL address
	 * @return root URL in the following format:  <blockquote>http(s)://(www).hostname</blockquote> (Note: no port number)
	 * @throws IllegalStateException when invalid URL parameter is passed and grouping fails
	 */
	public static String getRootUrl(String url) {
		Matcher m = HTTP_PATTERN.matcher(url);
		m.find(); // no need to check return value since URL is supposedly validated
		try {
			String root = m.group(1);
			return root;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	/**
	 * Captures the subexpression that represents the authority of the URL using the full address.
	 * Returns the authority.
	 * 
	 * <p>Validate URL before passing to this method.</p>
	 * 
	 * @param url    
	 *        A valid URL address
	 * @return authority part of provided URL
	 * @throws IllegalStateException when invalid URL parameter is passed and grouping fails
	 */
	public static String getAuthority(String url) {
		Matcher m = HTTP_PATTERN.matcher(url);
		m.find(); // no need to check return value since URL is supposedly validated
		try {
			String root = m.group(3);
			return root;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	/**
	 * Returns true if the passed URL is valid.
	 *
	 * @param url 
	 * 		  A URL to validate
	 * @return The result of URL validation
	 */
	public static boolean isValidUrl(String url) {
		Matcher m = HTTP_PATTERN.matcher(url);
		return m.find();
	}
	
	/**
	 * Returns true if the passed URL is invalid.
	 * <p>Added for easier code readability.</p>
	 *
	 * @param url
	 *        A URL to validate
	 * @return The result of URL validation
	 */
	public static boolean isInvalidUrl(String url) {
		return !isValidUrl(url);
	}
	
}
