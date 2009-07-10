/*
 * @author mchyzer
 * $Id: JdbcConnectionProvider.java,v 1.2 2008-10-13 08:04:29 mchyzer Exp $
 */
package edu.internet2.middleware.subject.provider;

import java.sql.SQLException;

import edu.internet2.middleware.subject.SourceUnavailableException;


/**
 * gives connections and allows returns.  e.b. dbcp pooling, c3p0 pooling,
 * grouper built in db
 */
public interface JdbcConnectionProvider {

  /**
   * if the provider requires jdbc data in the sources.xml
   * @return true if provider requires data in sources.xml
   */
  public boolean requiresJdbcConfigInSourcesXml();
  
  /** 
   * init the pool 
   * @param sourceId mainly for logging
   * @param driver driver
   * @param maxActive max active connection if pool
   * @param defaultMaxActive if max active is needed, and blank, then use this
   * @param maxIdle max idle connections if pool
   * @param defaultMaxIdle if max is needed, and blank, then use this
   * @param maxWaitSeconds max wait if pool empty in seconds.  -1 means long time
   * @param defaultMaxWaitSeconds if max wait is needed, and blank, then use this, -1 mean long time
   * @param dbUrl jdbc url of the database
   * @param dbUser user to login to the database
   * @param dbPassword password for the database (unencrypted)
   * @param readOnly if conn should be readonly
   * @param readOnlyDefault default if not specified and needed
   * @throws SourceUnavailableException if something is not right or not available
   */
  public void init(String sourceId, String driver, Integer maxActive, int defaultMaxActive, Integer maxIdle, int defaultMaxIdle,
      Integer maxWaitSeconds, int defaultMaxWaitSeconds, String dbUrl, String dbUser, 
      String dbPassword, Boolean readOnly, boolean readOnlyDefault) throws SourceUnavailableException;
  
  /**
   * get a connection (dont close this when done, just call "doneWithConnection()"
   * @return the connection
   * @throws SQLException if there is a problem
   */
  public JdbcConnectionBean connectionBean() throws SQLException;
  
}