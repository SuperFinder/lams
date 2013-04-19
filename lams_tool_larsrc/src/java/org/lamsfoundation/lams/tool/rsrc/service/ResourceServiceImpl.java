/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.tool.rsrc.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.contentrepository.InvalidParameterException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;
import org.lamsfoundation.lams.contentrepository.service.IRepositoryService;
import org.lamsfoundation.lams.contentrepository.service.SimpleCredentials;
import org.lamsfoundation.lams.events.IEventNotificationService;
import org.lamsfoundation.lams.learning.service.ILearnerService;
import org.lamsfoundation.lams.learningdesign.service.ExportToolContentException;
import org.lamsfoundation.lams.learningdesign.service.IExportToolContentService;
import org.lamsfoundation.lams.learningdesign.service.ImportToolContentException;
import org.lamsfoundation.lams.lesson.service.ILessonService;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.notebook.service.ICoreNotebookService;
import org.lamsfoundation.lams.tool.ToolContentImport102Manager;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.ToolSessionExportOutputData;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.SessionDataExistsException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.rsrc.ResourceConstants;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceAttachmentDAO;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceDAO;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceItemDAO;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceItemVisitDAO;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceSessionDAO;
import org.lamsfoundation.lams.tool.rsrc.dao.ResourceUserDAO;
import org.lamsfoundation.lams.tool.rsrc.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.rsrc.dto.Summary;
import org.lamsfoundation.lams.tool.rsrc.ims.IContentPackageConverter;
import org.lamsfoundation.lams.tool.rsrc.ims.IMSManifestException;
import org.lamsfoundation.lams.tool.rsrc.ims.ImscpApplicationException;
import org.lamsfoundation.lams.tool.rsrc.ims.SimpleContentPackageConverter;
import org.lamsfoundation.lams.tool.rsrc.model.Resource;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceAttachment;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceItem;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceItemInstruction;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceItemVisitLog;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceSession;
import org.lamsfoundation.lams.tool.rsrc.model.ResourceUser;
import org.lamsfoundation.lams.tool.rsrc.util.ReflectDTOComparator;
import org.lamsfoundation.lams.tool.rsrc.util.ResourceToolContentHandler;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.util.audit.IAuditService;
import org.lamsfoundation.lams.util.wddx.WDDXProcessor;
import org.lamsfoundation.lams.util.wddx.WDDXProcessorConversionException;
import org.lamsfoundation.lams.util.zipfile.ZipFileUtil;
import org.lamsfoundation.lams.util.zipfile.ZipFileUtilException;

/**
 * 
 * @author Dapeng.Ni
 * 
 */
public class ResourceServiceImpl implements IResourceService, ToolContentManager, ToolSessionManager,
	ToolContentImport102Manager

