package org.sakaiproject.signup.logic;

import java.util.List;

import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

/**
 * Helper to create and modify CalendarEventEdit and ExtEvent objects from meetings and timeslots.
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
public interface SignupCalendarHelper {

	/**
	 * Create a simple CalendarEventEdit object for an overall meeting. Does not include any timeslots.
	 * @param m signup meeting.
	 * @return
	 */
	public CalendarEventEdit generateEvent(SignupMeeting m);
	
	/**
	 * Create a simple CalendarEventEdit object for a specific timeslot.
	 * @param m signup meeting. Needed since it stores the main pieces of data liketitle/description/location etc.
	 * @param ts the actual timeslot.
	 * @return
	 */
	public CalendarEventEdit generateEvent(SignupMeeting m, SignupTimeslot ts);

	/**
	 * Create a ExtEvent for a timeslot. Checks if it exists in the given timeslot already (can be transient)
	 * @param meeting	overall SignupMeeting
	 * @param ts		SignupTimeslot we need ExtEvent for
	 * @return
	 */
	public ExtEvent generateExtEventForTimeslot(SignupMeeting meeting, SignupTimeslot ts);
	
	/**
	 * Create a ExtEvent for an overall meeting, no timeslots are included. Checks if it exists in the given meeting already (can be transient)
	 * @param meeting	overall SignupMeeting
	 * @return
	 */
	public ExtEvent generateExtEventForMeeting(SignupMeeting meeting);
	
	/**
	 * Create a calendar for a list of ExtEvents and return the path to the file
	 * @param vevents	List of ExtEvents
	 * @return
	 */
	public String createCalendarFile(List<ExtEvent> vevents);
	
	/**
	 * Cancel an event
	 * @param vevent ExtEvent to cancel
	 * @return
	 */
	public ExtEvent cancelExtEvent(ExtEvent vevent);
	
	/**
	 * Add the list of Users to the ExtEvent as attendees
	 * @param vevent	ExtEvent to modify
	 * @param users		List of Users to add
	 * @return
	 */
	public ExtEvent addAttendeesToExtEvent(ExtEvent vevent, List<User> users);
	
	/**
	 * Is ICS calendar generation enabled in the external calendaring service?
	 * @return	true/false
	 */
	public boolean isIcsEnabled();

	
}
