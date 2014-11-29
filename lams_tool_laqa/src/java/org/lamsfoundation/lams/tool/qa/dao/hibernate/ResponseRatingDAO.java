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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA 
 * 
 * http://www.gnu.org/licenses/gpl.txt 
 * **************************************************************** 
 */  
 
/* $Id$ */  
package org.lamsfoundation.lams.tool.qa.dao.hibernate;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.FlushMode;
import org.lamsfoundation.lams.dao.hibernate.LAMSBaseDAO;
import org.lamsfoundation.lams.tool.qa.ResponseRating;
import org.lamsfoundation.lams.tool.qa.dao.IResponseRatingDAO;
import org.lamsfoundation.lams.tool.qa.dto.AverageRatingDTO;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation of <code>ImageCommentDAO</code>.
 * 
 * @author Andrey Balan
 * @see org.lamsfoundation.lams.tool.qa.dao.IResponseRatingDAO
 */
@Repository
public class ResponseRatingDAO  extends LAMSBaseDAO implements IResponseRatingDAO {

    private static final String FIND_BY_RESPONSE_AND_USER = "from " + ResponseRating.class.getName()
	    + " as r where r.user.queUsrId = ? and r.response.responseId=?";

    private static final String FIND_BY_RESPONSE_ID = "from " + ResponseRating.class.getName()
	    + " as r where r.response.responseId=?";
    
    private static final String FIND_AVERAGE_RATING_BY_RESPONSE = "SELECT AVG(r.rating), COUNT(*) from "
	    + ResponseRating.class.getName() + " as r where r.response.responseId=?";
    
    private static final String FIND_AVERAGE_RATING_BY_QUESTION_AND_SESSION = "SELECT r.response.responseId, AVG(r.rating), COUNT(*) from "
	    + ResponseRating.class.getName() + " as r where r.response.qaQuestion.uid=? and r.response.qaQueUser.qaSession.qaSessionId=? group by r.response.responseId";

    private static final String FIND_AVERAGE_RATING_BY_USER_AND_CONTENT = "SELECT r.response.responseId, AVG(r.rating), COUNT(*) from "
	    + ResponseRating.class.getName() + " as r where r.response.qaQueUser.uid=? and r.response.qaQuestion.qaContent.qaContentId=? group by r.response.responseId";
    
    private static final String FIND_BY_USER_UID = "from " + ResponseRating.class.getName()
	    + " as r where r.user.uid = ?";

    public ResponseRating getRatingByResponseAndUser(Long responseId, Long userId) {
	List list = doFind(FIND_BY_RESPONSE_AND_USER, new Object[] { userId, responseId });
	if (list == null || list.size() == 0)
	    return null;
	return (ResponseRating) list.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<ResponseRating> getRatingsByResponse(Long responseId) {
	return (List<ResponseRating>) doFind(FIND_BY_RESPONSE_ID, responseId);
    }

    @SuppressWarnings("unchecked")
    public AverageRatingDTO getAverageRatingDTOByResponse(Long responseId) {
	List<Object[]> list = (List<Object[]>) doFind(FIND_AVERAGE_RATING_BY_RESPONSE, new Object[] { responseId });
	Object[] results = list.get(0);
	
	Object averageRatingObj = (results[0] == null) ? 0 : results[0];
	NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	numberFormat.setMaximumFractionDigits(1);	
	String averageRating = numberFormat.format(averageRatingObj);
	
	String numberOfVotes = (results[1] == null) ? "0" : String.valueOf(results[1]);
	return new AverageRatingDTO(averageRating, numberOfVotes);
    }
    
    public Map<Long, AverageRatingDTO> getAverageRatingDTOByQuestionAndSession(Long questionUid, Long qaSessionId) {
	List<Object[]> list = (List<Object[]>) doFind(FIND_AVERAGE_RATING_BY_QUESTION_AND_SESSION, new Object[] { questionUid, qaSessionId });
	
	Map<Long, AverageRatingDTO> mapResponseIdToAverageRating = new HashMap<Long, AverageRatingDTO>();
	for (Object[] results : list) {
	    if (results[0] == null) {
		return null;
	    }
	    
	    Long responseId = (Long) results[0];

	    Object averageRatingObj = results[1];
	    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	    numberFormat.setMaximumFractionDigits(1);
	    String averageRating = numberFormat.format(averageRatingObj);

	    String numberOfVotes = String.valueOf(results[2]);

	    mapResponseIdToAverageRating.put(responseId, new AverageRatingDTO(averageRating, numberOfVotes));

	}

	return mapResponseIdToAverageRating;

    }
    
    public Map<Long, AverageRatingDTO> getAverageRatingDTOByUserAndContentId(Long userUid, Long contentId) {
	List<Object[]> list = (List<Object[]>) doFind(FIND_AVERAGE_RATING_BY_USER_AND_CONTENT, new Object[] {userUid, contentId});
	
	Map<Long, AverageRatingDTO> mapResponseIdToAverageRating = new HashMap<Long, AverageRatingDTO>();
	for (Object[] results : list) {
	    if (results[0] == null) {
		return null;
	    }
	    
	    Long responseId = (Long) results[0];

	    Object averageRatingObj = results[1];
	    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
	    numberFormat.setMaximumFractionDigits(1);
	    String averageRating = numberFormat.format(averageRatingObj);

	    String numberOfVotes = String.valueOf(results[2]);

	    mapResponseIdToAverageRating.put(responseId, new AverageRatingDTO(averageRating, numberOfVotes));

	}

	return mapResponseIdToAverageRating;

    }

    public void saveObject(Object o) {
	super.insertOrUpdate(o);
    }
    
	public void removeResponseRating(ResponseRating rating) {
		getSessionFactory().getCurrentSession().setFlushMode(FlushMode.AUTO);
		getSession().delete(rating);
	}

	@SuppressWarnings("unchecked")
	public List<ResponseRating> getRatingsByUser(Long userUid) {
		return (List<ResponseRating>) doFind(FIND_BY_USER_UID, userUid);
	}
}