{
    static Logger log = Logger.getLogger(ResourceServiceImpl.class.getName());

    private ResourceDAO resourceDao;

    private ResourceItemDAO resourceItemDao;

    private ResourceAttachmentDAO resourceAttachmentDao;

    private ResourceUserDAO resourceUserDao;

    private ResourceSessionDAO resourceSessionDao;

    private ResourceItemVisitDAO resourceItemVisitDao;

    // tool service
    private ResourceToolContentHandler resourceToolContentHandler;

    private MessageService messageService;

    // system services
    private IRepositoryService repositoryService;

    private ILamsToolService toolService;

    private ILearnerService learnerService;

    private IAuditService auditService;

    private IUserManagementService userManagementService;

    private IExportToolContentService exportContentService;

    private ICoreNotebookService coreNotebookService;

    private IEventNotificationService eventNotificationService;

    private ILessonService lessonService;

    private ResourceOutputFactory resourceOutputFactory;

    public IVersionedNode getFileNode(Long itemUid, String relPathString) throws ResourceApplicationException {
	ResourceItem item = (ResourceItem) resourceItemDao.getObject(ResourceItem.class, itemUid);
	if (item == null) {
	    throw new ResourceApplicationException("Reource item " + itemUid + " not found.");
	}

	return getFile(item.getFileUuid(), item.getFileVersionId(), relPathString);
    }

    // *******************************************************************************
    // Service method
    // *******************************************************************************
    /**
     * Try to get the file. If forceLogin = false and an access denied exception occurs, call this method again to get a
     * new ticket and retry file lookup. If forceLogin = true and it then fails then throw exception.
     * 
     * @param uuid
     * @param versionId
     * @param relativePath
     * @param attemptCount
     * @return file node
     * @throws ImscpApplicationException
     */
    private IVersionedNode getFile(Long uuid, Long versionId, String relativePath) throws ResourceApplicationException {

	ITicket tic = getRepositoryLoginTicket();

	try {

	    return repositoryService.getFileItem(tic, uuid, versionId, relativePath);

	} catch (AccessDeniedException e) {

	    String error = "Unable to access repository to get file uuid " + uuid + " version id " + versionId
		    + " path " + relativePath + ".";

	    error = error + "AccessDeniedException: " + e.getMessage() + " Unable to retry further.";
	    ResourceServiceImpl.log.error(error);
	    throw new ResourceApplicationException(error, e);

	} catch (Exception e) {

	    String error = "Unable to access repository to get file uuid " + uuid + " version id " + versionId
		    + " path " + relativePath + "." + " Exception: " + e.getMessage();
	    ResourceServiceImpl.log.error(error);
	    throw new ResourceApplicationException(error, e);

	}
    }

    /**
     * This method verifies the credentials of the Share Resource Tool and gives it the <code>Ticket</code> to login
     * and access the Content Repository.
     * 
     * A valid ticket is needed in order to access the content from the repository. This method would be called evertime
     * the tool needs to upload/download files from the content repository.
     * 
     * @return ITicket The ticket for repostory access
     * @throws ResourceApplicationException
     */
    private ITicket getRepositoryLoginTicket() throws ResourceApplicationException {
	ICredentials credentials = new SimpleCredentials(resourceToolContentHandler.getRepositoryUser(),
		resourceToolContentHandler.getRepositoryId());
	try {
	    ITicket ticket = repositoryService.login(credentials, resourceToolContentHandler
		    .getRepositoryWorkspaceName());
	    return ticket;
	} catch (AccessDeniedException ae) {
	    throw new ResourceApplicationException("Access Denied to repository." + ae.getMessage());
	} catch (WorkspaceNotFoundException we) {
	    throw new ResourceApplicationException("Workspace not found." + we.getMessage());
	} catch (LoginException e) {
	    throw new ResourceApplicationException("Login failed." + e.getMessage());
	}
    }

    public Resource getResourceByContentId(Long contentId) {
	Resource rs = resourceDao.getByContentId(contentId);
	if (rs == null) {
	    ResourceServiceImpl.log.debug("Could not find the content by given ID:" + contentId);
	}
	return rs;
    }

    public Resource getDefaultContent(Long contentId) throws ResourceApplicationException {
	if (contentId == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ResourceServiceImpl.log.error(error);
	    throw new ResourceApplicationException(error);
	}

	Resource defaultContent = getDefaultResource();
	// save default content by given ID.
	Resource content = new Resource();
	content = Resource.newInstance(defaultContent, contentId, resourceToolContentHandler);
	return content;
    }

    public List getAuthoredItems(Long resourceUid) {
	return resourceItemDao.getAuthoringItems(resourceUid);
    }

    public ResourceAttachment uploadInstructionFile(FormFile uploadFile, String fileType)
	    throws UploadResourceFileException {
	if (uploadFile == null || StringUtils.isEmpty(uploadFile.getFileName())) {
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.upload.file.not.found",
		    new Object[] { uploadFile }));
	}

	// upload file to repository
	NodeKey nodeKey = processFile(uploadFile, fileType);

	// create new attachement
	ResourceAttachment file = new ResourceAttachment();
	file.setFileType(fileType);
	file.setFileUuid(nodeKey.getUuid());
	file.setFileVersionId(nodeKey.getVersion());
	file.setFileName(uploadFile.getFileName());
	file.setCreated(new Date());

	return file;
    }

    public void createUser(ResourceUser resourceUser) {
	resourceUserDao.saveObject(resourceUser);
    }

    public ResourceUser getUserByIDAndContent(Long userId, Long contentId) {

	return resourceUserDao.getUserByUserIDAndContentID(userId, contentId);

    }

    public ResourceUser getUserByIDAndSession(Long userId, Long sessionId) {

	return resourceUserDao.getUserByUserIDAndSessionID(userId, sessionId);

    }

    public void deleteFromRepository(Long fileUuid, Long fileVersionId) throws ResourceApplicationException {
	ITicket ticket = getRepositoryLoginTicket();
	try {
	    repositoryService.deleteVersion(ticket, fileUuid, fileVersionId);
	} catch (Exception e) {
	    throw new ResourceApplicationException("Exception occured while deleting files from" + " the repository "
		    + e.getMessage());
	}
    }

    public void saveOrUpdateResource(Resource resource) {
	resourceDao.saveObject(resource);
    }

    public void deleteResourceAttachment(Long attachmentUid) {
	resourceAttachmentDao.removeObject(ResourceAttachment.class, attachmentUid);

    }

    public void saveOrUpdateResourceItem(ResourceItem item) {
	resourceItemDao.saveObject(item);
    }

    public void deleteResourceItem(Long uid) {
	resourceItemDao.removeObject(ResourceItem.class, uid);
    }

    public List<ResourceItem> getResourceItemsBySessionId(Long sessionId) {
	ResourceSession session = resourceSessionDao.getSessionBySessionId(sessionId);
	if (session == null) {
	    ResourceServiceImpl.log.error("Failed get ResourceSession by ID [" + sessionId + "]");
	    return null;
	}
	// add resource items from Authoring
	Resource resource = session.getResource();
	List<ResourceItem> items = new ArrayList<ResourceItem>();
	items.addAll(resource.getResourceItems());

	// add resource items from ResourceSession
	items.addAll(session.getResourceItems());

	return items;
    }

    public List<Summary> exportBySessionId(Long sessionId, boolean skipHide) {
	ResourceSession session = resourceSessionDao.getSessionBySessionId(sessionId);
	if (session == null) {
	    ResourceServiceImpl.log.error("Failed get ResourceSession by ID [" + sessionId + "]");
	    return null;
	}
	// initial resource items list
	List<Summary> itemList = new ArrayList();
	Set<ResourceItem> resList = session.getResource().getResourceItems();
	for (ResourceItem item : resList) {
	    if (skipHide && item.isHide()) {
		continue;
	    }
	    // if item is create by author
	    if (item.isCreateByAuthor()) {
		Summary sum = new Summary(session.getSessionId(), session.getSessionName(), item, false);
		itemList.add(sum);
	    }
	}

	// get this session's all resource items
	Set<ResourceItem> sessList = session.getResourceItems();
	for (ResourceItem item : sessList) {
	    if (skipHide && item.isHide()) {
		continue;
	    }

	    // to skip all item create by author
	    if (!item.isCreateByAuthor()) {
		Summary sum = new Summary(session.getSessionId(), session.getSessionName(), item, false);
		itemList.add(sum);
	    }
	}

	return itemList;
    }

    public List<List<Summary>> exportByContentId(Long contentId) {
	Resource resource = resourceDao.getByContentId(contentId);
	List<List<Summary>> groupList = new ArrayList();

	// create init resource items list
	List<Summary> initList = new ArrayList();
	groupList.add(initList);
	Set<ResourceItem> resList = resource.getResourceItems();
	for (ResourceItem item : resList) {
	    if (item.isCreateByAuthor()) {
		Summary sum = new Summary(null, null, item, true);
		initList.add(sum);
	    }
	}

	// session by session
	List<ResourceSession> sessionList = resourceSessionDao.getByContentId(contentId);
	for (ResourceSession session : sessionList) {
	    List<Summary> group = new ArrayList<Summary>();
	    // get this session's all resource items
	    Set<ResourceItem> sessList = session.getResourceItems();
	    for (ResourceItem item : sessList) {
		// to skip all item create by author
		if (!item.isCreateByAuthor()) {
		    Summary sum = new Summary(session.getSessionId(), session.getSessionName(), item, false);
		    group.add(sum);
		}
	    }
	    if (group.size() == 0) {
		group.add(new Summary(session.getSessionId(), session.getSessionName(), null, false));
	    }
	    groupList.add(group);
	}

	return groupList;
    }

    public Resource getResourceBySessionId(Long sessionId) {
	ResourceSession session = resourceSessionDao.getSessionBySessionId(sessionId);
	// to skip CGLib problem
	Long contentId = session.getResource().getContentId();
	Resource res = resourceDao.getByContentId(contentId);
	int miniView = res.getMiniViewResourceNumber();
	// construct dto fields;
	res.setMiniViewNumberStr(messageService.getMessage("label.learning.minimum.review", new Object[] { new Integer(
		miniView) }));
	return res;
    }

    public ResourceSession getResourceSessionBySessionId(Long sessionId) {
	return resourceSessionDao.getSessionBySessionId(sessionId);
    }

    public void saveOrUpdateResourceSession(ResourceSession resSession) {
	resourceSessionDao.saveObject(resSession);
    }

    public void retrieveComplete(SortedSet<ResourceItem> resourceItemList, ResourceUser user) {
	for (ResourceItem item : resourceItemList) {
	    ResourceItemVisitLog log = resourceItemVisitDao.getResourceItemLog(item.getUid(), user.getUserId());
	    if (log == null) {
		item.setComplete(false);
	    } else {
		item.setComplete(log.isComplete());
	    }
	}
    }

    public void setItemComplete(Long resourceItemUid, Long userId, Long sessionId) {
	ResourceItemVisitLog log = resourceItemVisitDao.getResourceItemLog(resourceItemUid, userId);
	if (log == null) {
	    log = new ResourceItemVisitLog();
	    ResourceItem item = resourceItemDao.getByUid(resourceItemUid);
	    log.setResourceItem(item);
	    ResourceUser user = resourceUserDao.getUserByUserIDAndSessionID(userId, sessionId);
	    log.setUser(user);
	    log.setSessionId(sessionId);
	    log.setAccessDate(new Timestamp(new Date().getTime()));
	}
	log.setComplete(true);
	log.setCompleteDate(new Timestamp(new Date().getTime()));
	resourceItemVisitDao.saveObject(log);
    }

    public void setItemAccess(Long resourceItemUid, Long userId, Long sessionId) {
	ResourceItemVisitLog log = resourceItemVisitDao.getResourceItemLog(resourceItemUid, userId);
	if (log == null) {
	    log = new ResourceItemVisitLog();
	    ResourceItem item = resourceItemDao.getByUid(resourceItemUid);
	    log.setResourceItem(item);
	    ResourceUser user = resourceUserDao.getUserByUserIDAndSessionID(userId, sessionId);
	    log.setUser(user);
	    log.setComplete(false);
	    log.setSessionId(sessionId);
	    log.setAccessDate(new Timestamp(new Date().getTime()));
	    resourceItemVisitDao.saveObject(log);
	}
    }

    public String finishToolSession(Long toolSessionId, Long userId) throws ResourceApplicationException {
	ResourceUser user = resourceUserDao.getUserByUserIDAndSessionID(userId, toolSessionId);
	user.setSessionFinished(true);
	resourceUserDao.saveObject(user);

	// ResourceSession session = resourceSessionDao.getSessionBySessionId(toolSessionId);
	// session.setStatus(ResourceConstants.COMPLETED);
	// resourceSessionDao.saveObject(session);

	String nextUrl = null;
	try {
	    nextUrl = this.leaveToolSession(toolSessionId, userId);
	} catch (DataMissingException e) {
	    throw new ResourceApplicationException(e);
	} catch (ToolException e) {
	    throw new ResourceApplicationException(e);
	}
	return nextUrl;
    }

    public int checkMiniView(Long toolSessionId, Long userUid) {
	int miniView = resourceItemVisitDao.getUserViewLogCount(toolSessionId, userUid);
	ResourceSession session = resourceSessionDao.getSessionBySessionId(toolSessionId);
	if (session == null) {
	    ResourceServiceImpl.log.error("Failed get session by ID [" + toolSessionId + "]");
	    return 0;
	}
	int reqView = session.getResource().getMiniViewResourceNumber();

	return reqView - miniView;
    }

    public ResourceItem getResourceItemByUid(Long itemUid) {
	return resourceItemDao.getByUid(itemUid);
    }

    public List<List<Summary>> getSummary(Long contentId) {
	List<List<Summary>> groupList = new ArrayList<List<Summary>>();
	List<Summary> group = new ArrayList<Summary>();

	// get all item which is accessed by user
	Map<Long, Integer> visitCountMap = resourceItemVisitDao.getSummary(contentId);

	Resource resource = resourceDao.getByContentId(contentId);
	Set<ResourceItem> resItemList = resource.getResourceItems();

	// get all sessions in a resource and retrieve all resource items under this session
	// plus initial resource items by author creating (resItemList)
	List<ResourceSession> sessionList = resourceSessionDao.getByContentId(contentId);
	for (ResourceSession session : sessionList) {
	    // one new group for one session.
	    group = new ArrayList<Summary>();
	    // firstly, put all initial resource item into this group.
	    for (ResourceItem item : resItemList) {
		Summary sum = new Summary(session.getSessionId(), session.getSessionName(), item);
		// set viewNumber according visit log
		if (visitCountMap.containsKey(item.getUid())) {
		    sum.setViewNumber(visitCountMap.get(item.getUid()).intValue());
		}
		group.add(sum);
	    }
	    // get this session's all resource items
	    Set<ResourceItem> sessItemList = session.getResourceItems();
	    for (ResourceItem item : sessItemList) {
		// to skip all item create by author
		if (!item.isCreateByAuthor()) {
		    Summary sum = new Summary(session.getSessionId(), session.getSessionName(), item);
		    // set viewNumber according visit log
		    if (visitCountMap.containsKey(item.getUid())) {
			sum.setViewNumber(visitCountMap.get(item.getUid()).intValue());
		    }
		    group.add(sum);
		}
	    }
	    // so far no any item available, so just put session name info to Summary
	    if (group.size() == 0) {
		group.add(new Summary(session.getSessionId(), session.getSessionName(), null));
	    }
	    groupList.add(group);
	}

	return groupList;

    }

    public Map<Long, Set<ReflectDTO>> getReflectList(Long contentId, boolean setEntry) {
	Map<Long, Set<ReflectDTO>> map = new HashMap<Long, Set<ReflectDTO>>();

	List<ResourceSession> sessionList = resourceSessionDao.getByContentId(contentId);
	for (ResourceSession session : sessionList) {
	    Long sessionId = session.getSessionId();
	    boolean hasRefection = session.getResource().isReflectOnActivity();
	    Set<ReflectDTO> list = new TreeSet<ReflectDTO>(new ReflectDTOComparator());
	    // get all users in this session
	    List<ResourceUser> users = resourceUserDao.getBySessionID(sessionId);
	    for (ResourceUser user : users) {
		ReflectDTO ref = new ReflectDTO(user);

		if (setEntry) {
		    NotebookEntry entry = getEntry(sessionId, CoreNotebookConstants.NOTEBOOK_TOOL,
			    ResourceConstants.TOOL_SIGNATURE, user.getUserId().intValue());
		    if (entry != null) {
			ref.setReflect(entry.getEntry());
		    }
		}

		ref.setHasRefection(hasRefection);
		list.add(ref);
	    }
	    map.put(sessionId, list);
	}

	return map;
    }

    public List<ResourceUser> getUserListBySessionItem(Long sessionId, Long itemUid) {
	List<ResourceItemVisitLog> logList = resourceItemVisitDao.getResourceItemLogBySession(sessionId, itemUid);
	List<ResourceUser> userList = new ArrayList(logList.size());
	for (ResourceItemVisitLog visit : logList) {
	    ResourceUser user = visit.getUser();
	    user.setAccessDate(visit.getAccessDate());
	    user.setCompleteDate(visit.getCompleteDate());
	    Date timeTaken = (visit.getCompleteDate() != null && visit.getAccessDate() != null) ? 
		    new Date(visit.getCompleteDate().getTime() - visit.getAccessDate().getTime()) : null;
	    user.setTimeTaken(timeTaken);
	    userList.add(user);
	}
	return userList;
    }

    public void setItemVisible(Long itemUid, boolean visible) {
	ResourceItem item = resourceItemDao.getByUid(itemUid);
	if (item != null) {
	    // createBy should be null for system default value.
	    Long userId = 0L;
	    String loginName = "No user";
	    if (item.getCreateBy() != null) {
		userId = item.getCreateBy().getUserId();
		loginName = item.getCreateBy().getLoginName();
	    }
	    if (visible) {
		auditService.logShowEntry(ResourceConstants.TOOL_SIGNATURE, userId, loginName, item.toString());
	    } else {
		auditService.logHideEntry(ResourceConstants.TOOL_SIGNATURE, userId, loginName, item.toString());
	    }
	    item.setHide(!visible);
	    resourceItemDao.saveObject(item);
	}
    }

    public Long createNotebookEntry(Long sessionId, Integer notebookToolType, String toolSignature, Integer userId,
	    String entryText) {
	return coreNotebookService.createNotebookEntry(sessionId, notebookToolType, toolSignature, userId, "",
		entryText);
    }

    public NotebookEntry getEntry(Long sessionId, Integer idType, String signature, Integer userID) {
	List<NotebookEntry> list = coreNotebookService.getEntry(sessionId, idType, signature, userID);
	if (list == null || list.isEmpty()) {
	    return null;
	} else {
	    return list.get(0);
	}
    }

    /**
     * @param notebookEntry
     */
    public void updateEntry(NotebookEntry notebookEntry) {
	coreNotebookService.updateEntry(notebookEntry);
    }

    public ResourceUser getUser(Long uid) {
	return (ResourceUser) resourceUserDao.getObject(ResourceUser.class, uid);
    }

    // *****************************************************************************
    // private methods
    // *****************************************************************************
    private Resource getDefaultResource() throws ResourceApplicationException {
	Long defaultResourceId = getToolDefaultContentIdBySignature(ResourceConstants.TOOL_SIGNATURE);
	Resource defaultResource = getResourceByContentId(defaultResourceId);
	if (defaultResource == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ResourceServiceImpl.log.error(error);
	    throw new ResourceApplicationException(error);
	}

	return defaultResource;
    }

    private Long getToolDefaultContentIdBySignature(String toolSignature) throws ResourceApplicationException {
	Long contentId = null;
	contentId = new Long(toolService.getToolDefaultContentIdBySignature(toolSignature));
	if (contentId == null) {
	    String error = messageService.getMessage("error.msg.default.content.not.find");
	    ResourceServiceImpl.log.error(error);
	    throw new ResourceApplicationException(error);
	}
	return contentId;
    }

    /**
     * Process an uploaded file.
     * 
     * @throws ResourceApplicationException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RepositoryCheckedException
     * @throws InvalidParameterException
     */
    private NodeKey processFile(FormFile file, String fileType) throws UploadResourceFileException {
	NodeKey node = null;
	if (file != null && !StringUtils.isEmpty(file.getFileName())) {
	    String fileName = file.getFileName();
	    try {
		node = resourceToolContentHandler.uploadFile(file.getInputStream(), fileName, file.getContentType(),
			fileType);
	    } catch (InvalidParameterException e) {
		throw new UploadResourceFileException(messageService.getMessage("error.msg.invaid.param.upload"));
	    } catch (FileNotFoundException e) {
		throw new UploadResourceFileException(messageService.getMessage("error.msg.file.not.found"));
	    } catch (RepositoryCheckedException e) {
		throw new UploadResourceFileException(messageService.getMessage("error.msg.repository"));
	    } catch (IOException e) {
		throw new UploadResourceFileException(messageService.getMessage("error.msg.io.exception"));
	    }
	}
	return node;
    }

    private NodeKey processPackage(String packageDirectory, String initFile) throws UploadResourceFileException {
	NodeKey node = null;
	try {
	    node = resourceToolContentHandler.uploadPackage(packageDirectory, initFile);
	} catch (InvalidParameterException e) {
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.invaid.param.upload"));
	} catch (RepositoryCheckedException e) {
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.repository"));
	}
	return node;
    }

    public void uploadResourceItemFile(ResourceItem item, FormFile file) throws UploadResourceFileException {
	try {
	    InputStream is = file.getInputStream();
	    String fileName = file.getFileName();
	    String fileType = file.getContentType();
	    // For file only upload one sigle file
	    if (item.getType() == ResourceConstants.RESOURCE_TYPE_FILE) {
		NodeKey nodeKey = processFile(file, IToolContentHandler.TYPE_ONLINE);
		item.setFileUuid(nodeKey.getUuid());
		item.setFileVersionId(nodeKey.getVersion());
	    }
	    // need unzip upload, and check the initial item :default.htm/html or index.htm/html
	    if (item.getType() == ResourceConstants.RESOURCE_TYPE_WEBSITE) {
		String packageDirectory = ZipFileUtil.expandZip(is, fileName);
		String initFile = findWebsiteInitialItem(packageDirectory);
		if (initFile == null) {
		    throw new UploadResourceFileException(messageService
			    .getMessage("error.msg.website.no.initial.file"));
		}
		item.setInitialItem(initFile);
		// upload package
		NodeKey nodeKey = processPackage(packageDirectory, initFile);
		item.setFileUuid(nodeKey.getUuid());
		item.setFileVersionId(nodeKey.getVersion());
	    }
	    // need unzip upload, and parse learning object information from XML file.
	    if (item.getType() == ResourceConstants.RESOURCE_TYPE_LEARNING_OBJECT) {
		String packageDirectory = ZipFileUtil.expandZip(is, fileName);
		IContentPackageConverter cpConverter = new SimpleContentPackageConverter(packageDirectory);
		String initFile = cpConverter.getDefaultItem();
		item.setInitialItem(initFile);
		item.setImsSchema(cpConverter.getSchema());
		item.setOrganizationXml(cpConverter.getOrganzationXML());
		// upload package
		NodeKey nodeKey = processPackage(packageDirectory, initFile);
		item.setFileUuid(nodeKey.getUuid());
		item.setFileVersionId(nodeKey.getVersion());
	    }
	    // create the package from the directory contents
	    item.setFileType(fileType);
	    item.setFileName(fileName);
	} catch (ZipFileUtilException e) {
	    ResourceServiceImpl.log.error(messageService.getMessage("error.msg.zip.file.exception") + " : "
		    + e.toString());
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.zip.file.exception"));
	} catch (FileNotFoundException e) {
	    ResourceServiceImpl.log.error(messageService.getMessage("error.msg.file.not.found") + ":" + e.toString());
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.file.not.found"));
	} catch (IOException e) {
	    ResourceServiceImpl.log.error(messageService.getMessage("error.msg.io.exception") + ":" + e.toString());
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.io.exception"));
	} catch (IMSManifestException e) {
	    ResourceServiceImpl.log.error(messageService.getMessage("error.msg.ims.package") + ":" + e.toString());
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.ims.package"));
	} catch (ImscpApplicationException e) {
	    ResourceServiceImpl.log.error(messageService.getMessage("error.msg.ims.application") + ":" + e.toString());
	    throw new UploadResourceFileException(messageService.getMessage("error.msg.ims.application"));
	}
    }

    /**
     * Find out default.htm/html or index.htm/html in the given directory folder
     * 
     * @param packageDirectory
     * @return
     */
    private String findWebsiteInitialItem(String packageDirectory) {
	File file = new File(packageDirectory);
	if (!file.isDirectory()) {
	    return null;
	}

	File[] initFiles = file.listFiles(new FileFilter() {
	    public boolean accept(File pathname) {
		if (pathname == null || pathname.getName() == null) {
		    return false;
		}
		String name = pathname.getName();
		if (name.endsWith("default.html") || name.endsWith("default.htm") || name.endsWith("index.html")
			|| name.endsWith("index.htm")) {
		    return true;
		}
		return false;
	    }
	});
	if (initFiles != null && initFiles.length > 0) {
	    return initFiles[0].getName();
	} else {
	    return null;
	}
    }
    
    public boolean isGroupedActivity(long toolContentID) {
	return toolService.isGroupedActivity(toolContentID);
    }

    // *****************************************************************************
    // set methods for Spring Bean
    // *****************************************************************************
    public void setAuditService(IAuditService auditService) {
	this.auditService = auditService;
    }

    public void setLearnerService(ILearnerService learnerService) {
	this.learnerService = learnerService;
    }

    public void setMessageService(MessageService messageService) {
	this.messageService = messageService;
    }

    public void setRepositoryService(IRepositoryService repositoryService) {
	this.repositoryService = repositoryService;
    }

    public void setResourceAttachmentDao(ResourceAttachmentDAO resourceAttachmentDao) {
	this.resourceAttachmentDao = resourceAttachmentDao;
    }

    public void setResourceDao(ResourceDAO resourceDao) {
	this.resourceDao = resourceDao;
    }

    public void setResourceItemDao(ResourceItemDAO resourceItemDao) {
	this.resourceItemDao = resourceItemDao;
    }

    public void setResourceSessionDao(ResourceSessionDAO resourceSessionDao) {
	this.resourceSessionDao = resourceSessionDao;
    }

    public void setResourceToolContentHandler(ResourceToolContentHandler resourceToolContentHandler) {
	this.resourceToolContentHandler = resourceToolContentHandler;
    }

    public void setResourceUserDao(ResourceUserDAO resourceUserDao) {
	this.resourceUserDao = resourceUserDao;
    }

    public void setToolService(ILamsToolService toolService) {
	this.toolService = toolService;
    }

    public ResourceItemVisitDAO getResourceItemVisitDao() {
	return resourceItemVisitDao;
    }

    public void setResourceItemVisitDao(ResourceItemVisitDAO resourceItemVisitDao) {
	this.resourceItemVisitDao = resourceItemVisitDao;
    }

    // *******************************************************************************
    // ToolContentManager, ToolSessionManager methods
    // *******************************************************************************

    public void exportToolContent(Long toolContentId, String rootPath) throws DataMissingException, ToolException {
	Resource toolContentObj = resourceDao.getByContentId(toolContentId);
	if (toolContentObj == null) {
	    try {
		toolContentObj = getDefaultResource();
	    } catch (ResourceApplicationException e) {
		throw new DataMissingException(e.getMessage());
	    }
	}
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to find default content for the share resources tool");
	}

	// set ResourceToolContentHandler as null to avoid copy file node in repository again.
	toolContentObj = Resource.newInstance(toolContentObj, toolContentId, null);
	toolContentObj.setToolContentHandler(null);
	toolContentObj.setOfflineFileList(null);
	toolContentObj.setOnlineFileList(null);
	toolContentObj.setMiniViewNumberStr(null);
	try {
	    exportContentService.registerFileClassForExport(ResourceAttachment.class.getName(), "fileUuid",
		    "fileVersionId");
	    exportContentService.registerFileClassForExport(ResourceItem.class.getName(), "fileUuid", "fileVersionId");
	    exportContentService.exportToolContent(toolContentId, toolContentObj, resourceToolContentHandler, rootPath);
	} catch (ExportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    public void importToolContent(Long toolContentId, Integer newUserUid, String toolContentPath, String fromVersion,
	    String toVersion) throws ToolException {

	try {
	    exportContentService.registerFileClassForImport(ResourceAttachment.class.getName(), "fileUuid",
		    "fileVersionId", "fileName", "fileType", null, null);
	    exportContentService.registerFileClassForImport(ResourceItem.class.getName(), "fileUuid", "fileVersionId",
		    "fileName", "fileType", null, "initialItem");

	    Object toolPOJO = exportContentService.importToolContent(toolContentPath, resourceToolContentHandler,
		    fromVersion, toVersion);
	    if (!(toolPOJO instanceof Resource)) {
		throw new ImportToolContentException(
			"Import Share resources tool content failed. Deserialized object is " + toolPOJO);
	    }
	    Resource toolContentObj = (Resource) toolPOJO;

	    // reset it to new toolContentId
	    toolContentObj.setContentId(toolContentId);
	    ResourceUser user = resourceUserDao.getUserByUserIDAndContentID(new Long(newUserUid.longValue()),
		    toolContentId);
	    if (user == null) {
		user = new ResourceUser();
		UserDTO sysUser = ((User) userManagementService.findById(User.class, newUserUid)).getUserDTO();
		user.setFirstName(sysUser.getFirstName());
		user.setLastName(sysUser.getLastName());
		user.setLoginName(sysUser.getLogin());
		user.setUserId(new Long(newUserUid.longValue()));
		user.setResource(toolContentObj);
	    }
	    toolContentObj.setCreatedBy(user);

	    // reset all resourceItem createBy user
	    Set<ResourceItem> items = toolContentObj.getResourceItems();
	    for (ResourceItem item : items) {
		item.setCreateBy(user);
	    }
	    resourceDao.saveObject(toolContentObj);
	} catch (ImportToolContentException e) {
	    throw new ToolException(e);
	}
    }

    /**
     * Get the definitions for possible output for an activity, based on the toolContentId. These may be definitions
     * that are always available for the tool (e.g. number of marks for Multiple Choice) or a custom definition created
     * for a particular activity such as the answer to the third question contains the word Koala and hence the need for
     * the toolContentId
     * 
     * @return SortedMap of ToolOutputDefinitions with the key being the name of each definition
     * @throws ResourceApplicationException
     */
    public SortedMap<String, ToolOutputDefinition> getToolOutputDefinitions(Long toolContentId, int definitionType)
	    throws ToolException {
	Resource content = getResourceByContentId(toolContentId);
	if (content == null) {
	    try {
		content = getDefaultContent(toolContentId);
	    } catch (ResourceApplicationException e) {
		throw new ToolException(e);
	    }
	}
	return getResourceOutputFactory().getToolOutputDefinitions(content, definitionType);
    }

    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException {
	if (toContentId == null) {
	    throw new ToolException("Failed to create the SharedResourceFiles tool seession");
	}

	Resource resource = null;
	if (fromContentId != null) {
	    resource = resourceDao.getByContentId(fromContentId);
	}
	if (resource == null) {
	    try {
		resource = getDefaultResource();
	    } catch (ResourceApplicationException e) {
		throw new ToolException(e);
	    }
	}

	Resource toContent = Resource.newInstance(resource, toContentId, resourceToolContentHandler);
	resourceDao.saveObject(toContent);

	// save resource items as well
	Set items = toContent.getResourceItems();
	if (items != null) {
	    Iterator iter = items.iterator();
	    while (iter.hasNext()) {
		ResourceItem item = (ResourceItem) iter.next();
		// createRootTopic(toContent.getUid(),null,msg);
	    }
	}
    }

    public void setAsDefineLater(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Resource resource = resourceDao.getByContentId(toolContentId);
	if (resource == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	resource.setDefineLater(value);
    }

    public void setAsRunOffline(Long toolContentId, boolean value) throws DataMissingException, ToolException {
	Resource resource = resourceDao.getByContentId(toolContentId);
	if (resource == null) {
	    throw new ToolException("No found tool content by given content ID:" + toolContentId);
	}
	resource.setRunOffline(value);
    }

    public void removeToolContent(Long toolContentId, boolean removeSessionData) throws SessionDataExistsException,
	    ToolException {
	Resource resource = resourceDao.getByContentId(toolContentId);
	if (removeSessionData) {
	    List list = resourceSessionDao.getByContentId(toolContentId);
	    Iterator iter = list.iterator();
	    while (iter.hasNext()) {
		ResourceSession session = (ResourceSession) iter.next();
		resourceSessionDao.delete(session);
	    }
	}
	resourceDao.delete(resource);
    }

    public void createToolSession(Long toolSessionId, String toolSessionName, Long toolContentId) throws ToolException {
	ResourceSession session = new ResourceSession();
	session.setSessionId(toolSessionId);
	session.setSessionName(toolSessionName);
	Resource resource = resourceDao.getByContentId(toolContentId);
	session.setResource(resource);
	resourceSessionDao.saveObject(session);
    }

    public String leaveToolSession(Long toolSessionId, Long learnerId) throws DataMissingException, ToolException {
	if (toolSessionId == null) {
	    ResourceServiceImpl.log.error("Fail to leave tool Session based on null tool session id.");
	    throw new ToolException("Fail to remove tool Session based on null tool session id.");
	}
	if (learnerId == null) {
	    ResourceServiceImpl.log.error("Fail to leave tool Session based on null learner.");
	    throw new ToolException("Fail to remove tool Session based on null learner.");
	}

	ResourceSession session = resourceSessionDao.getSessionBySessionId(toolSessionId);
	if (session != null) {
	    session.setStatus(ResourceConstants.COMPLETED);
	    resourceSessionDao.saveObject(session);
	} else {
	    ResourceServiceImpl.log.error("Fail to leave tool Session.Could not find shared resources "
		    + "session by given session id: " + toolSessionId);
	    throw new DataMissingException("Fail to leave tool Session."
		    + "Could not find shared resource session by given session id: " + toolSessionId);
	}
	return learnerService.completeToolSession(toolSessionId, learnerId);
    }

    public ToolSessionExportOutputData exportToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	return null;
    }

    public ToolSessionExportOutputData exportToolSession(List toolSessionIds) throws DataMissingException,
	    ToolException {
	return null;
    }

    public void removeToolSession(Long toolSessionId) throws DataMissingException, ToolException {
	resourceSessionDao.deleteBySessionId(toolSessionId);
    }

    /**
     * Get the tool output for the given tool output names.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.util.List<String>, java.lang.Long,
     *      java.lang.Long)
     */
    public SortedMap<String, ToolOutput> getToolOutput(List<String> names, Long toolSessionId, Long learnerId) {
	return getResourceOutputFactory().getToolOutput(names, this, toolSessionId, learnerId);
    }

    /**
     * Get the tool output for the given tool output name.
     * 
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#getToolOutput(java.lang.String, java.lang.Long,
     *      java.lang.Long)
     */
    public ToolOutput getToolOutput(String name, Long toolSessionId, Long learnerId) {
	return getResourceOutputFactory().getToolOutput(name, this, toolSessionId, learnerId);
    }

    /* ===============Methods implemented from ToolContentImport102Manager =============== */

    /**
     * Import the data for a 1.0.2 Noticeboard or HTMLNoticeboard
     */
    public void import102ToolContent(Long toolContentId, UserDTO user, Hashtable importValues) {
	Date now = new Date();
	Resource toolContentObj = new Resource();

	try {
	    toolContentObj.setTitle((String) importValues.get(ToolContentImport102Manager.CONTENT_TITLE));
	    toolContentObj.setContentId(toolContentId);
	    toolContentObj.setContentInUse(Boolean.FALSE);
	    toolContentObj.setCreated(now);
	    toolContentObj.setDefineLater(Boolean.FALSE);
	    toolContentObj.setInstructions(WebUtil.convertNewlines((String) importValues
		    .get(ToolContentImport102Manager.CONTENT_BODY)));
	    toolContentObj.setOfflineInstructions(null);
	    toolContentObj.setOnlineInstructions(null);
	    toolContentObj.setRunOffline(Boolean.FALSE);
	    toolContentObj.setUpdated(now);
	    toolContentObj.setReflectOnActivity(Boolean.FALSE);
	    toolContentObj.setReflectInstructions(null);

	    toolContentObj.setRunAuto(Boolean.FALSE);
	    Boolean bool = WDDXProcessor.convertToBoolean(importValues,
		    ToolContentImport102Manager.CONTENT_URL_RUNTIME_LEARNER_SUBMIT_FILE);
	    toolContentObj.setAllowAddFiles(bool != null ? bool : Boolean.TRUE);
	    bool = WDDXProcessor.convertToBoolean(importValues,
		    ToolContentImport102Manager.CONTENT_URL_RUNTIME_LEARNER_SUBMIT_URL);
	    toolContentObj.setAllowAddUrls(bool != null ? bool : Boolean.TRUE);
	    Integer minToComplete = WDDXProcessor.convertToInteger(importValues,
		    ToolContentImport102Manager.CONTENT_URL_MIN_NUMBER_COMPLETE);
	    toolContentObj.setMiniViewResourceNumber(minToComplete != null ? minToComplete.intValue() : 0);
	    bool = WDDXProcessor.convertToBoolean(importValues,
		    ToolContentImport102Manager.CONTENT_URL_RUNTIME_LEARNER_SUBMIT_URL);
	    toolContentObj.setLockWhenFinished(Boolean.FALSE);
	    toolContentObj.setRunAuto(Boolean.FALSE);

	    // leave as empty, no need to set them to anything.
	    // toolContentObj.setAttachments(attachments);

	    /*
	     * unused entries from 1.0.2 [directoryName=] no equivalent in 2.0 [runtimeSubmissionStaffFile=true] no
	     * equivalent in 2.0 [contentShowUser=false] no equivalent in 2.0 [isHTML=false] no equivalent in 2.0
	     * [showbuttons=false] no equivalent in 2.0 [isReusable=false] not used in 1.0.2 (would be lock when
	     * finished)
	     */
	    ResourceUser ruser = new ResourceUser();
	    ruser.setUserId(new Long(user.getUserID().longValue()));
	    ruser.setFirstName(user.getFirstName());
	    ruser.setLastName(user.getLastName());
	    ruser.setLoginName(user.getLogin());
	    createUser(ruser);
	    toolContentObj.setCreatedBy(ruser);

	    // Resource Items. They are ordered on the screen by create date so they need to be saved in the right
	    // order.
	    // So read them all in first, then go through and assign the dates in the correct order and then save.
	    Vector urls = (Vector) importValues.get(ToolContentImport102Manager.CONTENT_URL_URLS);
	    SortedMap<Integer, ResourceItem> items = new TreeMap<Integer, ResourceItem>();
	    if (urls != null) {
		Iterator iter = urls.iterator();
		while (iter.hasNext()) {
		    Hashtable urlMap = (Hashtable) iter.next();
		    Integer itemOrder = WDDXProcessor.convertToInteger(urlMap,
			    ToolContentImport102Manager.CONTENT_URL_URL_VIEW_ORDER);
		    ResourceItem item = new ResourceItem();
		    item.setTitle((String) urlMap.get(ToolContentImport102Manager.CONTENT_TITLE));
		    item.setCreateBy(ruser);
		    item.setCreateByAuthor(true);
		    item.setHide(false);

		    Vector instructions = (Vector) urlMap
			    .get(ToolContentImport102Manager.CONTENT_URL_URL_INSTRUCTION_ARRAY);
		    if (instructions != null && instructions.size() > 0) {
			item.setItemInstructions(new HashSet());
			Iterator insIter = instructions.iterator();
			while (insIter.hasNext()) {
			    item.getItemInstructions().add(createInstruction((Hashtable) insIter.next()));
			}
		    }

		    String resourceType = (String) urlMap.get(ToolContentImport102Manager.CONTENT_URL_URL_TYPE);
		    if (ToolContentImport102Manager.URL_RESOURCE_TYPE_URL.equals(resourceType)) {
			item.setType(ResourceConstants.RESOURCE_TYPE_URL);
			item.setUrl((String) urlMap.get(ToolContentImport102Manager.CONTENT_URL_URL_URL));
			item.setOpenUrlNewWindow(false);
		    } else if (ToolContentImport102Manager.URL_RESOURCE_TYPE_WEBSITE.equals(resourceType)) {
			item.setType(ResourceConstants.RESOURCE_TYPE_WEBSITE);
		    } else if (ToolContentImport102Manager.URL_RESOURCE_TYPE_FILE.equals(resourceType)) {
			item.setType(ResourceConstants.RESOURCE_TYPE_FILE);
		    } else {
			throw new ToolException("Invalid shared resources type. Type was " + resourceType);
		    }

		    items.put(itemOrder, item);
		}
	    }

	    Iterator iter = items.values().iterator();
	    Date itemDate = null;
	    while (iter.hasNext()) {
		if (itemDate != null) {
		    try {
			Thread.sleep(1000);
		    } catch (Exception e) {
		    }
		}
		itemDate = new Date();

		ResourceItem item = (ResourceItem) iter.next();
		item.setCreateDate(itemDate);
		toolContentObj.getResourceItems().add(item);
	    }

	} catch (WDDXProcessorConversionException e) {
	    ResourceServiceImpl.log.error("Unable to content for activity " + toolContentObj.getTitle()
		    + "properly due to a WDDXProcessorConversionException.", e);
	    throw new ToolException(
		    "Invalid import data format for activity "
			    + toolContentObj.getTitle()
			    + "- WDDX caused an exception. Some data from the design will have been lost. See log for more details.");
	}

	resourceDao.saveObject(toolContentObj);

    }

    private ResourceItemInstruction createInstruction(Hashtable instructionEntry)
	    throws WDDXProcessorConversionException {

	Integer instructionOrder = WDDXProcessor.convertToInteger(instructionEntry,
		ToolContentImport102Manager.CONTENT_URL_URL_VIEW_ORDER);

	// the description column in 1.0.2 was longer than 255 chars, so truncate.
	String instructionText = (String) instructionEntry.get(ToolContentImport102Manager.CONTENT_URL_INSTRUCTION);
	if (instructionText != null && instructionText.length() > 255) {
	    if (ResourceServiceImpl.log.isDebugEnabled()) {
		ResourceServiceImpl.log
			.debug("1.0.2 Import truncating Item Instruction to 255 characters. Original text was\'"
				+ instructionText + "\'");
	    }
	    instructionText = instructionText.substring(0, 255);
	}

	ResourceItemInstruction instruction = new ResourceItemInstruction();
	instruction.setDescription(instructionText);
	instruction.setSequenceId(instructionOrder);

	return instruction;
    }

    /** Set the description, throws away the title value as this is not supported in 2.0 */
    public void setReflectiveData(Long toolContentId, String title, String description) throws ToolException,
	    DataMissingException {

	Resource toolContentObj = getResourceByContentId(toolContentId);
	if (toolContentObj == null) {
	    throw new DataMissingException("Unable to set reflective data titled " + title
		    + " on activity toolContentId " + toolContentId + " as the tool content does not exist.");
	}

	toolContentObj.setReflectOnActivity(Boolean.TRUE);
	toolContentObj.setReflectInstructions(description);
    }

    /* =================================================================================== */

    public IExportToolContentService getExportContentService() {
	return exportContentService;
    }

    public void setExportContentService(IExportToolContentService exportContentService) {
	this.exportContentService = exportContentService;
    }

    public IUserManagementService getUserManagementService() {
	return userManagementService;
    }

    public void setUserManagementService(IUserManagementService userManagementService) {
	this.userManagementService = userManagementService;
    }

    public ICoreNotebookService getCoreNotebookService() {
	return coreNotebookService;
    }

    public void setCoreNotebookService(ICoreNotebookService coreNotebookService) {
	this.coreNotebookService = coreNotebookService;
    }

    public IEventNotificationService getEventNotificationService() {
	return eventNotificationService;
    }

    public void setEventNotificationService(IEventNotificationService eventNotificationService) {
	this.eventNotificationService = eventNotificationService;
    }

    public String getLocalisedMessage(String key, Object[] args) {
	return messageService.getMessage(key, args);
    }

    public ILessonService getLessonService() {
	return lessonService;
    }

    public void setLessonService(ILessonService lessonService) {
	this.lessonService = lessonService;
    }

    /**
     * Finds out which lesson the given tool content belongs to and returns its monitoring users.
     * 
     * @param sessionId
     *                tool session ID
     * @return list of teachers that monitor the lesson which contains the tool with given session ID
     */
    public List<User> getMonitorsByToolSessionId(Long sessionId) {
	return getLessonService().getMonitorsByToolSessionId(sessionId);
    }

    public Class[] getSupportedToolOutputDefinitionClasses(int definitionType) {
	return getResourceOutputFactory().getSupportedDefinitionClasses(definitionType);
    }

    public ResourceOutputFactory getResourceOutputFactory() {
	return resourceOutputFactory;
    }

    public void setResourceOutputFactory(ResourceOutputFactory resourceOutputFactory) {
	this.resourceOutputFactory = resourceOutputFactory;
    }
}