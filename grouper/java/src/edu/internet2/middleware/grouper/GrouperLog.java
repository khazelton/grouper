/*
 * Copyright (C) 2004 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.internet2.middleware.grouper;

import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  net.sf.hibernate.*;
import  org.apache.log4j.*;


/** 
 * Internal logging class.
 * <p />
 * This class is only used internally.
 *
 * @author  blair christensen.
 * @version $Id: GrouperLog.java,v 1.3 2004-12-08 05:15:00 blair Exp $
 */
public class GrouperLog {

  /*
   * PROTECTED CONSTANTS
   */
  private static final Logger LOG     = 
    Logger.getLogger(Grouper.class.getName());
  private static final Logger LOG_GB  = 
    Logger.getLogger(GrouperBackend.class.getName());
  private static final Logger LOG_QRY = 
    Logger.getLogger(GrouperBackend.class.getName() + ".Query");


  /*
   * CONSTRUCTORS
   */
  protected GrouperLog() {
    super();
  }


  /*
   * PROTECTED INSTANCE METHODS
   */

  // Backend events
  protected void backend(String msg) {
    LOG_GB.info(msg);
  }

  // General events
  protected void event(String msg) {
    LOG.info(msg);
  }

  // Privilege: Grant
  protected void grant(
                   boolean rv, GrouperSession s, GrouperGroup g, 
                   GrouperMember m, String priv
                 ) 
  {
    Subject tgt  = GrouperSubject.load(
                              m.subjectID(), m.typeID()
                            );
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " '" + priv + "' to memberID='" + m.memberID() + 
                  "' subjectID='" + tgt.getId() + "' on '" +
                  g.name() + "'";
    if (rv == true) {
      LOG.info(pre + "granted" + post);
    } else {
      LOG.info(pre + "failed to grant" + post);
    }
  }

  // Group: Add
  protected void groupAdd(
                   GrouperSession s, GrouperGroup g, String name, 
                   String type
                 ) 
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " '" + name + "' (" + type + ")";
    if (g != null) {
      LOG.info(pre + "created" + post);
    } else {
      LOG.info(pre + "failed to create" + post);
    }
  }

  // Grouper: Add Cannot
  protected void groupAddCannot(
                   GrouperSession s, String name, String type
                 ) 
  {
    LOG.info(
      "'" + s.subject().getId() + "' cannot create '" + name + 
      "' (" + type + ") as it already exists"
    );
  }

  // Group: Attribute Add
  protected void groupAttrAdd(
                   boolean rv, GrouperSession s, GrouperGroup g,
                   String attr, String value
                 )
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " attribute '" + attr + "'='" + value +
                  "' to '" + g.name() + "'";
    if (rv == true) {
      LOG.info(pre + "added" + post);
    } else {
      LOG.info(pre + "failed to add" + post);
    }
  }

  // Group: Attribute Delete
  protected void groupAttrDel(
                   boolean rv, GrouperSession s, GrouperGroup g, 
                   String attr
                 ) 
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " attribute '" + attr + "' from '" + g.name() + "'";
    if (rv == true) {
      LOG.info(pre + "deleted" + post);
    } else {
      LOG.info(pre + "failed to delete" + post);
    }
  }

  // Group: Attribute No Modification
  protected void groupAttrNoMod(String attribute) {
    LOG.info(
      "'" + attribute + "' modification is not currently supported"
    );
  }

  // Group: Attribute Update
  protected void groupAttrUpdate(
                   boolean rv, GrouperSession s, GrouperGroup g,
                   String attr, String value
                 )
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " attribute '" + attr + "'='" + value +
                  "' to '" + g.name() + "'";
    if (rv == true) {
      LOG.info(pre + "updated" + post);
    } else {
      LOG.info(pre + "failed to update" + post);
    }
  }

  // Group: Delete
  protected void groupDel(
                   boolean rv, GrouperSession s, GrouperGroup g
                 ) 
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " '" + g.name() + "' (" + g.type() + ")";
    if (rv == true) {
      LOG.info(pre + "deleted" + post);
    } else {
      LOG.info(pre + "failed to delete" + post);
    }
  }

  // Group: List Value Add
  protected void groupListAdd(
                   boolean rv, GrouperSession s, GrouperGroup g,
                   GrouperMember m
                 )
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " memberID='" + m.memberID() + "' subjectID='" +
                  m.subjectID() + "' to '" + g.name() + "' (" +
                  Grouper.DEF_LIST_TYPE + ")";
    if (rv == true) {
      LOG.info(pre + "added" + post);
    } else {
      LOG.info(pre + "failed to add" + post);
    }
  }

  // Group: List Value Delete
  protected void groupListDel(
                   boolean rv, GrouperSession s, GrouperGroup g,
                   GrouperMember m
                 )
  {
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " memberID='" + m.memberID() + "' subjectID='" +
                  m.subjectID() + "' from '" + g.name() + "' (" +
                  Grouper.DEF_LIST_TYPE + ")";
    if (rv == true) {
      LOG.info(pre + "removed" + post);
    } else {
      LOG.info(pre + "failed to remove" + post);
    }
  }

  // Member: Add
  protected void memberAdd(GrouperMember m, Subject subj) {
    String post = " to member table";
    if (m != null) { 
      LOG.info(
        "Added memberID='" + m.memberID() + "' " +
        "subjectID='" + m.subjectID() + "'" + post
      );
    } else {
      LOG.info(
        "Failed to add subjectID='" + subj.getId() + "' (" +
        subj.getSubjectType().getId() + ")" + post
      );
    }
  }

  // Not Implemented
  protected void notimpl(String method) {
    LOG.warn("Not Implemented: '" + method + "'");
  }

  // Privilege: Revoke
  protected void revoke(
                   boolean rv, GrouperSession s, GrouperGroup g, 
                   GrouperMember m, String priv
                 ) 
  {
    Subject tgt  = GrouperSubject.load(
                              m.subjectID(), m.typeID()
                            );
    String pre  = "'" + s.subject().getId() + "' ";
    String post = " '" + priv + "' from memberID='" + m.memberID() + 
                  "' subjectID='" + tgt.getId() + "' on '" +
                  g.name() + "'";
    if (rv == true) {
      LOG.info(pre + "revoked" + post);
    } else {
      LOG.info(pre + "failed to revoke" + post);
    }
  }

  // Hibernate queries
  protected void query(String method, Query q) {
    LOG_QRY.info(method + ": " + q.getQueryString());
  }

  // Session: Start
  protected void sessionStart(boolean rv, GrouperSession s) {
    String post = " session for '" + s.subject().getId() + "'";
    if (rv == true) {
      LOG.info("Started" + post);
    } else {
      LOG.info("Failed to start" + post);
    }
  }

  // Session: Stop
  protected void sessionStop(boolean rv, GrouperSession s) {
    if (rv == true) {
      LOG.info("Stopped session for '" + s.subject().getId() + "'");
    } else {
      LOG.info("Failed to stop session");
    }
  }

  // General warnings
  protected void warn(String msg) {
    LOG.warn(msg);
  }

}

