<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as 
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
  USA

  http://www.gnu.org/licenses/gpl.txt
--%>


<%@ taglib uri="tags-bean" prefix="bean"%> 
<%@ taglib uri="tags-html" prefix="html"%>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

	<!DOCTYPE HTML PUBLIC "-//W3C//DTD hTML 4.01 Transitional//EN">
	<html:html locale="true">
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<title> <bean:message key="label.learning.report"/> </title>
	
	 <lams:css/>
	
	<!-- depending on user / site preference this will get changed probably use passed in variable from flash to select which one to use-->

 	<!-- ******************** FCK Editor related javascript & HTML ********************** -->
    <script type="text/javascript" src="${lams}fckeditor/fckeditor.js"></script>
    <script type="text/javascript" src="${lams}includes/javascript/fckcontroller.js"></script>
    <link href="${lams}css/fckeditor_style.css" rel="stylesheet" type="text/css">
	
	
	<script language="JavaScript" type="text/JavaScript">

		// general submit
		// actionMethod: name of the method to be called in the DispatchAction
		function submitMonitoringMethod(actionMethod) 
		{
			document.VoteMonitoringForm.method.value=actionMethod; 
			document.VoteMonitoringForm.submit();
		}
		
		function submitMethod(actionMethod) 
		{
			submitMonitoringMethod(actionMethod);
		}
		
		//-->
	</script>	
</head>
<body>

<c:if test="${ requestLearningReportProgress != 'true'}"> 			
	<c:if test="${ requestLearningReportViewOnly != 'true'}"> 			
		<b> <font size=2> <c:out value="${sessionScope.reportTitleLearner}" escapeXml="false"/> </font></b>
	</c:if> 				    
	<c:if test="${ requestLearningReportViewOnly == 'true'}"> 			
		<b> <font size=2> <bean:message key="label.learning.viewOnly"/> </font></b>
	</c:if> 				    

		<c:set var="monitoringURL">
			<html:rewrite page="/monitoring.do" />
		</c:set>

	  <html:form  action="/monitoring?validate=false" enctype="multipart/form-data" method="POST" target="_self">		
		<html:hidden property="method"/>	 

			<div class="tabbody content_b" >
				<jsp:include page="/monitoring/SummaryContent.jsp" />
			</div>		         

			<c:if test="${ requestLearningReportViewOnly != 'true'}"> 			
				<table align=right> 	  
				<tr>
					 <td> 
						<html:submit onclick="javascript:submitMethod('endLearning');" styleClass="button">
							<bean:message key="button.endLearning"/>
						</html:submit>	 				 		  					
					</td> 
				</tr>
				</table>
			</c:if> 			
	</html:form>
</c:if> 				    

<c:if test="${ requestLearningReportProgress == 'true'}"> 			
		 <font size=2> <b> <bean:message key="label.learner.progress"/> </b> </font>

		<c:set var="monitoringURL">
			<html:rewrite page="/monitoring.do" />
		</c:set>

	  <html:form  action="/monitoring?validate=false" enctype="multipart/form-data" method="POST" target="_self">		
		<html:hidden property="method"/>	 

			<div class="tabbody content_b" >
				<jsp:include page="/monitoring/SummaryContent.jsp" />
			</div>		         
	</html:form>
</c:if> 				    

</body>
</html:html>







