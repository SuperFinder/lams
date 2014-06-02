﻿/**
 * This file contains methods for Activity definition and manipulation on canvas.
 */

/**
 * Stores different Activity types structures.
 */
var ActivityDefs = {
		
	/**
	 * Either branching (start) or converge (end) point.
	 */
	BranchingEdgeActivity : function(id, uiid, x, y, title, branchingType, branchingActivity) {
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (branchingActivity) {
			// branchingActivity already exists, so this is the converge point
			this.isStart = false;
			branchingActivity.end = this;
		} else {
			// this is the branching point
			this.isStart = true;
			branchingActivity = new ActivityDefs.BranchingActivity(id, uiid, this);
			branchingActivity.branchingType = branchingType || 'chosen';
			branchingActivity.title = title || LABELS.DEFAULT_BRANCHING_TITLE;
		}
		this.branchingActivity = branchingActivity;
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.branchingProperties;
		}
		
		this.draw = ActivityDraw.branching;
		this.draw(x, y);
	},
	
	
	/**
	 * Represents a set of branches. It is not displayed on canvas, but holds all the vital data.
	 */
	BranchingActivity : function(id, uiid, branchingEdgeStart) {
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.start = branchingEdgeStart;
		this.branches = [];
		// mapping between groups and branches, if applicable
		this.groupsToBranches = [];
		// mapping between tool output and branches, if applicable
		this.conditionsToBranches = [];
		
		this.minOptions = 0;
		this.maxOptions = 0;
	},
	
	
	/**
	 * Represents a subsequence of activities. It is not displayed on canvas, but is the parent activity for its children.
	 */
	BranchActivity : function(id, uiid, title, branchingActivity, transitionFrom, defaultBranch) {
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.title = title || (LABELS.DEFAULT_BRANCH_PREFIX + (branchingActivity.branches.length + 1));
		this.branchingActivity = branchingActivity;
		this.transitionFrom = transitionFrom;
		if (defaultBranch) {
			this.defaultBranch = true;
			// there can be only one default branch
			$.each(branchingActivity.branches, function(){
				this.defaultBranch = false;
			});
		}
	},
	
	
	/**
	 * Constructor for a Floating Activity.
	 */
	FloatingActivity : function(id, uiid, x, y) {
		DecorationDefs.Container.call(this, id, uiid, LABELS.SUPPORT_ACTIVITY_TITLE);
		
		this.draw = ActivityDraw.floatingActivity;
		this.draw(x, y);
		
		// there can only be one Floating Activity container
		layout.floatingActivity = this;
	},
	
	
	/**
	 * Constructor for a Gate Activity.
	 */
	GateActivity : function(id, uiid, x, y, title, description, gateType, startTimeOffset, gateActivityCompletionBased) {
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.title = title;
		this.description = description;
		this.gateType = gateType || 'permission';
		if (gateType == 'schedule') {
			var day = 24*60;
			this.offsetDay = Math.floor(startTimeOffset/day);
			startTimeOffset -= this.offsetDay * day;
			this.offsetHour = Math.floor(startTimeOffset/60);
			this.offsetMinute = startTimeOffset - this.offsetHour * 60;
			
			this.gateActivityCompletionBased = gateActivityCompletionBased;
		};
		// mapping between tool output and gate states ("branches"), if applicable
		this.conditionsToBranches = [];
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.gateProperties;
		}
		
		this.draw = ActivityDraw.gate;
		this.draw(x, y);
	},
	
	
	/**
	 * Constructor for a Grouping Activity.
	 */
	GroupingActivity : function(id, uiid, x, y, title, groupingID, groupingUIID, groupingType, groupDivide,
								groupCount, learnerCount, equalSizes, viewLearners, groups) {
		this.id = +id || null;
		this.groupingID = +groupingID || null;
		this.groupingUIID = +groupingUIID  || ++layout.ld.maxUIID;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.title = title || LABELS.DEFAULT_GROUPING_TITLE;
		this.groupingType = groupingType || 'random';
		this.groupDivide = groupDivide || 'groups';
		this.groupCount = +groupCount || layout.conf.defaultGroupingGroupCount;
		this.learnerCount = +learnerCount || layout.conf.defaultGroupingLearnerCount;
		this.equalSizes = equalSizes || false;
		this.viewLearners = viewLearners || false;
		// either groups are already defined or create them with default names
		this.groups = groups || PropertyLib.fillNameAndUIIDList(this.groupCount, [], 'name', LABELS.DEFAULT_GROUP_PREFIX);
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.groupingProperties;
		}
		this.draw = ActivityDraw.grouping;
		this.draw(x, y);
	},
	
	
	/**
	 * Constructor for an Optional Activity.
	 */
	OptionalActivity : function(id, uiid, x, y, title, minOptions, maxOptions) {
		DecorationDefs.Container.call(this, id, uiid, title || LABELS.DEFAULT_OPTIONAL_ACTIVITY_TITLE);
		
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.minOptions = minOptions || 0;
		this.maxOptions = maxOptions || 0;
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.optionalActivityProperties;
		}
		this.draw = ActivityDraw.optionalActivity;
		this.draw(x, y);
	},
	
	
	/**
	 * Constructor for a Parallel (double) Activity
	 */
	ParallelActivity : function(id, uiid, learningLibraryID, x, y, title, childActivityDefs){
		DecorationDefs.Container.call(this, id, uiid, title);
		
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.learningLibraryID = +learningLibraryID;
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		if (childActivityDefs){
			this.childActivityDefs = childActivityDefs;
		}
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.parallelProperties;
		}
		
		this.draw = ActivityDraw.parallelActivity;
		this.draw(x, y);
	},
	
	
	/**
	 * Constructor for a Tool Activity.
	 */
	ToolActivity : function(id, uiid, toolContentID, toolID, learningLibraryID, authorURL, x, y, title) {
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.toolContentID = toolContentID;
		this.toolID = +toolID;
		this.learningLibraryID = +learningLibraryID;
		this.authorURL = authorURL;
		this.title = title;
		this.transitions = {
			'from' : [],
			'to'   : []
		};
		
		if (!isReadOnlyMode){
			this.loadPropertiesDialogContent = PropertyDefs.toolProperties;
		}
		
		this.draw = ActivityDraw.tool;
		this.draw(x, y);
	},
	
	
	/**
	 * Constructor for a Transition
	 */
	Transition : function(id, uiid, fromActivity, toActivity, title) {
		this.id = +id || null;
		this.uiid = +uiid || ++layout.ld.maxUIID;
		this.fromActivity = fromActivity;
		this.toActivity = toActivity;
		if (title) {
			// only branches have titles
			this.title = title;
			
			if (!isReadOnlyMode){
				this.loadPropertiesDialogContent = PropertyDefs.transitionProperties;
			}
		}
		
		this.draw = ActivityDraw.transition;
		this.draw();
		
		// set up references in edge activities
		fromActivity.transitions.from.push(this);
		toActivity.transitions.to.push(this);
	}
},


