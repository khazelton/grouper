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
/**
 * 
 */
package edu.internet2.middleware.grouper.j2ee.status;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.misc.GrouperVersion;
import edu.internet2.middleware.grouper.util.GrouperUtil;


/**
 * status servlet to see if grouper is ok (e.g. the DB and the loader jobs, etc)
 * @author mchyzer
 *
 */
@SuppressWarnings("serial")
public class GrouperStatusServlet extends HttpServlet {

  /** last error */
  private static String lastDiagnosticsError;

  /** last error date */
  private static Long lastDiagnosticsErrorDate;

  /** last error date */
  private static String lastDiagnosticsErrorDateString;

  /** number of requests */
  private static long numberOfRequests = 0;
  
  /**
   * call this from each servlet (except diagnostics)
   */
  public static synchronized void incrementNumberOfRequest() {
    numberOfRequests++;
  }
  
  /** count the number of errors since server start */
  private static int diagnosticErrorCount = 0;
  
  /** logger */
  private static final Log LOG = LogFactory.getLog(GrouperStatusServlet.class);

  /** start nanos of these status requests */
  private static ThreadLocal<Long> startNanos = new ThreadLocal<Long>();
  
  /** date we were started */
  private static String startupString = null; 
  
  /**
   * register that we are starting
   */
  public static void registerStartup() {
    if (startupString == null) {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd kk:mm");
      Date startUp = new Date();
      startupString = formatter.format(startUp);
    }
  }
  
  static {
    GrouperStatusServlet.registerStartup();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    
    startNanos.set(System.nanoTime());
    
    StringBuilder outerResult = new StringBuilder("<?xml version=\"1.0\" ?>\n"
        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
        + "\n"
        + "<html><head></head><body>\n");
    StringBuilder result = new StringBuilder("<pre>");
    
    DiagnosticTask currentTask = null;
    GrouperSession grouperSession = null;
    try {
      
      response.setContentType("text/html");

      // Start of the text to return.
      result.append("Server: ");
      result.append(GrouperUtil.hostname());
      result.append(", grouperVersion: ").append(GrouperVersion.currentVersion());
      result.append(", up since: ");
      result.append(startupString).append(", ");
      result.append(numberOfRequests);
      result.append(" requests\n");
      
      // List all of the diagnostic tasks to execute.
      Set<DiagnosticTask> tasksToExecute = new LinkedHashSet<DiagnosticTask>();

      String diagnosticTypeString = request.getParameter("diagnosticType");
      
      if (StringUtils.isBlank(diagnosticTypeString)) {
        throw new RuntimeException("You need to pass in the diagnosticType parameter.  e.g. status?diagnosticType=trivial|db|sources|all");
      }
      
      DiagnosticType diagnosticType = DiagnosticType.valueOfIgnoreCase(diagnosticTypeString, true);

      grouperSession = GrouperSession.startRootSession();
      
      // List all of the diagnostic tasks to execute.
        
      diagnosticType.appendDiagnostics(tasksToExecute);

      // Execute each task, until all are complete or there is a 
      // failure, in which case, throw an exception. If there
      // is a failure, the error text (if any) of the task
      // will be picked up and utilized in the catch block.
      for (DiagnosticTask diagnosticTask : tasksToExecute) {
        currentTask = diagnosticTask;
        if (diagnosticTask.executeTask()){
          result.append(diagnosticTask.retrieveSuccessText().toString());
        } else {
          throw new RuntimeException();
        }
      }

      result.append("\n\n");

      // Append the error count.
      result.append("Diagnostics errors since start: ")
          .append(diagnosticErrorCount)
          .append(elapsedSuffix());

      // See if there was an error, in the last day store the results if so.
      if (diagnosticErrorCount > 0 && lastDiagnosticsErrorDate != null 
          && System.currentTimeMillis() - lastDiagnosticsErrorDate < (long)24 * 60 * 60 * 1000 ) {
        result.append("\nLast diagnostics error date: ")
          .append(lastDiagnosticsErrorDateString)
          .append("\nLast diagnostics error message: ")
          .append(lastDiagnosticsError);
      }

      
      response.setStatus(200);
            
      Writer writer = null;
      
      try {
        writer = response.getWriter();
        result.append("</pre>\n");
        outerResult.append("<h1>Grouper status SUCCESS</h1><br />").append(result).append(
            "</body></html>");
        writer.write(outerResult.toString());
      } catch (Exception e) {
        LOG.error("error", e);
      } finally {
        GrouperUtil.closeQuietly(writer);
      }

      
    } catch (RuntimeException re) {

      //this is not a real diagnostics error
      if (re.getMessage().contains("Cant find DiagnosticType from string")
          || re.getMessage().contains("You need to pass in the diagnosticType parameter")) {
        response.setStatus(500);

        LOG.warn("Invalid DiagnosticsType", re);

        writeToScreen(response, "<?xml version=\"1.0\" ?>\n"
            + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
            + "\n"
            + "<html><head></head><body><h1>Grouper status invalid request!</h1><br /><pre>" + re.getMessage() + "</pre></body></html>");
        
        
        return;
      }
      
      diagnosticErrorCount++;
      lastDiagnosticsErrorDate = System.currentTimeMillis();
      
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss");
      Date now = new Date();
      lastDiagnosticsErrorDateString = formatter.format(now);

      response.setStatus(500);
      
      String theLastDiagnosticsError = null;
      
      if (currentTask != null) {
        
        String message = "\nThere was an error in the diagnostic task " + currentTask.getClass().getSimpleName()  
            + ", " + currentTask.retrieveNameFriendly() + "\n\n"
            + currentTask.retrieveFailureText() + ":" + re.getMessage();
        
        theLastDiagnosticsError = message + "\n" + ExceptionUtils.getFullStackTrace(re);
        
        LOG.error(message, re);
        
        
        
      } else {

        theLastDiagnosticsError = "Error in status:\n" + ExceptionUtils.getFullStackTrace(re);
        LOG.error("Error in status: ", re);
      }
      
      lastDiagnosticsError = theLastDiagnosticsError;
      
      //dont throw exception since that will trigger the struts error handling, we need to print to commit the response
      writeToScreen(response, "<?xml version=\"1.0\" ?>\n"
        + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
        + "\n"
        + "<html><head></head><body><h1>Grouper status error!</h1><br /><pre>" + theLastDiagnosticsError + "</pre></body></html>");
      
      
    } finally {
      
      
      try {
        GrouperSession.stopQuietly(grouperSession);
        
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
          httpSession.invalidate();
        }
      } catch (Exception e) {
        LOG.error("Error", e);
      }
      startNanos.remove();
    }
  }

  /**
   * write a message to screen
   * @param response
   * @param toWrite
   */
  public static void writeToScreen(HttpServletResponse response, String toWrite) {
    try {
      response.getWriter().println(toWrite);
    } catch (IOException e) {
      throw new RuntimeException("Cant write to screen", e);
    }
  }
  
  /**
   * put this string to be appended after each step: (14ms elapsed)
   * @return the string
   */
  public static String elapsedSuffix() {
    
    //divide the nanos by 1,000,000 to get down to millis
    int elapsedMillis = (int)((System.nanoTime() - 
        startNanos.get()) / 1000000);
    return " (" + elapsedMillis + "ms elapsed)";
  }


}
