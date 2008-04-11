/**********************************************************************************
 * $URL: https://sakai21-dev.its.yale.edu/svn/signup/branches/2-5/api/src/java/org/sakaiproject/signup/model/SignupSite.java $
 * $Id: SignupSite.java 2972 2008-04-10 17:53:15Z gl256 $
***********************************************************************************
 *
 * Copyright (c) 2007, 2008 Yale University
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
 **********************************************************************************/
package org.sakaiproject.signup.model;

import java.util.List;

/**
 * <p>
 * This class holds the information for signup site. This object is mapped
 * directly to the DB storage by Hibernate
 * </p>
 */
public class SignupSite {

	private Long id;

	@SuppressWarnings("unused")
	private int version;

	private String title;

	private String siteId;

	private String calendarEventId;

	private String calendarId;

	private List<SignupGroup> signupGroups;

	/**
	 * get the Calendar Event Id
	 * 
	 * @return the Calendar Event Id
	 */
	public String getCalendarEventId() {
		return calendarEventId;
	}

	/**
	 * this is a setter
	 * 
	 * @param calendarEventId
	 *            the Calendar Event Id
	 */
	public void setCalendarEventId(String calendarEventId) {
		this.calendarEventId = calendarEventId;
	}

	/**
	 * get the Calendar Id
	 * 
	 * @return the Calendar Id
	 */
	public String getCalendarId() {
		return calendarId;
	}

	/**
	 * this is a setter
	 * 
	 * @param calendarId
	 *            the Calendar Id
	 */
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	/**
	 * get the sequence Id, which is generated by DB
	 * 
	 * @return the sequence Id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * this is a setter
	 * 
	 * @param id
	 *            the sequence Id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * get a list of SignupGroup objects, which belong to the site
	 * 
	 * @return a list of SignupGroup objects
	 */
	public List<SignupGroup> getSignupGroups() {
		return signupGroups;
	}

	/**
	 * this is a setter
	 * 
	 * @param signupGroup
	 *            a list of SignupGroup objects
	 */
	public void setSignupGroups(List<SignupGroup> signupGroup) {
		this.signupGroups = signupGroup;
	}

	/**
	 * get the Site id, which is a sakai unique site Id
	 * 
	 * @return the unique site Id
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * this is a setter
	 * 
	 * @param siteId
	 *            the unique site Id
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * get the title of the site
	 * 
	 * @return the name of the site
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * this is a setter
	 * 
	 * @param title
	 *            the name of the site
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * check if the event/meeting is a site scope-wide
	 * 
	 * @return true if the event/meeting is a site scope-wide
	 */
	public boolean isSiteScope() {
		return (signupGroups == null || signupGroups.isEmpty()) ? true : false;
	}

	/**
	 * check if the two SignupSite object are equal
	 */
	public boolean equals(Object object) {
		if (object == null || !(object instanceof SignupSite))
			return false;
		SignupSite other = (SignupSite) object;

		return (siteId.equals(other.getSiteId()) || id.equals(other.getId()));
	}

	public int hashCode() {
		return siteId.hashCode();
	}
}
