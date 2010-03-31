/**
 * 
 */
package edu.internet2.middleware.grouper.ws.soap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.MembershipFinder;
import edu.internet2.middleware.grouper.exception.MembershipNotFoundException;
import edu.internet2.middleware.grouper.group.GroupMember;
import edu.internet2.middleware.grouper.ws.exceptions.WsInvalidQueryException;
import edu.internet2.middleware.grouper.ws.soap.WsGroupLookup.GroupFindResult;
import edu.internet2.middleware.grouper.ws.soap.WsSubjectLookup.SubjectFindResult;

/**
 * <pre>
 * Class to lookup a membership via web service.  Put in a uuid, or fill in the other fields
 * 
 * developers make sure each setter calls this.clearMembership();
 * </pre>
 * @author mchyzer
 */
public class WsMembershipAnyLookup {

  /** group lookup for group */
  private WsGroupLookup wsGroupLookup;
  
  /** subject lookup for subject */
  private WsSubjectLookup wsSubjectLookup;
  
  /**
   * group lookup for group
   * @return group lookup
   */
  public WsGroupLookup getWsGroupLookup() {
    return this.wsGroupLookup;
  }

  /**
   * group lookup for group
   * @param wsGroupLookup1
   */
  public void setWsGroupLookup(WsGroupLookup wsGroupLookup1) {
    this.wsGroupLookup = wsGroupLookup1;
  }

  /**
   * subject lookup for subject
   * @return subject lookup
   */
  public WsSubjectLookup getWsSubjectLookup() {
    return this.wsSubjectLookup;
  }

  /**
   * subject lookup for subject
   * @param wsSubjectLookup1
   */
  public void setWsSubjectLookup(WsSubjectLookup wsSubjectLookup1) {
    this.wsSubjectLookup = wsSubjectLookup1;
  }

  /**
   * see if blank
   * @return true if blank
   */
  public boolean blank() {
    return !this.hasData()
      && this.groupMember == null && this.membershipAnyFindResult == null;
  }
  
  /**
   * see if this membership lookup has data
   * @return true if it has data
   */
  public boolean hasData() {
    return this.wsGroupLookup != null && this.wsSubjectLookup != null 
      && this.wsGroupLookup.hasData() && this.wsSubjectLookup.hasData();
  }
  
  /**
   * logger 
   */
  private static final Log LOG = LogFactory.getLog(WsMembershipAnyLookup.class);

  /** group / subject combination */
  @XStreamOmitField
  private GroupMember groupMember = null;

  /** result of attribute def name find */
  public static enum MembershipAnyFindResult {

    /** found the membership */
    SUCCESS,

    /** invalid query (e.g. if everything blank) */
    INVALID_QUERY;

    /**
     * if this is a successful result
     * @return true if success
     */
    public boolean isSuccess() {
      return this == SUCCESS;
    }
  }

  /**
   * <pre>
   * 
   * Note: this is not a javabean property because we dont want it in the web service
   * </pre>
   * @return the membership
   */
  public GroupMember retrieveGroupMember() {
    return this.groupMember;
  }

  /**
   * <pre>
   * 
   * Note: this is not a javabean property because we dont want it in the web service
   * </pre>
   * @return the membershipFindResult, this is never null
   */
  public MembershipAnyFindResult retrieveMembershipAnyFindResult() {
    return this.membershipAnyFindResult;
  }

  /**
   * make sure this is an explicit toString
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * retrieve the membership any for this lookup if not looked up yet.  pass in a grouper session
   * @param grouperSession 
   */
  public void retrieveMembershipAnyIfNeeded(GrouperSession grouperSession) {
    this.retrieveMembershipAnyIfNeeded(grouperSession, null);
  }

  /**
   * retrieve the membership any for this lookup if not looked up yet.  pass in a grouper session
   * @param grouperSession 
   * @param invalidQueryReason is the text to go in the WsInvalidQueryException
   * @return the membership
   * @throws WsInvalidQueryException if there is a problem, and if the invalidQueryReason is set
   */
  public GroupMember retrieveMembershipAnyIfNeeded(GrouperSession grouperSession,
      String invalidQueryReason) throws WsInvalidQueryException {
    
    //see if we already retrieved
    if (this.membershipAnyFindResult != null) {
      return this.groupMember;
    }

    //assume success (set otherwise if there is a problem)
    this.membershipAnyFindResult = MembershipAnyFindResult.SUCCESS;

    //must have a uuid or the other stuff
    if (!hasData()) {
      this.membershipAnyFindResult = MembershipAnyFindResult.INVALID_QUERY;
      if (!StringUtils.isEmpty(invalidQueryReason)) {
        throw new WsInvalidQueryException("Invalid membership any query (doesnt have data) for '"
            + invalidQueryReason + "', " + this);
      }
      String logMessage = "Invalid query (no data): " + this;
      LOG.warn(logMessage);
    } else {
      
      this.wsGroupLookup.retrieveGroupIfNeeded(grouperSession);
      this.wsSubjectLookup.retrieveSubject();
      
      if (GroupFindResult.SUCCESS != this.wsGroupLookup.retrieveGroupFindResult()) {
        
        this.membershipAnyFindResult = MembershipAnyFindResult.INVALID_QUERY;
        
      } else if (SubjectFindResult.SUCCESS != this.wsSubjectLookup.retrieveSubjectFindResult()) {
        
      } else if (true) {
        //TODO
      }
      
    }
    return this.groupMember;
  }

  /**
   * clear the membership if a setter is called
   */
  private void clearMembershipAny() {
    this.groupMember = null;
    this.membershipAnyFindResult = null;
  }

  /** result of membership find */
  @XStreamOmitField
  private MembershipAnyFindResult membershipAnyFindResult = null;

  /**
   * 
   */
  public WsMembershipAnyLookup() {
    //blank
  }

}
