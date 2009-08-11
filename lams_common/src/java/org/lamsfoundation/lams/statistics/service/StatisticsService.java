package org.lamsfoundation.lams.statistics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.lamsfoundation.lams.dao.IBaseDAO;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.LearningDesign;
import org.lamsfoundation.lams.lesson.CompletedActivityProgress;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.statistics.dto.GroupStatisticsDTO;
import org.lamsfoundation.lams.statistics.dto.StatisticsDTO;
import org.lamsfoundation.lams.usermanagement.Organisation;
import org.lamsfoundation.lams.usermanagement.OrganisationType;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;

public class StatisticsService implements IStatisticsService {

    private IBaseDAO baseDAO;
    private IUserManagementService userService;

    /**
     * Get the overall statistics for the server
     */
    public StatisticsDTO getOverallStatistics() {

	StatisticsDTO statisticsDTO = new StatisticsDTO();

	// Counting the organisations
	HashMap<String, Object> groupMap = new HashMap<String, Object>();
	groupMap.put("organisationType.organisationTypeId", OrganisationType.COURSE_TYPE);
	statisticsDTO.setGroups(baseDAO.countByProperties(Organisation.class, groupMap));

	// Counting the sub-organisations
	HashMap<String, Object> subGroupMap = new HashMap<String, Object>();
	subGroupMap.put("organisationType.organisationTypeId", OrganisationType.CLASS_TYPE);
	statisticsDTO.setSubGroups(baseDAO.countByProperties(Organisation.class, subGroupMap));

	// Getting the rest of the counts
	statisticsDTO.setActivities(baseDAO.countAll(Activity.class));
	statisticsDTO.setCompletedActivities(baseDAO.countAll(CompletedActivityProgress.class));
	statisticsDTO.setLessons(baseDAO.countAll(Lesson.class));
	statisticsDTO.setSequences(baseDAO.countAll(LearningDesign.class));
	statisticsDTO.setUsers(baseDAO.countAll(User.class));

	// Getting the stats for all the groups and sub-groups
	ArrayList<GroupStatisticsDTO> groupStatsList = new ArrayList<GroupStatisticsDTO>();
	List<Organisation> groups = (List<Organisation>) userService.findByProperty(Organisation.class, "organisationType.organisationTypeId",
		OrganisationType.COURSE_TYPE);
	if (groups != null) {
	    for (Organisation group : groups) {
		GroupStatisticsDTO groupStats = new GroupStatisticsDTO();
		groupStats.setName(group.getName());
		groupStats.setLessons(group.getLessons().size());
		groupStats.setTotalUsers(userService.getAllUsers(group.getOrganisationId()).size());
		groupStats.setAuthors(userService.getUsersFromOrganisationByRole(group.getOrganisationId(), Role.AUTHOR, false, false).size());
		groupStats.setMonitors(userService.getUsersFromOrganisationByRole(group.getOrganisationId(), Role.MONITOR, false, false).size());
		groupStats.setLearners(userService.getUsersFromOrganisationByRole(group.getOrganisationId(), Role.LEARNER, false, false).size());

		Set<Organisation> subGroups = (Set<Organisation>) group.getChildOrganisations();

		ArrayList<GroupStatisticsDTO> subGroupStatsList = new ArrayList<GroupStatisticsDTO>();
		if (subGroups != null) {	    
		    for (Organisation subGroup : subGroups) {
			GroupStatisticsDTO subGroupStats = new GroupStatisticsDTO();
			subGroupStats.setName(subGroup.getName());
			subGroupStats.setLessons(subGroup.getLessons().size());
			subGroupStats.setTotalUsers(userService.getAllUsers(subGroup.getOrganisationId()).size());
			subGroupStats.setAuthors(userService.getUsersFromOrganisationByRole(subGroup.getOrganisationId(), Role.AUTHOR, false, false).size());
			subGroupStats.setMonitors(userService.getUsersFromOrganisationByRole(subGroup.getOrganisationId(), Role.MONITOR, false, false).size());
			subGroupStats.setLearners(userService.getUsersFromOrganisationByRole(subGroup.getOrganisationId(), Role.LEARNER, false, false).size());
			subGroupStatsList.add(subGroupStats);
		    }
		}
		groupStats.setSubGroups(subGroupStatsList);

		groupStatsList.add(groupStats);
	    }
	}
	statisticsDTO.setGroupStatistics(groupStatsList);

	return statisticsDTO;

    }

    public void setBaseDAO(IBaseDAO baseDAO) {
	this.baseDAO = baseDAO;
    }

    public void setUserService(IUserManagementService userService) {
	this.userService = userService;
    }
}
