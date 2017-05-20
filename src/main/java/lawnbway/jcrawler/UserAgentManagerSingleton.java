package lawnbway.jcrawler;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UserAgentManagerSingleton is a singleton that keeps open the file with available user agents ("usr_agents")
 * and provides random user agent strings (names) to CrawlJob instances that request user agent change.
 * 
 * enum type fields are compile time constants and are constructed when the type is referenced for the
 * first time. Therefore multiple instantiations are not possible.
 */
public enum UserAgentManagerSingleton {
	INSTANCE;
	
	private FileReader fr;
	private int usrAgentNum;
	private RandomAccessFile raf;
	volatile private String usrAgentName;
	private static final String USR_AGENTS_NAME = "usr_agents";
	private static final String USR_AGENTS_RELATIVE_PATH = "src/main/resources/";
	private static final int DEFAULT_PROBABILITY = 30;	
	
	/**
	 * Instantiates the class by opening the file with user agents
	 * and counting the number of user agents in the file.
	 * Also, randomly chooses a user agent from the file, so it is available for CrawlJob to take.
	 * 
	 * @param fileName    the file with available user agent strings ("user_agents" by default) with one user agent per line
	 * @throws IOException    if there is a problem with opening the file containing user agents
	 */
	private UserAgentManagerSingleton() {
		try {
			fr = new FileReader(USR_AGENTS_RELATIVE_PATH + USR_AGENTS_NAME);
			LineNumberReader lnr = new LineNumberReader(fr);
			lnr.skip(Long.MAX_VALUE);
			setUsrAgentNum(lnr.getLineNumber());
			lnr.close();
			raf = new RandomAccessFile(USR_AGENTS_RELATIVE_PATH + USR_AGENTS_NAME, "r");	
			
		} catch (IOException e) {
			System.out.println("ERROR! There is a problem opening file containing user agents.");
			e.printStackTrace();
			System.exit(1);
		}	
		this.setRandom();
	}
	
	/**
	 * Chooses a random user agent from the file.
	 * 
	 */
	public synchronized void setRandom() {
		int usrAgentNum = getUsrAgentNum();
		int randomLine = ThreadLocalRandom.current().nextInt(0, usrAgentNum + 1);
		try {
			raf.seek(0);  // move RAF pointer to the beginning
			for(int i = 1; i < randomLine; i++) 
				  raf.readLine(); 
			setName(raf.readLine()); // set user agent to user agent #randomLine from usr_agents file
		} catch (IOException e) {
			System.out.println("There is a problem reading file containing user agents.");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/**
	 * Changes user agent with a certain probability (from 0 to 100).
	 * Uses DEFAULT_PROBABILITY if the provided parameter is not a valid probability.
	 * 
	 * @param probability    the probability that the user agent will be changed at this method invocation
	 */
	public void changeUserAgent(int probability) {
		if (!isValidProbability(probability))
			probability = DEFAULT_PROBABILITY;
		
		int chance = ThreadLocalRandom.current().nextInt(0, 100 + 1);	// Get random number from 0 to 100 (inclusive; this is why +1)
		if (chance < probability || probability == 100) {  
			UserAgentManagerSingleton.INSTANCE.setRandom();		// Switch to random user agent if the chance falls within the chosen probability
		}
		setName(UserAgentManagerSingleton.INSTANCE.getName());	
	}
	
	/**
	 * Validates the probability that the changeUserAgent method uses.
	 * 
	 * @param probability    the probability to be validated
	 * @return true if the probability is valid, false if it is invalid
	 */
	private boolean isValidProbability(int probability) {
		return (probability >= 0 && probability <= 100);
	}
	
	/**
	 * Sets the name of the currently selected user agent.
	 * 
	 * @param name    name of the selected user agent
	 */
	private void setName(String name) {
		usrAgentName = name;
	}
	
	/**
	 * Gets the name of the currently selected user agent.
	 * 
	 * @return name of the selected user agent
	 */
	public String getName() {
		return usrAgentName;
	}
	
	/**
	 * Sets the number of available user agents from the user agent file.
	 * 
	 * @param usrAgentNum    the number of available user agents
	 */
	private void setUsrAgentNum(int usrAgentNum) {
		if(usrAgentNum < 0) {
			System.out.println("ERROR! " + USR_AGENTS_NAME + "cannot have negative number of lines.");
			System.exit(1);
		}
		this.usrAgentNum = usrAgentNum;
	}
	
	/**
	 * Gets the number of lines (corresponding to the number of user agents) from the user agent file.
	 * 
	 * @return the number of available user agents
	 */
	private int getUsrAgentNum() {
		return usrAgentNum;
	}
	
	/**
	 * Closes file streams used by this class.
	 * To be invoked just before the program termination.
	 */
	public void cleanup() {
		try {
			raf.close();
			fr.close();
		} catch (IOException e) {
			System.out.println("ERROR! UserAgent cleanup failed.");
			e.printStackTrace();
		}	
	}
}