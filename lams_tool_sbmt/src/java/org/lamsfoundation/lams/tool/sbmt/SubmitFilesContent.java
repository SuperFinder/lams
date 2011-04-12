/****************************************************************
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
 * ****************************************************************
 */

/* $$Id$$ */
package org.lamsfoundation.lams.tool.sbmt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.client.IToolContentHandler;

/**
 * @hibernate.class table="tl_lasbmt11_content"
 * @serial 9072799761861936838L
 */
public class SubmitFilesContent implements Serializable, Cloneable {

	private static final long serialVersionUID = 9072799761861936838L;

	private static Logger log = Logger.getLogger(SubmitFilesContent.class);

	/** identifier field */
	private Long contentID;

	/** persistent field */
	//basic tab fields
	private String title;

	private String instruction;

	// advance tab fields
	private boolean lockOnFinished;

	private boolean notifyLearnersOnMarkRelease;

	private boolean notifyTeachersOnFileSubmit;

	//instruction tab fields
	private String offlineInstruction;

	private String onlineInstruction;

	private Set instructionFiles;

	//system level fields
	private boolean defineLater;

	private boolean runOffline;

	private boolean contentInUse;

	private boolean limitUpload;

	private int limitUploadNumber;

	private boolean reflectOnActivity;

	private String reflectInstructions;

	private Date created;

	private Date updated;
	
	private Date submissionDeadline;

	private SubmitUser createdBy;

	//temporary fields
	private IToolContentHandler toolContentHandler;

	/** default constructor */
	public SubmitFilesContent() {
	}

	/**
	 * Copy constructor to create a new SubmitFiles tool's content.
	 * 
	 * @param content The original tool content
	 * @param newContentID The new <code>SubmitFiles</code> contentID
	 * @param toolContentHandler 
	 * @return SubmitFilesContent The new SubmitFilesContent object
	 */
	public static SubmitFilesContent newInstance(SubmitFilesContent content, Long newContentID,
			IToolContentHandler toolContentHandler) {
		content.toolContentHandler = toolContentHandler;
		SubmitFilesContent newContent = (SubmitFilesContent) content.clone();
		newContent.setContentID(newContentID);
		return newContent;
	}

	/**
	 * @hibernate.id generator-class="assigned" 
	 * 				 type="java.lang.Long"
	 *               column="content_id"
	 */
	public Long getContentID() {
		return contentID;
	}

	public void setContentID(Long contentID) {
		this.contentID = contentID;
	}

