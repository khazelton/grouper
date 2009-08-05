<%@ include file="../common/commonTaglib.jsp" %>
<div class="section">
<div class="sectionHeader">Membership list</div>
<div class="sectionBody"><br>

  
  <c:if test="${fn:length(simpleMembershipUpdateContainer.members) == 0}">
    There are no members in this group
  
  </c:if>
  
  <c:forEach items="${simpleMembershipUpdateContainer.members}" var="theMember">

    <div class="memberLink"><%-- input type="checkbox"    / --%> 
    <a href="#" onclick="if (confirm('Are you sure you want to delete this membership?')) {ajax('SimpleMembershipUpdate.deleteSingle?memberId=${theMember.uuid}');} return false;" 
    ><img src="../public/assets/page_cross.gif" height="14px" border="0"/></a>
    &nbsp;
    <grouperGui:subjectIcon guiSubject="${theMember.subject}" /> 
    <span class=simpleMembershipUpdateMemberDescription>${fn:escapeXml(theMember.subject.screenLabel)}</span></div> 

  </c:forEach>
 
<br />
<%-- input class="blueButton" type="submit" onclick="return confirm('You are about to remove some members of this group. You cannot undo this operation. Are you sure?')" value="Remove selected members" name="submit.remove.selected"/ --%>
<%-- input class="blueButton" type="submit" onclick="return confirm('You are about to remove all members of this group. You cannot undo this operation. Are you sure?')" value="Remove all members" name="submit.remove.all"/ --%>
</div>
</div>