package org.sakaiproject.signup.logic.messages;

import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.user.api.User;

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

    }

}