	/**
	 * @hibernate.property column="title" length="64" not-null="true"
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @hibernate.property column="instruction" type="text"
	 */
	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instructions) {
		instruction = instructions;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("contentID", getContentID()).append("title", getTitle()).append("instructions",
				getInstruction()).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SubmitFilesContent)) {
			return false;
		}
		SubmitFilesContent castOther = (SubmitFilesContent) other;
		return new EqualsBuilder().append(this.getContentID(), castOther.getContentID()).append(this.getTitle(),
				castOther.getTitle()).append(this.getInstruction(), castOther.getInstruction()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getContentID()).append(getTitle()).append(getInstruction()).toHashCode();
	}

	/** 
	 * @hibernate.property column="define_later" length="1"
	 *  not-null="true"
	 */
	public boolean isDefineLater() {
		return defineLater;
	}

	public void setDefineLater(boolean defineLater) {
		this.defineLater = defineLater;
	}

	/** 
	 * @hibernate.property column="run_offline" length="1"
	 * not-null="true"
	 */
	public boolean isRunOffline() {
		return runOffline;
	}

	public void setRunOffline(boolean runOffline) {
		this.runOffline = runOffline;
	}

	/**
	 * @hibernate.property column="offline_instruction" type="text"
	 * @return Returns the offlineInstruction.
	 */
	public String getOfflineInstruction() {
		return offlineInstruction;
	}

	/**
	 * @param offlineInstruction The offlineInstruction to set.
	 */
	public void setOfflineInstruction(String offlineInstruction) {
		this.offlineInstruction = offlineInstruction;
	}

	/**
	 * @hibernate.property column="online_instruction" type="text"
	 * @return Returns the onlineInstruction.
	 */
	public String getOnlineInstruction() {
		return onlineInstruction;
	}

	/**
	 * @param onlineInstruction The onlineInstruction to set.
	 */
	public void setOnlineInstruction(String onlineInstruction) {
		this.onlineInstruction = onlineInstruction;
	}

	/**
	 * @hibernate.property column="content_in_use" length="1"
	 * @return Returns the contentInUse.
	 */
	public boolean isContentInUse() {
		return contentInUse;
	}

	/**
	 * @param contentInUse The contentInUse to set.
	 */
	public void setContentInUse(boolean contentInUse) {
		this.contentInUse = contentInUse;
	}

	/**
	 * @hibernate.set lazy="true" inverse="false" cascade="all"
	 * @hibernate.collection-key column="content_id"
	 * @hibernate.collection-one-to-many class="org.lamsfoundation.lams.tool.sbmt.InstructionFiles"
	 * 
	 * @return Returns the instructionFiles.
	 */
	public Set getInstructionFiles() {
		return instructionFiles;
	}

	/**
	 * @param instructionFiles The instructionFiles to set.
	 */
	public void setInstructionFiles(Set instructionFiles) {
		this.instructionFiles = instructionFiles;
	}

	/**
	 * 
	 * @hibernate.property column="lock_on_finished" length="1"
	 * @return Returns the lockOnFinished.
	 */
	public boolean isLockOnFinished() {
		return lockOnFinished;
	}

	/**
	 * @param lockOnFinished The lockOnFinished to set.
	 */
	public void setLockOnFinished(boolean lockOnFinished) {
		this.lockOnFinished = lockOnFinished;
	}

	@Override
	public Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
			//never clone key!
			((SubmitFilesContent) obj).setContentID(null);
			//clone InstructionFiles object
			if (instructionFiles != null) {
				Iterator iter = instructionFiles.iterator();
				Set set = new HashSet();
				while (iter.hasNext()) {
					InstructionFiles file = (InstructionFiles) iter.next();
					InstructionFiles newFile = (InstructionFiles) file.clone();
					set.add(newFile);
				}
				((SubmitFilesContent) obj).instructionFiles = set;

				if (createdBy != null) {
					((SubmitFilesContent) obj).setCreatedBy((SubmitUser) createdBy.clone());
				}
			}
		}
		catch (CloneNotSupportedException e) {
			SubmitFilesContent.log.error("When clone " + SubmitFilesContent.class + " failed");
		}

		return obj;
	}

	public IToolContentHandler getToolContentHandler() {
		return toolContentHandler;
	}

	public void setToolContentHandler(IToolContentHandler toolContentHandler) {
		this.toolContentHandler = toolContentHandler;
	}

	/**
	 * @hibernate.property column="reflect_instructions"
	 * @return
	 */
	public String getReflectInstructions() {
		return reflectInstructions;
	}

	public void setReflectInstructions(String reflectInstructions) {
		this.reflectInstructions = reflectInstructions;
	}

	/**
	 * @hibernate.property column="reflect_on_activity"
	 * @return
	 */
	public boolean isReflectOnActivity() {
		return reflectOnActivity;
	}

	public void setReflectOnActivity(boolean reflectOnActivity) {
		this.reflectOnActivity = reflectOnActivity;
	}

	/**
	 * @hibernate.property column="limit_upload"
	 * @return
	 */
	public boolean isLimitUpload() {
		return limitUpload;
	}

	public void setLimitUpload(boolean limitUpload) {
		this.limitUpload = limitUpload;
	}

	/**
	 * @hibernate.property column="limit_upload_number"
	 * @return
	 */
	public int getLimitUploadNumber() {
		return limitUploadNumber;
	}

	public void setLimitUploadNumber(int limitUploadNumber) {
		this.limitUploadNumber = limitUploadNumber;
	}

	/**
	 * @hibernate.property column="created"
	 * @return
	 */
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @hibernate.many-to-one  column="created_by" cascade="none"
	 *  
	 * @return
	 */
	public SubmitUser getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(SubmitUser createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @hibernate.property column="updated"
	 * @return
	 */
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	/**
	 * @hibernate.property column="submission_deadline"
	 * @return
	 */
	public Date getSubmissionDeadline() {
		return submissionDeadline;
	}

	public void setSubmissionDeadline(Date submissionDeadline) {
		this.submissionDeadline = submissionDeadline;
	}

	/**
	 * @hibernate.property column="mark_release_notify"
	 * @return
	 */
	public boolean isNotifyLearnersOnMarkRelease() {
		return notifyLearnersOnMarkRelease;
	}

	public void setNotifyLearnersOnMarkRelease(boolean notifyLearnersOnMarkRelease) {
		this.notifyLearnersOnMarkRelease = notifyLearnersOnMarkRelease;
	}

	/**
	 * @hibernate.property column="file_submit_notify"
	 * @return
	 */
	public boolean isNotifyTeachersOnFileSubmit() {
		return notifyTeachersOnFileSubmit;
	}

	public void setNotifyTeachersOnFileSubmit(boolean notifyTeachersOnFileSubmit) {
		this.notifyTeachersOnFileSubmit = notifyTeachersOnFileSubmit;
	}
}
