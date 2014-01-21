package org.sakaiproject.signup.logic.messages;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * An email that is sent when an attendee is transfered from one event to another
 * @author Ben Holmes
 */
abstract public class TransferEmailBase extends SignupEmailBase implements SignupTimeslotChanges {

    /**
     * {@inheritDoc}
     */
    public List<ExtEvent> generateEvents(User user, SignupCalendarHelper calendarHelper) {
        //NOTE: sent to attendee when organiser moves them to a different timeslot/ swaps them with another user
        if(logger.isDebugEnabled()){
            logger.debug("MoveAttendeeEmail/SwapAttendeeEmail");
        }

        //get new list of events. For all removed cancel them, add the added ones
        //need to handle this separately for the two diff email object types though
        List<SignupTimeslot> removed = new ArrayList<SignupTimeslot>();
        List<SignupTimeslot> added = new ArrayList<SignupTimeslot>();
        if(email instanceof MoveAttendeeEmail) {
            removed = ((MoveAttendeeEmail) email).getRemoved();
            added = ((MoveAttendeeEmail) email).getAdded();
        } else if (email instanceof SwapAttendeeEmail) {
            removed = ((SwapAttendeeEmail) email).getRemoved();
            added = ((SwapAttendeeEmail) email).getAdded();
        }

        //The tracking classes don't  maintain the transient VEVents we have created previously
        //so we need to check and recreate.

        //cancel all of the removed events
        List<ExtEvent> vevents = new ArrayList<ExtEvent>();
        for(SignupTimeslot ts: removed) {

            //check and recreate if necessary
            ExtEvent v = ensureExtEventForTimeslot(meeting, ts);

            if(v != null){
                //set it to be cancelled, add to list
                vevents.add(calendarHelper.cancelExtEvent(v));
            }
        }

        //add all of the new events
        for(SignupTimeslot ts: added) {

            //check and recreate if necessary
            ExtEvent v = ensureExtEventForTimeslot(meeting, ts);

            if(v != null){
                vevents.add(v);
            }
        }

        //create calendar and final attachment, if we have vevents to work with
        if(vevents.size()>0){
            attachments.add(formatICSAttachment(vevents, "REQUEST"));
        }
    }
}
