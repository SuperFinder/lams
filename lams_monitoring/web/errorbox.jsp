<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-logic" prefix="logic" %>
<%@ page import="org.apache.struts.Globals" %>
<%
String cprotocol;
if(request.isSecure()){
	cprotocol = "https://";
}else{
	cprotocol = "http://";
}
String rootPath = cprotocol+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<logic:present name="<%=Globals.ERROR_KEY%>">
<tr>
	<td width="10%"  align="right" >
		<img src="<%=rootPath%>/images/error.jpg" alt="<fmt:message key="error.title"/>"/>
	</td>
	<td width="90%" valign="center" class="body" colspan="2">
		<html:errors/>
	</td>
</tr>
</logic:present>