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
 * An email that is sent to an attendee when they are signed up
 * @author Ben Holmes
 */
abstract public class OrganizerEmailBase extends SignupEmailBase {

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {
        List<ExtEvent> events = new ArrayList<ExtEvent>();

        //check vevent for meeting exists, otherwise skip
        ExtEvent v = meeting.getExtEvent();
        if (v == null) {
            return events;
        }

        //get full list of attendees for the entire meeting, update attendee list, and create ICS. This is for overall meeting, not timeslot.
        List<SignupAttendee> attendees = new ArrayList<SignupAttendee>();
        for(SignupTimeslot ts: meeting.getSignupTimeSlots()) {
            attendees.addAll(ts.getAttendees());
        }

        calendarHelper.addAttendeesToExtEvent(v, attendees);

        events.add(v);

        return events;
    }
}
