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

package edu.internet2.middleware.grouper.privs;
import java.util.Set;

import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.cache.EhcacheController;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.exception.GrouperSessionException;
import edu.internet2.middleware.grouper.exception.UnableToPerformException;
import edu.internet2.middleware.grouper.hibernate.HqlQuery;
import edu.internet2.middleware.grouper.misc.GrouperCheckConfig;
import edu.internet2.middleware.grouper.misc.GrouperSessionHandler;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;


/**
 * Decorator that provides <i>Wheel</i> privilege resolution for {@link NamingResolver}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: WheelNamingResolver.java,v 1.12.2.1 2009-04-10 18:44:20 mchyzer Exp $
 * @since   1.2.1
 */
public class WheelNamingResolver extends NamingResolverDecorator {
  // TODO 20070820 DRY w/ access resolution

  /** if use wheel group */
  private boolean useWheel    = false;
  
  /** wheel group */
  private Group   wheelGroup;
  
  /** wheel session */
  private GrouperSession wheelSession = null;

  /** logger */
  private static final Log LOG = GrouperUtil.getLog(WheelNamingResolver.class);

  /**
   * 2007-11-02 Gary Brown Provide cache for wheel group members Profiling showed lots of time rechecking memberships 
   */
  public static final String CACHE_IS_WHEEL_MEMBER = 
    WheelNamingResolver.class.getName() + ".isWheelMember";

  /**
   * @param resolver 
   * @since   1.2.1
   */
  public WheelNamingResolver(NamingResolver resolver) {
    super(resolver);
    // TODO 20070816 this is ugly
    this.useWheel = Boolean.valueOf( this.getConfig( GrouperConfig.PROP_USE_WHEEL_GROUP ) ).booleanValue();
    // TODO 20070816 and this is even worse
    if (this.useWheel) {
      String wheelGroupName = "";
      try {
        wheelGroupName = this.getConfig( GrouperConfig.PROP_WHEEL_GROUP );
        this.wheelSession = GrouperSession.start( SubjectFinder.findRootSubject(), false );
        this.wheelGroup = GroupFinder.findByName(
                            //dont replace the current grouper session
                            this.wheelSession,
                            wheelGroupName
                          );
      }
      catch (Exception e) {
        
    	  //OK, so wheel group does not exist. Not fatal...
      	String error = "Cant find wheel group '" + wheelGroupName + "': " + e.getClass().getSimpleName();
    	  
      	if (!loggedWheelNotThere && !GrouperCheckConfig.inCheckConfig) {   
      	  LOG.error(error);
      	  loggedWheelNotThere = true;
      	}
    	  //exception stack is not that interesting
    	  LOG.info(error, e);
        this.useWheel=false;  
      }
    }
  }

  /** cache controller */
  private EhcacheController cc;

  /** */
  private static boolean loggedWheelNotThere = false;
  
  /**
   * @see     NamingResolver#getConfig(String)
   * @throws  IllegalStateException if any parameter is null.
   */
  public String getConfig(String key) 
    throws IllegalStateException
  {
    return super.getDecoratedResolver().getConfig(key);
  }

  /**
   * Put boolean into cache for <code>isWheelMember(...)</code>.
   * @param subj 
   * @param rv 
   * @since   1.2.1
   */
  private void putInHasPrivilegeCache(Subject subj,  Boolean rv) {
    this.cc.getCache(CACHE_IS_WHEEL_MEMBER).put( new Element( subj,rv) );
  }

  /**
   * Put boolean into cache for <code>isWheelMember(...)</code>.
   * @param subj 
   * @return  if wheel member
   * @since   1.2.1
   */
  private boolean isWheelMember(final Subject subj) {
    Boolean rv = getFromIsWheelMemberCache(subj);
    if(rv==null) {
      
      rv = (Boolean)GrouperSession.callbackGrouperSession(this.wheelSession, new GrouperSessionHandler() {

        public Object callback(GrouperSession grouperSession)
            throws GrouperSessionException {
          return WheelNamingResolver.this.wheelGroup.hasMember(subj);
        }
      });
      
      putInHasPrivilegeCache(subj, rv);
    }
    return rv;
  }

  /**
   * @see     NamingResolver#getStemsWhereSubjectHasPrivilege(Subject, Privilege)
   * @since   1.2.1
   */
  public Set<Stem> getStemsWhereSubjectHasPrivilege(Subject subject, Privilege privilege)
    throws  IllegalArgumentException
  {
    return super.getDecoratedResolver().getStemsWhereSubjectHasPrivilege(subject, privilege);
  }

  /**
   * @see     NamingResolver#getPrivileges(Stem, Subject)
   * @since   1.2.1
   */
  public Set<NamingPrivilege> getPrivileges(Stem stem, Subject subject)
    throws  IllegalArgumentException
  {
    return super.getDecoratedResolver().getPrivileges(stem, subject);
  }

