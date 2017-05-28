package lawnbway.jcrawler;

public class WordCollectResult extends JobResult{
	
	/**
	 * Creates an instance of theWordCollectResult class.
	 * 
	 * @param result	the result of the word gathering from current HTML document
	 * @param errorStatus    the error status of the word collection
	 * @param url	the URL address of the current word collection
	 */
	public WordCollectResult(boolean result, boolean errorStatus, String url) {
		super(result, errorStatus, url);
	}

}
