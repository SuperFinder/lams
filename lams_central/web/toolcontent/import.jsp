<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf8">
		<title><fmt:message key="title.import" /></title>
		<!-- ********************  CSS ********************** -->
		<lams:css />
		<script type="text/javascript">
			function closeWin(){
				window.close();
			}
		</script>
	</head>

	<BODY>
		<h1>
			<fmt:message key="title.import" />
		</h1>
		<h2>
			<fmt:message key="title.import.instruction" />
		</h2>

		<form action="<c:url value="/authoring/importToolContent.do"/>" method="post" enctype="multipart/form-data">
			<fmt:message key="label.ld.zip.file" />
			:
			<input type="file" name="UPLOAD_FILE" />
			<input type="submit" name="import" value="<fmt:message key="button.import" />" class="buttonStyle"/>
		</form>

	</BODY>
</HTML>
