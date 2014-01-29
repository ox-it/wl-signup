package org.sakaiproject.signup.logic.messages;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.model.SignupAttendee;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * An email that is sent to an organizer when they are signed up
 * @author Ben Holmes
 */
abstract public class OrganizerEmailBase extends SignupEmailBase {

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {
        List<ExtEvent> events = new ArrayList<ExtEvent>();

        ExtEvent meetingEvent = meeting.getExtEvent();
        if (meetingEvent == null) {
            return events;
        }

        List<SignupAttendee> attendees = new ArrayList<SignupAttendee>();
        for(SignupTimeslot timeslot: meeting.getSignupTimeSlots()) {
            attendees.addAll(timeslot.getAttendees());
        }

        calendarHelper.addAttendeesToExtEvent(meetingEvent, attendees);

        events.add(meetingEvent);

        return events;
    }
}
