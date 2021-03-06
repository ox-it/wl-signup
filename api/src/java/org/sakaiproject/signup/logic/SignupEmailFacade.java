/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/signup/branches/2-6-x/api/src/java/org/sakaiproject/signup/logic/SignupEmailFacade.java $
 * $Id: SignupEmailFacade.java 56827 2009-01-13 21:52:18Z guangzheng.liu@yale.edu $
***********************************************************************************
 *
 * Copyright (c) 2007, 2008, 2009 Yale University
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *   
 * See the LICENSE.txt distributed with this file.
 *
 **********************************************************************************/
package org.sakaiproject.signup.logic;

import org.sakaiproject.signup.logic.messages.SignupEventTrackingInfo;
import org.sakaiproject.signup.model.SignupMeeting;

/**
 * <P>
 * This is an interface, which provided methods for Signup tool to send emails
 * out via emailService
 * </P>
 */
public interface SignupEmailFacade extends SignupMessageTypes {

	/**
	 * This send email event/meeting informaiotn to all related participants
	 * according to the message type
	 * 
	 * @param meeting
	 *            a SignupMeeting object
	 * @param messageType
	 *            a string type, which classifies what type of message, which
	 *            should be emailed away
	 * @throws Exception
	 *             thrown if something goes bad
	 */
	void sendEmailAllUsers(SignupMeeting meeting, String messageType) throws Exception;

	/**
	 * Attend will send an email to organizer when he/she sign up
	 * 
	 * @param signupEventTrackingInfo
	 *            an EventTrackingInfo object, which contains all the
	 *            information about user action such as signup and cancel as
	 *            well as auto-promotion
	 * @throws Exception
	 *             thrown if something goes bad
	 */
	void sendEmailToOrganizer(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception;

	/**
	 * This sends cancellation email to event/meeting organizer as well as to
	 * the people on waiting list, who get promoted due to the attendee's
	 * cancellation
	 * 
	 * @param signupEventTrackingInfo
	 *            an EventTrackingInfo object, which contains all the
	 *            information about user action such as signup and cancel as
	 *            well as auto-promotion
	 * @throws Exception
	 *             thrown if something goes bad
	 */
	void sendCancellationEmail(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception;

	/**
	 * This will send email to participants by organizer
	 * 
	 * @param signupEventTrackingInfo
	 *            an EventTrackingInfo object, which contains all the
	 *            information about user action such as signup and cancel as
	 *            well as auto-promotion
	 * @throws Exception
	 *             thrown if something goes bad
	 */
	void sendEmailToParticipantsByOrganizerAction(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception;
	
	/**
	 * Send email to attendee when they sign up/cancel their attendance
	 * 
	 * @param eventTrackingInfo
	 *            an EventTrackingInfo object, which contains all the
	 *            information about user action such as signup and cancel
	 * @throws Exception
	 *             thrown if something goes bad
	 */
	void sendEmailToAttendee(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception;

}
