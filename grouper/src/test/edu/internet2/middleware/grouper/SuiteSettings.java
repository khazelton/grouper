/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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

package edu.internet2.middleware.grouper;
import  junit.framework.*;

/**
 * @author  blair christensen.
 * @version $Id: SuiteSettings.java,v 1.5 2006-10-17 13:38:22 blair Exp $
 */
public class SuiteSettings extends TestCase {

  static public Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite( TestSetting0.class      ); // get CURRENT_SCHEMA_VERSION
    suite.addTestSuite( TestSetting1.class      ); // get schema version
    suite.addTestSuite( TestSetting2.class      ); // get grouper config props
    suite.addTestSuite( TestSetting3.class      ); // get bad grouper config props
    suite.addTestSuite( TestSetting4.class      ); // get hibernate config props
    return suite;
  } // static public Test suite()

}

