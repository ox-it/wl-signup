<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:view locale="#{UserLocale.localeExcludeCountryForIE}">
	<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="session">
	   <jsp:setProperty name="msgs" property="baseName" value="messages"/>
	</jsp:useBean>
	<sakai:view_container title="Signup Tool">
		<style type="text/css">
			@import url("/sakai-signup-tool/css/signupStyle.css");
		</style>	
		
		<script type="text/javascript" src="/library/js/jquery/1.4.2/jquery-1.4.2.min.js"></script>
        <script type="text/javascript" src="/sakai-signup-tool/js/signupScript.js"></script>
        
		<script type="text/javascript">
			jQuery.noConflict();
			jQuery(document).ready(function(){
        		sakai.initSignupBeginAndEndsExact();
        	});
    	</script>
		
		
		<sakai:view_content>
			<h:outputText value="#{msgs.event_error_alerts} #{messageUIBean.errorMessage}" styleClass="alertMessage" escape="false" rendered="#{messageUIBean.error}"/>      			
			<h:form id="meeting">
			 	<sakai:view_title value="#{msgs.event_modify_meeting_page_title}"/>
			 	<sakai:doc_section> 
					<h:panelGrid columns="1" styleClass="instruction">						
						<h:panelGroup>
							<h:outputText value="#{msgs.star_character}" styleClass="reqStarInline" />
							<h:outputText value="&nbsp;#{msgs.required2}" escape="false" /> 
						</h:panelGroup>
						<h:outputText value="&nbsp;" escape="false" />
					</h:panelGrid>
				</sakai:doc_section>
												
				<h:inputHidden id="iframeId" value="#{EditMeetingSignupMBean.iframeId}" />
				<h:panelGrid columns="1">
						<h:panelGrid columns="2" columnClasses="titleColumn,valueColumn" onmouseover="delayedRecalculateDateTime();">
							<h:panelGroup styleClass="titleText" rendered="#{EditMeetingSignupMBean.signupMeeting.recurredMeeting}">
								<h:outputText value="#{msgs.star_character}" style="color:#B11;"/>
								<h:outputText value="#{msgs.event_modify_option}" escape="false"/>
							</h:panelGroup>			
							 <h:selectOneRadio  value="#{EditMeetingSignupMBean.convertToNoRecurrent}" layout="pageDirection" styleClass="rs" rendered="#{EditMeetingSignupMBean.signupMeeting.recurredMeeting}">
					                          <f:selectItem id="modify_all" itemValue="#{false}" itemLabel="#{msgs.modify_all}"/>                                              
					                          <f:selectItem id="modify_current" itemValue="#{true}" itemLabel="#{msgs.modify_current}"/>					                                  	                      	         	 
					         </h:selectOneRadio> 
						
							<%-- title --%>
							<h:panelGroup styleClass="titleText">
								<h:outputText value="#{msgs.star_character}" style="color:#B11;"/>
								<h:outputText value="#{msgs.event_name}"  escape="false"/>
							</h:panelGroup>
							<h:panelGroup>
								<h:inputText id="title" value="#{EditMeetingSignupMBean.signupMeeting.title}" required="true" size="40" styleClass="editText">
									<f:validator validatorId="Signup.EmptyStringValidator"/>
									<f:validateLength maximum="255" />
								</h:inputText>
								<h:message for="title" errorClass="alertMessageInline"/>
							</h:panelGroup>
							
							<%-- organiser --%>
							<h:outputText value="#{msgs.event_owner}" styleClass="titleText" escape="false"/>
							<h:panelGroup>
						 		<h:selectOneMenu id="creatorUserId" value="#{EditMeetingSignupMBean.creatorUserId}">
									<f:selectItems value="#{EditMeetingSignupMBean.instructors}"/>
								</h:selectOneMenu>
							</h:panelGroup>
							
							<%-- location --%>
							<h:panelGroup styleClass="titleText">
								<h:outputText value="#{msgs.star_character}" style="color:#B11;"/>
								<h:outputText value="#{msgs.event_location}" escape="false"/>
							</h:panelGroup>
							<h:panelGroup>
		                    	  <!-- Displays all the locations in the dropdown -->
		                        <h:selectOneMenu id="selectedLocation" value="#{EditMeetingSignupMBean.selectedLocation}">
									<f:selectItems value="#{EditMeetingSignupMBean.allLocations}"/>
								</h:selectOneMenu>
		                        <h:inputText id="customLocation" size="35" value="#{EditMeetingSignupMBean.customLocation}" style="display:none" styleClass="editText">  
		                            <f:validator validatorId="Signup.EmptyStringValidator"/>
		                            <f:validateLength maximum="255" />
		                        </h:inputText>
		                        
								<h:outputLabel id="customLocationLabel" for="customLocation" styleClass="activeTag"  onclick="handleDropDownAndInput('meeting:customLocationLabel','meeting:customLocationLabel_undo','meeting:customLocation','meeting:selectedLocation')">
									<h:graphicImage value="/images/plus.gif"  alt="open" title="#{msgs.tab_event_location_custom}" style="border:none;vertical-align: middle; padding:0 5px 0 15px;" styleClass="openCloseImageIcon"/>
							   	    <h:outputText value="#{msgs.tab_event_location_custom}" escape="false" style="vertical-align: middle;"/>
								</h:outputLabel>
								<h:outputLabel id="customLocationLabel_undo" for="customLocation" styleClass="activeTag" style="display:none" onclick="handleDropDownAndInput('meeting:customLocationLabel','meeting:customLocationLabel_undo','meeting:customLocation','meeting:selectedLocation')">
									<h:graphicImage value="/images/minus.gif"  alt="undo" title="#{msgs.event_custom_undo_tip}" style="border:none;vertical-align: middle;padding:0 5px 0 15px;" styleClass="openCloseImageIcon"/>
							   	    <h:outputText value="#{msgs.event_custom_undo}" escape="false" style="vertical-align: middle;"/>
								</h:outputLabel>
								<h:outputText value="&nbsp;" escape="false" />
				                        
		                        <h:message for="customLocation" errorClass="alertMessageInline"/>
		                    </h:panelGroup> 
		                    
		                     <%-- category --%>	                                   
		                    <h:panelGroup styleClass="titleText">
		                        <h:outputText value="#{msgs.event_category}" escape="false" />
		                    </h:panelGroup>                                                   
		                    <h:panelGroup>
								<!-- Displays all the categories in the dropdown -->
		                        <h:selectOneMenu id="selectedCategory" value="#{EditMeetingSignupMBean.selectedCategory}">
									<f:selectItems value="#{EditMeetingSignupMBean.allCategories}"/>
								</h:selectOneMenu>
		                        <h:inputText id="customCategory" size="35" value="#{EditMeetingSignupMBean.customCategory}" style="display:none" styleClass="editText">  
		                            <f:validator validatorId="Signup.EmptyStringValidator"/>
		                            <f:validateLength maximum="255" />
		                        </h:inputText>
				                        
								<h:outputLabel id="customCategoryLabel" for="customLocation" styleClass="activeTag"  onclick="handleDropDownAndInput('meeting:customCategoryLabel','meeting:customCategoryLabel_undo','meeting:customCategory','meeting:selectedCategory')" >
									<h:graphicImage value="/images/plus.gif"  alt="open" title="#{msgs.event_category_custom}" style="border:none;vertical-align: middle; padding:0 5px 0 15px;" styleClass="openCloseImageIcon"/>
							   	    <h:outputText value="#{msgs.event_category_custom}" escape="false" style="vertical-align: middle;"/>
								</h:outputLabel>
								<h:outputLabel id="customCategoryLabel_undo" for="customLocation" styleClass="activeTag" style="display:none" onclick="handleDropDownAndInput('meeting:customCategoryLabel','meeting:customCategoryLabel_undo','meeting:customCategory','meeting:selectedCategory')">
									<h:graphicImage value="/images/minus.gif"  alt="undo" title="#{msgs.event_custom_undo_tip}" style="border:none;vertical-align: middle;padding:0 5px 0 15px;" styleClass="openCloseImageIcon"/>
							   	    <h:outputText value="#{msgs.event_custom_undo}" escape="false" style="vertical-align: middle;"/>
								</h:outputLabel>
								<h:outputText value="&nbsp;" escape="false"/>
		                        
		                        <h:message for="customCategory" errorClass="alertMessageInline"/>
		                    </h:panelGroup>  
							
							<%-- description --%>
							<h:outputText value="#{msgs.event_description}" styleClass="titleText" escape="false"/>
							<sakai:rich_text_area value="#{EditMeetingSignupMBean.signupMeeting.description}" width="720" height="200" rows="5"  columns="80"/>
							
							<h:outputText  value="" styleClass="titleText" escape="false" />
		         			<h:panelGrid columns="1">
		         				<t:dataTable value="#{EditMeetingSignupMBean.tempAttachmentCopyList}" var="attach" rendered="#{!EditMeetingSignupMBean.signupAttachmentEmpty}">
		         					<t:column>
        								<%@ include file="/signup/common/mimeIcon.jsp" %>
      								</t:column>
		         					<t:column>
		         						<h:outputLink  value="#{attach.location}" target="new_window">
		         							<h:outputText value="#{attach.filename}"/>
		         						</h:outputLink>
		         					</t:column>
		         					<t:column>
		         						<h:outputText escape="false" value="(#{attach.fileSize}kb)" rendered="#{!attach.isLink}"/>
		         					</t:column>
		         				</t:dataTable>
		         				
		         				<h:commandButton action="#{EditMeetingSignupMBean.addRemoveAttachments}" value="#{msgs.add_attachments}" rendered="#{EditMeetingSignupMBean.signupAttachmentEmpty}"/>		
		         				<h:commandButton action="#{EditMeetingSignupMBean.addRemoveAttachments}" value="#{msgs.add_remove_attachments}" rendered="#{!EditMeetingSignupMBean.signupAttachmentEmpty}"/>		         			
		         			</h:panelGrid>
							
							<h:outputText id="rescheduleWarnLabel_1" value="" escape="false" style="display:none;" rendered="#{EditMeetingSignupMBean.someoneSignedUp}"/>
							<h:outputText id="rescheduleWarnLabel_2" value="#{msgs.warn_reschedule_event}" styleClass="alertMessage" style="display:none;width:95%" escape="false" rendered="#{EditMeetingSignupMBean.someoneSignedUp}"/>
							
							<%-- start time --%>	
							<h:panelGroup styleClass="titleText" style="margin-top: 20px;">
								<h:outputText value="#{msgs.star_character}" style="color:#B11;"/>
								<h:outputText value="#{msgs.event_start_time}"  escape="false"/>
							</h:panelGroup>	
							<h:panelGroup styleClass="editText" rendered="#{!EditMeetingSignupMBean.customTsType}">
		        						<t:inputDate id="startTime" type="both"  ampm="true" value="#{EditMeetingSignupMBean.signupMeeting.startTime}"
		        							style="color:black;" popupCalendar="true" onfocus="showRescheduleWarning();" onkeyup="setEndtimeMonthDateYear(); getSignupDuration(); sakai.updateSignupBeginsExact(); return false;"
		        							onchange="sakai.updateSignupBeginsExact();"/>
										<h:message for="startTime" errorClass="alertMessageInline"/>
							</h:panelGroup>
							<h:panelGroup rendered="#{EditMeetingSignupMBean.customTsType}">
									<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.startTime}" styleClass="longtext">
				 						<f:convertDateTime pattern="EEEEEEEE, " />
				 					</h:outputText>
									<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.startTime}" styleClass="longtext">
				 						<f:convertDateTime dateStyle="long" />
				 					</h:outputText>
				 					<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.startTime}" styleClass="longtext">
				 						<f:convertDateTime pattern=", h:mm a" />
				 					</h:outputText>		
							</h:panelGroup>
							
							<%-- end time --%>
							<h:panelGroup styleClass="titleText">
								<h:outputText value="#{msgs.star_character}" style="color:#B11;" />					
								<h:outputText value="#{msgs.event_end_time}" escape="false"/>
							</h:panelGroup>
		        			<h:panelGroup styleClass="editText" rendered="#{!EditMeetingSignupMBean.customTsType}">
		        						<t:inputDate id="endTime" type="both" ampm="true" value="#{EditMeetingSignupMBean.signupMeeting.endTime}" style="color:black;" popupCalendar="true" 
		        						onfocus="showRescheduleWarning();" onkeyup="getSignupDuration(); sakai.updateSignupEndsExact(); return false;" onchange="sakai.updateSignupEndsExact();"/>
										<h:message for="endTime" errorClass="alertMessageInline"/>
							</h:panelGroup>	
							<h:panelGroup rendered="#{EditMeetingSignupMBean.customTsType}" >
									<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.endTime}" styleClass="longtext">
										<f:convertDateTime pattern="EEEEEEEE, " />
									</h:outputText>
									<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.endTime}" styleClass="longtext">
										<f:convertDateTime dateStyle="long" />
									</h:outputText>
									<h:outputText value="#{EditMeetingSignupMBean.signupMeeting.endTime}" styleClass="longtext">
										<f:convertDateTime pattern=", h:mm a" />
									</h:outputText>
							</h:panelGroup>
							
							<%-- signup start --%>	
							<h:panelGroup styleClass="signupBDeadline" id="signup_beginDeadline_1">    
								<h:outputText value="#{msgs.event_signup_start}" escape="false"/>
							</h:panelGroup>
							<h:panelGroup styleClass="signupBDeadline" id="signup_beginDeadline_2">
								<h:inputText id="signupBegins" value="#{EditMeetingSignupMBean.signupBegins}" size="3" required="true" onkeyup="sakai.updateSignupBeginsExact();">
									<f:validateLongRange minimum="0" maximum="99999"/>
								</h:inputText>
								<h:selectOneMenu id="signupBeginsType" value="#{EditMeetingSignupMBean.signupBeginsType}" onchange="isSignUpBeginStartNow(value); sakai.updateSignupBeginsExact();">
									<f:selectItem itemValue="minutes" itemLabel="#{msgs.label_minutes}"/>
									<f:selectItem itemValue="hours" itemLabel="#{msgs.label_hours}"/>
									<f:selectItem itemValue="days" itemLabel="#{msgs.label_days}"/>
									<f:selectItem itemValue="startNow" itemLabel="#{msgs.label_startNow}"/>
								</h:selectOneMenu>
								<h:outputText value="#{msgs.before_event_start}" escape="false" style="margin-left:18px"/>
								<h:message for="signupBegins" errorClass="alertMessageInline"/>
								
								<!--  show exact date, based on above -->
								<h:outputText id="signupBeginsExact" value="" escape="false" styleClass="dateExact" />
								
							</h:panelGroup>
							
							<%-- signup end --%>
							<h:panelGroup styleClass="signupBDeadline" id="signup_beginDeadline_3">		
								<h:outputText value="#{msgs.event_signup_deadline}" escape="false"/>
							</h:panelGroup>
							<h:panelGroup styleClass="signupBDeadline" id="signup_beginDeadline_4">
								<h:inputText id="signupDeadline" value="#{EditMeetingSignupMBean.deadlineTime}" size="3" required="true" onkeyup="sakai.updateSignupEndsExact();">
									<f:validateLongRange minimum="0" maximum="99999"/>
								</h:inputText>
								<h:selectOneMenu id="signupDeadlineType" value="#{EditMeetingSignupMBean.deadlineTimeType}" onchange="sakai.updateSignupEndsExact();">
									<f:selectItem itemValue="minutes" itemLabel="#{msgs.label_minutes}"/>
									<f:selectItem itemValue="hours" itemLabel="#{msgs.label_hours}"/>
									<f:selectItem itemValue="days" itemLabel="#{msgs.label_days}"/>
								</h:selectOneMenu>
								<h:outputText value="#{msgs.before_event_end}"  style="margin-left:18px"/>
								<h:message for="signupDeadline" errorClass="alertMessageInline"/>
								
								<!--  show exact date, based on above -->
								<h:outputText id="signupEndsExact" value="" escape="false" styleClass="dateExact" />
								
							</h:panelGroup>
							
							<h:panelGroup rendered="#{EditMeetingSignupMBean.attendanceOn}">
								<h:outputText value="Attendance" escape="false" styleClass="titleText"/>
							  </h:panelGroup>
              				<h:panelGroup rendered="#{EditMeetingSignupMBean.attendanceOn}">
								<h:selectBooleanCheckbox id="attendanceSelection" value="#{EditMeetingSignupMBean.signupMeeting.allowAttendance}" />
								<h:outputLabel value="#{msgs.attend_taken}" for="attendanceSelection" styleClass="titleText"/>
								<h:outputText value="#{msgs.attend_track_selected}" escape="false" styleClass="textPanelFooter"/>
							  </h:panelGroup>
              
							<%-- handle meeting types --%>
				           	<h:panelGroup styleClass="titleText">
				           			<h:outputText value="#{msgs.star_character}"  style="color:#B11;"/>
				            		<h:outputText value ="#{msgs.event_type_title}" />
				           	</h:panelGroup>
				           	<h:outputText value="#{msgs.label_custom_timeslots}"  escape="false" rendered="#{EditMeetingSignupMBean.customTsType}"/>
				            <h:panelGrid columns="2" columnClasses="miCol1,miCol2" rendered="#{!EditMeetingSignupMBean.customTsType}">                
					                   <h:panelGroup id="radios" styleClass="rs">                  
					                        <h:selectOneRadio id="meetingType" value="#{EditMeetingSignupMBean.signupMeeting.meetingType}"    layout="pageDirection" styleClass="rs" >
					                          	<f:selectItems value="#{EditMeetingSignupMBean.meetingTypeRadioBttns}"/>              	                      	         	 
					                 	   </h:selectOneRadio> 
					                   </h:panelGroup>
				                      
					               	   <h:panelGrid columns="1" columnClasses="miCol1">       
					               			<%-- multiple: --%>           
						               				<h:panelGroup rendered="#{EditMeetingSignupMBean.individualType}">            
								                        	<h:panelGrid columns="2" id="mutipleCh" styleClass="mi" columnClasses="miCol1,miCol2"> 
								                        			<h:outputText id="maxAttendeesPerSlot" style="display:none" value="#{EditMeetingSignupMBean.maxAttendeesPerSlot}"></h:outputText>
																	<h:outputText id="maxSlots" style="display:none" value="#{EditMeetingSignupMBean.maxSlots}"></h:outputText>   
								                        			<h:outputText value="#{msgs.event_num_slot_avail_for_signup}" />
												                    <h:inputText  id="numberOfSlot" value="#{EditMeetingSignupMBean.numberOfSlots}" size="2" styleClass="editText" onfocus="showRescheduleWarning();" onkeyup="getSignupDuration(); delayedValidMimimunTs('#{EditMeetingSignupMBean.numberOfSlots}','#{msgs.event_warning_no_lower_than_cur_ts_num}'); return false;" style="margin-left:12px" />
											                        <h:outputText value="#{msgs.event_num_participant_per_timeslot}" styleClass="titleText" escape="false"/>                    
													                <h:inputText id="numberOfAttendees" value="#{EditMeetingSignupMBean.maxNumOfAttendees}" styleClass="editText" size="2" style="margin-left:12px" onkeyup="validateAttendee();return false;" />
											                    	<h:outputText value="#{msgs.event_duration_each_timeslot_not_bold}" styleClass="titleText" escape="false"/>
																	<h:inputText id='currentTimeslotDuration' value="0" styleClass='longtext_red' size="2" onkeyup="this.blur();" onmouseup="this.blur();" style="margin-left:12px;" />             
								                			</h:panelGrid>          
						                         
						                			</h:panelGroup>                                
					               
						            				<%-- single: --%>
						               
								                	<h:panelGroup rendered="#{EditMeetingSignupMBean.groupType}">                
									                        <h:panelGrid columns="2" id="singleCh" rendered="true" styleClass="si" columnClasses="miCol1,miCol2">                
												                    <h:selectOneRadio id="groupSubradio" value="#{EditMeetingSignupMBean.unlimited}"  onclick="switchSingle(value)" styleClass="meetingRadioBtn" layout="pageDirection" >
												                        <f:selectItem itemValue="#{false}" itemLabel="#{msgs.tab_max_attendee}"/>                    
												                        <f:selectItem itemValue="#{true}" itemLabel="#{msgs.unlimited_num_attendee}"/>                            
												                    </h:selectOneRadio>
												                                    
										                   			<h:panelGrid columns="1" columnClasses="miCol1">       
												                     	<h:panelGroup  styleClass="meetingMaxAttd">                      
												                            <h:inputText id="maxAttendee" value="#{EditMeetingSignupMBean.maxNumOfAttendees}" size="2" styleClass="editText" onkeyup="validateParticipants();return false;"/>	                                 
												                        </h:panelGroup>
										                        		<h:outputText value="&nbsp;" styleClass="titleText" escape="false"/>
										                    		</h:panelGrid>
									                		</h:panelGrid>                    
								                	</h:panelGroup>
							                                       
					                 				<h:outputText id="announ" value="&nbsp;" rendered="#{EditMeetingSignupMBean.announcementType}" styleClass="titleText" escape="false"/>
					              		</h:panelGrid>
				              
				            </h:panelGrid>
				            <!-- User can switch from individual type to custom_ts type -->
				            <h:outputText id="userDefTsChoice_1" value=""  rendered="#{!EditMeetingSignupMBean.customTsType && !EditMeetingSignupMBean.announcementType}"/>
				            <h:panelGroup id="userDefTsChoice_2"  styleClass="longtext" rendered="#{!EditMeetingSignupMBean.customTsType && !EditMeetingSignupMBean.announcementType}">
				              		<h:panelGrid style="margin:-10px 0px 0px 25px;">
				              			<h:panelGroup>
											<h:selectBooleanCheckbox id="userDefTsChoice" value="#{EditMeetingSignupMBean.userDefinedTS}" onclick="userDefinedTsChoice();" />
											<h:outputText value="#{msgs.label_custom_timeslots}"  escape="false"/>
										</h:panelGroup>
										<h:panelGroup id="createEditTS" style="display:none;padding-left:35px;">
											<h:panelGroup>
												<h:commandLink action="#{EditMeetingSignupMBean.editUserDefTimeSlots}" >
													<h:graphicImage value="/images/cal.gif" alt="close" style="border:none;cursor:pointer; padding-right:5px;" styleClass="openCloseImageIcon" />
													<h:outputText value="#{msgs.label_edit_timeslots}" escape="false" styleClass="activeTag"/>
												</h:commandLink>
											</h:panelGroup>
											
										</h:panelGroup>	
									</h:panelGrid>	
							</h:panelGroup>        
				            
				            <!-- edit custom defined TS -->
				            <h:outputText value="#{msgs.event_show_schedule}" styleClass="titleText" rendered="#{EditMeetingSignupMBean.customTsType}"/>
				            <h:panelGroup rendered="#{EditMeetingSignupMBean.customTsType}">
								<h:commandLink action="#{EditMeetingSignupMBean.editUserDefTimeSlots}" >
									<h:graphicImage value="/images/cal.gif" alt="close" style="border:none;cursor:pointer; padding-right:5px;" styleClass="openCloseImageIcon" />
									<h:outputText value="#{msgs.label_view_edit_ts}" escape="false" styleClass="activeTag"/>
								</h:commandLink>
							</h:panelGroup>
				              
							<h:outputText value="&nbsp;" escape="false"/>
							<h:outputText value="&nbsp;" escape="false"/>														
							
							<h:outputText value="#{msgs.event_show_attendee_public}" styleClass="titleText" rendered="#{!EditMeetingSignupMBean.announcementType}" escape="false"/>
							<h:panelGroup styleClass="editText" rendered="#{!EditMeetingSignupMBean.announcementType}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.showAttendeeName}"/>
								<h:outputText value="#{msgs.event_yes_show_attendee_public}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText value="#{msgs.event_receive_notification}" styleClass="titleText" escape="false" rendered="#{!EditMeetingSignupMBean.announcementType}"/>
							<h:panelGroup styleClass="editText" rendered="#{!EditMeetingSignupMBean.announcementType}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.receiveEmailByOwner}"/>
								<h:outputText value="#{msgs.event_yes_receive_notification}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText value="#{msgs.event_email_notification}" styleClass="titleText" escape="false"/>
							<h:panelGrid columns="1" style="width:100%;margin-left:-3px;" rendered="#{EditMeetingSignupMBean.publishedSite}">
								<h:panelGroup styleClass="editText" >
									<h:selectBooleanCheckbox id="emailChoice" value="#{EditMeetingSignupMBean.sendEmail}" onclick="isShowEmailChoice()"/>
									<h:outputText value="#{msgs.event_yes_email_notification}" escape="false"/>
								</h:panelGroup>
								
								<h:panelGroup id="emailAttendeeOnly" style="display:none" >
									<h:selectOneRadio  value="#{EditMeetingSignupMBean.sendEmailToSelectedPeopleOnly}" layout="lineDirection" styleClass="rs" style="margin-left:20px;">
					                          <f:selectItem id="all_attendees" itemValue="all" itemLabel="#{msgs.label_email_all_people}"/>                                              
					                          <f:selectItem id="only_signedUp_ones" itemValue="signup_only" itemLabel="#{msgs.label_email_signed_up_ones_only}"/>	
					                          <f:selectItem id="only_organizers" itemValue="organizers_only" itemLabel="#{msgs.label_email_organizers_only}"/>	
					         		</h:selectOneRadio> 
								</h:panelGroup>
							</h:panelGrid>
							<h:panelGroup styleClass="editText" rendered="#{!EditMeetingSignupMBean.publishedSite}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.sendEmail}" disabled="true"/>
								<h:outputText value="#{msgs.event_email_not_send_out_label}" escape="false" style="color:#b11"/>
							</h:panelGroup>
							
							<h:outputText value="#{msgs.event_select_coordinators}" escape="false"  styleClass="titleText"/>
							<h:panelGroup>	
			   	    				<h:outputLabel  id="imageOpen_editCoordinators" style="display:none" styleClass="activeTag" onclick="showDetails('meeting:imageOpen_editCoordinators','meeting:imageClose_hideCordinators','meeting:coordinators');">
				   	    				<h:graphicImage value="/images/open.gif"  alt="open" title="Click to hide details." style="border:none;vertical-align:middle;" styleClass="openCloseImageIcon"/>
				   	    				<h:outputText value="#{msgs.event_hide_coordinators}" escape="false" style="vertical-align: middle;"/>
			   	    				</h:outputLabel>
			   	    				<h:outputLabel id="imageClose_hideCordinators" styleClass="activeTag" onclick="showDetails('meeting:imageOpen_editCoordinators','meeting:imageClose_hideCordinators','meeting:coordinators');">
			   	    					<h:graphicImage value="/images/closed.gif" alt="close" title="Click to show details." style="border:none;vertical-align:middle;" styleClass="openCloseImageIcon"/>
			   	    					<h:outputText value="#{msgs.event_addedit_Coordinators}" escape="false" style="vertical-align: middle;"/>
			   	    				</h:outputLabel>
			   	    				<h:outputText value="&nbsp;#{msgs.event_select_coordinators_instruction}" escape="false"  styleClass="longtext"/>
						   </h:panelGroup>
							
							<h:outputText id="coordinators_1" value="" escape="false" style="display:none"/>
							<h:dataTable id="coordinators_2" value="#{EditMeetingSignupMBean.allPossibleCoordinators}" var="coUser"  
							            styleClass="coordinatorTab" style="display:none">
								<h:column>
									<h:selectBooleanCheckbox value="#{coUser.checked}"/>
								    <h:outputText value="&nbsp;#{coUser.displayName}" escape="false" styleClass="longtext"/>				
								</h:column>
							</h:dataTable>		
												
							<h:outputText value="&nbsp;" escape="false"/>
							<h:outputText value="&nbsp;" escape="false"/>
							
							<h:outputText value="#{msgs.event_other_default_setting}" escape="false" styleClass="titleText" rendered="#{!EditMeetingSignupMBean.announcementType}"/>
							<h:panelGroup rendered="#{!EditMeetingSignupMBean.announcementType}">	
			   	    				<h:outputLabel  id="imageOpen_otherSetting" style="display:none" styleClass="activeTag" onclick="showDetails('meeting:imageOpen_otherSetting','meeting:imageClose_otherSetting','meeting:otherSetting');">
				   	    				<h:graphicImage value="/images/open.gif"  alt="open" title="#{msgs.event_tool_tips_hide_details}" style="border:none;vertical-align:middle;" styleClass="openCloseImageIcon"/>
				   	    				<h:outputText value="#{msgs.event_close_other_default_setting}" escape="false" style="vertical-align: middle;"/>
			   	    				</h:outputLabel>
			   	    				<h:outputLabel id="imageClose_otherSetting" styleClass="activeTag" onclick="showDetails('meeting:imageOpen_otherSetting','meeting:imageClose_otherSetting','meeting:otherSetting');">
			   	    					<h:graphicImage value="/images/closed.gif" alt="close" title="#{msgs.event_tool_tips_show_details}" style="border:none;vertical-align:middle;" styleClass="openCloseImageIcon"/>
			   	    					<h:outputText value="#{msgs.event_show_other_default_setting}" escape="false" style="vertical-align: middle;"/>
			   	    				</h:outputLabel>
						   </h:panelGroup>

							<h:outputText id="otherSetting_1" style="display:none"  value="#{msgs.event_allow_waitList}" styleClass="titleText" escape="false" rendered="#{!EditMeetingSignupMBean.announcementType}"/>
							<h:panelGroup id="otherSetting_2" style="display:none"  styleClass="longtext" rendered="#{!EditMeetingSignupMBean.announcementType}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.allowWaitList}"/>
								<h:outputText value="#{msgs.event_yes_to_allow_waitList}" escape="false"/>
							</h:panelGroup>
						
							<h:outputText id="otherSetting_3" style="display:none"  value="#{msgs.event_allow_addComment}" styleClass="titleText" escape="false" rendered="#{!EditMeetingSignupMBean.announcementType}"/>
							<h:panelGroup id="otherSetting_4" style="display:none"  styleClass="longtext" rendered="#{!EditMeetingSignupMBean.announcementType}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.allowComment}"/>
								<h:outputText value="#{msgs.event_yes_to_allow_addComment}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText id="otherSetting_5" style="display:none"  value="#{msgs.event_use_eid_input_mode}" styleClass="titleText" escape="false" rendered="#{!EditMeetingSignupMBean.announcementType && EditMeetingSignupMBean.userIdInputModeOptionChoice}"/>
							<h:panelGroup id="otherSetting_6" style="display:none"  styleClass="longtext" rendered="#{!EditMeetingSignupMBean.announcementType && EditMeetingSignupMBean.userIdInputModeOptionChoice}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.eidInputMode}"/>
								<h:outputText value="#{msgs.event_yes_to_use_eid_input_mode}" escape="false"/>
							</h:panelGroup>  
							
							<h:outputText id="otherSetting_7" style="display:none" value="#{msgs.event_email_autoReminder}" styleClass="titleText" escape="false"  rendered="#{!EditMeetingSignupMBean.announcementType && EditMeetingSignupMBean.autoReminderOptionChoice}"/>
							<h:panelGroup id="otherSetting_8" style="display:none" styleClass="longtext" rendered="#{!EditMeetingSignupMBean.announcementType && EditMeetingSignupMBean.autoReminderOptionChoice}">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.autoReminder}"/>
								<h:outputText value="#{msgs.event_yes_email_autoReminer_to_attendees}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText id="otherSetting_9" style="display:none" value="#{msgs.event_publish_to_calendar}" styleClass="titleText" escape="false" />
								<h:panelGroup id="otherSetting_10" style="display:none" styleClass="longtext">
									<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.publishToCalendar}"/>
									<h:outputText value="#{msgs.event_yes_publish_to_calendar}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText id="otherSetting_11" style="display:none" value="#{msgs.event_create_groups}" styleClass="titleText" escape="false" />
							<h:panelGroup id="otherSetting_12" style="display:none" styleClass="longtext">
								<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.signupMeeting.createGroups}"/>
								<h:outputText value="#{msgs.event_yes_create_groups}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText id="otherSetting_13" style="display:none" value="#{msgs.event_meeting_default_notify_setting}" styleClass="titleText" escape="false"/>
							<h:panelGroup id="otherSetting_14" style="display:none" styleClass="longtext" >
									<h:selectBooleanCheckbox value="#{EditMeetingSignupMBean.sendEmailByOwner}"/>
									<h:outputText value="#{msgs.event_yes_meeting_default_notify_setting}" escape="false"/>
							</h:panelGroup>
							
							<h:outputText id="otherSetting_15" style="display:none" value="#{msgs.event_allowed_slots }" styleClass="titleText" escape="false" />
							<h:panelGroup id="otherSetting_16" style="display:none" styleClass="longtext">
								<h:selectOneMenu value="#{ EditMeetingSignupMBean.signupMeeting.maxNumOfSlots}">  
									 <f:selectItems  value="#{EditMeetingSignupMBean.slots}"   /> 
								</h:selectOneMenu>
								<h:outputText value="#{msgs.event_allowed_slots_comments}" escape="false"/>
							</h:panelGroup>
						</h:panelGrid>
				</h:panelGrid>
					
										
				<sakai:button_bar>
					<h:commandButton id="goNextPage" action="#{EditMeetingSignupMBean.processSaveModify}" actionListener="#{EditMeetingSignupMBean.validateModifyMeeting}" value="#{msgs.public_modify_button}"/> 			
					<h:commandButton id="Cancel" action="#{EditMeetingSignupMBean.doCancelAction}" value="#{msgs.cancel_button}" />  
                </sakai:button_bar>

			 </h:form>
  		</sakai:view_content>	
	</sakai:view_container>
	
	<f:verbatim>
		<script>
			//for IE browser, it does nothing.
			initGroupTypeRadioButton();
			replaceCalendarImageIcon(); 
			userDefinedTsChoice();
			isShowEmailChoice();
			setIframeHeight_DueTo_Ckeditor();
			initDropDownAndInput('meeting:customLocationLabel','meeting:customLocationLabel_undo','meeting:customLocation','meeting:selectedLocation');
	        initDropDownAndInput('meeting:customCategoryLabel','meeting:customCategoryLabel_undo','meeting:customCategory','meeting:selectedCategory');

			var wait=false; 
			var originalTsNumVal = 4;//default
			var warningMsgs="You may not decrease the number of time slots below the original value:";
			function delayedValidMimimunTs(originalTsNum,warningMsg){
				originalTsNumVal = parseInt(originalTsNum);
				warningMsgs=warningMsg;
				if (!wait){
					wait = true;
				  	setTimeout("validateMimTs();wait=false;", 3000);//3 sec
				}	
			}
			
			function validateMimTs(){
				var slotNumTag = document.getElementById("meeting:numberOfSlot");
				if (!slotNumTag || slotNumTag.value.length == 0)
 						return;	 						
 						
				if(slotNumTag.value < originalTsNumVal){
					alert(warningMsgs +" " + originalTsNumVal);
					slotNumTag.value = originalTsNumVal;
					}			
			}
			
			var recurWarnTag1 = document.getElementById('meeting:rescheduleWarnLabel_1');
	        var recurWarnTag2 = document.getElementById('meeting:rescheduleWarnLabel_2');
			function showRescheduleWarning(){
	        	if(recurWarnTag1 && recurWarnTag2){	        	
		        	recurWarnTag1.style.display="";
		        	recurWarnTag2.style.display="";     		
        		}
	        }			
		</script>
	</f:verbatim>
	
</f:view> 