  /**
   * @see     NamingResolver#getSubjectsWithPrivilege(Stem, Privilege)
   * @since   1.2.1
   */
  public Set<Subject> getSubjectsWithPrivilege(Stem stem, Privilege privilege)
    throws  IllegalArgumentException
  {
    return super.getDecoratedResolver().getSubjectsWithPrivilege(stem, privilege);
  }

  /**
   * @see     NamingResolver#grantPrivilege(Stem, Subject, Privilege)
   * @since   1.2.1
   */
  public void grantPrivilege(Stem stem, Subject subject, Privilege privilege)
    throws  IllegalArgumentException,
            UnableToPerformException
  {
    super.getDecoratedResolver().grantPrivilege(stem, subject, privilege);
  }

  /**
   * @see     NamingResolver#hasPrivilege(Stem, Subject, Privilege)
   * @since   1.2.1
   */
  public boolean hasPrivilege(Stem stem, final Subject subject, Privilege privilege)
    throws  IllegalArgumentException
  {
    if (this.useWheel) {
      if ((Boolean)GrouperSession.callbackGrouperSession(this.wheelSession, new GrouperSessionHandler() {

        public Object callback(GrouperSession grouperSession)
            throws GrouperSessionException {
          if ( WheelNamingResolver.this.wheelGroup.hasMember(subject) ) {
            return true;
          }
          return false;
        }
        
      })) {
        return true;
      }
    }
    return super.getDecoratedResolver().hasPrivilege(stem, subject, privilege);
  }

  /**
   * 
   * @param subject
   * @return true if this is wheel and if using wheel
   */
  private boolean isAndUseWheel(Subject subject) {
    if (this.getGrouperSession().isConsiderIfWheelMember()) {
      if (this.useWheel) {
        if (isWheelMember(subject)) {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * @see     NamingResolver#revokePrivilege(Stem, Privilege)
   * @since   1.2.1
   */
  public void revokePrivilege(Stem stem, Privilege privilege)
    throws  IllegalArgumentException,
            UnableToPerformException
  {
    super.getDecoratedResolver().revokePrivilege(stem, privilege);
  }
            

  /**
   * @see     NamingResolver#revokePrivilege(Stem, Subject, Privilege)
   * @since   1.2.1
   */
  public void revokePrivilege(Stem stem, Subject subject, Privilege privilege)
    throws  IllegalArgumentException,
            UnableToPerformException
  {
    super.getDecoratedResolver().revokePrivilege(stem, subject, privilege);
  }

  /**
   * @see edu.internet2.middleware.grouper.privs.NamingResolver#hqlFilterStemsWhereClause(edu.internet2.middleware.subject.Subject, edu.internet2.middleware.grouper.hibernate.HqlQuery, java.lang.StringBuilder, java.lang.String, java.util.Set)
   */
  public boolean hqlFilterStemsWhereClause(Subject subject, HqlQuery hqlQuery,
      StringBuilder hql, String stemColumn, Set<Privilege> privInSet) {
    //Wheel can see all stems
    if (this.isAndUseWheel(subject)) {
      return false;
    }
    NamingResolver decoratedResolver = super.getDecoratedResolver();
    //CachingNamingResolver
    return decoratedResolver.hqlFilterStemsWhereClause(subject, hqlQuery, hql, stemColumn, privInSet);
  }

  /**
   * @see edu.internet2.middleware.grouper.privs.NamingResolver#postHqlFilterStems(java.util.Set, edu.internet2.middleware.subject.Subject, java.util.Set)
   */
  public Set<Stem> postHqlFilterStems(Set<Stem> stems, Subject subject,
      Set<Privilege> privInSet) {
    
    //Wheel can see all groups
    if (this.isAndUseWheel(subject)) {
      return stems;
    }
    Set<Stem> filteredStems = super.getDecoratedResolver().postHqlFilterStems(stems, subject, privInSet);
    
    //return filtered groups
    return filteredStems;
  }            

  /**
   * @see edu.internet2.middleware.grouper.privs.NamingResolver#getGrouperSession()
   */
  public GrouperSession getGrouperSession() {
    NamingResolver decoratedResolver = super.getDecoratedResolver();
    return decoratedResolver.getGrouperSession();
  }

  /**
   * Retrieve boolean from cache for <code>isWheelMember(...)</code>.
   * @param subj 
   * @return  Cached return value or null.
   * @since   1.2.1
   */
  private Boolean getFromIsWheelMemberCache(Subject subj) {
    Element el = this.cc.getCache(CACHE_IS_WHEEL_MEMBER).get(subj);
    if (el != null) {
      return (Boolean) el.getObjectValue();
    }
    return null;
  }

}

