package org.sakaiproject.signup.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;


import org.apache.commons.lang.StringUtils;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendaring.api.ExtCalendar;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.calendaring.api.ExternalCalendaringService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.signup.util.PlainTextFormat;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.util.ResourceLoader;

/**
 * Impl of SignupCalendarHelper
 * 
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@CommonsLog
public class SignupCalendarHelperImpl implements SignupCalendarHelper {

	@Override
	public CalendarEventEdit generateEvent(SignupMeeting m) {
		return generateEvent(m, null);
	}
	
	@Override
	public CalendarEventEdit generateEvent(SignupMeeting m, SignupTimeslot ts) {
		
		//only interested in first site
		String siteId = m.getSignupSites().get(0).getSiteId();
		
		Date start;
		Date end;
		//use timeslot if set, otherwise use meeting
		if(ts == null) {
			start = m.getStartTime();
			end = m.getEndTime();
		} else {
			start = ts.getStartTime();
			end = ts.getEndTime();
		}
		String title = m.getTitle();
		String description = m.getDescription();
		String location = m.getLocation();
		
		//note that there is no way to set the creator unless the user creating the event is the current session user
		//and sometimes this is not the case, esp for transient events that are not persisted
			
		return generateEvent(siteId,start,end,title,description,location);
	}
	
	@Override
	public ExtEvent generateExtEventForTimeslot(SignupMeeting meeting, SignupTimeslot ts) {
		
		if(meeting == null) {
			log.error("Meeting was null. Cannot generate ExtEvent.");
			return null;
		}
		if(ts == null) {
			log.error("Timeslot was null. Cannot generate ExtEvent.");
			return null;
		}
		
		ExtEvent v = ts.getExtEvent();
		
		if(v == null) {
			SecurityAdvisor advisor = sakaiFacade.pushSecurityAdvisor();
			try {
				
				CalendarEventEdit tsEvent = generateEvent(meeting, ts);
				if(tsEvent == null) {
					return null; // something went wrong when fetching the calendar
				}
				tsEvent.setField("vevent_uuid", ts.getUuid());
				
				//SIGNUP-180 add sequence to vevents
				tsEvent.setField("vevent_sequence", String.valueOf(ts.getVersion()));

				tsEvent.getProperties().addProperty(ResourceProperties.PROP_CREATOR, meeting.getCreatorUserId());

				//generate ExtEvent for timeslot
				v = externalCalendaringService.createEvent(tsEvent);
				
			} finally {
				sakaiFacade.popSecurityAdvisor(advisor);
			}
		}
		
		return v;
	}
	
	@Override
	public ExtEvent generateExtEventForMeeting(SignupMeeting meeting) {
		
		if(meeting == null) {
			log.error("Meeting was null. Cannot generate ExtEvent.");
			return null;
		}
		
		ExtEvent v = meeting.getExtEvent();
		
		if(v == null) {
			SecurityAdvisor advisor = sakaiFacade.pushSecurityAdvisor();
			try {
				
				CalendarEventEdit mEvent = generateEvent(meeting);
				if(mEvent == null) {
					//calendar may not be in site - this will be skipped
					return null;
				}
				mEvent.setField("vevent_uuid", meeting.getUuid());
				mEvent.getProperties().addProperty(ResourceProperties.PROP_CREATOR, meeting.getCreatorUserId());

				//generate ExtEvent for timeslot
				v = externalCalendaringService.createEvent(mEvent);
				v = externalCalendaringService.addChairAttendeesToEvent(v, getCoordinators(meeting));
				
			} finally {
				sakaiFacade.popSecurityAdvisor(advisor);
			}
		}
		
		return v;
		
	}

	/**
	 * Helper to get a list of Users who are coordinates for a given meeting
	 *     meeting.coordinatorIds.map(userDirectoryService.getUser)
	 *
	 * @param meeting  the meeting in question
	 * @return the list of coordinator Users
	 */
	private List<User> getCoordinators(SignupMeeting meeting) {
		List<User> users = new ArrayList<User>();
		List<String> ids = meeting.getCoordinatorIdsList();
		for (String coordinator : ids) {
			try {
				users.add(userDirectoryService.getUser(coordinator));
			} catch (UserNotDefinedException e) {
				log.error("SignupCalendarHelperImpl.getCoordinators: Coordinator of meeting " + meeting.getId() + "is not defined - %s" + e);
			}
		}
		return users;
	}

	@Override
	public String createCalendarFile(List<ExtEvent> vevents) {
		//create calendar
		ExtCalendar cal = externalCalendaringService.createCalendar(vevents);
				
		//get path to file
		return externalCalendaringService.toFile(cal);
	}

	@Override
	public String createCalendarFile(List<ExtEvent> vevents, String method) {
		//create calendar
		ExtCalendar cal = externalCalendaringService.createCalendar(vevents, method);
				
		//get path to file
		return externalCalendaringService.toFile(cal);
	}

	@Override
	public ExtEvent cancelExtEvent(ExtEvent vevent) {
		return externalCalendaringService.cancelEvent(vevent);
	}
	
	@Override
	public ExtEvent addAttendeesToExtEvent(ExtEvent vevent, List<User> users) {
		return externalCalendaringService.addAttendeesToEvent(vevent, users);
	}
	
	@Override
	public boolean isIcsEnabled() {
		return externalCalendaringService.isIcsEnabled();
	}
	
	/**
	 * Helper to generate a calendar event from some pieces of data
	 * @param siteId
	 * @param startTime
	 * @param endTime
	 * @param title
	 * @param description
	 * @param location
	 * @return
	 */
	private CalendarEventEdit generateEvent(String siteId, Date startTime, Date endTime, String title, String description, String location) {
		
		Calendar calendar;
		CalendarEventEdit event = null;
		try {
			calendar = sakaiFacade.getCalendar(siteId);
		
			if (calendar == null) {
				return null;
			}
			
			event = calendar.addEvent();
			event.setType("Meeting");
			
			//time range for this timeslot
			TimeService timeService = sakaiFacade.getTimeService();
			Time start = timeService.newTime(startTime.getTime());
			Time end = timeService.newTime(endTime.getTime());
			TimeRange timeRange = timeService.newTimeRange(start, end, true, false);
			event.setRange(timeRange);
			
			//NOTE: these pieces of data may need adjusting so that its obvious this is a timeslot within the meeting
			event.setDisplayName(title);
			event.setDescription(PlainTextFormat.convertFormattedHtmlTextToICalText(addWarningMessageForCancellation(description, siteId)));
			event.setLocation(location);
			
			//SIGNUP-183 add URL property to all events
			String url = getSiteAccessUrl(siteId);
			if(StringUtils.isNotBlank(url)){
				event.setField("vevent_url", url);
			}
			
			
		} catch (PermissionException e) {
			log.error("SignupCalendarHelperImpl.generateEvent: " + e);
		}
		
		return event;
	}
	
	
	/**
	 * SIGNUP-183 append some additional text to the event 
	 */
	protected static ResourceLoader rb = new ResourceLoader("emailMessage");
	public static final String newline = "<br />";// somehow the "\n" is not working for linebreak here.
	private String addWarningMessageForCancellation(String meetingDesc, String siteId){
		meetingDesc = meetingDesc ==null? " ": meetingDesc;
		StringBuffer sb = new StringBuffer(meetingDesc);
		sb.append(newline + newline);
		sb.append(rb.getString("ical.footer.separator"));
		sb.append(newline + rb.getString("ical.footer.text"));
		return sb.toString();
	};
	
	/**
	 *  Helper to get the link to access the current-site signup tool page in a site. Added to events.
	 */ 
	private String getSiteAccessUrl(String siteId) {
		if (StringUtils.isNotBlank(siteId)) {
			return sakaiFacade.getServerConfigurationService().getPortalUrl() + "/site/" + siteId + "/page/" + sakaiFacade.getCurrentPageId();
		}
		return null;
	}
	

	
	@Setter
	private SakaiFacade sakaiFacade;
	
	@Setter
	private ExternalCalendaringService externalCalendaringService;

	@Setter
	private UserDirectoryService userDirectoryService;

}
