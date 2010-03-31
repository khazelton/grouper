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
import edu.internet2.middleware.grouper.ws.exceptions.WsInvalidQueryException;

/**
 * <pre>
 * Class to lookup a membership via web service.  Put in a uuid, or fill in the other fields
 * 
 * developers make sure each setter calls this.clearMembership();
 * </pre>
 * @author mchyzer
 */
public class WsMembershipLookup {

  /**
   * see if blank
   * @return true if blank
   */
  public boolean blank() {
    return !this.hasData()
      && this.membership == null && this.membershipFindResult == null;
  }

  
  /**
   * see if this membership lookup has data
   * @return true if it has data
   */
  public boolean hasData() {
    return !StringUtils.isBlank(this.uuid);
  }
  
  /**
   * logger 
   */
  private static final Log LOG = LogFactory.getLog(WsMembershipLookup.class);

  /** find the membership */
  @XStreamOmitField
  private Membership membership = null;

  /** result of attribute def name find */
  public static enum MembershipFindResult {

    /** found the membership */
    SUCCESS,

    /** cant find the membership */
    MEMBERSHIP_NOT_FOUND,

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
   * uuid of the membership to find
   */
  private String uuid;
  
  /**
   * <pre>
   * 
   * Note: this is not a javabean property because we dont want it in the web service
   * </pre>
   * @return the membership
   */
  public Membership retrieveMembership() {
    return this.membership;
  }

  /**
   * <pre>
   * 
   * Note: this is not a javabean property because we dont want it in the web service
   * </pre>
   * @return the membershipFindResult, this is never null
   */
  public MembershipFindResult retrieveMembershipFindResult() {
    return this.membershipFindResult;
  }

  /**
   * make sure this is an explicit toString
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  /**
   * retrieve the membership for this lookup if not looked up yet.  pass in a grouper session
   * @param grouperSession 
   */
  public void retrieveMembershipIfNeeded(GrouperSession grouperSession) {
    this.retrieveMembershipIfNeeded(grouperSession, null);
  }

  /**
   * retrieve the membership for this lookup if not looked up yet.  pass in a grouper session
   * @param grouperSession 
   * @param invalidQueryReason is the text to go in the WsInvalidQueryException
   * @return the membership
   * @throws WsInvalidQueryException if there is a problem, and if the invalidQueryReason is set
   */
  public Membership retrieveMembershipIfNeeded(GrouperSession grouperSession,
      String invalidQueryReason) throws WsInvalidQueryException {
    
    //see if we already retrieved
    if (this.membershipFindResult != null) {
      return this.membership;
    }
    try {
      //assume success (set otherwise if there is a problem)
      this.membershipFindResult = MembershipFindResult.SUCCESS;

      boolean hasUuid = !StringUtils.isBlank(this.uuid);

      //must have a uuid or the other stuff
      if (!hasData()) {
        this.membershipFindResult = MembershipFindResult.INVALID_QUERY;
        if (!StringUtils.isEmpty(invalidQueryReason)) {
          throw new WsInvalidQueryException("Invalid membership query for '"
              + invalidQueryReason + "', " + this);
        }
        String logMessage = "Invalid query: " + this;
        LOG.warn(logMessage);
      }

      if (hasUuid) {
        this.membership = MembershipFinder.findByUuid(grouperSession, this.uuid, true, false);
      }

    } catch (MembershipNotFoundException anf) {
      this.membershipFindResult = MembershipFindResult.MEMBERSHIP_NOT_FOUND;
      if (!StringUtils.isBlank(invalidQueryReason)) {
        throw new WsInvalidQueryException("Invalid membership for '" + invalidQueryReason
            + "', " + this, anf);
      }
    }
    return this.membership;
  }

  /**
   * clear the membership if a setter is called
   */
  private void clearMembership() {
    this.membership = null;
    this.membershipFindResult = null;
  }

  /** result of membership find */
  @XStreamOmitField
  private MembershipFindResult membershipFindResult = null;

  /**
   * uuid of the membership to find
   * @return the uuid
   */
  public String getUuid() {
    return this.uuid;
  }

  /**
   * uuid of the membership to find
   * @param uuid1 the uuid to set
   */
  public void setUuid(String uuid1) {
    this.uuid = uuid1;
    this.clearMembership();
  }

  /**
   * 
   */
  public WsMembershipLookup() {
    //blank
  }

}
