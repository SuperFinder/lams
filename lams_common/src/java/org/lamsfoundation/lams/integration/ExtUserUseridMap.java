package org.lamsfoundation.lams.integration;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.usermanagement.User;

/**
 *
 */
public class ExtUserUseridMap implements Serializable {

    private static final long serialVersionUID = 1755818193730728064L;

    /** identifier field */
    private Integer sid;

    /** persistent field */
    private String extUsername;

    /** persistent field */
    private User user;

    /** persistent field */
    private ExtServer extServer;
    
    /** id of gradebook item in LTI tool consumer */
    private String tcGradebookId;

    /** full constructor */
    public ExtUserUseridMap(String extUsername, User user, ExtServer extServer) {
	this.extUsername = extUsername;
	this.user = user;
	this.extServer = extServer;
    }

    /** default constructor */
    public ExtUserUseridMap() {
    }

    public Integer getSid() {
	return this.sid;
    }

    public void setSid(Integer sid) {
	this.sid = sid;
    }

    public String getExtUsername() {
	return this.extUsername;
    }

    public void setExtUsername(String extUsername) {
	this.extUsername = extUsername;
    }

    public User getUser() {
	return this.user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public ExtServer getExtServer() {
	return this.extServer;
    }

    public void setExtServer(ExtServer extServer) {
	this.extServer = extServer;
    }
    
    public String getTcGradebookId() {
	return this.tcGradebookId;
    }

    public void setTcGradebookId(String tcGradebookId) {
	this.tcGradebookId = tcGradebookId;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this).append("sid", getSid()).append("extUsername", getExtUsername()).toString();
    }

}
