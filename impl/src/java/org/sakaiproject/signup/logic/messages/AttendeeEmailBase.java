package org.sakaiproject.signup.logic.messages;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * An email that is sent to an attendee when they are signed up
 * @author Ben Holmes
 */
abstract public class AttendeeEmailBase extends SignupEmailBase {

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {
        List<ExtEvent> events = new ArrayList<ExtEvent>();
        final List<SignupTimeslot> timeslots = meeting.getSignupTimeSlots();
        events.addAll(eventsWhichUserIsAttending(user, timeslots));

        for (ExtEvent event : events) {
            calendarHelper.addAttendeesToExtEvent(event, Collections.singletonList(user));
        }

        return events;
    }
}
