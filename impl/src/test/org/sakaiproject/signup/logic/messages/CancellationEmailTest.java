package org.sakaiproject.signup.logic.messages;

import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.ListUtil;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Ben Holmes
 */
public class CancellationEmailTest {

    protected CancellationEmail _cancellationEmail;
    protected User _mockedUser;
    protected SignupMeeting _mockedMeeting;


    @Before
    public void setUp() {
        _mockedUser = mock(User.class);
        _mockedMeeting = mock(SignupMeeting.class);

        SignupTrackingItem mockedItem = mock(SignupTrackingItem.class);
        SignupAttendee mockedAttendee = mock(SignupAttendee.class);
        when(mockedItem.getAttendee()).thenReturn(mockedAttendee);
        when(mockedAttendee.getSignupSiteId()).thenReturn("123");

        SakaiFacade mockedFacade = mock(SakaiFacade.class);
        
        // Make some fake timeslots
        SignupTimeslot mockedTimeslot = mock(SignupTimeslot.class);
        SignupTimeslot mockedCancelledTimeslot = mock(SignupTimeslot.class);
        ExtEvent mockedEvent = mock(ExtEvent.class);
        when(mockedCancelledTimeslot.getExtEvent()).thenReturn(mockedEvent);
        
        
        List<SignupTimeslot> timeslots = new ArrayList<SignupTimeslot>();
        timeslots.add(mockedTimeslot);
        timeslots.add(mockedCancelledTimeslot);
        
        when(_mockedMeeting.getSignupTimeSlots()).thenReturn(timeslots);
        when(mockedItem.getRemovedFromTimeslot()).thenReturn(Collections.singletonList(mockedCancelledTimeslot));
        
        _cancellationEmail = new CancellationEmail(_mockedUser, mockedItem, _mockedMeeting, mockedFacade);
    }

    @Test
    public void shouldBeACancellation() {
        assertTrue(_cancellationEmail.isCancellation());
    }

    @Test
    public void shouldGenerateCancelledEvents() {
        SignupCalendarHelper mockedCalendarHelper = mock(SignupCalendarHelper.class);
        final List<ExtEvent> events = _cancellationEmail.generateEvents(_mockedUser, mockedCalendarHelper);

        verify(mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());

    }

}
