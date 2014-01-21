package org.sakaiproject.signup.logic.messages;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.List;


/**
 * An email that is sent to all users as an announcement
 * @author Ben Holmes
 */
abstract public class AllUsersEmailBase extends SignupEmailBase {

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {

        List<ExtEvent> events = new ArrayList<ExtEvent>();

        if (this.userIsAnOrganiser(user)) {

            final ExtEvent meetingEvent = this.meeting.getExtEvent();
            if (meetingEvent != null) {
                events.add(meetingEvent);
            }

        } else {
            final List<SignupTimeslot> timeslots = meeting.getSignupTimeSlots();
            events.addAll(eventsWhichUserIsAttending(user, timeslots));
        }

        if (this.cancellation) {
            for (ExtEvent event : events) {
                calendarHelper.cancelExtEvent(event);
            }
        }

        return events;
    }

    private List<String> meetingCreatorAndOrganisers() {
        String creatorUserId = this.meeting.getCreatorUserId();
        List<String> coordinatorIds = this.meeting.getCoordinatorIdsList();
        coordinatorIds.add(creatorUserId);
        return coordinatorIds;
    }

    private boolean userIsAnOrganiser(User user) {
        return this.meetingCreatorAndOrganisers().contains(user.getId());
    }

    private List<ExtEvent> eventsWhichUserIsAttending(User user, List<SignupTimeslot> timeslots) {
        List<ExtEvent> events = new ArrayList<ExtEvent>();
        for (SignupTimeslot timeslot : timeslots) {
            if (userIsAttendingTimeslot(user, timeslot)) {
                final ExtEvent event = timeslot.getExtEvent();
                if (event != null) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    private boolean userIsAttendingTimeslot(User user, SignupTimeslot timeslot) {
        return timeslot.getAttendee(user.getId()) != null;
    }

}