/**
 * Mehtods for drawing various kinds of activities.
 * They are not defined in constructors so there is a static reference, 
 * not a separate definition for each object instance.
 */
ActivityDraw = {
	
	/**
	 * Draws a Branching Activity
	 */
	branching : function(x, y) {
		if (x == undefined || y == undefined) {
			// just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}
		
		if (this.items) {
			this.items.remove();
		}
		
		// create activity SVG elements
		paper.setStart();
		var shape = paper.path(Raphael.format('M {0} {1} a 8 8 0 1 0 16 0 a 8 8 0 1 0 -16 0', x, y + 8))
						 .attr({
							 'fill' : this.isStart ? layout.colors.branchingEdgeStart
						                           : layout.colors.branchingEdgeEnd
						 });
		
		var title = this.branchingActivity.title;
		paper.text(x + 8, y + 27,  title + '\n' + (this.isStart ? LABELS.BRANCHING_START_SUFFIX
	                                        		 	 		: LABELS.BRANCHING_END_SUFFIX))
		     .attr(layout.defaultTextAttributes);
		
		this.items = paper.setFinish();
		this.items.shape = shape;
		
		ActivityLib.activityHandlersInit(this);
	},
	
	
	/**
	 * Draws a Floating (support) Activity container
	 */
	floatingActivity : function(x, y, ignoredParam1, ignoredParam2, childActivityDefs) {
		if (x == undefined || y == undefined) {
			// if no new coordinates are given, just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}
		
		// either check what children are on canvas or use the priovided parameter
		if (childActivityDefs) {
			this.childActivityDefs = childActivityDefs;
		}
		
		if (this.childActivityDefs && this.childActivityDefs.length > 0) {
			// draw one by one, horizontally
			var activityX = x + layout.conf.containerActivityPadding,
				allElements = paper.set(),
				floatingActivity = this,
				box = this.items.shape.getBBox();
			$.each(this.childActivityDefs, function(orderID){
				this.parentActivity = floatingActivity;
				this.orderID = orderID;
				var childBox = this.items.shape.getBBox();
				this.draw(activityX, y + Math.max(layout.conf.containerActivityPadding + 10, (box.height - childBox.height)/2));
				childBox = this.items.shape.getBBox();
				activityX = childBox.x2 + layout.conf.containerActivityChildrenPadding;
				allElements.push(this.items.shape);
			});
			// area containing all drawn child activities
			box = allElements.getBBox();
			
			this.drawContainer(x, y,
							   box.x2 + layout.conf.containerActivityPadding,
							   box.y2 + layout.conf.containerActivityPadding,
							   layout.colors.optionalActivity);
		} else {
			this.drawContainer(x, y,
							   x + layout.conf.containerActivityEmptyWidth,
							   y + layout.conf.containerActivityEmptyHeight,
							   layout.colors.optionalActivity);
		}
		
		this.items.data('parentObject', this);
	},
	
	
	/**
	 * Draws a Gate activity
	 */
	gate : function(x, y) {
		if (x == undefined || y == undefined) {
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}

		if (this.items) {
			this.items.remove();
		}
		
		// create activity SVG elements
		paper.setStart();
		var shape = paper.path(Raphael.format('M {0} {1} l-8 8 v14 l8 8 h14 l8 -8 v-14 l-8 -8 z', x + 8, y))
						 .attr({
							 'fill' : layout.colors.gate
						 });
		
		paper.text(x + 15, y + 14, LABELS.GATE_ACTIVITY_LABEL)
			 .attr(layout.defaultTextAttributes)
		     .attr('stroke', layout.colors.gateText);
		
		this.items = paper.setFinish();
		this.items.shape = shape;
		
		ActivityLib.activityHandlersInit(this);
	},
	
	
	/**
	 * Draws a Gropuing activity
	 */
	grouping : function(x, y) {
		if (x == undefined || y == undefined) {
			// just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}

		if (this.items) {
			this.items.remove();
		}
		
		// create activity SVG elements
		paper.setStart();
		var shape = paper.path(Raphael.format('M {0} {1} h 125 v 50 h -125 z', x, y))
						 .attr({
								'fill' : layout.colors.grouping
							 });
		
		paper.image('../images/grouping.gif', x + 47, y + 2, 30, 30);
		paper.text(x + 62, y + 40, ActivityLib.shortenActivityTitle(this.title));
		
		this.items = paper.setFinish();
		this.items.shape = shape;
		
		ActivityLib.activityHandlersInit(this);
	},
	
	
	/**
	 * Draws an Optional Activity container
	 */
	optionalActivity : function(x, y, ignoredParam1, ignoredParam2, childActivityDefs) {
		if (x == undefined || y == undefined) {
			// if no new coordinates are given, just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}
		
		// either check what children are on canvas or use the priovided parameter
		if (childActivityDefs) {
			this.childActivityDefs = childActivityDefs;
		}
		
		if (this.childActivityDefs && this.childActivityDefs.length > 0) {
			// draw one by one, vertically
			var activityY = y + containerActivityPadding + 10,
				allElements = paper.set(),
				optionalActivity = this,
				box = this.items.shape.getBBox();
			$.each(this.childActivityDefs, function(orderID){
				this.parentActivity = optionalActivity;
				this.orderID = orderID + 1;
				var childBox = this.items.shape.getBBox();
				this.draw(x + Math.max(containerActivityPadding, (box.width - childBox.width)/2), activityY);
				childBox = this.items.shape.getBBox();
				activityY = childBox.y2 + containerActivityChildrenPadding;
				allElements.push(this.items.shape);
			});
			// area containing all drawn child activities
			box = allElements.getBBox();
			
			this.drawContainer(x, y,
							  box.x2 + layout.conf.containerActivityPadding,
							  box.y2 + layout.conf.containerActivityPadding,
							  layout.colors.optionalActivity);
		} else {
			this.drawContainer(x, y,
							   x + containerActivityEmptyWidth,
							   y + containerActivityEmptyHeight,
							   layout.colors.optionalActivity);
		}
		
		if (!isReadOnlyMode){
			// allow transition drawing and other activity behaviour
			this.items.shape.unmousedown().mousedown(HandlerActivityLib.activityMousedownHandler);
		}
		
		this.items.data('parentObject', this);
	},
	
	
	/**
	 * Draws a Parallel (double) Activity container
	 */
	parallelActivity : function(x, y) {
		if (x == undefined || y == undefined) {
			// if no new coordinates are given, just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}
		
		if (this.childActivityDefs && this.childActivityDefs.length > 0) {
			// draw one by one, vertically
			var activityY = y + layout.conf.containerActivityPadding + 10,
				allElements = paper.set(),
				optionalActivity = this;
			$.each(this.childActivityDefs, function(orderID){
				this.parentActivity = optionalActivity;
				this.orderID = orderID + 1;
				this.draw(x + layout.conf.containerActivityPadding, activityY);
				activityY = this.items.shape.getBBox().y2 + layout.conf.containerActivityChildrenPadding;
				allElements.push(this.items.shape);
			});
			// area containing all drawn child activities
			var box = allElements.getBBox();
			
			this.drawContainer(x, y,
							  box.x2 + layout.conf.containerActivityPadding,
							  box.y2 + layout.conf.containerActivityPadding,
							  layout.colors.optionalActivity);
		} else {
			this.drawContainer(x, y,
							   x + layout.conf.containerActivityEmptyWidth,
							   y + layout.conf.containerActivityEmptyHeight,
							   layout.colors.optionalActivity);
		}
		
		if (!isReadOnlyMode){
			// allow transition drawing and other activity behaviour
			this.items.shape.unmousedown().mousedown(HandlerActivityLib.activityMousedownHandler);
		}
		
		this.items.data('parentObject', this);
	},
	
	
	/**
	 * Draws a Tool activity
	 */
	tool : function(x, y) {
		if (x == undefined || y == undefined) {
			// if no new coordinates are given, just redraw the activity
			x = this.items.shape.getBBox().x;
			y = this.items.shape.getBBox().y;
		}
		
		if (this.items) {
			this.items.remove();
		}
		
		// create activity SVG elements
		this.items = paper.set();
		var shape = paper.path(Raphael.format('M {0} {1} h 125 v 50 h -125 z', x, y))
						 // activity colour depends on its category ID
						 .attr({
							'fill' : layout.colors.activity[layout.toolMetadata[this.learningLibraryID].activityCategoryID]
						 });

		this.items.shape = shape;
		this.items.push(shape);
		
		if (this.grouping) {
			ActivityLib.addGroupingEffect(this);
		}
		
		// check for icon in the library
		this.items.push(paper.image(layout.toolMetadata[this.learningLibraryID].iconPath, x + 47, y + 2, 30, 30));
		this.items.push(paper.text(x + 62, y + 40, ActivityLib.shortenActivityTitle(this.title))
							 .attr(layout.defaultTextAttributes)
							 .attr('fill', layout.colors.activityText));
		
		ActivityLib.activityHandlersInit(this);
	},
	
	
	/**
	 * Draws a Transition
	 */
	transition : function() {
		// clear previous canvas elements
		if (this.items) {
			this.items.remove();
		}
		
		// calculate middle points of each activity
		var points = ActivityLib.findTransitionPoints(this.fromActivity, this.toActivity);
		
		// create transition SVG elements
		paper.setStart();
		paper.path(Raphael.format('M {0} {1} L {2} {3}', points.startX, points.startY, points.endX, points.endY))
		                  .attr({
		                 	'stroke'       : layout.colors.transition,
		                	'stroke-width' : 2
		                  });

		// draw the arrow and turn it in the same direction as the line
		var angle = 90 + Math.atan2(points.endY - points.startY, points.endX - points.startX) * 180 / Math.PI,
			arrowPath = Raphael.transformPath(Raphael.format('M {0} {1} l 10 15 a 25 25 0 0 0 -20 0 z',
															 points.middleX, points.middleY), 
				                              Raphael.format('R {0} {1} {2}', angle, points.middleX, points.middleY));
		paper.path(arrowPath)
						 .attr({
							'stroke' : layout.colors.transition,
							'fill'   : layout.colors.transition
						 });
		if (this.title) {
			// adjust X & Y depending on the angle, so the label does not overlap with the transition;
			// angle in Javascript is -90 <= a <= 270
			paper.text(points.middleX + ((angle > -45 && angle < 45) || (angle > 135 && angle < 225) ? 20 : 0),
					   points.middleY + ((angle > 45 && angle < 135) || angle > 225 || angle < 45 ? -20 : 0),
					   this.title)
				 .attr('text-anchor', 'start');
		}
		this.items = paper.setFinish();

		this.items.toBack();
		this.items.data('parentObject', this);
		
		if (!isReadOnlyMode){
			this.items.attr('cursor', 'pointer');
			this.items.mousedown(HandlerTransitionLib.transitionMousedownHandler);
			this.items.click(HandlerLib.itemClickHandler);
		}
	}
},



/**
 * Contains utility methods for Activity manipulation.
 */
ActivityLib = {
		
	/**
	 * Make a new activity fully functional on canvas.
	 */
	activityHandlersInit : function(activity) {
		activity.items.data('parentObject', activity);
		
		if (!isReadOnlyMode) {
			// set all the handlers
			activity.items.mousedown(HandlerActivityLib.activityMousedownHandler)
						  .click(HandlerLib.itemClickHandler)
						  .dblclick(HandlerActivityLib.activityDblclickHandler)
						  .attr({
							  'cursor' : 'pointer'
						  });
			
			if (activity instanceof ActivityDefs.BranchingEdgeActivity
					&& activity.branchingActivity.end) {
				// highligh branching edges on hover
				activity.branchingActivity.start.items.hover(HandlerActivityLib.branchingEdgeMouseoverHandler,
						HandlerActivityLib.branchingEdgeMouseoutHandler);
				activity.branchingActivity.end.items.hover(HandlerActivityLib.branchingEdgeMouseoverHandler,
						HandlerActivityLib.branchingEdgeMouseoutHandler);
			}
		}
	},

	
	
	/**
	 * Adds branching activity when user draws an extra outbout transition from.
	 */
	addBranching : function(fromActivity, toActivity1) {
		// find the other toActivity
		var existingTransition = fromActivity.transitions.from[0],
			toActivity2 = existingTransition.toActivity,
			branchingEdgeStart = null,
			branchingEdgeEnd = null,
			convergeActivity1 = toActivity1,
		    convergeActivity2 = toActivity2;
		// find converge activity of the new branch
		while (convergeActivity1.transitions.from.length > 0) {
			convergeActivity1 = convergeActivity1.transitions.from[0].toActivity;
		};
		
		if (toActivity2 instanceof ActivityDefs.BranchingEdgeActivity && toActivity2.isStart) {
			// there is already a branching activity, reuse existing items
			branchingEdgeStart = toActivity2;
			branchingEdgeEnd = toActivity2.branchingActivity.end;
		} else {
			// add new branching
			ActivityLib.removeTransition(existingTransition);
			
			// calculate position of branching point
			var branchPoints1 = ActivityLib.findTransitionPoints(fromActivity, toActivity1),
			    branchPoints2 = ActivityLib.findTransitionPoints(fromActivity, toActivity2),
			    branchEdgeStartX = branchPoints1.middleX + (branchPoints2.middleX - branchPoints1.middleX)/2,
			    branchEdgeStartY = branchPoints1.middleY + (branchPoints2.middleY - branchPoints1.middleY)/2,
			    branchingEdgeStart = new ActivityDefs.BranchingEdgeActivity(null, null, branchEdgeStartX,
			    		branchEdgeStartY, null, null, null);
			layout.activities.push(branchingEdgeStart);
			
			// find last activities in subsequences and make an converge point between them
			while (convergeActivity2.transitions.from.length > 0) {
				convergeActivity2 = convergeActivity2.transitions.from[0].toActivity;
			};

			var convergePoints = ActivityLib.findTransitionPoints(convergeActivity1, convergeActivity2),
				branchingEdgeEnd = new ActivityDefs.BranchingEdgeActivity(null, null, convergePoints.middleX,
					convergePoints.middleY, null, null, branchingEdgeStart.branchingActivity);
			layout.activities.push(branchingEdgeEnd);
			
			// draw all required transitions
			ActivityLib.addTransition(fromActivity, branchingEdgeStart);
			ActivityLib.addTransition(branchingEdgeStart, toActivity2);
			ActivityLib.addTransition(convergeActivity2, branchingEdgeEnd);
		}

		ActivityLib.addTransition(branchingEdgeStart, toActivity1);
		ActivityLib.addTransition(convergeActivity1, branchingEdgeEnd);
		GeneralLib.setModified(true);
	},
	

	
	/**
	 * Adds visual grouping effect on an activity.
	 */
	addGroupingEffect : function(activity) {
		// do not draw twice if it already exists
		if (!activity.items.groupingEffect) {
			var shape = activity.items.shape,
				activityBox = shape.getBBox();
			
			activity.items.groupingEffect = paper.rect(
					activityBox.x + layout.conf.groupingEffectPadding,
					activityBox.y + layout.conf.groupingEffectPadding,
					activityBox.width,
					activityBox.height)
				.attr({
					'fill' : shape.attr('fill')
				});
			
			shape.toFront();
			activity.items.push(activity.items.groupingEffect);
			
			// this is needed, for some reason, otherwise the activity can not be selected
			HandlerLib.resetCanvasMode(true);
		}
	},
	
	
	/**
	 * Adds visual select effect around an activity.
	 */
	addSelectEffect : function (object, markSelected) {
		// do not draw twice
		if (!object.items.selectEffect) {
			// different effects for different types of objects
			if (object instanceof DecorationDefs.Region) {
				object.items.shape.attr({
					'stroke'           : layout.colors.selectEffect,
					'stroke-dasharray' : '-'
				});
				object.items.resizeButton.show();
				object.items.resizeButton.toFront();
				object.items.selectEffect = true;
				
				// also select encapsulated activities
				var childActivityDefs = DecorationLib.getChildActivityDefs(object.items.shape);
				if (childActivityDefs.length > 0) {
					object.items.fitButton.show();
					
					$.each(childActivityDefs, function(){
						if (!this.parentActivity || !(this.parentActivity instanceof DecorationDefs.Container)) {
							ActivityLib.addSelectEffect(this, false);
						}
					});
				}
			} else if (object instanceof ActivityDefs.Transition) {
				// show only if Transition is selectable, i.e. is a branch, has a title
				if (object.loadPropertiesDialogContent) {
					object.items.attr({
						'stroke' : layout.colors.selectEffect,
						'fill'   : layout.colors.selectEffect
					 });
					
					object.items.selectEffect = true;
				}
			} else {
				// this goes for ActivityDefs and Labels
				var box = object.items.getBBox();
				
				// a simple rectange a bit wider than the actual activity boundaries
				object.items.selectEffect = paper.rect(
						box.x - layout.conf.selectEffectPadding,
						box.y - layout.conf.selectEffectPadding,
						box.width + 2*layout.conf.selectEffectPadding,
						box.height + 2*layout.conf.selectEffectPadding)
					.attr({
						'stroke'           : layout.colors.selectEffect,
						'stroke-dasharray' : '-'
					});
				object.items.push(object.items.selectEffect);
			}
			
			// make it officially marked?
			if (markSelected && object.items.selectEffect){
				layout.selectedObject = object;
				// show the properties dialog for the selected object
				if (object.loadPropertiesDialogContent) {
					PropertyLib.openPropertiesDialog(object);
				}
			}
		}
	},
	
	
	/**
	 * Draws a transition between two activities.
	 */
	addTransition : function(fromActivity, toActivity, redraw, id, uiid, branchData) {
		// if a child activity was detected, use the parent activity as the target
		if (toActivity.parentActivity && toActivity.parentActivity instanceof DecorationDefs.Container){
			toActivity = toActivity.parentActivity;
		}
		if (fromActivity.parentActivity && fromActivity.parentActivity instanceof DecorationDefs.Container){
			fromActivity = fromActivity.parentActivity;
		}
		// no transitions to/from support activities
		if (toActivity instanceof ActivityDefs.FloatingActivity
			|| fromActivity instanceof ActivityDefs.FloatingActivity){
			return;
		}
		
		// only converge points are allowed to have few inbound transitions
		if (!redraw
				&& toActivity.transitions.to.length > 0
				&& !(toActivity instanceof ActivityDefs.BranchingEdgeActivity && !toActivity.isStart)) {
			alert(LABELS.TRANSITION_TO_EXISTS_ERROR);
			return;
		}

		// user chose to create outbound transition from an activity that already has one
		if (!redraw
				&& fromActivity.transitions.from.length > 0
				&& !(fromActivity instanceof ActivityDefs.BranchingEdgeActivity && fromActivity.isStart)
				&& !(toActivity instanceof ActivityDefs.BranchingEdgeActivity  && toActivity.isStart)) {
			if (confirm(LABELS.BRANCHING_CREATE_CONFIRM)) {
				ActivityLib.addBranching(fromActivity, toActivity);
			}
			return;
		}
		
		// branchData can be either an existing branch or a title for the new branch
		var branch = branchData && branchData instanceof ActivityDefs.BranchActivity ? branchData : null,
			transition = null;
		// remove the existing transition
		$.each(fromActivity.transitions.from, function(index) {
			if (this.toActivity == toActivity) {
				id = this.id;
				uiid = this.uiid;
				transition = this;
				if (!branch){
					branch = this.branch;
				}

				return false;
			}
		});
		
		if (!branch && fromActivity instanceof ActivityDefs.BranchingEdgeActivity && fromActivity.isStart) {
			// if a title was provided, try to find the branch based on this information
			$.each(fromActivity.branchingActivity.branches, function(){
				if (branchData == this.title) {
					branch = this;
					return false;
				}
			});
			if (!branch) {
				// create a new branch
				branch = new ActivityDefs.BranchActivity(null, null, branchData, fromActivity.branchingActivity, false);
			}
		}
		
		
		if (transition) {
			ActivityLib.removeTransition(transition, redraw);
		}
		
		// finally add the new transition
		transition = new ActivityDefs.Transition(id, uiid, fromActivity, toActivity,
						 branch ? branch.title : null);

		if (branch) {
			// set the corresponding branch (again)
			branch.transitionFrom = transition;
			transition.branch = branch;
			fromActivity.branchingActivity.branches.push(branch);
			if (fromActivity.branchingActivity.branches.length == 1) {
				branch.defaultBranch = true;
			}
		}
		
		GeneralLib.setModified(true);
		return transition;
	},

	
	/**
	 * Calculates start, middle and end points of a line between two activities. 
	 */
	findTransitionPoints : function(fromActivity, toActivity) {
		var fromActivityBox = fromActivity.items.shape.getBBox(),
			toActivityBox = toActivity.items.shape.getBBox(),
			
			// find points in the middle of each activity
			points = {
				'startX'  : fromActivityBox.x + fromActivityBox.width / 2,
				'startY'  : fromActivityBox.y + fromActivityBox.height / 2,
				'endX'    : toActivityBox.x + toActivityBox.width / 2,
				'endY'    : toActivityBox.y + toActivityBox.height / 2
			},

			// find intersection points of the temporary transition
			tempTransition = Raphael.parsePathString(Raphael.format(
				'M {0} {1} L {2} {3}', points.startX, points.startY, points.endX, points.endY)),
			fromIntersect = Raphael.pathIntersection(tempTransition, fromActivity.items.shape.attr('path')),
			toIntersect = Raphael.pathIntersection(tempTransition, toActivity.items.shape.attr('path'));
		
		// find points on borders of activities, if they exist
		if (fromIntersect.length > 0) {
			points.startX = fromIntersect[0].x;
			points.startY = fromIntersect[0].y;
		}
		if (toIntersect.length > 0) {
			points.endX = toIntersect[0].x;
			points.endY = toIntersect[0].y;
		}
		// middle point of the transition
		points.middleX = points.startX + (points.endX - points.startX)/2;
		points.middleY = points.startY + (points.endY - points.startY)/2;
		
		return points;
	},
	
	
	/**
	 * Drop the dragged activity on the canvas.
	 */
	dropActivity : function(activity, x, y) {
		if (!(activity instanceof ActivityDefs.OptionalActivity || activity instanceof ActivityDefs.FloatingActivity)) {
			// check if it was removed from an Optional or Floating Activity
			if (activity.parentActivity && activity.parentActivity instanceof DecorationDefs.Container) {
				var childActivityDefs = DecorationLib.getChildActivityDefs(activity.parentActivity.items.shape);
				if ($.inArray(activity, childActivityDefs) == -1) {
					activity.parentActivity.draw();
					ActivityLib.redrawTransitions(activity.parentActivity);
					activity.parentActivity = null;
				}
			}
			
			// check if it was added to an Optional or Floating Activity
			var container = layout.floatingActivity
							&& Raphael.isPointInsideBBox(layout.floatingActivity.items.getBBox(),x,y)
							? layout.floatingActivity : null;
			if (!container) {
				$.each(layout.activities, function(){
					if (this instanceof ActivityDefs.OptionalActivity
						&& Raphael.isPointInsideBBox(this.items.getBBox(),x,y)) {
						container = this;
						return false;
					}
				});
			}
			if (container) {
				if ($.inArray(activity, container.childActivityDefs) == -1) {
					$.each(activity.transitions.from, function(){
						ActivityLib.removeTransition(this);
					});
					$.each(activity.transitions.to, function(){
						ActivityLib.removeTransition(this);
					});
	
					// for properties dialog to reload
					ActivityLib.removeSelectEffect(container);
					
					container.childActivityDefs.push(activity);
					container.draw(null, null, null, null, childActivityDefs);
					ActivityLib.redrawTransitions(container);
				}
			}
		}
		
		ActivityLib.redrawTransitions(activity);
		
		$.each(layout.regions, function(){
			// redraw all annotation regions so they are pushed to back
			this.draw();
		});
		
		GeneralLib.setModified(true);
	},
	

	/**
	 * Open separate window with activity authoring on double click.
	 */
	openActivityAuthoring : function(activity){
		// fetch authoring URL for a Tool Activity
		if (!activity.authorURL && activity.toolID) {
			$.ajax({
				async : false,
				cache : false,
				url : LAMS_URL + "authoring/author.do",
				dataType : 'json',
				data : {
					'method'          : 'createToolContent',
					'toolID'          : activity.toolID,
					'contentFolderID' : layout.ld.contentFolderID
				},
				success : function(response) {
					activity.authorURL = response.authorURL;
					activity.toolContentID = response.toolContentID;
					if (!layout.ld.contentFolderID) {
						// if LD did not have contentFolderID, it was just generated
						// so remember it
						layout.ld.contentFolderID = response.contentFolderID;
					}
				}
			});
		}
		
		if (activity.authorURL) {
			window.open(activity.authorURL, 'activityAuthoring' + activity.id,
					"HEIGHT=800,WIDTH=1024,resizable=yes,scrollbars=yes,status=false," +
					"menubar=no,toolbar=no");
			GeneralLib.setModified(true);
		}
	},
	
	
	/**
	 * Draw each of activity's inboud and outbound transitions again.
	 */
	redrawTransitions : function(activity) {
		if (activity.transitions) {
			$.each(activity.transitions.from.slice(), function(){
				ActivityLib.addTransition(activity, this.toActivity, true);
			});
			$.each(activity.transitions.to.slice(), function(){
				ActivityLib.addTransition(this.fromActivity, activity, true);
			});
		}
	},
	
	
	/**
	 * Deletes the given activity.
	 */
	removeActivity : function(activity, forceRemove) {
		var coreActivity =  activity.branchingActivity || this;
		if (!forceRemove && activity instanceof ActivityDefs.BranchingEdgeActivity){
			// user removes one of the branching edges, so remove the whole activity
			if (confirm(LABELS.REMOVE_ACTIVITY_CONFIRM)){
				var otherEdge = activity.isStart ? coreActivity.end
						                         : coreActivity.start;
				ActivityLib.removeActivity(otherEdge, true);
			} else {
				return;
			}
		}
		
		if (activity instanceof ActivityDefs.FloatingActivity) {
			layout.floatingActivity = null;
			// re-enable the button, as the only possible Floating Activity is gone now
			$('#floatingActivityButton').attr('disabled', null)
									 	.css('opacity', 1);
		} else {
			// remove the transitions
			// need to use slice() to copy the array as it gets modified in removeTransition()
			$.each(activity.transitions.from.slice(), function() {
				ActivityLib.removeTransition(this);
			});
			$.each(activity.transitions.to.slice(), function() {
				ActivityLib.removeTransition(this);
			});
			
			// remove the activity from reference tables
			layout.activities.splice(layout.activities.indexOf(activity), 1);
			if (layout.copiedActivity = activity) {
				layout.copiedActivity = null;
			}

			// find references of this activity as grouping or input
			$.each(layout.activities, function(){
				if (activity == coreActivity.grouping) {
					coreActivity.grouping = null;
					this.draw();
				} else if (activity == coreActivity.input) {
					coreActivity.input = null;
				}
			});
		}
		
		// remove the activity from parent activity
		if (activity.parentActivity && activity.parentActivity instanceof DecorationDefs.Container) {
			activity.parentActivity.childActivityDefs.splice(activity.parentActivity.childActivityDefs.indexOf(activity), 1);
		}
		
		// remove child activities
		if (activity instanceof DecorationDefs.Container) {
			$.each(activity.childActivityDefs, function(){
				ActivityLib.removeActivity(this);
			});
		}
		
		// visually remove the activity
		activity.items.remove();
	},
	
	
	/**
	 * Deselects an activity/transition/annotation
	 */
	removeSelectEffect : function(object) {
		// remove the effect from the given object or the selected one, whatever it is
		if (!object) {
			object = layout.selectedObject;
		}
		
		if (object) {
			if (object.items.selectEffect) {
				// different effects for different types of objects
				if (object instanceof DecorationDefs.Region) {
					object.items.shape.attr({
						'stroke'           : 'black',
						'stroke-dasharray' : ''
					});
					object.items.fitButton.hide();
					object.items.resizeButton.hide();
					
					var childActivityDefs = DecorationLib.getChildActivityDefs(object.items.shape);
					$.each(childActivityDefs, function(){
						ActivityLib.removeSelectEffect(this);
					});
				} else if (object instanceof ActivityDefs.Transition) {
					// just redraw the transition, it's easier
					object.draw();
				} else {
					object.items.selectEffect.remove();
				}
				object.items.selectEffect = null;
			}
			
			// no selected activity = no properties dialog
			layout.propertiesDialog.dialog('close');
			layout.selectedObject = null;
		}
	},
	
	
	/**
	 * Removes the given transition.
	 */
	removeTransition : function(transition, redraw) {
		// find the transition and remove it
		var transitions = transition.fromActivity.transitions.from;
		transitions.splice(transitions.indexOf(transition), 1);
		transitions = transition.toActivity.transitions.to;
		transitions.splice(transitions.indexOf(transition), 1);
		
		if (transition.branch) {
			// remove corresponding branch
			var branches = transition.branch.branchingActivity.branches;
			branches.splice(branches.indexOf(transition.branch), 1);
			
			if (transition.branch.defaultBranch && branches.length > 0) {
				// reset the first branch as the default one
				branches[0].defaultBranch = true;
			}
		}
		
		// redraw means that the transition will be drawn again in just a moment
		// so do not do any structural changes
		if (!redraw){
			// remove grouping or input references if chain was broken by the removed transition
			$.each(layout.activities, function(){
				var coreActivity =  this.branchingActivity || this;
				if (coreActivity.grouping || coreActivity.input) {
					var candidate = this.branchingActivity ? coreActivity.start : this,
						groupingFound = false,
						inputFound = false;
					do {
						if (candidate.transitions && candidate.transitions.to.length > 0) {
							candidate = candidate.transitions.to[0].fromActivity;
						} else if (candidate.branchingActivity && !candidate.isStart) {
							candidate = candidate.branchingActivity.start;
						}  else if (!candidate.branchingActivity && candidate.parentActivity) {
							candidate = candidate.parentActivity;
						} else {
							candidate = null;
						}
						
						if (coreActivity.grouping == candidate) {
							groupingFound = true;
						}
						if (coreActivity.input == candidate) {
							inputFound = true;
						}
					} while (candidate != null);
					
					if (!groupingFound) {
						coreActivity.grouping = null;
						this.draw();
					}
					if (!inputFound) {
						coreActivity.input = null;
					}
				}
			});
		}
		
		transition.items.remove();
		GeneralLib.setModified(true);
	},


	/**
	 * Reduce length of activity's title so it fits in its SVG shape.
	 */
	shortenActivityTitle : function(title) {
		if (title.length > 18) {
			title = title.substring(0, 17) + '...';
		}
		return title;
	},
	
	
	/**
	 * Crawles through branches setting their lengths and finding the longest one.
	 */
	updateBranchesLength : function(branchingActivity) {
		var longestBranchLength = 0;
		$.each(branchingActivity.branches, function(){
			// include the first activity
			var branchLength = 1,
				activity = this.transitionFrom.toActivity;
			if (activity instanceof ActivityDefs.BranchingEdgeActivity
					&& branchingActivity == activity.branchingActivity){
				// branch with no activities
				return true;
			}
			
			while (activity.transitions.from.length > 0) {
				activity = activity.transitions.from[0].toActivity;
				// check if reached the end of branch
				if (activity instanceof ActivityDefs.BranchingEdgeActivity) {
					break;
				} else {
					branchLength++;
				}
			};
			this.branchLength = branchLength;
			if (branchLength > longestBranchLength) {
				longestBranchLength = branchLength;
			}
		});
		
		branchingActivity.longestBranchLength = longestBranchLength;
	}
};