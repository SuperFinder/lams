<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.wiki.util.WikiConstants"%>

<script type="text/javascript">
	var initialTabId = "${wikiDTO.currentTab}";
</script>

<c:set var="title"><fmt:message key="activity.title" /></c:set>
<lams:Page title="${title}" type="navbar">

	<lams:Tabs title="${title}" control="true" helpToolSignature="<%= WikiConstants.TOOL_SIGNATURE %>" helpModule="monitoring">
		<lams:Tab id="1" key="button.summary" />
		<lams:Tab id="2" key="button.editActivity" />
		<lams:Tab id="3" key="button.statistics" />
	</lams:Tabs>
	
	<lams:TabBodyArea>
		<lams:TabBodys>
			<lams:TabBody id="1" titleKey="button.summary" page="summary.jsp" />
			<lams:TabBody id="2" titleKey="button.editActivity" page="editActivity.jsp" />
			<lams:TabBody id="3" titleKey="button.statistics" page="statistics.jsp" />
		</lams:TabBodys>
	</lams:TabBodyArea>
	
	<div id="footer" />
	
</lams:Page>
