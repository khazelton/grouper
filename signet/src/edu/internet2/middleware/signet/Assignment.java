/*--
$Id: Assignment.java,v 1.17 2007-02-24 02:11:32 ddonn Exp $
$Date: 2007-02-24 02:11:32 $

Copyright 2006 Internet2, Stanford University

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
package edu.internet2.middleware.signet;

import java.util.Set;

import edu.internet2.middleware.signet.Function;
import edu.internet2.middleware.signet.SignetAuthorityException;
import edu.internet2.middleware.signet.subjsrc.SignetSubject;
import edu.internet2.middleware.signet.tree.TreeNode;

/**
* 
* An Assignment represents some authority which has been granted to a 
* {@link SignetSubject} (often a person).  The granularity of an Assignment
* is always {@link Function}; that is, a <code>Function</code> is the smallest unit
* of authority which can be assigned.  An Assignment always has an
* organizational scope associated with it, and in the future, will also have a
* condition organization associated with it.  
* <p>
* The organizational scope represents the highest level in the
* organizational hierarchy at which the <code>SignetSubject</code> can
* exercise the <code>Function</code>; 
* the condition organization, when it is introduced, will be an organization to
* which the <code>SignetSubject</code> must belong for the authority to be
* active. For example, an Assignment can be interpreted to mean:
* <p>
* "(<code>SignetSubject</code>) can perform (<code>Function</code>) in
* (organizational scope) as long as (<code>SignetSubject</code>)
* belongs to (condition organization)".
* <p>
* In addition, there can be {@link Limit}s (constraints) on the assignment.
* <code>getLimitValues</code> returns the <code>Limit</code>s applicable to this
* assignment, along with the values of those <code>Limit</code>s.
* <p>
* Also, an Assignment may or may not be grantable.  If the Assignment is
* grantable, then the <code>SignetSubject</code> may assign this
* <code>Function</code>, with scope equal to or more restrictive than his own,
* and with <code>Limit</code>-values equal to or more restrictive than his own,
* to another <code>SignetSubject</code>.
* <p>
* An existing Assignment may be modified. To save the modified Assignment,
* call Assignment.save().
* 
* @see SignetSubject
* @see Function
* 
*/

public interface Assignment extends Grantable
{
  /**
   * Gets the scope (usually an organization) of this Assignment.
   * 
   * @return the scope (usually an organization) of this Assignment.
   */
  public TreeNode getScope();

  /**
   * Gets the <code>Function</code> which is the subject of this Assignment.
   * 
   * @return the <code>Function</code> which is the subject of this Assignment.
   */
  public Function getFunction();

  /**
   * Indicates whether or not this Assignment can be granted to others
   * by its current grantee.
   * 
   * @return <code>true</code> if this Assignment can be granted to others
   * by its current grantee.
   */
  public boolean canGrant();
  
  /**
   * Changes the grantability of an existing Assignment. To save this change
   * to the database, call <code>Assignment.save()</code>.
   * @param editor the <code>SignetSubject</code> who is responsible for
   * this change.
   * @param canGrant <code>true</code> if this Assignment should be grantable
   * to others by its current grantee, and <code>false</code> otherwise.
   * @param checkAuth Flag to indicate whether to check for Edit authority by given actor.
   * Note that quite often several values may be set/updated for a Grantable for
   * the actor. Setting checkAuth to false assumes that the caller of the 'set'
   * methods has already called checkEditAuthority(SignetSubject).
   * @throws SignetAuthorityException
   */
  public void setCanGrant(SignetSubject editor, boolean canGrant, boolean checkAuth)
  throws SignetAuthorityException;

  /**
   * Indicates whether or not this Assignment can be used directly
   * by its current grantee, or can only be granted to others.
   * 
   * @return <code>false</code> if this Assignment can only be granted to others
   * by its current grantee, and not used directly by its current grantee.
   */
  public boolean canUse();
  
  /**
   * Changes the direct usability of an existing Assignment. To save this change
   * to the database, call <code>Assignment.save()</code>;
   * @param editor the <code>SignetSubject</code> who is responsible for
   * this change.
   * @param canUse <code>false</code> if this Assignment should only be
   * granted to others (and not directly used) by its current grantee, and
   * <code>true</code> otherwise.
   * @param checkAuth Flag to indicate whether to check for Edit authority by given actor.
   * Note that quite often several values may be set/updated for a Grantable for
   * the actor. Setting checkAuth to false assumes that the caller of the 'set'
   * methods has already called checkEditAuthority(SignetSubject).
   * @throws SignetAuthorityException
   */
  public void setCanUse(SignetSubject editor, boolean canUse, boolean checkAuth)
  throws SignetAuthorityException;

  /**
   * Gets the {@link Limit}s and <code>Limit</code>-values applied to this
   * Assignment.
   * 
   * @return a set of {@link LimitValue} objects, which represents all of the
   * <code>Limit</code>s (constraints) applied to this Assignment, along with
   * the values of those <code>Limit</code>s.
   */
  public Set getLimitValues();
  
  /**
   * Changes the {@link Limit}-values applied to an existing Assignment. To save
   * this change in the database, call Assignment.save().
   * @param editor the <code>SignetSubject</code> who is responsible for
   * this change.
   * @param limitValues the complete Set of {@link LimitValue}s that should be
   * associated with this Assignment.
   * @param checkAuth Flag to indicate whether to check for Edit authority by given actor.
   * Note that quite often several values may be set/updated for a Grantable for
   * the actor. Setting checkAuth to false assumes that the caller of the 'set'
   * methods has already called checkEditAuthority(SignetSubject).
   * @throws SignetAuthorityException
   *
   */
  public void setLimitValues(SignetSubject editor, Set limitValues, boolean checkAuth)
  throws SignetAuthorityException;
}