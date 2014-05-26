/***************************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.vote.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.lamsfoundation.lams.authoring.web.AuthoringConstants;
import org.lamsfoundation.lams.learningdesign.DataFlowObject;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.tool.vote.VoteAppConstants;
import org.lamsfoundation.lams.tool.vote.dto.VoteGeneralAuthoringDTO;
import org.lamsfoundation.lams.tool.vote.dto.VoteQuestionDTO;
import org.lamsfoundation.lams.tool.vote.pojos.VoteContent;
import org.lamsfoundation.lams.tool.vote.pojos.VoteQueContent;
import org.lamsfoundation.lams.tool.vote.service.IVoteService;
import org.lamsfoundation.lams.tool.vote.service.VoteServiceProxy;
import org.lamsfoundation.lams.tool.vote.util.VoteUtils;
import org.lamsfoundation.lams.tool.vote.web.form.VoteAuthoringForm;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;

/**
 * @author Ozgur Demirtas
 */
public class VoteAction extends LamsDispatchAction implements VoteAppConstants {
    private static Logger logger = Logger.getLogger(VoteAction.class.getName());

    /**
     * 
     * main content/question content management and workflow logic
     * 
     * if the passed toolContentID exists in the db, we need to get the relevant data into the Map if not, create the
     * default Map
     */
    @Override
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteUtils.cleanUpUserExceptions(request);
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;
	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);
	
	String richTextTitle = request.getParameter(TITLE);
	String richTextInstructions = request.getParameter(INSTRUCTIONS);

	if (richTextTitle != null) {
	    voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	}

	if (richTextInstructions != null) {
	    voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);
	}
	
	voteAuthoringForm.resetUserAction();
	return null;
    }

    /**
     * repopulateRequestParameters reads and saves request parameters
     * 
     * @param request
     * @param voteAuthoringForm
     * @param voteGeneralAuthoringDTO
     */
    protected void repopulateRequestParameters(HttpServletRequest request, VoteAuthoringForm voteAuthoringForm,
	    VoteGeneralAuthoringDTO voteGeneralAuthoringDTO) {

	String toolContentID = request.getParameter(VoteAppConstants.TOOL_CONTENT_ID);
	voteAuthoringForm.setToolContentID(toolContentID);
	voteGeneralAuthoringDTO.setToolContentID(toolContentID);

	String httpSessionID = request.getParameter(VoteAppConstants.HTTP_SESSION_ID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);

	String lockOnFinish = request.getParameter(VoteAppConstants.LOCK_ON_FINISH);
	voteAuthoringForm.setLockOnFinish(lockOnFinish);
	voteGeneralAuthoringDTO.setLockOnFinish(lockOnFinish);
	
	String useSelectLeaderToolOuput = request.getParameter(VoteAppConstants.USE_SELECT_LEADER_TOOL_OUTPUT);
	voteAuthoringForm.setUseSelectLeaderToolOuput(useSelectLeaderToolOuput);
	voteGeneralAuthoringDTO.setUseSelectLeaderToolOuput(useSelectLeaderToolOuput);

	String allowText = request.getParameter(VoteAppConstants.ALLOW_TEXT);
	voteAuthoringForm.setAllowText(allowText);
	voteGeneralAuthoringDTO.setAllowText(allowText);

	String showResults = request.getParameter(VoteAppConstants.SHOW_RESULTS);
	voteAuthoringForm.setShowResults(showResults);
	voteGeneralAuthoringDTO.setShowResults(showResults);

	String maxNominationCount = request.getParameter(VoteAppConstants.MAX_NOMINATION_COUNT);
	voteAuthoringForm.setMaxNominationCount(maxNominationCount);
	voteGeneralAuthoringDTO.setMaxNominationCount(maxNominationCount);

        String minNominationCount=request.getParameter(MIN_NOMINATION_COUNT);
        voteAuthoringForm.setMinNominationCount(minNominationCount);
        voteGeneralAuthoringDTO.setMinNominationCount(minNominationCount);

	String reflect = request.getParameter("reflect");
	voteAuthoringForm.setReflect(reflect);
	voteGeneralAuthoringDTO.setReflect(reflect);

	String reflectionSubject = request.getParameter("reflectionSubject");
	voteAuthoringForm.setReflectionSubject(reflectionSubject);
	voteGeneralAuthoringDTO.setReflectionSubject(reflectionSubject);

	String maxInputs = request.getParameter(VoteAppConstants.MAX_INPUTS);
	if (maxInputs == null) {
	    maxInputs = "0";
	}
	voteAuthoringForm.setMaxInputs(new Short(maxInputs));
	
	ToolAccessMode mode = getAccessMode(request);
	request.setAttribute(AttributeNames.ATTR_MODE, mode.toString());
    }

    /**
     * moves a nomination down in the authoring list
     */
    public ActionForward moveNominationDown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	questionDTOs = AuthoringUtil.swapQuestions(questionDTOs, questionIndex, "down");
	
	questionDTOs = AuthoringUtil.reorderQuestionDTOs(questionDTOs);
	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);

	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	sessionMap.put(VoteAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);

	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }

    /**
     * moves a nomination up in the authoring list
     */
    public ActionForward moveNominationUp(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	questionDTOs = AuthoringUtil.swapQuestions(questionDTOs, questionIndex, "up");

	questionDTOs = AuthoringUtil.reorderQuestionDTOs(questionDTOs);

	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);

	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	sessionMap.put(VoteAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);

	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);
	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }

    /**
     * removes a nomination from the authoring list
     */
    public ActionForward removeNomination(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String questionIndexToDelete = request.getParameter("questionIndex");
	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	List<VoteQuestionDTO> listFinalQuestionDTO = new LinkedList<VoteQuestionDTO>();
	int queIndex = 0;
	for (VoteQuestionDTO questionDTO : questionDTOs) {

	    String questionText = questionDTO.getNomination();
	    String displayOrder = questionDTO.getDisplayOrder();

	    if (questionText != null && !questionText.equals("") && (!displayOrder.equals(questionIndexToDelete))) {
		
		++queIndex;
		questionDTO.setDisplayOrder(new Integer(queIndex).toString());
		listFinalQuestionDTO.add(questionDTO);
	    }
	    if ((questionText != null) && (!questionText.isEmpty()) && displayOrder.equals(questionIndexToDelete)) {
		List<VoteQuestionDTO> deletedQuestionDTOs = (List<VoteQuestionDTO>) sessionMap.get(LIST_DELETED_QUESTION_DTOS);;
		deletedQuestionDTOs.add(questionDTO);
		sessionMap.put(LIST_DELETED_QUESTION_DTOS, deletedQuestionDTOs);
	    }
	}

	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, listFinalQuestionDTO);
	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, listFinalQuestionDTO);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);
	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	sessionMap.put(VoteAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);

	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }

    /**
     * enables editing a nomination
     */
    public ActionForward newEditableNominationBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();

	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String questionIndex = request.getParameter("questionIndex");

	voteAuthoringForm.setEditableNominationIndex(questionIndex);

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	String editableNomination = "";
	Iterator iter = questionDTOs.iterator();
	while (iter.hasNext()) {
	    VoteQuestionDTO voteQuestionDTO = (VoteQuestionDTO) iter.next();
	    String question = voteQuestionDTO.getNomination();
	    String displayOrder = voteQuestionDTO.getDisplayOrder();

	    if (displayOrder != null && !displayOrder.equals("")) {
		if (displayOrder.equals(questionIndex)) {
		    editableNomination = voteQuestionDTO.getNomination();
		    break;
		}

	    }
	}
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);
	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);
	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	voteGeneralAuthoringDTO.setEditableNominationText(editableNomination);

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	return mapping.findForward("editNominationBox");
    }

    /**
     * enables adding a new nomination
     */
    public ActionForward newNominationBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();

	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);
	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);

	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	return mapping.findForward("newNominationBox");
    }

    /**
     * enables adding a new nomination to the authoring nominations list
     */
    public ActionForward addSingleNomination(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {
	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	IVoteService voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());

	String httpSessionID = voteAuthoringForm.getHttpSessionID();

	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);
	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	String newNomination = request.getParameter("newNomination");

	int listSize = questionDTOs.size();

	if (newNomination != null && newNomination.length() > 0) {
	    boolean duplicates = AuthoringUtil.checkDuplicateNominations(questionDTOs, newNomination);

	    if (!duplicates) {
		VoteQuestionDTO voteQuestionDTO = new VoteQuestionDTO();
		voteQuestionDTO.setDisplayOrder(new Long(listSize + 1).toString());
		voteQuestionDTO.setNomination(newNomination);

		questionDTOs.add(voteQuestionDTO);
	    }
	}
	
	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);
	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	String richTextTitle = (String) sessionMap.get(VoteAppConstants.ACTIVITY_TITLE_KEY);
	String richTextInstructions = (String) sessionMap.get(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);
	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);

	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }

    /**
     * saves a new or updated nomination in the authoring nominations list
     */
    public ActionForward saveSingleNomination(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	String httpSessionID = voteAuthoringForm.getHttpSessionID();

	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	String editNominationBoxRequest = request.getParameter("editNominationBoxRequest");

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();

	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	String newNomination = request.getParameter("newNomination");

	String editableNominationIndex = request.getParameter("editableNominationIndex");

	if (newNomination != null && newNomination.length() > 0) {
	    if (editNominationBoxRequest != null && editNominationBoxRequest.equals("false")) {
		boolean duplicates = AuthoringUtil.checkDuplicateNominations(questionDTOs, newNomination);

		if (!duplicates) {
		    VoteQuestionDTO voteQuestionDTO = null;
		    Iterator iter = questionDTOs.iterator();
		    while (iter.hasNext()) {
			voteQuestionDTO = (VoteQuestionDTO) iter.next();

			String question = voteQuestionDTO.getNomination();
			String displayOrder = voteQuestionDTO.getDisplayOrder();

			if (displayOrder != null && !displayOrder.equals("")) {
			    if (displayOrder.equals(editableNominationIndex)) {
				break;
			    }

			}
		    }

		    voteQuestionDTO.setQuestion(newNomination);
		    voteQuestionDTO.setDisplayOrder(editableNominationIndex);

		    questionDTOs = AuthoringUtil.reorderUpdateListQuestionDTO(
			    questionDTOs, voteQuestionDTO, editableNominationIndex);
		} else {
		    //duplicate question entry, not adding
		}
	    } else {
		//request for edit and save
		VoteQuestionDTO voteQuestionDTO = null;
		Iterator iter = questionDTOs.iterator();
		while (iter.hasNext()) {
		    voteQuestionDTO = (VoteQuestionDTO) iter.next();

		    String question = voteQuestionDTO.getNomination();
		    String displayOrder = voteQuestionDTO.getDisplayOrder();

		    if (displayOrder != null && !displayOrder.equals("")) {
			if (displayOrder.equals(editableNominationIndex)) {
			    break;
			}

		    }
		}

		voteQuestionDTO.setNomination(newNomination);
		voteQuestionDTO.setDisplayOrder(editableNominationIndex);

		questionDTOs = AuthoringUtil.reorderUpdateListQuestionDTO(
			questionDTOs, voteQuestionDTO, editableNominationIndex);
	    }
	} else {
	    //entry blank, not adding
	}

	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);
	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);

	String richTextTitle = (String) sessionMap.get(VoteAppConstants.ACTIVITY_TITLE_KEY);
	String richTextInstructions = (String) sessionMap.get(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);
	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);

	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	request.getSession().setAttribute(httpSessionID, sessionMap);

	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }

    /**
     * persists the nominations list and other user selections in the db.
     */
    public ActionForward submitAllContent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException {

	VoteAuthoringForm voteAuthoringForm = (VoteAuthoringForm) form;

	IVoteService voteService = VoteServiceProxy.getVoteService(getServlet().getServletContext());

	String httpSessionID = voteAuthoringForm.getHttpSessionID();

	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession().getAttribute(httpSessionID);

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	voteAuthoringForm.setContentFolderID(contentFolderID);

	String strToolContentID = request.getParameter(AttributeNames.PARAM_TOOL_CONTENT_ID);

	List<VoteQuestionDTO> questionDTOs = (List<VoteQuestionDTO>) sessionMap.get(VoteAppConstants.LIST_QUESTION_DTO);

	ActionMessages errors = new ActionMessages();
	if (questionDTOs.isEmpty()
		&& (voteAuthoringForm.getAssignedDataFlowObject() == null || voteAuthoringForm
			.getAssignedDataFlowObject() == 0)) {
	    ActionMessage error = new ActionMessage("nominations.none.submitted");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	String maxNomCount = voteAuthoringForm.getMaxNominationCount();
	if (maxNomCount != null) {
	    if (maxNomCount.equals("0") || maxNomCount.contains("-")) {
		ActionMessage error = new ActionMessage("maxNomination.invalid");
		errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	    }

	    try {
		int intMaxNomCount = new Integer(maxNomCount).intValue();
	    } catch (NumberFormatException e) {
		ActionMessage error = new ActionMessage("maxNomination.invalid");
		errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	    }
	}

	//verifyDuplicateNominations
	Map mapQuestion = AuthoringUtil.extractMapQuestion(questionDTOs);
	int optionCount = 0;
	boolean isNominationsDuplicate = false;
	for (long i = 1; i <= VoteAppConstants.MAX_OPTION_COUNT; i++) {
	    String currentOption = (String) mapQuestion.get(new Long(i).toString());

	    optionCount = 0;
	    for (long j = 1; j <= VoteAppConstants.MAX_OPTION_COUNT; j++) {
		String backedOption = (String) mapQuestion.get(new Long(j).toString());

		if (currentOption != null && backedOption != null) {
		    if (currentOption.equals(backedOption)) {
			optionCount++;
		    }

		    if (optionCount > 1) {
			isNominationsDuplicate = true;
		    }
		}
	    }
	}

	if (isNominationsDuplicate == true) {
	    ActionMessage error = new ActionMessage("nominations.duplicate");
	    errors.add(ActionMessages.GLOBAL_MESSAGE, error);
	}

	VoteGeneralAuthoringDTO voteGeneralAuthoringDTO = new VoteGeneralAuthoringDTO();
	repopulateRequestParameters(request, voteAuthoringForm, voteGeneralAuthoringDTO);

	DataFlowObject assignedDataFlowObject = null;

	List<DataFlowObject> dataFlowObjects = voteService.getDataFlowObjects(new Long(strToolContentID));
	List<String> dataFlowObjectNames = null;
	if (dataFlowObjects != null) {
	    dataFlowObjectNames = new ArrayList<String>(dataFlowObjects.size());
	    int objectIndex = 1;
	    for (DataFlowObject dataFlowObject : dataFlowObjects) {
		dataFlowObjectNames.add(dataFlowObject.getDisplayName());
		if (VoteAppConstants.DATA_FLOW_OBJECT_ASSIGMENT_ID.equals(dataFlowObject.getToolAssigmentId())) {
		    voteAuthoringForm.setAssignedDataFlowObject(objectIndex);
		}
		objectIndex++;

	    }
	}
	voteGeneralAuthoringDTO.setDataFlowObjectNames(dataFlowObjectNames);

	if (voteAuthoringForm.getAssignedDataFlowObject() != null && voteAuthoringForm.getAssignedDataFlowObject() != 0) {
	    assignedDataFlowObject = dataFlowObjects.get(voteAuthoringForm.getAssignedDataFlowObject() - 1);
	}

	voteGeneralAuthoringDTO.setContentFolderID(contentFolderID);

	String richTextTitle = request.getParameter(VoteAppConstants.TITLE);
	String richTextInstructions = request.getParameter(VoteAppConstants.INSTRUCTIONS);

	voteGeneralAuthoringDTO.setActivityTitle(richTextTitle);
	voteAuthoringForm.setTitle(richTextTitle);

	voteGeneralAuthoringDTO.setActivityInstructions(richTextInstructions);

	sessionMap.put(VoteAppConstants.ACTIVITY_TITLE_KEY, richTextTitle);
	sessionMap.put(VoteAppConstants.ACTIVITY_INSTRUCTIONS_KEY, richTextInstructions);

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	VoteContent voteContentTest = voteService.getVoteContent(new Long(strToolContentID));
	if (!errors.isEmpty()) {
	    saveErrors(request, errors);
	    VoteAction.logger.error("errors saved: " + errors);
	}

	if (errors.isEmpty()) {
	    ToolAccessMode mode = getAccessMode(request);
	    request.setAttribute(AttributeNames.ATTR_MODE, mode.toString());

	    List<VoteQuestionDTO> deletedQuestionDTOs = (List<VoteQuestionDTO>) sessionMap.get(LIST_DELETED_QUESTION_DTOS);

	    // in case request is from monitoring module - recalculate User Answers
	    if (mode.isTeacher()) {
		Set<VoteQueContent> oldQuestions = voteContentTest.getVoteQueContents();
		voteService.removeQuestionsFromCache(voteContentTest);
		VoteUtils.setDefineLater(request, false, strToolContentID, voteService);
		    
		// recalculate User Answers
		voteService.recalculateUserAnswers(voteContentTest, oldQuestions, questionDTOs, deletedQuestionDTOs);
	    }
	    
	    // remove deleted questions
	    for (VoteQuestionDTO deletedQuestionDTO : deletedQuestionDTOs) {
		VoteQueContent removeableQuestion = voteService.getVoteQueContentByUID(deletedQuestionDTO.getUid());
		if (removeableQuestion != null) {
//		    Set<McUsrAttempt> attempts = removeableQuestion.getMcUsrAttempts();
//		    Iterator<McUsrAttempt> iter = attempts.iterator();
//		    while (iter.hasNext()) {
//			McUsrAttempt attempt = iter.next();
//			iter.remove();
//		    }
//		    mcService.updateQuestion(removeableQuestion);
		    voteContentTest.getVoteQueContents().remove(removeableQuestion);
		    voteService.removeVoteQueContent(removeableQuestion);
		}
	    }

	    // store content
	    VoteContent voteContent = AuthoringUtil.saveOrUpdateVoteContent(voteService, voteAuthoringForm, request,
		    voteContentTest, strToolContentID);

	    //store questions
	    voteContent = AuthoringUtil.createQuestions(questionDTOs, voteService, voteContent);

	    //store DataFlowObjectAssigment
	    voteService.saveDataFlowObjectAssigment(assignedDataFlowObject);

	    //reOrganizeDisplayOrder
	    List<VoteQueContent> sortedQuestions = voteService.getAllQuestionsSorted(voteContent.getUid().longValue());
	    Iterator iter = sortedQuestions.iterator();
	    while (iter.hasNext()) {
		VoteQueContent question = (VoteQueContent) iter.next();

		VoteQueContent existingQuestion = voteService.getQuestionByUid(question.getUid());
		voteService.saveOrUpdateVoteQueContent(existingQuestion);
	    }

	    // standard authoring close
	    request.setAttribute(AuthoringConstants.LAMS_AUTHORING_SUCCESS_FLAG, Boolean.TRUE);

	}

	voteAuthoringForm.resetUserAction();

	request.setAttribute(VoteAppConstants.VOTE_GENERAL_AUTHORING_DTO, voteGeneralAuthoringDTO);

	request.setAttribute(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);
	sessionMap.put(VoteAppConstants.LIST_QUESTION_DTO, questionDTOs);
	request.getSession().setAttribute(httpSessionID, sessionMap);

	voteGeneralAuthoringDTO.setToolContentID(strToolContentID);
	voteGeneralAuthoringDTO.setHttpSessionID(httpSessionID);

	voteAuthoringForm.setToolContentID(strToolContentID);
	voteAuthoringForm.setHttpSessionID(httpSessionID);
	voteAuthoringForm.setCurrentTab("1");

	return mapping.findForward(VoteAppConstants.LOAD_QUESTIONS);
    }
    
    /**
     * Get ToolAccessMode from HttpRequest parameters. Default value is AUTHOR mode.
     * 
     * @param request
     * @return
     */
    private ToolAccessMode getAccessMode(HttpServletRequest request) {
	ToolAccessMode mode;
	String modeStr = request.getParameter(AttributeNames.ATTR_MODE);
	if (StringUtils.equalsIgnoreCase(modeStr, ToolAccessMode.TEACHER.toString())) {
	    mode = ToolAccessMode.TEACHER;
	} else {
	    mode = ToolAccessMode.AUTHOR;
	}
	return mode;
    }

}
