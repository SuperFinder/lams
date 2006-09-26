<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">


<%@ include file="/common/taglibs.jsp"%>
<c:set var="tool"><lams:WebAppURL/></c:set>
<%@ page import="org.lamsfoundation.lams.tool.survey.SurveyConstants"%>
<html>
<head>
	    <%@ include file="/common/header.jsp" %>
</head>
<body>
	<div id="page-learner">
		<h1 class="no-tabs-below">
			<fmt:message key="title.chart.report"/>
		</h1>
		<div id="header-no-tabs-learner">
		</div>
		<div id="content-learner">
			<table border="0" cellspacing="3" width="98%">
			<c:forEach var="entry" items="${answerList}" varStatus="status">
				<c:set var="user" value="${entry.key}"/>
				<c:set var="question" value="${entry.value}"/>
				<%--  display question header  --%>
				<c:if test="${status.first}">
					<tr>
						<td><fmt:message key="label.question"/></td>
						<td><c:out value="${question.description}" escapeXml="false"/></td>
					</tr>
					<tr>
						<td colspan="2"><fmt:message key="message.possible.answers"/></td>
					</tr>
					<c:forEach var="option" items="${question.options}" varStatus="optStatus">
						<tr>
							<td>
								<%= SurveyConstants.OPTION_SHORT_HEADER %>${optStatus.count}
							</td>
							<td>
								${option.description}
							</td>
						</tr>
					</c:forEach>
					<c:if test="${question.appendText ||question.type == 3}">
						<tr>
							<td>
								<fmt:message key="label.open.response"/>
							</td>
							<td></td>
						</tr>
					</c:if>
					<tr>
						<td colspan="2">
							<table>
								<tr>
									<th><fmt:message key="label.learner"/></th>
									<c:forEach var="option" items="${question.options}" varStatus="optStatus">
										<th>
											<%= SurveyConstants.OPTION_SHORT_HEADER %>${optStatus.count}
										</th>
									</c:forEach>
									<c:if test="${question.appendText || question.type == 3}">
										<th>
											<fmt:message key="label.open.response"/>
										</th>
									</c:if>
								</tr>
				<%--  End first check  --%>
				</c:if>
								<%--  User answer list --%>
								<tr>
									<td>${user.loginName}</td>
									<c:forEach var="option" items="${question.options}">
										<td>
											<c:if test="${not empty question.answer}">
												<c:set var="checked" value="false"/>
												<c:forEach var="choice" items="${question.answer.choices}">
													<c:if test="${choice == option.uid}">
														<c:set var="checked" value="true"/>
													</c:if>
												</c:forEach>
												<c:if test="${checked}">
													<img src="${tool}/includes/images/tick_red.gif" title="<fmt:message key="message.learner.choose.answer"/>">
												</c:if>
											</c:if>
										</td>
									</c:forEach>
									<c:if test="${question.appendText ||question.type == 3}">
										<td>
											<c:if test="${not empty question.answer}">
												<c:if test="${not empty question.answer.answerText}">
													<img src="${tool}/includes/images/tick_red.gif" title="<fmt:message key="message.learner.choose.answer"/>">
												</c:if>
											</c:if>
										</td>
									</c:if>
								</tr>
				<c:if test="${status.first}">
							</table>
						</td>
					</tr>
				<%--  End first check  --%>
				</c:if>
				</c:forEach>
			</table>
		</div>
		<div id="footer-learner"></div>
		</div>
	</div>
</body>
</html>
