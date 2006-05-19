<%@ include file="/common/taglibs.jsp" %>
	<table class="forms">
		<!--hidden field contentID passed by flash-->
		<tr>
		</tr>
		<!-- Instructions Row -->
		<tr>
			<td class="formlabel"><fmt:message
				key="label.authoring.online.instruction" />:</td>
			<td class="formcontrol">
				<c:out value="${authoring.onlineInstruction}" escapeXml="false"/>
			</td>
		</tr>
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.online.filelist"/>:</td>
			<td class="formcontrol">
				<c:forEach var="file" items="${authoring.onlineFiles}">
					<li><c:out value="${file.name}"/>
					<html:link href="javascript:launchInstructionsPopup('download/?uuid=${file.uuID}&preferDownload=false')">
						<fmt:message key="label.view"/>
					</html:link>
					<html:link href="../download/?uuid=${file.uuID}&preferDownload=true">
						<fmt:message key="label.download"/>
					</html:link>
					</li>
				</c:forEach>
			</td>
		</tr>
		<tr><td colspan="2"><hr></td></tr>
		<tr>
			<td class="formlabel">
			<fmt:message
				key="label.authoring.offline.instruction" />:</td>
			<td class="formcontrol">
				<c:out value="${authoring.offlineInstruction}" escapeXml="false"/>
			</td>
		</tr>
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.offline.filelist"/>:</td>
			<td class="formcontrol">
				<c:forEach var="file" items="${authoring.offlineFiles}">
					<li><c:out value="${file.name}"/></li>
					<html:link href="javascript:launchInstructionsPopup('download/?uuid=${file.uuID}&preferDownload=false')">
						<fmt:message key="label.view"/>
					</html:link>
					<html:link href="../download/?uuid=${file.uuID}&preferDownload=true">
						<fmt:message key="label.download"/>
					</html:link>
				</c:forEach>
			</td>
		</tr>
		</tr>
	</table>
