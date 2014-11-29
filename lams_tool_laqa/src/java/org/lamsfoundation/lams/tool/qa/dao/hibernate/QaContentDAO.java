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
package org.lamsfoundation.lams.tool.qa.dao.hibernate;

import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.lamsfoundation.lams.dao.hibernate.LAMSBaseDAO;
import org.lamsfoundation.lams.tool.qa.QaCondition;
import org.lamsfoundation.lams.tool.qa.QaContent;
import org.lamsfoundation.lams.tool.qa.QaQueContent;
import org.lamsfoundation.lams.tool.qa.dao.IQaContentDAO;
import org.springframework.stereotype.Repository;

/**
 * @author Ozgur Demirtas
 * 
 */
@Repository
public class QaContentDAO extends LAMSBaseDAO implements IQaContentDAO {

    public QaContent getQaByContentId(long qaId) {
	String query = "from QaContent as qa where qa.qaContentId = ?";
	List list = getSessionFactory().getCurrentSession().createQuery(query).setLong(0, qaId).list();

	if (list != null && list.size() > 0) {
	    QaContent qa = (QaContent) list.get(0);
	    return qa;
	}
	return null;
    }

    public void updateQa(QaContent qa) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	getSession().update(qa);
    }

    public void saveQa(QaContent qa) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	getSession().save(qa);
    }

    public void saveOrUpdateQa(QaContent qa) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	getSession().saveOrUpdate(qa);
    }

    public void createQa(QaContent qa) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	getSession().save(qa);
    }

    public void removeAllQaSession(QaContent qaContent) {
    	deleteAll(qaContent.getQaSessions());
    }

    public void removeQa(Long qaContentId) {
	if (qaContentId != null) {
	    String query = "from qa in class org.lamsfoundation.lams.tool.qa.QaContent" + " where qa.qaContentId = ?";
	    Object obj = getSessionFactory().getCurrentSession().createQuery(query).setLong(0, qaContentId.longValue()).uniqueResult();
	    getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	    getSession().delete(obj);
	}
    }

    public void deleteQa(QaContent qaContent) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	getSession().delete(qaContent);
    }

    public void removeQaById(Long qaId) {
	getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	removeQa(qaId);
    }

    public void flush() {
    	getSession().flush();
    }

    public void deleteCondition(QaCondition condition) {
	if (condition != null && condition.getConditionId() != null) {
	    getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
	    getSession().delete(condition);
	}
    }
    
    public void removeQaContentFromCache(QaContent qaContent) {
	if (qaContent != null) {
		getSession().evict(qaContent);
	}

    }

    public void removeQuestionsFromCache(QaContent qaContent) {
	if (qaContent != null) {

	    for (QaQueContent question : (Set<QaQueContent>) qaContent.getQaQueContents()) {
	    	getSession().evict(question);
	    }
	}

    }

    @Override
    public void delete(Object object) {
    	getSession().delete(object);
    }
}