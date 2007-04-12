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

package edu.internet2.middleware.grouper;
import  edu.internet2.middleware.subject.Subject;
import  java.io.Serializable;
import  net.sf.hibernate.*;

/**
 * Stub Hibernate {@link Member} DAO.
 * <p/>
 * @author  blair christensen.
 * @version $Id: HibernateMemberDAO.java,v 1.18 2007-04-12 17:56:03 blair Exp $
 * @since   1.2.0
 */
class HibernateMemberDAO extends HibernateDAO implements Lifecycle,MemberDAO {

  // PRIVATE CLASS CONSTANTS //
  private static final String KLASS = HibernateMemberDAO.class.getName();


  // PRIVATE CLASS VARIABLES //
  private static SimpleBooleanCache existsCache   = new SimpleBooleanCache();
  private static SimpleCache        uuid2dtoCache = new SimpleCache();


  // HIBERNATE PROPERTIES //
  private String  id;
  private String  subjectID;
  private String  subjectSourceID;
  private String  subjectTypeID;
  private String  uuid;


  // PUBLIC INSTANCE METHODS //

  /**
   * @since   1.2.0
   */
  public String create(MemberDTO _m) 
    throws  GrouperDAOException
  {
    try {
      Session       hs  = HibernateDAO.getSession();
      Transaction   tx  = hs.beginTransaction();
      HibernateDAO  dao = (HibernateDAO) Rosetta.getDAO(_m);
      try {
        hs.save(dao);
        tx.commit();
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      }
      return dao.getId();
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public boolean exists(String uuid) 
    throws  GrouperDAOException
  {
    if ( existsCache.containsKey(uuid) ) {
      return existsCache.getBoolean(uuid).booleanValue();
    }
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("select m.id from HibernateMemberDAO as m where m.uuid = :uuid");
      qry.setCacheable(false);
      qry.setCacheRegion(KLASS + ".Exists");
      qry.setString("uuid", uuid);
      boolean rv  = false;
      if ( qry.uniqueResult() != null ) {
        rv = true; 
      }
      hs.close();
      existsCache.put(uuid, rv);
      return rv;
    }
    catch (HibernateException eH) {
      ErrorLog.fatal( HibernateMemberDAO.class, eH.getMessage() );
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /**
   * @since   1.2.0
   */
  public MemberDTO findBySubject(Subject subj)
    throws  GrouperDAOException,
            MemberNotFoundException
  {
    return this.findBySubject( subj.getId(), subj.getSource().getId(), subj.getType().getName() );
  } 

  /**
   * @since   1.2.0
   */
  public MemberDTO findBySubject(String id, String src, String type) 
    throws  GrouperDAOException,
            MemberNotFoundException
  {
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery(
        "from HibernateMemberDAO as m where "
        + "     m.subjectId       = :sid    "  
        + "and  m.subjectSourceId = :source "
        + "and  m.subjectTypeId   = :type"
      );
      qry.setCacheable(false); // but i probably should - or at least permit it
      //qry.setCacheRegion(KLASS + ".FindBySubject");
      qry.setString( "sid",    id   );
      qry.setString( "type",   type );
      qry.setString( "source", src  );
      HibernateMemberDAO dao = (HibernateMemberDAO) qry.uniqueResult();
      hs.close();
      if (dao == null) {
        throw new MemberNotFoundException();
      }
      MemberDTO _m = new MemberDTO()
        .setId( dao.getId() )
        .setUuid( dao.getUuid() )
        .setSubjectId( dao.getSubjectId() )
        .setSubjectSourceId( dao.getSubjectSourceId() )
        .setSubjectTypeId( dao.getSubjectTypeId() );
      uuid2dtoCache.put( _m.getUuid(), _m );
      return _m;
    }
    catch (HibernateException eH) {
      String msg = E.MEMBER_NEITHER_FOUND_NOR_CREATED + eH.getMessage();
      ErrorLog.fatal(HibernateMemberDAO.class, msg);
      throw new GrouperDAOException(msg, eH);
    }
  } 

  /**
   * @since   1.2.0
   */
  public MemberDTO findByUuid(String uuid) 
    throws  GrouperDAOException,
            MemberNotFoundException
  {
    if ( uuid2dtoCache.containsKey(uuid) ) {
      return (MemberDTO) uuid2dtoCache.get(uuid);
    }
    try {
      Session hs  = HibernateDAO.getSession();
      Query   qry = hs.createQuery("from HibernateMemberDAO as m where m.uuid = :uuid");
      qry.setCacheable(false); // but i probably should - or at least permit it
      //qry.setCacheRegion(KLASS + ".FindByUuid");
      qry.setString("uuid", uuid);
      HibernateMemberDAO dao = (HibernateMemberDAO) qry.uniqueResult(); 
      hs.close();
      if (dao == null) {
        throw new MemberNotFoundException();
      }
      MemberDTO _m = new MemberDTO()
        .setId( dao.getId() )
        .setUuid( dao.getUuid() )
        .setSubjectId( dao.getSubjectId() )
        .setSubjectSourceId( dao.getSubjectSourceId() )
        .setSubjectTypeId( dao.getSubjectTypeId() );
      uuid2dtoCache.put(uuid, _m);
      return _m;
    }
    catch (HibernateException eH) {
      ErrorLog.fatal( HibernateMemberDAO.class, eH.getMessage() );
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 

  /** 
   * @since   1.2.0
   */
  public String getId() {
    return this.id;
  } 

  /** 
   * @since   1.2.0
   */
  public String getSubjectId() {
    return this.subjectID;
  }

  /**
   * @since   1.2.0
   */
  public String getSubjectSourceId() {
    return this.subjectSourceID;
  }

  /**
   * @since   1.2.0
   */
  public String getSubjectTypeId() {
    return this.subjectTypeID;
  }

  /**
   * @since   1.2.0
   */
  public String getUuid() {
    return this.uuid;
  }

  // @since   1.2.0 
  public boolean onDelete(Session hs) 
    throws  CallbackException
  {
    existsCache.put( this.getUuid(), false );
    uuid2dtoCache.remove( this.getUuid() );
    return Lifecycle.NO_VETO;
  } // public boolean onDelete(hs)

  // @since   1.2.0
  public void onLoad(Session hs, Serializable id) {
    // nothing
  } // public void onLoad(hs, id)

  // @since   1.2.0
  public boolean onSave(Session hs) 
    throws  CallbackException
  {
    existsCache.put( this.getUuid(), true );
    return Lifecycle.NO_VETO;
  } // public boolean onSave(hs)

  // @since   1.2.0
  public boolean onUpdate(Session hs) 
    throws  CallbackException
  {
    // nothing
    return Lifecycle.NO_VETO;
  } // public boolean onUpdate(hs)k


  /** 
   * @since   1.2.0
   */
  public MemberDAO setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public MemberDAO setSubjectId(String subjectID) {
    this.subjectID = subjectID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public MemberDAO setSubjectSourceId(String subjectSourceID) {
    this.subjectSourceID = subjectSourceID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public MemberDAO setSubjectTypeId(String subjectTypeID) {
    this.subjectTypeID = subjectTypeID;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public MemberDAO setUuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * @since   1.2.0
   */
  public void update(MemberDTO _m) 
    throws  GrouperDAOException
  {
    try {
      Session     hs  = HibernateDAO.getSession();
      Transaction tx  = hs.beginTransaction();
      try {
        hs.update( _m.getDAO() );
      }
      catch (HibernateException eH) {
        tx.rollback();
        throw eH;
      }
      finally {
        hs.close();
      } 
    }
    catch (HibernateException eH) {
      throw new GrouperDAOException( eH.getMessage(), eH );
    }
  } 


  // PROTECTED CLASS METHODS //

  // @since   1.2.0
  protected static void reset(Session hs) 
    throws  HibernateException
  {
    hs.delete("from HibernateMemberDAO as m where m.subjectId != 'GrouperSystem'");
    existsCache.removeAll(); 
  } // protected static void reset(hs)

} // class HibernateMemberDAO extends HibernateDAO implements Lifecycle, MemberDAO

