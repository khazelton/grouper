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


import  java.io.Serializable;
import  java.util.*;
import  net.sf.hibernate.*;
import  org.apache.commons.lang.builder.*;
import  org.apache.commons.logging.*;


/** 
 * Grouper Transaction Queue.
 * @author blair christensen.
 *     
*/
class TxQueue implements Serializable {

  // Private Class Constants
  private static final Log LOG = LogFactory.getLog(TxQueue.class);

  
  // Hibernate Properties
  private Member      actor;
  private List        dirty       = new ArrayList();
  private Field       field;
  private String      id;
  private String      klass;
  private Member      member;
  private String      owner;      // Switch to _Owner_
  private String      sessionId;
  private long        queueTime;
  private QueueStatus status;
  private String      uuid;


  // Constructors

  // For Hibernate
  public TxQueue() { 
    super();
    this.setQueueTime(  new Date().getTime()            );
    this.setStatus(     QueueStatus.getInstance("wait") );
    this.setUuid(       GrouperUuid.getUuid()           );
  }


  // Public Instance Methods
  public String toString() {
    String k = this.getKlass();
    return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
      .append("class"       , this.getClass().getName()                 )
      .append("time"        , this.getQueueTime()                       )
      .append("uuid"        , this.getUuid()                            )
      .append("status"      , this.getStatus().getName()                )
      .append("time"        , new Date( this.getQueueTime()).toString() )
      .toString()
      ;
  } // public String toString() 


  // Protected Instance Methods
  protected boolean apply(GrouperDaemon gd) {
    return false;
  } // protected boolean apply(gd)

  protected boolean setFailed(GrouperDaemon gd) {
    boolean rv = false;
    try {
      this.setStatus( QueueStatus.getInstance("fail") );
      HibernateHelper.save(this);
      rv = true;
    }
    catch (HibernateException eH) {
      gd.getLog().error(eH.getMessage());
    }
    return rv;
  } // protected boolean setFailed(gd)


  // Hibernate Accessors
  protected Member getActor() {
    return this.actor;
  }

  protected void setActor(Member actor) {
    this.actor = actor;
  }

  protected List getDirty() {
    return this.dirty;
  }
  
  protected void setDirty(List dirty) {
    this.dirty = dirty;
  }

  protected Field getField() {
    return this.field;
  }

  protected void setField(Field f) {
    this.field = f;
  }

  protected String getId() {
    return this.id;
  }
  protected void setId(String id) {
    this.id = id;
  }

  protected String getKlass() {
    return this.klass;
  }
  protected void setKlass(String klass) {
    this.klass = klass;
  }

  protected String getOwner() {
    return this.owner;
  }

  protected void setOwner(String owner) {
    this.owner = owner;
  }

  protected Member getMember() {
    return this.member;
  }
  protected void setMember(Member m) {
    this.member = m;
  }

  protected long getQueueTime() {
    return this.queueTime;
  }
  protected void setQueueTime(long time) {
    this.queueTime = time;
  }
 
  protected String getSessionId() {
    return this.sessionId;
  }

  protected void setSessionId(String id) {
    this.sessionId = id;
  }

  protected QueueStatus getStatus() {
    return this.status;
  }
  protected void setStatus(QueueStatus s) {
    this.status = s;
  }

  protected String getUuid() {
    return this.uuid;
  }
  protected void setUuid(String uuid) {
    this.uuid = uuid;
  }

}

