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

package org.lamsfoundation.lams.tool.wiki.persistence;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class WikiMessageSeqDao extends HibernateDaoSupport {
	private static final String SQL_QUERY_FIND_TOPIC_THREAD = "from " + MessageSeq.class.getName() 
										+ " where root_message_uid = ?";
	private static final String SQL_QUERY_FIND_TOPIC_ID = "from " + MessageSeq.class.getName() 
						+ " where message_uid = ?";

	public List getTopicThread(Long rootTopicId) {
		return this.getHibernateTemplate().find(SQL_QUERY_FIND_TOPIC_THREAD,rootTopicId);
	}

	public MessageSeq getByTopicId(Long messageId) {
		List list =  this.getHibernateTemplate().find(SQL_QUERY_FIND_TOPIC_ID,messageId);
		if(list == null || list.isEmpty())
			return null;
		return (MessageSeq) list.get(0);
	}

	public void save(MessageSeq msgSeq) {
		this.getHibernateTemplate().save(msgSeq);
	}

	public void deleteByTopicId(Long topicUid) {
		MessageSeq seq = getByTopicId(topicUid);
		if(seq != null)
			this.getHibernateTemplate().delete(seq);
	}


}
