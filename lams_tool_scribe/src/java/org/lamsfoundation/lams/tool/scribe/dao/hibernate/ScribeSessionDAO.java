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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$ */

package org.lamsfoundation.lams.tool.scribe.dao.hibernate;

import java.util.List;

import org.lamsfoundation.lams.dao.hibernate.BaseDAO;
import org.lamsfoundation.lams.tool.scribe.dao.IScribeSessionDAO;
import org.lamsfoundation.lams.tool.scribe.model.ScribeSession;

/**
 * DAO for accessing the ScribeSession objects - Hibernate specific code.
 */
public class ScribeSessionDAO extends BaseDAO implements IScribeSessionDAO {

	public static final String SQL_QUERY_FIND_BY_SESSION_ID = "from "
			+ ScribeSession.class.getName() + " where session_id=?";

	public void saveOrUpdate(ScribeSession session) {
		this.getHibernateTemplate().saveOrUpdate(session);
		this.getHibernateTemplate().flush();
	}

	public ScribeSession getBySessionId(Long toolSessionId) {
		List list = this.getHibernateTemplate().find(
				SQL_QUERY_FIND_BY_SESSION_ID, toolSessionId);
		if (list == null || list.isEmpty())
			return null;
		return (ScribeSession) list.get(0);
	}

	public void deleteBySessionID(Long toolSessionID) {
		ScribeSession session = getBySessionId(toolSessionID);
		if(session != null){
			this.getHibernateTemplate().delete(session);
			this.getHibernateTemplate().flush();
		}
	}
}
