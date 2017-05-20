package lawnbway.jcrawler.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import lawnbway.jcrawler.test.UrlUtilTest;
import lawnbway.jcrawler.test.UserAgentManagerSingletonTest;

@RunWith(Suite.class)
@SuiteClasses({
        AuthorityCheckerSingletonTest.class,
        UrlUtilTest.class,
        UserAgentManagerSingletonTest.class
        })

public class AllTests {
}
