package org.sakaiproject.signup.logic.messages;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.sakaiproject.calendaring.api.ExtEvent;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Ben Holmes
 */
public class CancellationEmailTest extends GeneralCancellationTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        super.setUp();

        when(_mockedItem.getAttendee()).thenReturn(_mockedAttendee);
        when(_mockedAttendee.getSignupSiteId()).thenReturn("123");

        when(_mockedItem.getRemovedFromTimeslot()).thenReturn(Collections.singletonList(_mockedCancelledTimeslot));

        _email = new CancellationEmail(_mockedUser, _mockedItem, _mockedMeeting, _mockedFacade);
    }

    @Test
    public void shouldGenerateCancelledEvents() {
        List<ExtEvent> events = _email.generateEvents(_mockedUser, _mockedCalendarHelper);
        verify(_mockedCalendarHelper, times(1)).cancelExtEvent(any(ExtEvent.class));
        assertEquals(1, events.size());
    }

}
