package lawnbway.jcrawler.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.*;

import lawnbway.jcrawler.UserAgentManagerSingleton;


public class UserAgentManagerSingletonTest {
	private static final String USR_AGENTS_NAME = "usr_agents";
	private static final String USR_AGENTS_RELATIVE_PATH = "src/main/resources/";
	
	public static BufferedReader br;
	public static ArrayList<String> agentList;
	
	@BeforeClass
	public static void loadUsrAgents() {

		try {
			br = new BufferedReader(new FileReader(USR_AGENTS_RELATIVE_PATH + USR_AGENTS_NAME));
		} catch (FileNotFoundException e) {
			System.out.println("Can't open " + USR_AGENTS_RELATIVE_PATH + USR_AGENTS_NAME + " for testing.");
			e.printStackTrace();
		}
		agentList = new ArrayList<String>();
		String line;
		
		try {
			while ((line = br.readLine()) != null) {
				agentList.add(line);
			}
			br.close();
		} catch (IOException e) {
			System.out.println("A problem occured when reading " + 
		USR_AGENTS_RELATIVE_PATH + USR_AGENTS_NAME + " during testing.");
			e.printStackTrace();
		}
	}
	
	@Test
	public void setRandomShouldSetValidUserAgent() {
		// Test setRandom() 200 times
		for (int i = 0; i < 200; i++){
			UserAgentManagerSingleton.INSTANCE.setRandom();
			assertTrue("User agent should be selected from a file."
					, agentList.contains(UserAgentManagerSingleton.INSTANCE.getName()));
		}
	}
	
	@Test
	public void userAgentShouldChangeFor100Probability() {
		// Test changeUserAgent(100) 200 times
		for (int i = 0; i < 200; i++){
			String rememberAgent = UserAgentManagerSingleton.INSTANCE.getName();
			UserAgentManagerSingleton.INSTANCE.changeUserAgent(100);
			assertNotSame("With 100% change probability, user should change after each call"
					, UserAgentManagerSingleton.INSTANCE.getName(), rememberAgent);
		}
	}
	
	@Test
	public void userAgentShouldNotChangeFor0Probability() {
		// Test changeUserAgent(0) 200 times
		for (int i = 0; i < 200; i++){
			String rememberAgent = UserAgentManagerSingleton.INSTANCE.getName();
			UserAgentManagerSingleton.INSTANCE.changeUserAgent(0);
			assertSame("With 0% change probability, user agent should stay the same"
					, UserAgentManagerSingleton.INSTANCE.getName(), rememberAgent);
		}
	}
}