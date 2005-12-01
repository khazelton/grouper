/*
  Copyright 2004-2005 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2005 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package test.edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.util.*;
import  junit.framework.*;

/**
 * Test use of the CREATE {@link NamingPrivilege}.
 * <p />
 * @author  blair christensen.
 * @version $Id: TestPrivCREATE.java,v 1.1 2005-12-01 19:55:53 blair Exp $
 */
public class TestPrivCREATE extends TestCase {

  // Private Class Constants
  private static final Privilege PRIV = NamingPrivilege.CREATE;


  public TestPrivCREATE(String name) {
    super(name);
  }

  protected void setUp () {
    Db.refreshDb();
  }

  protected void tearDown () {
    // Nothing 
  }

  // Tests

  // Create child group without CREATE
  public void testCreateChildGroupFail() {
    // Get root and !root sessions
    GrouperSession  s       = SessionHelper.getRootSession();
    GrouperSession  nrs     = SessionHelper.getSession(SubjectHelper.SUBJ0_ID);
    // Get root stem
    Stem            root    = StemHelper.findRootStem(s);
    // Create child stem
    Stem  edu   = StemHelper.addChildStem(root, "edu", "education");
    // Now get child stem as !root subject
    Stem            nredu   = StemHelper.findByName(nrs, "edu");
    // Now fail to add a child group as !root subject
    StemHelper.addChildGroupFail(nredu, "i2", "internet2");
  } // public void testCreateChildGroupFail()

  // Create child group with CREATE
  public void testCreateChildGroup() {
    // Get root and !root sessions
    GrouperSession  s       = SessionHelper.getRootSession();
    GrouperSession  nrs     = SessionHelper.getSession(SubjectHelper.SUBJ0_ID);
    // Get root stem
    Stem            root    = StemHelper.findRootStem(s);
    // Create child stem
    Stem  edu   = StemHelper.addChildStem(root, "edu", "education");
    // Grant priv on child stem to !root subject
    PrivHelper.grantPriv(s, edu, nrs.getSubject(), PRIV);
    // Now get child stem as !root subject
    Stem            nredu   = StemHelper.findByName(nrs, "edu");
    // Now add a child group as !root subject
    StemHelper.addChildGroup(nredu, "i2", "internet2");
  } // public void testCreateChildGroup()

}

