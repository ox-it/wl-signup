/**********************************************************************************
 * $URL: https://sakai21-dev.its.yale.edu/svn/signup/branches/2-5/api/src/java/org/sakaiproject/signup/logic/Retry.java $
 * $Id: Retry.java 2973 2008-04-10 18:05:19Z gl256 $
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
package org.sakaiproject.signup.logic;

/**
 * <P>
 * This interface defines how many times the system should try befor giving up
 * when system tries to save, update or remove an event/meeting due to hibernate
 * version for multiple user concurrency cases
 * </P>
 */
public interface Retry {
	static final int MAX_NUMBER_OF_RETRY = 10;
}
