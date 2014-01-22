package org.sakaiproject.signup.logic.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.signup.logic.SignupTrackingItem;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Ben Holmes
 */
public class AttendeeCancellationOwnEmailTest extends GeneralCancellationTest {

    private SignupTrackingItem mockedItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp();

        when(_mockedUser.getId()).thenReturn("userId");
        when(_mockedCancelledTimeslot.getAttendee("userId")).thenReturn(_mockedAttendee);

        final List<SignupTrackingItem> items = Collections.singletonList(_mockedItem);
        _email = new AttendeeCancellationOwnEmail(_mockedUser, items, _mockedMeeting, _mockedFacade);
    }

    @Test
    public void shouldGenerateCancelledEvents() {
        final List<ExtEvent> events = _email.generateEvents(_mockedUser, _mockedCalendarHelper);
        verify(_mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());
    }

}
