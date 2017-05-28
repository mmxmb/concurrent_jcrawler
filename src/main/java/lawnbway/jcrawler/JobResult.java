package lawnbway.jcrawler;

/**
 * JobResult is an abstract class that represents the a 
 * single unit of crawling activity performed on a specified HTML document.
 */
public abstract class JobResult {
	
	private boolean errorStatus;
	private boolean success;
	private String currentUrl;
	
	/**
	 * A blueprint for the constructors used by subclasses.
	 * 
	 * @param result	the result of the job
	 * @param errorStatus    the error status of the job
	 * @param url	the URL address where the job takes place
	 */
	public JobResult(boolean result, boolean errorStatus, String url) {
		setSuccess(result);
		setErrorStatus(errorStatus);
		setCurrentUrl(url);
	}
	
	/**
	 * Sets the result of the job 
	 * (true or false; successful or unsuccessful).
	 * 
	 * @param result	the result of the job
	 */
	public void setSuccess(boolean result) {
		success = result;
	}
	
	/**
	 * Returns true if the job is successful.
	 *
	 * @return the result of the job
	 */
	public boolean isSuccessful() {
		return success;
	}
	
	/**
	 * Returns true if the job is unsuccessful.
	 * Added for easier code readability.
	 *
	 * @return the result of the job
	 */
	public boolean isUnsuccessful() {
		return !success;
	}
	
	/**
	 * Sets the URL of the job.
	 * 
	 * @param url	the URL address where the job takes place
	 */
	public void setCurrentUrl(String url) {
		currentUrl = url;
	}
	
	/**
	 * Returns the URL of the job.
	 *
	 * @return the URL address where the job takes place
	 */
	public String getCurrentUrl() {
		return currentUrl;
	}
	
	/**
	 * Sets the error status of the job.
	 * 
	 * @param error    true if error occured, false if error did not occur
	 */
	public void setErrorStatus(boolean error) {
		this.errorStatus = error;
	}
	
	/**
	 * Returns error status of the job.
	 * 
	 * @return true if error occured, false if error did not occur 
	 */
	public boolean errorOccured() {
		return this.errorStatus;
	}

}
