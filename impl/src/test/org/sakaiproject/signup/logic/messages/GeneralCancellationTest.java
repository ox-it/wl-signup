package org.sakaiproject.signup.logic.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SakaiFacade;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.logic.SignupTrackingItem;
import org.sakaiproject.signup.model.SignupAttendee;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Ben Holmes
 */
abstract public class GeneralCancellationTest {

    protected SignupEmailBase _email;

    @Mock protected User _mockedUser;
    @Mock protected SignupMeeting _mockedMeeting;
    @Mock protected SignupTrackingItem _mockedItem;
    @Mock protected SignupAttendee _mockedAttendee;
    @Mock protected SakaiFacade _mockedFacade;
    @Mock protected SignupCalendarHelper _mockedCalendarHelper;

    @Mock protected SignupTimeslot _mockedTimeslot;
    @Mock protected SignupTimeslot _mockedCancelledTimeslot;

    @Before
    public void setUp() {

        // Make some fake timeslots
        ExtEvent mockedEvent = mock(ExtEvent.class);
        when(_mockedCancelledTimeslot.getExtEvent()).thenReturn(mockedEvent);

        List<SignupTimeslot> timeslots = new ArrayList<SignupTimeslot>();
        timeslots.add(_mockedTimeslot);
        timeslots.add(_mockedCancelledTimeslot);
        when(_mockedMeeting.getSignupTimeSlots()).thenReturn(timeslots);

    }

    @Test
    public void shouldBeACancellation() {
        assertTrue(_email.isCancellation());
    }

}
