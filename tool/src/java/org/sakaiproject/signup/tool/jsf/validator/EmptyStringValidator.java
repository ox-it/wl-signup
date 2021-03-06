/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/signup/branches/2-6-x/tool/src/java/org/sakaiproject/signup/tool/jsf/validator/EmptyStringValidator.java $
 * $Id: EmptyStringValidator.java 56827 2009-01-13 21:52:18Z guangzheng.liu@yale.edu $
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
package org.sakaiproject.signup.tool.jsf.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.sakaiproject.signup.tool.util.Utilities;

/**
 * <P>
 * This class is a validator to make sure that user has to input something else
 * other than space(s) character only.
 * </P>
 */
public class EmptyStringValidator implements Validator {

	/**
	 * Throw exception if there is only space(s) character as input.
	 */
	public void validate(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
		String str = (String) value;
		if (str.trim().length() < 1) {
			((UIInput) toValidate).setValid(false);

			FacesMessage message = new FacesMessage();
			message.setDetail(Utilities.rb.getString("signup.validator.stringWithSpaceOnly"));
			message.setSummary(Utilities.rb.getString("signup.validator.stringWithSpaceOnly"));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(message);
		}
	}

}
