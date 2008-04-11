/**********************************************************************************
 * $URL: https://sakai21-dev.its.yale.edu/svn/signup/branches/2-5/tool/src/java/org/sakaiproject/signup/tool/jsf/SignupGroupWrapper.java $
 * $Id: SignupGroupWrapper.java 2975 2008-04-11 16:01:29Z gl256 $
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
package org.sakaiproject.signup.tool.jsf;

import org.sakaiproject.signup.model.SignupGroup;

/**
 * <p>
 * This class is a wrapper class for SignupGroup for UI purpose
 * </P>
 */
public class SignupGroupWrapper {

	private SignupGroup signupGroup;

	private boolean selected = false;

	private boolean allowedToCreate = false;

	/**
	 * Constructor
	 * 
	 * @param signupGroup
	 *            a SignupGroup object.
	 * @param allowedToCreate
	 *            a boolean value.
	 */
	public SignupGroupWrapper(SignupGroup signupGroup, boolean allowedToCreate) {
		this.signupGroup = signupGroup;
		this.allowedToCreate = allowedToCreate;
	}

	/**
	 * This is a getter method for UI.
	 * 
	 * @return true if it is selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * This is a setter.
	 * 
	 * @param selected
	 *            a boolean value.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * This is a getter method for UI.
	 * 
	 * @return a Signupgroup object.
	 */
	public SignupGroup getSignupGroup() {
		return signupGroup;
	}

	/**
	 * This is a setter.
	 * 
	 * @param signupGroup
	 *            a SignupGroup object.
	 */
	public void setSignupGroup(SignupGroup signupGroup) {
		this.signupGroup = signupGroup;
	}

	/**
	 * This is a getter method for UI.
	 * 
	 * @return true if user is allowed to create.
	 */
	public boolean isAllowedToCreate() {
		return allowedToCreate;
	}

}
