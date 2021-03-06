/**********************************************************************************
 * $URL$
 * $Id$
***********************************************************************************
 *
 * Copyright (c) 2007, 2008, 2009 Yale University
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *   
 * See the LICENSE.txt distributed with this file.
 *
 **********************************************************************************/
package org.sakaiproject.signup.logic.messages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SakaiFacade;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.logic.SignupTrackingItem;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

/**
 * <p>
 * This class is used to notify attendee when they signup to a meeting themselves
 * </p>
 */
public class AttendeeCancellationOwnEmail extends SignupEmailBase implements SignupTimeslotChanges {

	private final User attendee;
	private final SignupTimeslot timeslot;
	private List<SignupTimeslot> removed;

	/**
	 * Constructor
	 * 
	 * @param attendee the user who cancels their own attendance
	 * @param signupMeeting the SignupMeeting they signed up to
	 * @param timeslot the SignupTimeslot they signed up to
	 * @param sakaiFacade a SakaiFacade object
	 */
	public AttendeeCancellationOwnEmail(User attendee, List<SignupTrackingItem> items, SignupMeeting meeting, SakaiFacade sakaiFacade) {
		this.attendee = attendee;
		this.meeting = meeting;
		
		//determine the timeslot. Only caters for one though, just like others.
		SignupTimeslot timeslot = null;
		for (SignupTrackingItem item : items) {
			if (item.isInitiator()) {
				removed = item.getRemovedFromTimeslot();
				timeslot = removed.get(0);
			}
		}
		this.timeslot = timeslot;
		this.setSakaiFacade(sakaiFacade);
		this.cancellation = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getHeader() {
		List<String> rv = new ArrayList<String>();
		rv.add("Content-Type: text/html; charset=UTF-8");
		rv.add("Subject: " + getSubject());
		rv.add("From: " + getFromAddress());
		rv.add("To: " + attendee.getEmail());

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMessage() {
		

		StringBuilder message = new StringBuilder();
		message.append(MessageFormat.format(rb.getString("body.top.greeting.part"), new Object[] { makeFirstCapLetter(attendee.getDisplayName()) }));

		message.append(newline + newline + MessageFormat.format(rb.getString("body.attendee.cancel.own"), new Object[] { getSiteTitleWithQuote(), getServiceName() }));

		message.append(newline + newline + MessageFormat.format(rb.getString("body.meetingTopic.part"), new Object[] { meeting.getTitle() }));
		message.append(newline + rb.getString("body.timeslot") + space);
		
		/** only handles a single timeslot, as per organiser cancellation email class */
		if (!meeting.isMeetingCrossDays()) {
			Object[] paramsTimeframe = new Object[] {
					getTime(timeslot.getStartTime()).toStringLocalTime(),
					getTime(timeslot.getEndTime()).toStringLocalTime(),
					getTime(timeslot.getStartTime()).toStringLocalDate() };
			message.append(MessageFormat.format(rb.getString("body.meeting.timeslot.timeframe"), paramsTimeframe));
		} else {
			Object[] paramsTimeframe = new Object[] {
					getTime(timeslot.getStartTime()).toStringLocalTime(),
					getTime(timeslot.getStartTime()).toStringLocalShortDate(),
					getTime(timeslot.getEndTime()).toStringLocalTime(),
					getTime(timeslot.getEndTime()).toStringLocalShortDate() };
			message.append(MessageFormat.format(rb.getString("body.meeting.crossdays.timeslot.timeframe"),
					paramsTimeframe));
		}

		message.append(newline + getFooter(newline));

		return message.toString();
	}
	
	@Override
	public String getFromAddress() {
		return getServerFromAddress();
	}

	@Override
	public String getSubject() {
		return MessageFormat.format(rb.getString("subject.attendee.cancel.own.field"), new Object[] { getAbbreviatedMeetingTitle(), getSiteTitle()});
	}
	
	@Override
	public List<SignupTimeslot> getRemoved() {
		return removed;
	}

	@Override
	public List<SignupTimeslot> getAdded() {
		return Collections.EMPTY_LIST; //not applicable here
	}

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {

        List<ExtEvent> events = new ArrayList<ExtEvent>();
        for (SignupTimeslot timeslot : removed) {
            ExtEvent event = timeslot.getExtEvent();
            calendarHelper.cancelExtEvent(event);
            events.add(event);
        }

        return events;
    }

}
