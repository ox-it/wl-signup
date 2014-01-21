/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/signup/branches/2-6-x/impl/src/java/org/sakaiproject/signup/logic/SignupEmailFacadeImpl.java $
 * $Id: SignupEmailFacadeImpl.java 56827 2009-01-13 21:52:18Z guangzheng.liu@yale.edu $
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

import java.io.File;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.commons.validator.EmailValidator;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendaring.api.ExtEvent;
import org.sakaiproject.email.api.AddressValidationException;
import org.sakaiproject.email.api.Attachment;
import org.sakaiproject.email.api.EmailAddress;
import org.sakaiproject.email.api.EmailMessage;
import org.sakaiproject.email.api.EmailService;
import org.sakaiproject.email.api.NoRecipientsException;
import org.sakaiproject.signup.logic.messages.AddAttendeeEmail;
import org.sakaiproject.signup.logic.messages.AttendeeCancellationEmail;
import org.sakaiproject.signup.logic.messages.AttendeeCancellationOwnEmail;
import org.sakaiproject.signup.logic.messages.AttendeeSignupEmail;
import org.sakaiproject.signup.logic.messages.AttendeeSignupOwnEmail;
import org.sakaiproject.signup.logic.messages.CancelMeetingEmail;
import org.sakaiproject.signup.logic.messages.CancellationEmail;
import org.sakaiproject.signup.logic.messages.EmailDeliverer;
import org.sakaiproject.signup.logic.messages.ModifyMeetingEmail;
import org.sakaiproject.signup.logic.messages.MoveAttendeeEmail;
import org.sakaiproject.signup.logic.messages.NewMeetingEmail;
import org.sakaiproject.signup.logic.messages.OrganizerPreAssignEmail;
import org.sakaiproject.signup.logic.messages.PromoteAttendeeEmail;
import org.sakaiproject.signup.logic.messages.SignupEmailNotification;
import org.sakaiproject.signup.logic.messages.SignupEventTrackingInfo;
import org.sakaiproject.signup.logic.messages.SwapAttendeeEmail;
import org.sakaiproject.signup.model.SignupAttendee;
import org.sakaiproject.signup.model.SignupMeeting;
import org.sakaiproject.signup.model.SignupTimeslot;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * <P>
 * This is an implementation of SignupEmailFacade interface, which provided
 * methods for Signup tool to send emails out via emailService
 * </P>
 */
public class SignupEmailFacadeImpl implements SignupEmailFacade {

	@Getter @Setter
	private EmailService emailService;

	@Getter @Setter
	private UserDirectoryService userDirectoryService;

	@Getter @Setter
	private SakaiFacade sakaiFacade;
	
	@Setter
	private SignupCalendarHelper calendarHelper;
	
