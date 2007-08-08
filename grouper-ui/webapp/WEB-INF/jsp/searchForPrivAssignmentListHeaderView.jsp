<%-- @annotation@
		  Dynamic tile used by genericList mechanism to render
		  content above list items - i.e. is an alternative header
		  when searching for subjects in 'Find' mode
--%><%--
  @author Gary Brown.
  @version $Id: searchForPrivAssignmentListHeaderView.jsp,v 1.4 2007-08-08 09:42:13 isgwb Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>

<h2 class="actionheader">
	<fmt:message bundle="${nav}" key="find.heading.select-privs">
		<fmt:param value="${subtitleArgs[0]}"/>
	</fmt:message>
</h2>
<div class="assignMembersForm">
    <form class="AssignMembersForm" action="doAssignNewMembers.do" method="post">
	<input type="hidden" name="callerPageId" value="<c:out value="${SearchFormBean.map.callerPageId}"/>"/>
	<fieldset>
		<c:if test="${forStems}">
			<input type="hidden" name="stems" value="true"/>
			<input type="hidden" name="stemId" value="<c:out value="${SearchFormBean.map.stemId}"/>"/>
    		
		</c:if>
		<c:if test="${!forStems}">
    	<input type="hidden" name="groupId" value="<c:out value="${SearchFormBean.map.groupId}"/>"/> 
		</c:if>
		
		<c:choose>
			<c:when test="${empty findForPriv}">
				<c:set var="memberChecked"> checked="CHECKED"</c:set>
			</c:when>
			<c:otherwise>
				<%pageContext.setAttribute((String)session.getAttribute("findForPriv")+"Checked","checked='CHECKED'");%>
			</c:otherwise>	
		</c:choose>
		<div class="privilegeCheckBoxes">
			<c:if test="${forStems}">
    			<span class="checkbox"><input type="checkbox" name="privileges" 
				value="CREATE" id="privCreate"<c:out value="${CREATEChecked}"/>/>&#160;<label for="privCreate"><fmt:message bundle="${nav}" key="priv.create"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" 
				value="STEM" id="privStem"<c:out value="${STEMChecked}"/>/>&#160;<label for="privCreate"><fmt:message bundle="${nav}" key="priv.stem"/></label></span>
				<input type="hidden" name="stems" value="true"/>
			</c:if>

                      
			<c:if test="${!forStems}">
				
    			<span class="checkbox"><input type="checkbox" name="privileges" value="member"  id="privMember"<c:out value="${memberChecked}"/>/> 
					<label for="privMember"><fmt:message bundle="${nav}" key="priv.member"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" value="OPTIN"  id="privOptin"<c:out value="${OPTINChecked}"/>/> 
					<label for="privOptin"><fmt:message bundle="${nav}" key="priv.optin"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" value="OPTOUT"  id="privOptout"<c:out value="${OPTOUTChecked}"/>/> 
					<label for="privOptout"><fmt:message bundle="${nav}" key="priv.optout"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" value="VIEW"  id="privView"<c:out value="${VIEWChecked}"/>/> 
					<label for="privView"><fmt:message bundle="${nav}" key="priv.view"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" value="READ"  id="privRead"<c:out value="${READChecked}"/>/> 
					<label for="privRead"><fmt:message bundle="${nav}" key="priv.read"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" value="UPDATE"  id="privUpdate"<c:out value="${UPDATEChecked}"/>/> 
					<label for="privUpdate"><fmt:message bundle="${nav}" key="priv.update"/></label></span>
    			<span class="checkbox"><input type="checkbox" name="privileges" 
				value="ADMIN"  id="privAdmin"<c:out value="${ADMINChecked}"/>/>&#160;<label for="privAdmin"><fmt:message bundle="${nav}" key="priv.admin"/></label></span>
			</c:if>


		</div>
		</fieldset>
	<h2 class="actionheader">
		<fmt:message bundle="${nav}" key="find.heading.select-results"/>
	</h2>
	<tiles:importAttribute ignore="true"/>
			<tiles:insert definition="dynamicTileDef" flush="false">
	  			<tiles:put name="viewObject" beanName="pager" beanProperty="collection"/>
	  			<tiles:put name="view" value="privilegeLinksHeader"/>
				<tiles:put name="noResultsMsg" beanName="noResultsMsg"/>
				<tiles:put name="allowPageSizeChange" value="false"/>
				<tiles:put name="listInstruction" beanName="listInstruction"/>
  			</tiles:insert>