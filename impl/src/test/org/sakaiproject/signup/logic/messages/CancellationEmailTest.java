package org.sakaiproject.signup.logic.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
public class CancellationEmailTest {

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

        MockitoAnnotations.initMocks(this);

        ExtEvent mockedEvent = mock(ExtEvent.class);
        when(_mockedCancelledTimeslot.getExtEvent()).thenReturn(mockedEvent);

        List<SignupTimeslot> timeslots = new ArrayList<SignupTimeslot>();
        timeslots.add(_mockedTimeslot);
        timeslots.add(_mockedCancelledTimeslot);
        when(_mockedMeeting.getSignupTimeSlots()).thenReturn(timeslots);

    }

    @Test
    public void canGenerateEventsFromAttendeeCancellationOwnEmail() {

        when(_mockedUser.getId()).thenReturn("userId");
        when(_mockedCancelledTimeslot.getAttendee("userId")).thenReturn(_mockedAttendee);

        final List<SignupTrackingItem> items = Collections.singletonList(_mockedItem);
        _email = new AttendeeCancellationOwnEmail(_mockedUser, items, _mockedMeeting, _mockedFacade);

        final List<ExtEvent> events = _email.generateEvents(_mockedUser, _mockedCalendarHelper);
        verify(_mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());

        assertTrue(_email.isCancellation());
    }

    @Test
    public void canGenerateEventsFromCancellationEmail() {

        when(_mockedItem.getAttendee()).thenReturn(_mockedAttendee);
        when(_mockedAttendee.getSignupSiteId()).thenReturn("123");

        when(_mockedItem.getRemovedFromTimeslot()).thenReturn(Collections.singletonList(_mockedCancelledTimeslot));

        _email = new CancellationEmail(_mockedUser, _mockedItem, _mockedMeeting, _mockedFacade);

        List<ExtEvent> events = _email.generateEvents(_mockedUser, _mockedCalendarHelper);
        verify(_mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());

        assertTrue(_email.isCancellation());
    }

}
