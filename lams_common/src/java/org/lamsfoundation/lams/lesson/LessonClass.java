/*
 * LessonClass.java
 *
 * Created on 14 January 2005, 10:56
 */

package org.lamsfoundation.lams.lesson;

import java.util.Set;

import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;

/**
 * A type of Grouping that represents all the Learners in a Lesson. The
 * LessonClass is used as the default Grouping.
 * 
 * @author chris
 */
public class LessonClass extends Grouping {
    
    private Group staffGroup;

	private Lesson lesson;

	/** Creates a new instance of LessonClass */
	public LessonClass() {
	}
	
	/** full constructor */
	public LessonClass(Long groupingId, Set groups,
			Set activities, Group staffGroup, Lesson lesson) {
	    //don't think lesson class need perform doGrouping. set grouper to null.
		super(groupingId, groups, activities,null);
		this.staffGroup = staffGroup;
		this.lesson = lesson;
	}

	public Group getStaffGroup() {
		return this.staffGroup;
	}

	public void setStaffGroup(Group staffGroup) {
		this.staffGroup = staffGroup;
	}

	public Lesson getLesson() {
		return lesson;
	}
	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}

    /**
     * @see org.lamsfoundation.lams.learningdesign.Grouping#isLearnerGroup(org.lamsfoundation.lams.learningdesign.Group)
     */
    public boolean isLearnerGroup(Group group)
    {
        if(group.getGroupId()==null||staffGroup.getGroupId()==null)
            throw new IllegalArgumentException("Can't check up whether group" +
            		" is learner group without group id.");
        
        return staffGroup.getGroupId()!=group.getGroupId();
    }

    /**
     * This method creates a deep copy of the LessonClass 
     * @return LessonClass The deep copied LessonClass object
     */
    public Grouping createCopy()
    {
    	LessonClass lessonClass = new LessonClass();
    	lessonClass.staffGroup = this.staffGroup;
    	lessonClass.lesson = this.lesson;
    	return lessonClass;
    }

}