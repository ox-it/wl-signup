package org.sakaiproject.signup.logic.messages;

import org.junit.Before;
import org.junit.Test;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SakaiFacade;
import org.sakaiproject.signup.logic.SignupCalendarHelper;
import org.sakaiproject.signup.logic.SignupTrackingItem;
import org.sakaiproject.signup.model.SignupAttendee;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Ben Holmes
 */
public class AttendeeCancellationOwnEmailTest {

    protected AttendeeCancellationOwnEmail _email;
    protected User _mockedUser;
    protected SignupMeeting _mockedMeeting;


    @Before
    public void setUp() {
        _mockedUser = mock(User.class);
        when(_mockedUser.getId()).thenReturn("userId");

        _mockedMeeting = mock(SignupMeeting.class);

        SignupTrackingItem mockedItem = mock(SignupTrackingItem.class);
        List<SignupTrackingItem> items = Collections.singletonList(mockedItem);

        SakaiFacade mockedFacade = mock(SakaiFacade.class);

        SignupTimeslot mockedTimeslot = new SignupTimeslot();
        SignupTimeslot mockedCancelledTimeslot = mock(SignupTimeslot.class);
        ExtEvent mockedEvent = mock(ExtEvent.class);
        when(mockedCancelledTimeslot.getExtEvent()).thenReturn(mockedEvent);
        when(mockedCancelledTimeslot.getAttendee("userId")).thenReturn(new SignupAttendee());

        List<SignupTimeslot> timeslots = new ArrayList<SignupTimeslot>();
        timeslots.add(mockedTimeslot);
        timeslots.add(mockedCancelledTimeslot);

        when(_mockedMeeting.getSignupTimeSlots()).thenReturn(timeslots);
        when(mockedItem.getRemovedFromTimeslot()).thenReturn(Collections.singletonList(mockedCancelledTimeslot));

        _email = new AttendeeCancellationOwnEmail(_mockedUser, items, _mockedMeeting, mockedFacade);
    }

    @Test
    public void shouldBeACancellation() {
        assertTrue(_email.isCancellation());
    }

    @Test
    public void shouldGenerateCancelledEvents() {
        SignupCalendarHelper mockedCalendarHelper = mock(SignupCalendarHelper.class);
        final List<ExtEvent> events = _email.generateEvents(_mockedUser, mockedCalendarHelper);

        verify(mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());

    }

}
