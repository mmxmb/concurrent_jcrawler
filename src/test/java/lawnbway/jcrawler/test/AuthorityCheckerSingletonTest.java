package lawnbway.jcrawler.test;

import static org.junit.Assert.*;

import org.junit.*;

import lawnbway.jcrawler.AuthorityCheckerSingleton;

public class AuthorityCheckerSingletonTest {
	

        @Test
        public void sameAuthorityShouldBeSame() {
        	
        	// Uninitialized authority must be null
    	    assertNull("Authority URL is not set yet.",AuthorityCheckerSingleton.INSTANCE.getAuthorityUrl());
        	AuthorityCheckerSingleton.INSTANCE.setAuthorityUrl("https://www.google.com/");
        	// Initialized authority must not be null
        	assertNotNull("Authority URL is set.",AuthorityCheckerSingleton.INSTANCE.getAuthorityUrl());
        	
        	assertTrue("https://www.google.com/humans.txt has the same authority as https://www.google.com/", 
            		AuthorityCheckerSingleton.INSTANCE.isSameAuthority("https://www.google.com/humans.txt"));
        	assertTrue("https://www.google.com/intl/en/about/products/ has the same authority as https://www.google.com/", 
            		AuthorityCheckerSingleton.INSTANCE.isSameAuthority("https://www.google.com/intl/en/about/products/"));
        	assertFalse("https://www.google.ca/intl/en/about/products/ does not have the same authority as https://www.google.com/", 
            		AuthorityCheckerSingleton.INSTANCE.isSameAuthority("https://www.google.ca/intl/en/about/products/"));
        	assertFalse("https://developers.google.com/?hl=en does not have the same authority as https://www.google.com/", 
            		AuthorityCheckerSingleton.INSTANCE.isSameAuthority("https://developers.google.com/?hl=en"));  	
        }
}