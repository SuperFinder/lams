<%@ include file="/common/taglibs.jsp" %>
<script lang="javascript">
<!-- Common Javascript functions for LAMS -->

	/**
	 * Launches the popup window for the instruction files
	 */
	function showMessage(url) {
		var area=document.getElementById("reourceInputArea");
		area.style.width="100%";
		area.style.height="100%";
		area.src=url;
		area.style.display="block";
	}
	function hideMessage(){
		var area=document.getElementById("reourceInputArea");
		area.style.width="0px";
		area.style.height="0px";
		area.style.display="none";
	}
	function launchPopup(url,title) {
		var wd = null;
		if(wd && wd.open && !wd.closed){
			wd.close();
		}
		wd = window.open(url,title,'resizable,width=796,height=570,scrollbars');
		wd.window.focus();
	}
	function verifyUrl(myUrl, title){
		launchPopup(myUrl,title);
	}
	function previewItem(idx){
	}
	function editItem(idx){
		var url = "<c:url value="/authoring/editItem.do?itemIndex="/>" + idx;
		
	}
	function deleteItem(idx){
		var url = "<c:url value="/authoring/deleteItem.do?itemUid="/>" + idx;
		
	}
</script>
	<!---------------------------Basic Tab Content ------------------------>
	<table class="forms">
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.basic.title" />:</td>
			<td NOWRAP width="700"><lams:SetEditor id="Title" text="${authoring.title}" small="true"/></td>
		</tr>
		<tr>
			<td class="formlabel"><fmt:message key="label.authoring.basic.instruction" />:</td>
			<td NOWRAP width="700">
				<lams:SetEditor id="Instructions" text="${authoring.instruction}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div id="resourceListArea">
						<%@ include file="/pages/authoring/parts/itemlist.jsp"%>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="forms">
					<tr>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/authoring/addUrlInit.do"/>');">
								<fmt:message key="label.authoring.basic.add.url" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/pages/authoring/parts/addfile.jsp"/>');">
								<fmt:message key="label.authoring.basic.add.file" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/pages/authoring/parts/addwebsite.jsp"/>');">
								<fmt:message key="label.authoring.basic.add.website" />
							</a>
						</td>
						<td>
							<a href="javascript:showMessage('<html:rewrite page="/pages/authoring/parts/addlearningobject.jsp"/>');">
								<fmt:message key="label.authoring.basic.add.learning.object"/>
							</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td  colspan="2">
				<iframe onload="javascript:this.style.height=this.contentWindow.document.body.scrollHeight+'px'" 
					id="reourceInputArea" name="reourceInputArea" style="width:0px;height:0px;border:0px;display:none" frameborder="no" scrolling="no">
				</iframe>
			</td>
		</tr>
		<tr><td colspan="2">
			<%@ include file="/common/messages.jsp" %>
		</td></tr>
	</table>