	private Log logger = LogFactoryImpl.getLog(getClass());
	
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void sendEmailAllUsers(SignupMeeting meeting, String messageType) throws Exception {
		if (messageType.equals(SIGNUP_NEW_MEETING) || messageType.equals(SIGNUP_MEETING_MODIFIED) || messageType.equals(SIGNUP_CANCEL_MEETING)) {
			sendEmailToAllUsers(meeting, messageType);
			return;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendEmailToOrganizer(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception {
		
		boolean isException=false;
		
		//generate ExtEvents
		generateExtEvents(signupEventTrackingInfo.getMeeting());
		
		List<SignupTrackingItem> sigupTList = signupEventTrackingInfo.getAttendeeTransferInfos();
		for (SignupTrackingItem item : sigupTList) {
			if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP)) {
				//User creator = null;
				User participant = null;
				try {
					//creator = userDirectoryService.getUser(signupEventTrackingInfo.getMeeting().getCreatorUserId())
					List<User> OwnerAndCoordinators = getMeetingOwnerAndCoordinators(signupEventTrackingInfo.getMeeting());
					if(OwnerAndCoordinators.isEmpty()){
						throw new Exception("No Organizer/Coordinator is not found and Email is not sent away for oraginzer");
					}
					
					participant = userDirectoryService.getUser(item.getAttendee().getAttendeeUserId());
					for (User organizer : OwnerAndCoordinators) {
						SignupEmailNotification email = new AttendeeSignupEmail(organizer, participant,
								signupEventTrackingInfo.getMeeting(), item.getAddToTimeslot(), this.sakaiFacade);

						sendEmail(organizer, email);
					}
					/*SignupEmailNotification email = new AttendeeSignupEmail(creator, participant,
							signupEventTrackingInfo.getMeeting(), item.getAddToTimeslot(), this.sakaiFacade);

					sendEmail(creator, email);*/
				} catch (UserNotDefinedException e) {
					isException = true;
				}
			}
		}
		
		if(isException)
			throw new Exception("Email may not be sent out due to error.");

		return;

	}
	
	private List<User> getMeetingOwnerAndCoordinators(SignupMeeting meeting){
		Set<User> organizerCoordinators = new LinkedHashSet<User>();
		try{
			User creator = userDirectoryService.getUser(meeting.getCreatorUserId());
			organizerCoordinators.add(creator);
		}catch (UserNotDefinedException e) {
			logger.warn("User is not found and Email is not sent away for oraginzer userId:"
					+ meeting.getCreatorUserId());
		}
		
		List<String> coordinatorIds = meeting.getCoordinatorIdsList();
		for (String cId : coordinatorIds) {
			try{
				User coUser = userDirectoryService.getUser(cId);
				organizerCoordinators.add(coUser);
			}catch (UserNotDefinedException e) {
				logger.warn("User is not found and Email is not sent away for coordinator userId:" + cId);
			}
		}
		
		
		return new ArrayList<User>(organizerCoordinators);
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendCancellationEmail(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception {
		
		//generate ExtEvents
		generateExtEvents(signupEventTrackingInfo.getMeeting());
		
		/* send email to everyone who get promoted during the process */
		// TODO Do we need to send info about promoted guys to organizer?
		List<SignupTrackingItem> sigupTList = signupEventTrackingInfo.getAttendeeTransferInfos();
		for (SignupTrackingItem item : sigupTList) {
			/* no email send to the cancellation guy */
			if (item.getMessageType().equals(SIGNUP_ATTENDEE_PROMOTE)) {
				User attendee = null;
				try {
					attendee = userDirectoryService.getUser(item.getAttendee().getAttendeeUserId());
					PromoteAttendeeEmail email = new PromoteAttendeeEmail(attendee, item, signupEventTrackingInfo
							.getMeeting(), this.sakaiFacade);
					sendEmail(attendee, email);
				} catch (UserNotDefinedException e) {
					throw new Exception("User is not found and Email is not sent away for attendee userId: "
							+ item.getAttendee().getAttendeeUserId());
				}
			}

		}

		//User organizer = null;
		User initiator = null;
		try {
			//organizer = userDirectoryService.getUser(signupEventTrackingInfo.getMeeting().getCreatorUserId())
			List<User> OwnerAndCoordinators = getMeetingOwnerAndCoordinators(signupEventTrackingInfo.getMeeting());
			if(OwnerAndCoordinators.isEmpty()){
				throw new Exception("No Organizer/Coordinator is not found and Email is not sent away for oraginzer");
			}
			
			initiator = userDirectoryService.getUser(signupEventTrackingInfo.getInitiatorAllocationInfo().getAttendee() .getAttendeeUserId());

			/* send one email to organizer about the update status */			
			if (signupEventTrackingInfo.getMeeting().isReceiveEmailByOwner()){				
				for (User organizer : OwnerAndCoordinators) {
					try{
						AttendeeCancellationEmail email = new AttendeeCancellationEmail(organizer, initiator, sigupTList, signupEventTrackingInfo.getMeeting(), this.sakaiFacade);
						sendEmail(organizer, email);
					}catch(Exception e){
						//do nothing: avoid one wrong email address for one user to break all things
					}
				}				
			}
			
			//also send an email to the attendee
			AttendeeCancellationOwnEmail attendeeEmail = new AttendeeCancellationOwnEmail(initiator, sigupTList, signupEventTrackingInfo.getMeeting(), this.sakaiFacade);
			sendEmail(initiator, attendeeEmail);
			
		} catch (UserNotDefinedException e) {
			throw new Exception("User is not found for userId: "
					+ signupEventTrackingInfo.getMeeting().getCreatorUserId());
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendEmailToParticipantsByOrganizerAction(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception {
		
		//generate ExtEvents
		generateExtEvents(signupEventTrackingInfo.getMeeting());
		
		List<SignupTrackingItem> sigupTList = signupEventTrackingInfo.getAttendeeTransferInfos();
		for (SignupTrackingItem item : sigupTList) {

			User organizer = null;
			User participant = null;
			User participant2 = null;
			try {
				organizer = userDirectoryService.getUser(getSakaiFacade().getCurrentUserId());
				participant = userDirectoryService.getUser(item.getAttendee().getAttendeeUserId());
				if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP_SWAP)
						|| item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP_REPLACE)) {
					participant2 = userDirectoryService.getUser(item.getReplacedAttendde().getAttendeeUserId());
				}

				SignupEmailNotification email = null;
				if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP_MOVE))
					email = new MoveAttendeeEmail(organizer, participant, item, signupEventTrackingInfo.getMeeting(),
							sakaiFacade);
				else if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP_SWAP))
					email = new SwapAttendeeEmail(organizer, participant, participant2, item, signupEventTrackingInfo
							.getMeeting(), sakaiFacade);
				else if (item.getMessageType().equals(SIGNUP_ATTENDEE_CANCEL))
					email = new CancellationEmail(organizer, participant, item, signupEventTrackingInfo.getMeeting(),
							sakaiFacade);
				else if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP_REPLACE)
						|| item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP))
					email = new AddAttendeeEmail(organizer, participant, item, signupEventTrackingInfo.getMeeting(),
							sakaiFacade);
				else if (item.getMessageType().equals(SIGNUP_ATTENDEE_PROMOTE))
					email = new PromoteAttendeeEmail(participant, item, signupEventTrackingInfo.getMeeting(),
							sakaiFacade);
				else {
					logger.warn("For attendee(Eid):" + participant.getEid() + " - No such message type:"
							+ item.getMessageType() + " was found and no email was able to send away");
					return;
				}
				// send email out
				sendEmail(participant, email);

			} catch (UserNotDefinedException e) {
				throw new Exception("User is not found and Email is not sent away for oraginzer userId:"
						+ signupEventTrackingInfo.getMeeting().getCreatorUserId());
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void sendEmailToAttendee(SignupEventTrackingInfo signupEventTrackingInfo) throws Exception {
		
		//generate ExtEvents
		generateExtEvents(signupEventTrackingInfo.getMeeting());
		
		List<SignupTrackingItem> sigupTList = signupEventTrackingInfo.getAttendeeTransferInfos();
		for (SignupTrackingItem item : sigupTList) {
			if (item.getMessageType().equals(SIGNUP_ATTENDEE_SIGNUP)) {
				User participant = null;
				try {
					participant = userDirectoryService.getUser(item.getAttendee().getAttendeeUserId());
					SignupEmailNotification email = new AttendeeSignupOwnEmail(participant, signupEventTrackingInfo.getMeeting(), item.getAddToTimeslot(), this.sakaiFacade);
					sendEmail(participant, email);
				} catch (UserNotDefinedException e) {
					throw new Exception("User is not found and email has not been sent for participant: " + signupEventTrackingInfo.getMeeting().getCreatorUserId());
				}
			}
		}
		return;
	}

	/* send email via Sakai email Service */
	private void sendEmail(User user, SignupEmailNotification email) {
		
		logger.debug("sendMail called for user:" + user.getEid());
		
		try {
			EmailMessage message = convertSignupEmail(email, user);
			
			if(message != null) {
				emailService.send(message);
			}
			
		} catch (NoRecipientsException e) {
			logger.error("Cannot send mail. No recipient." + e.getMessage());
		} catch (AddressValidationException e) {
			//this should be caught when adding the email address, since it is validated then.
			logger.warn("Cannot send mail to user: " +  user.getEid() + ". Invalid email address." + EmailAddress.toString(e.getInvalidEmailAddresses()));
		}
		
	}
	
	/* all email is sent in this manner, so that we can send attachments. So individual users are processed separately
	 * There may be issues with this if there are many users. */
	private void sendEmail(List<User> users, SignupEmailNotification email) {
		for(User u: users) {
			sendEmail(u, email);
		}
	}

	@SuppressWarnings("unchecked")
	private List<EmailUserSiteGroup> getUserSiteEmailGroups(List<SignupUser> signupUsers) {
		List<EmailUserSiteGroup> userSiteGroupList = new ArrayList<EmailUserSiteGroup>();
		for (SignupUser signupUser : signupUsers) {
			String siteId = signupUser.getMainSiteId();
			boolean found = false;
			for (EmailUserSiteGroup userSiteGroup : userSiteGroupList) {
				if (siteId.equals(userSiteGroup.getSiteId())) {
					userSiteGroup.addSignupUser(signupUser);
					found = true;
					break;
				}
			}
			if (!found) {
				EmailUserSiteGroup usg = new EmailUserSiteGroup(siteId, signupUser);
				userSiteGroupList.add(usg);
			}
		}

		return userSiteGroupList;
	}

	
	/* send email to all according to the message type */
	@SuppressWarnings("unchecked")
	private void sendEmailToAllUsers(SignupMeeting meeting, String messageType) throws Exception {
		
		//generate ExtEvents
		generateExtEvents(meeting);
		
		List<SignupUser> signupUsers = sakaiFacade.getAllUsers(meeting);

		List<EmailUserSiteGroup> userSiteGroupList = getUserSiteEmailGroups(signupUsers);
		boolean isException = false;
		boolean isAlreadyEmailedToOrganizerCoordinators = false; //avoid duplicated email
		/*divide email-send out by site to site*/
		for (EmailUserSiteGroup emailUserSiteGroup : userSiteGroupList) {
			if (!emailUserSiteGroup.isPublishedSite())
				continue;// skip sending email

			List<String> userIds = null;
			List<User> sakaiUsers = null;
			User organizer = null;			
			try {
				SignupEmailNotification email = null;
				if (messageType.equals(SIGNUP_NEW_MEETING)) {
					organizer = userDirectoryService.getUser(meeting.getCreatorUserId());
					email = new NewMeetingEmail(organizer, meeting, this.sakaiFacade, emailUserSiteGroup.getSiteId());
					/*
					 * We have to send it no matter what!!!
					 * send email to pre-assiged people for this site group and
					 * also excluding them for next step.
					 */
					try{
						//don't stop to send another email out due to exception
						sendEmailToPreAssignedAttendee(emailUserSiteGroup, meeting);
					}catch(Exception e){
						isException=true;
					}
					
					/*Check to see if email is for all participants*/
					if(SEND_EMAIL_ALL_PARTICIPANTS.equals(meeting.getSendEmailToSelectedPeopleOnly())){
						/* get the people list excluding pre-assigned ones */
						userIds = emailUserSiteGroup.getUserInternalIds();
						sakaiUsers = userDirectoryService.getUsers(userIds);
					}
					else {
						//Case: SEND_EMAIL_ONLY_ORGANIZER_COORDINATORS/SEND_EMAIL_ONLY_SIGNED_UP_ATTENDEES
						//we have to send to organizers no matter what!!
						//case: email attendee Only and the email are already sent away
						/*Now, we need to send email to organizer and coordinators 
						 * since the iCal file is involved*/
						if(!isAlreadyEmailedToOrganizerCoordinators){
							sakaiUsers =getMeetingOwnerAndCoordinators(meeting);
							isAlreadyEmailedToOrganizerCoordinators=true;
						}
					}

				} else if (messageType.equals(SIGNUP_MEETING_MODIFIED) || messageType.equals(SIGNUP_CANCEL_MEETING)) {
					organizer = userDirectoryService.getUser(getSakaiFacade().getCurrentUserId());
					
					//same logic for both, just different emails
					if(messageType.equals(SIGNUP_MEETING_MODIFIED)) {
						email = new ModifyMeetingEmail(organizer, meeting, this.sakaiFacade, emailUserSiteGroup.getSiteId());
					}else if(messageType.equals(SIGNUP_CANCEL_MEETING)) {
						email = new CancelMeetingEmail(organizer, meeting, this.sakaiFacade, emailUserSiteGroup.getSiteId());
					}
					
					if (SEND_EMAIL_ALL_PARTICIPANTS.equals(meeting.getSendEmailToSelectedPeopleOnly())){
						userIds = emailUserSiteGroup.getUserInternalIds();
						sakaiUsers = userDirectoryService.getUsers(userIds);
					}else if (SEND_EMAIL_ONLY_ORGANIZER_COORDINATORS.equals(meeting.getSendEmailToSelectedPeopleOnly())){
						if(!isAlreadyEmailedToOrganizerCoordinators){
							//avoid duplicated email
							sakaiUsers =getMeetingOwnerAndCoordinators(meeting);
							isAlreadyEmailedToOrganizerCoordinators=true;
						}
					}else if (SEND_EMAIL_ONLY_SIGNED_UP_ATTENDEES.equals(meeting.getSendEmailToSelectedPeopleOnly())){
						List<SignupTimeslot> tsList = meeting.getSignupTimeSlots();
						User user = null;
						List<SignupUser> sgpUsers = emailUserSiteGroup.getSignupUsers();
						sakaiUsers = new ArrayList<User>();
						
						/*due to iCal file, we need to handle for canceling meeting case for organizer etc.*/
						//this one may not be needed since SIGNUP_CANCEL_MEETING always should with value:SEND_EMAIL_ALL_PARTICIPANTS
						//TODO need to find a way: update related ones iCals if the meeting schedule is modified 
						if(!isAlreadyEmailedToOrganizerCoordinators && messageType.equals(SIGNUP_CANCEL_MEETING) ){
							sakaiUsers =getMeetingOwnerAndCoordinators(meeting);
							isAlreadyEmailedToOrganizerCoordinators=true;
						}
						
						if(tsList !=null){
							for (SignupTimeslot ts : tsList) {
								List<SignupAttendee> attList = ts.getAttendees();
								if(attList !=null){
									for (SignupAttendee att : attList) {
										/*test to see if attendee is in this site-group*/
										for (SignupUser spgUser : sgpUsers) {
											if(att.getAttendeeUserId().equals(spgUser.getInternalUserId())){
												try{
													user = userDirectoryService.getUser(att.getAttendeeUserId());
												} catch (UserNotDefinedException e) {
													logger.warn("User is not found for userId: " + att.getAttendeeUserId());
													isException = true;
												}
												sakaiUsers.add(user);
												break;
											}
										}
										
									}
								}
								
							}
						}
					}
				} 

				if (email != null){
					if(sakaiUsers.size()> 400){
						/*Currently, use this only for heavy case.
						 * One drawback: the organizer don't know weather the email is really sent away or not.
						 * There is no error message for user if an unexpected exception happens.
						 * Will add the email-send-error notification at near future time.
						 * */
						
						//NO ICS FILE IS SENT VIA THIS METHOD YET
						EmailDeliverer deliverer = new EmailDeliverer(sakaiUsers, email.getHeader(),email.getMessage(),emailService);
						Thread t = new Thread(deliverer);
						t.start();
					}
					else {
						//emailService.sendToUsers(sakaiUsers, email.getHeader(), email.getMessage());
						sendEmail(sakaiUsers, email);
					}
				}

			} catch (UserNotDefinedException ue) {
				isException = true;
				logger.warn("User is not found for userId: " + meeting.getCreatorUserId());
			} catch (Exception e) {
				isException = true;
				logger.error("Exception: " + e.getClass() + ": " + e.getMessage());
			}
		}
		
		if(isException)
			throw new Exception("Some emails may not be sent out due to error.");
	}

	private void sendEmailToPreAssignedAttendee(EmailUserSiteGroup emailUserSiteGroup, SignupMeeting meeting)
			throws Exception {
		List<SignupUser> sgpUsers = emailUserSiteGroup.getSignupUsers();
		List<SignupTimeslot> signupTimeSlots = meeting.getSignupTimeSlots();
		if (signupTimeSlots == null)
			return;

		boolean isExcepiotn = false;
		User currentUser = userDirectoryService.getCurrentUser();
		for (SignupTimeslot timeslot : signupTimeSlots) {
			List<SignupAttendee> attendees = timeslot.getAttendees();
			if (attendees == null)
				continue;

			for (SignupAttendee attendee : attendees) {
				for (Iterator iter = sgpUsers.iterator(); iter.hasNext();) {
					SignupUser spUser = (SignupUser) iter.next();
					if (spUser.getInternalUserId().equals(attendee.getAttendeeUserId())) {
						User user;
						try {
							user = userDirectoryService.getUser(attendee.getAttendeeUserId());
							SignupEmailNotification email = new OrganizerPreAssignEmail(currentUser, meeting, timeslot,
									user, this.sakaiFacade, emailUserSiteGroup.getSiteId());
							sendEmail(user, email);
						} catch (UserNotDefinedException e) {
							logger.warn("User is not found for userId: " + attendee.getAttendeeUserId());
							isExcepiotn = true;
						}
						/*
						 * remove it to avoid send new meeting notification
						 * again
						 */
						iter.remove();
						break;
					}
				}

			}
		}

		if (isExcepiotn)
			throw new Exception("User is not found and email may not be sent out.");
	}

	/**
	 * This will exlude the pre-assigned attendee for a new meeting since they
	 * will receive a different email
	 * 
	 * @param sakaiUsers
	 *            a list of User objects.
	 * @param meeting
	 *            a SignupMeeting Object.
	 */
	private void excludPreAssignedAttendee(List<User> sakaiUsers, SignupMeeting meeting) {
		List<SignupTimeslot> signupTimeSlots = meeting.getSignupTimeSlots();
		if (signupTimeSlots == null)
			return;

		for (SignupTimeslot timeslot : signupTimeSlots) {
			List<SignupAttendee> attendees = timeslot.getAttendees();
			if (attendees == null)
				continue;

			User preAssignedUser = null;
			for (SignupAttendee attendee : attendees) {
				try {
					preAssignedUser = userDirectoryService.getUser(attendee.getAttendeeUserId());
					for (Iterator iter = sakaiUsers.iterator(); iter.hasNext();) {
						User sakaiUser = (User) iter.next();
						if (sakaiUser.getEid().equals(preAssignedUser.getEid())) {
							iter.remove();
						}
					}

				} catch (UserNotDefinedException e) {
					logger.warn("User is not found for userId: " + attendee.getAttendeeUserId());
				}

			}
		}
	}

	private class EmailUserSiteGroup {
		private String siteId;

		private boolean publishedSite;

		private List<SignupUser> signupUsers = new ArrayList<SignupUser>();

		public EmailUserSiteGroup(String siteId, SignupUser user) {
			this.siteId = siteId;
			this.signupUsers.add(user);
			this.publishedSite = user.isPublishedSite();
		}

		public void addSignupUser(SignupUser user) {
			this.signupUsers.add(user);
		}

		public String getSiteId() {
			return siteId;
		}

		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}

		public List<SignupUser> getSignupUsers() {
			return signupUsers;
		}

		public void setSignupUsers(List<SignupUser> signupUsers) {
			this.signupUsers = signupUsers;
		}

		public List<String> getUserInternalIds() {
			List<String> userIds = new ArrayList<String>();
			for (SignupUser signupUser : signupUsers) {
				userIds.add(signupUser.getInternalUserId());
			}
			return userIds;
		}

		public boolean isPublishedSite() {
			return publishedSite;
		}

		public void setPublishedSite(boolean publishedSite) {
			this.publishedSite = publishedSite;
		}

	}
	
	/**
	 * Helper to convert a signup email notification into a Sakai EmailMessage, which can encapsulate attachments.
	 * 
	 * <p>Due to the way the email objects are created, ie one per email that needs to be sent, not one per user, we cannot store any
	 * user specific attachments within the email objects themselves. So this method assembles an EmailMessage per user
	 * 
	 * @param email	- the signup email obj we will extract info from
	 * @param recipients - list of users to receive email
	 * @return
	 */
	private EmailMessage convertSignupEmail(SignupEmailNotification email, User recipient) {
		
		EmailMessage message = new EmailMessage();
			
		//setup message
		message.setHeaders(email.getHeader());
		message.setBody(email.getMessage());
		
		// Pass a flag to the EmailService to indicate that we want the MIME multipart subtype set to alternative
		// so that an email client can present the message as a meeting invite
		message.setHeader("multipart-subtype", "alternative");
		
		//note that the headers are largely ignored so we need to repeat some things here that are actually in the headers
		//if these are eventaully converted to proper email templates, this should be alleviated
		message.setSubject(email.getSubject());
		
		logger.debug("email.getFromAddress(): " + email.getFromAddress());
		
		message.setFrom(email.getFromAddress());
		message.setContentType("text/html; charset=UTF-8");
		
		for(Attachment a: collectAttachments(email, recipient)){
			message.addAttachment(a);
		}
		
		//add recipient, only if valid email
		String emailAddress = recipient.getEmail();
		if(StringUtils.isNotBlank(emailAddress) && EmailValidator.getInstance().isValid(emailAddress)) {
			message.addRecipient(EmailAddress.RecipientType.TO, recipient.getDisplayName(), emailAddress);
		} else {
			logger.error("Invalid email for user:" + recipient.getDisplayId() + ". No email will be sent to this user");
			return null;
		}
		
				
		return message;
	}

	/**
	 * Helper method to collect all attachments that need to go out for this email and to this user
	 * @param email	signupemailnotification
	 * @param user the user that will receive this email
	 * @return list of Attachments
	 */
	private List<Attachment> collectAttachments(SignupEmailNotification email, User user) {
		
		List<Attachment> attachments = new ArrayList<Attachment>();
		attachments.addAll(generateICS(email, user));
		//add more here as required
		
		return attachments;
		
	}
	
	/**
	 * Generate an ICS file for the user and the email message type and return as an attachment.
	 * 
	 * @param email	email obj
	 * @param user	User
	 * @return
	 */
	private List<Attachment> generateICS(SignupEmailNotification email, User user) {
		
		List<Attachment> attachments = new ArrayList<Attachment>();
		
		SignupMeeting meeting = email.getMeeting();
		
		//for each message type, determine what we need to do to get the correct ICS file.
		if (email instanceof NewMeetingEmail || email instanceof ModifyMeetingEmail || email instanceof CancelMeetingEmail) {
		
		} else if (email instanceof AddAttendeeEmail || email instanceof PromoteAttendeeEmail || email instanceof OrganizerPreAssignEmail || email instanceof AttendeeSignupOwnEmail) {

		} else if (email instanceof AttendeeCancellationOwnEmail) {

		} else if (email instanceof AttendeeSignupEmail || email instanceof AttendeeCancellationEmail) {
			//NOTE: sent to organiser when someone signs up or cancels. The output is the same, a full calendar for the meeting, with all attendees, updated.			
			if(logger.isDebugEnabled()){
				logger.debug("AttendeeSignupEmail/AttendeeCancellationEmail");
			}
			
		
		} else if (email instanceof CancellationEmail) {
			//NOTE: sent to the attendee when their signup is cancelled by an organiser
			if(logger.isDebugEnabled()){
				logger.debug("CancellationEmail");
			}
			
			//use the timeslot info in this particular email object to adjust the applicable vevents
			List<SignupTimeslot> cancelled = ((CancellationEmail) email).getRemoved();
			List<ExtEvent> vevents = new ArrayList<ExtEvent>();
			for(SignupTimeslot ts: cancelled) {
				ExtEvent v = ts.getExtEvent();
				if(v != null){
					//set it to be cancelled, add to list
					vevents.add(calendarHelper.cancelExtEvent(v));
				}
			}
			
			//create calendar and final attachment, if we have vevents to work with
			if(vevents.size()>0){
				attachments.add(formatICSAttachment(vevents, "CANCEL"));
			}
		
		} else if (email instanceof MoveAttendeeEmail || email instanceof SwapAttendeeEmail) {
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
		
		
		/*
		 * AutoReminderEmail - handled by cron job, not yet implemented.
		 * Note that there is no notification if a meeting is removed altogether.
		 */
		
		
		return attachments;
	}
	
	
	/**
	 * Helper to generate the calendareventedit objects, turn them into ExtEvents, then write back into the meeting/timeslot objects
	 * One is generated for the signupmeeting itself, then one for each timeslot.
	 * @param meeting SignupMeeting
	 * @return modifiedSignupmeeting with the ExtEvents injected
	 */
	private SignupMeeting generateExtEvents(SignupMeeting meeting) {
		
		//generate ExtEvent for meeting, add to object
		meeting.setExtEvent(calendarHelper.generateExtEventForMeeting(meeting));
			
		//now one for each timeslot
		for(SignupTimeslot ts: meeting.getSignupTimeSlots()){
				
			//generate ExtEvent for timeslot, add to object
			ts.setExtEvent(calendarHelper.generateExtEventForTimeslot(meeting, ts));
		}
		
		return meeting;
	}
	
	/**
	 * Under certain conditions (particular when attendee moved or swapped), the transient ExtEvents for timeslots are lost, so create them again.
	 * The calendarhelper checks first though.
	 * @param meeting	overall SignupMeeting
	 * @param ts		SignupTimeslot we need ExtEvent for
	 * @return
	 */
	private ExtEvent ensureExtEventForTimeslot(SignupMeeting meeting, SignupTimeslot ts) {
		return calendarHelper.generateExtEventForTimeslot(meeting, ts);
	}
	
	
	/**
	 * Helper to create an ICS calendar from a list of vevents, then turn it into an attachment
	 * @param vevents list of vevents
	 * @param method the ITIP method for the calendar, e.g. "REQUEST"
	 * @return
	 */
	private Attachment formatICSAttachment(List<ExtEvent> vevents, String method) {
		String path = calendarHelper.createCalendarFile(vevents, method);

		// Explicitly define the Content-Type and Content-Diposition headers so the invitation appears inline
		String filename = StringUtils.substringAfterLast(path, File.separator);
		String type = String.format("text/calendar; charset=\"utf-8\"; method=%s; name=signup-invite.ics", method);
		return new Attachment(new File(path), filename, type, Attachment.ContentDisposition.INLINE);
	}
	
	
}
