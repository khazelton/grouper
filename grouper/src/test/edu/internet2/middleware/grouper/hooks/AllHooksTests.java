/*
 * @author mchyzer
 * $Id: AllHooksTests.java,v 1.1.2.2 2008-06-09 19:26:05 mchyzer Exp $
 */
package edu.internet2.middleware.grouper.hooks;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 *
 */
public class AllHooksTests {

  /**
   * 
   * @return the test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite("Test for edu.internet2.middleware.grouper.hooks");
    //$JUnit-BEGIN$
    suite.addTestSuite(GroupHooksTest.class);
    suite.addTestSuite(MembershipHooksTest.class);
    //$JUnit-END$
    return suite;
  }

}
