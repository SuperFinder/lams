<%--
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

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
<%@ taglib uri="tags-logic-el" prefix="logic-el" %>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="fck-editor" prefix="FCK" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>

		<html:form  action="/learning?method=displayMc&validate=false" method="POST" target="_self">
				<table align=center bgcolor="#FFFFFF">
					  <tr>
					  	<td NOWRAP align=left class="input" valign=top bgColor="#333366" colspan=2> 
						  	<font size=2 color="#FFFFFF"> <b>  <bean:message key="label.assessment"/> </b> 
						  	<c:out value="${reportTitleLearner}" escapeXml="false" /></font>
					  	</td>
					  </tr>
				
			 		<c:if test="${sessionScope.isRetries == 'true'}"> 		
						  <tr>
						  	<td NOWRAP align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withRetries"/> </b> </font>
						  	</td>
						  </tr>
  					</c:if> 			

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
						  <tr>
						  	<td NOWRAP align=center class="input" valign=top colspan=2> 
							  	<font size=3> <b>  <bean:message key="label.individual.results.withoutRetries"/> </b> </font>
						  	</td>
						  </tr>
					</c:if> 			


					  <tr>
					  	<td NOWRAP align=right class="input" valign=top colspan=2> 
						  	<font size=2 color="#000000"> <b>  <bean:message key="label.mark"/>  
						  	<c:out value="${sessionScope.learnerMark}"/>  &nbsp
							<bean:message key="label.outof"/> &nbsp
						  	<c:out value="${sessionScope.totalQuestionCount}"/>
						  	</b> </font>
					  	</td>
					  </tr>	

					<tr>
						<td NOWRAP align=right class="input" valign=top colspan=2> 
							<hr>
						</td> 
					</tr>
					
					<c:if test="${sessionScope.userPassed != 'true'}">
						 <tr>
						  	<td NOWRAP align=left class="input" valign=top colspan=2> 
							  	<font size=2 color="#FF0000"> <b>  <bean:message key="label.mustGet"/> &nbsp
							  	<c:out value="${sessionScope.learnerMarkAtLeast}"/>  &nbsp
								<bean:message key="label.outof"/> &nbsp
							  	<c:out value="${sessionScope.totalQuestionCount}"/>
								<bean:message key="label.toFinish"/>						  	
							  	</b> </font>
						  	</td>
						  </tr>	
  					</c:if> 			
					
					
  		  	 		<c:set var="mainQueIndex" scope="session" value="0"/>
					<c:forEach var="questionEntry" items="${sessionScope.mapQuestionContentLearner}">
					<c:set var="mainQueIndex" scope="session" value="${mainQueIndex +1}"/>
						  <tr>
						  	<td NOWRAP align=left class="input" valign=top bgColor="#999966" colspan=2> 
							  	<font color="#FFFFFF"> 
								  	<font size=2>
								  		<c:out value="${questionEntry.value}"/> 
								  	</font>
							  	</font> 
						  	</td>
						  </tr>

								  								  
						  <tr>						 
							<td NOWRAP align=left>
							<table align=left>
			  		  	 		<c:set var="queIndex" scope="session" value="0"/>
								<c:forEach var="mainEntry" items="${sessionScope.mapGeneralOptionsContent}">
									<c:set var="queIndex" scope="session" value="${queIndex +1}"/>
										<c:if test="${sessionScope.mainQueIndex == sessionScope.queIndex}"> 		
									  		<c:forEach var="subEntry" items="${mainEntry.value}">
				  								<tr> 
													<td NOWRAP align=left class="input" valign=top> 
					   								    <img src="<c:out value="${tool}"/>images/dot.jpg" align=left> &nbsp
														<font size=2 color="#669966">	<c:out value="${subEntry.value}"/> </font>					   								    
													</td> 
												</tr>	
											</c:forEach>

												<tr>												
												<td NOWRAP colspan=2 align=left class="input" valign=top> 
													<font size=2>
					   								    <bean:message key="label.you.answered"/>
				   								    </font>
												</td> 
												</tr>
												
												<tr>
													<td NOWRAP align=left class="input" valign=top> 											
														<table align=left>
													  		<c:forEach var="subEntry" items="${mainEntry.value}">
						  											<c:forEach var="selectedMainEntry" items="${sessionScope.mapGeneralCheckedOptionsContent}">
																				<c:if test="${selectedMainEntry.key == sessionScope.queIndex}"> 		
																			  		<c:forEach var="selectedSubEntry" items="${selectedMainEntry.value}">
																						<c:if test="${subEntry.key == selectedSubEntry.key}"> 		
																								<tr> 
																									<td NOWRAP align=left class="input" valign=top> 
																										<font size=2>
																											<b> <c:out value="${subEntry.value}"/> </b>
																										</font>
																									</td> 
																								</tr>
										  												</c:if> 			
																					</c:forEach>																						
							  													</c:if> 			
																	</c:forEach>		
																</tr>																			
												  			</c:forEach>											
														</table>
													</td>

													<td NOWRAP align=right class="input" valign=top> 											
														<font size=2>
															<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
																	<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																		<c:if test="${mainEntry.value == 'true'}"> 		
																			<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																				<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					    <img src="<c:out value="${tool}"/>images/tick.gif" align=right width=20 height=20>
																				</c:if> 																																				
																			</c:forEach>											
																		</c:if> 														
																		
																		<c:if test="${mainEntry.value == 'false'}"> 		
																			<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																				<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																					    <img src="<c:out value="${tool}"/>images/cross.gif" align=right width=20 height=20>
																				</c:if> 																																				
																			</c:forEach>											
																		</c:if> 														
																	</c:if> 																		
															</c:forEach>		
														</font>
													</td>
												</tr>

												<tr>
												<td NOWRAP bgcolor="#CCCC99" colspan=2 align=left class="input" valign=top> 											
													<font size=2>
														<c:forEach var="mainEntry" items="${sessionScope.mapLearnerAssessmentResults}">
																<c:if test="${mainEntry.key == sessionScope.queIndex}"> 		
																	<c:if test="${mainEntry.value == 'true'}"> 		
																		<c:forEach var="feedbackEntry" items="${sessionScope.mapLeanerFeedbackCorrect}">
																			<c:if test="${feedbackEntry.key == sessionScope.queIndex}"> 		
																				<c:out value="${feedbackEntry.value}" escapeXml="false" />
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																	
																	<c:if test="${mainEntry.value != 'true'}"> 		
																		<c:forEach var="feedbackEntryIncorrect" items="${sessionScope.mapLeanerFeedbackIncorrect}">
																			<c:if test="${feedbackEntryIncorrect.key == sessionScope.queIndex}"> 		
																				<c:out value="${feedbackEntryIncorrect.value}" escapeXml="false" />
																			</c:if> 																																				
																		</c:forEach>											
																	</c:if> 														
																</c:if> 																		
														</c:forEach>											
													</font>
												</td>
												</tr>
											
										</c:if> 			
								</c:forEach>
							</table>
							</td>
						</tr>
					</c:forEach>

			  	   	<tr> 
				 		<td NOWRAP colspan=2 class="input" valign=top> 
				 		&nbsp
				 		</td>
			  	   </tr>

			  	   

			 		<c:if test="${sessionScope.isRetries == 'true'}"> 					  	   
		  	   		  <tr>
					  	<td NOWRAP colspan=2 align=center class="input" valign=top> 
						  	<font size=2>
					  			<html:submit property="redoQuestions" styleClass="button">
									<bean:message key="label.redo.questions"/>
								</html:submit>	 		
			       
								<c:if test="${sessionScope.userPassed == 'true'}">
							  	   <html:submit property="learnerFinished" styleClass="button">
										<bean:message key="label.finished"/>
								   </html:submit>
						  	   </c:if>
	
		   						<html:submit property="viewSummary" styleClass="button">
									<bean:message key="label.view.summary"/>
								</html:submit>	 				 		  					
							</font>
					  	 </td>
					  </tr>
					</c:if> 																		

					<c:if test="${sessionScope.isRetries != 'true'}"> 							  
		  	   		  <tr>
		  	   		    <td NOWRAP colspan=2 align=right class="input" valign=top>
			  	   		    <font size=2>
				  	   		  	<c:if test="${sessionScope.userPassed == 'true'}">
							  	   <html:submit property="learnerFinished" styleClass="button">
												<bean:message key="label.finished"/>
								   </html:submit>
					  	   		</c:if>
	
		   						<html:submit property="viewSummary" styleClass="button">
									<bean:message key="label.view.summary"/>
								</html:submit>	 				 		  					
							</font>
					  	 </td>
					  </tr>
					</c:if> 																		
					
				</table>
	</html:form>

