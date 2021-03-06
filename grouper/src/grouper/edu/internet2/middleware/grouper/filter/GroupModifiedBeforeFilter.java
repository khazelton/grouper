/**
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

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

package edu.internet2.middleware.grouper.filter;
import java.util.Date;
import java.util.Set;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.exception.QueryException;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.privs.PrivilegeHelper;


/** 
 * Query by groups modified before the specified date.
 * <p/>
 * @author  blair christensen.
 * @version $Id: GroupModifiedBeforeFilter.java,v 1.3 2008-11-04 15:19:56 shilen Exp $
 * @since   1.1.0
 */
public class GroupModifiedBeforeFilter extends BaseQueryFilter {

  // PRIVATE INSTANCE VARIABLES //
  private Date  d;      
  private Stem  ns;


  // CONSTRUCTORS //

  /**
   * {@link QueryFilter} that returns groups modified before the
   * specified date. 
   * <p/>
   * @param   d   Find groups modified before this date.
   * @param   ns  Restrict results to within this stem.
   */
  public GroupModifiedBeforeFilter(Date d, Stem ns) {
    this.d  = (Date) d.clone();
    this.ns = ns;
  } // public GroupModifiedBeforeFilter(d, ns)


  // PUBLIC INSTANCE METHODS //

  public Set getResults(GrouperSession s) 
    throws QueryException
  {
    //note, no need for GrouperSession inverse of control
    GrouperSession.validate(s);
    Set results;

    if (ns.isRootStem()) {
      results = PrivilegeHelper.canViewGroups(
        s, GrouperDAOFactory.getFactory().getGroup().findAllByModifiedBefore(this.d));
    } else {
      results = PrivilegeHelper.canViewGroups(
        s, GrouperDAOFactory.getFactory().getGroup().findAllByModifiedBefore(this.d, getStringForScope(ns)));
    }
    return results;
  } // public Set getResults(s)

} // public class GroupModifiedBeforeFilter

