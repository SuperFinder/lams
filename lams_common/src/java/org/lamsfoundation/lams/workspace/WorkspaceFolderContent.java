/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lamsfoundation.lams.workspace;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;

import org.lamsfoundation.lams.usermanagement.WorkspaceFolder;
import org.lamsfoundation.lams.workspace.dto.FolderContentDTO;


/**
 * @author Manpreet Minhas
 */
public class WorkspaceFolderContent implements Serializable{
	
	public static final Integer CONTENT_TYPE_FILE = new Integer(1);
	public static final Integer CONTENT_TYPE_PACKAGE = new Integer(2);
	
	/** identifier field */
	private Long folderContentID;
	
	/**
	 * non-nullable persistent field 
	 * representing the type of content. 
	 * It can be either CONTENT_TYPE_FILE or 
	 * CONTENT_TYPE_PACKAGE
	 */
	private Integer contentTypeID;
	
	/** non-nullable persistent field 
	 * representing the name of the File
	 **/
	private String name;
	
	/** non-nullable persistent field
	 * representing the description of the File 
	 **/
	private String description;
	
	/**
	 * non-nullable persistent field
	 * representing the date the content
	 * was created.
	 **/
	private Date createDate;
	
	/**
	 * non-nullable persistent field 
	 * representing the date the content
	 * was last modified.
	 **/
	private Date lastModified;
	
	/**
	 * nullable persistent field. Represents the two
	 * part key - UUID and version, returned by the 
	 * ContentRepository once the content has been successfully updated. 
	 **/
	private Long uuid;
	
	/**
	 * nullable persistent field. Represents the two
	 * part key - UUID and version, returned by the 
	 * ContentRepository once the content has been successfully updated. 
	 **/
	private Long versionID;
	
	/**
	 * non-nullable persistent field
	 * indicating the type of the file
	 **/
	private String mimeType;
	
	/** non-nullable persistent field indicating the
	 * <code>WorkspaceFolder</code> that contains
	 * this content
	 **/
	private WorkspaceFolder workspaceFolder;
	
	/** Default Constructor*/
	public WorkspaceFolderContent(){
		
	}
	/** Full Constructor*/
	public WorkspaceFolderContent(Long folderContentID,Integer contentTypeID,
								  String name, String description,
								  Date createDate,Date lastModified, 
								  Long uuid, Long versionID, 
								  String mimeType,WorkspaceFolder workspaceFolder) {
		super();
		this.folderContentID = folderContentID;
		this.contentTypeID = contentTypeID;
		this.name = name;
		this.description = description;
		this.createDate = createDate;
		this.lastModified = lastModified;
		this.uuid = uuid;
		this.versionID = versionID;
		this.mimeType = mimeType;
		this.workspaceFolder = workspaceFolder;
	}
	/** Minimal Constructor */
	public WorkspaceFolderContent(Integer contentTypeID, String name,
			String description, Date createDate, Date lastModified,
			String mimeType, WorkspaceFolder workspaceFolder) {
		super();
		this.contentTypeID = contentTypeID;
		this.name = name;
		this.description = description;
		this.createDate = createDate;
		this.lastModified = lastModified;
		this.mimeType = mimeType;
		this.workspaceFolder = workspaceFolder;
	}
	/**
	 * @return Returns the contentTypeID.
	 */
	public Integer getContentTypeID() {
		return contentTypeID;
	}
	/**
	 * @param contentTypeID The contentTypeID to set.
	 */
	public void setContentTypeID(Integer contentTypeID) {
		this.contentTypeID = contentTypeID;
	}
	/**
	 * @return Returns the createDate.
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate The createDate to set.
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the folderContentID.
	 */
	public Long getFolderContentID() {
		return folderContentID;
	}
	/**
	 * @param folderContentID The folderContentID to set.
	 */
	public void setFolderContentID(Long folderContentID) {
		this.folderContentID = folderContentID;
	}
	/**
	 * @return Returns the lastModified.
	 */
	public Date getLastModified() {
		return lastModified;
	}
	/**
	 * @param lastModified The lastModified to set.
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	/**
	 * @return Returns the mimeType.
	 */
	public String getMimeType() {
		return mimeType;
	}
	/**
	 * @param mimeType The mimeType to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the uuid.
	 */
	public Long getUuid() {
		return uuid;
	}
	/**
	 * @param uuid The uuid to set.
	 */
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return Returns the versionID.
	 */
	public Long getVersionID() {
		return versionID;
	}
	/**
	 * @param versionID The versionID to set.
	 */
	public void setVersionID(Long versionID) {
		this.versionID = versionID;
	}
	/**
	 * @return Returns the workspaceFolder.
	 */
	public WorkspaceFolder getWorkspaceFolder() {
		return workspaceFolder;
	}
	/**
	 * @param workspaceFolder The workspaceFolder to set.
	 */
	public void setWorkspaceFolder(WorkspaceFolder workspaceFolder) {
		this.workspaceFolder = workspaceFolder;
	}
	public FolderContentDTO getFolderContentDTO(SortedSet set, Integer permissions){
		return new FolderContentDTO(permissions,this,set);
	}
}
