/**
 *  Enhanced Mobile Presence
 *    Builds off SmartThings mobile presence and adds override capability which can be invoked manually or by 3rd
 *    party endpoints
 *
 *  Copyright 2016 tybo27
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
/* ******************************************************************************************
* Metadata: definitions, capabilities, attributes, simulator, tiles						 	*
*********************************************************************************************/
metadata {
	definition (name: "Enhanced Mobile Presence", namespace: "tybo27", author: "tybo27") {
		capability "Presence Sensor"
		capability "Sensor"
        capability "Switch"
        
        attribute "mobilePresence", "enum", ["present", "not present"]
	}

	simulator {
		status "present": "presence: 1"
		status "not present": "presence: 0"
	}

	tiles {
		standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
			state("present", labelIcon:"st.presence.tile.mobile-present", backgroundColor:"#53a7c0")
			state("not present", labelIcon:"st.presence.tile.mobile-not-present", backgroundColor:"#ebeef2")
		}
        standardTile("override", "device.switch", width: 2, height: 2) {
			state("on", label: 'override', action: "switch.off", icon:"st.Home.home2", backgroundColor:"#53a7c0", nextState: "off")
			state("off", label: 'off', action: "switch.on", icon:"st.Home.home2", backgroundColor:"#ebeef2", nextState: "on")
		}
        // st.presence.tile.mobile-presence-default
        valueTile("mobilePresence", "device.mobilePresence", width: 1, height: 1) {
			state("present", label: 'present', icon:"st.Office.office9", backgroundColor:"#53a7c0")
			state("not present", label: 'not present', icon:"st.Office.office9", backgroundColor:"#ebeef2")
            state("default", label: 'not set', icon:"st.Office.office9", backgroundColor:"#ffffff")
		}
		main "presence"
		details (["presence", "override", "mobilePresence"])
	}
}

/* ******************************************************************************************
* Parse: parse input from mobile presence, handle override state						 	*
*********************************************************************************************/
def parse(String description) {

    def switchState = device.currentValue("switch")
    def presenceState = device.currentValue("presence")
    def mobilePresenceState = device.currentValue("mobilePresence")
    log.debug "PARSING '$description', switchState=$switchState, presenceState=$presenceState, mobilePresenceState=$mobilePresenceState"
    
	// Store latest description in case of override
	state.descriptionVar = description
    
    // Parse description
    def valueVar = parseValue(description)
    def nameVar = parseName(description)
    def linkTextVar = getLinkText(device)
    def descriptionTextVar = parseDescriptionText(linkTextVar, valueVar, description)
    def handlerNameVar = getState(valueVar)
    def isStateChangePresence = isStateChange(device, nameVar, valueVar)
    def displayedPresence = displayed(description, isStateChangePresence)
  	def isStateChangeMobile = isStateChange(device, "mobilePresence", valueVar)
    def displayedMobile = displayed(description, isStateChangeMobile)
    
    log.debug "name=$nameVar, value=$valueVar, handlerName=$handlerNameVar"
    log.debug "descriptionText=$descriptionTextVar, linkText=$linkTextVar"
    log.debug "isStateChangePresence=$isStateChangePresence, displayedPresence=$displayedPresence"
    log.debug "isStateChangeMobile=$isStateChangeMobile, displayedMobile:=$displayedMobile"
    
    //Generate mobile presence event to track input
    def mobileEvent = createEvent(
        translatable: true,
        name: "mobilePresence",
        value: valueVar,
        unit: null,
        linkText: "Mobile presence",
        descriptionText: "Mobile component: $descriptionTextVar",
        isStateChange: isStateChangeMobile,
        displayed: displayedMobile
    )
    
    // If override switch is OFF, or it is asserting presence parse description and generate presence event
    if (switchState=="off" || valueVar=="present") {
        log.debug "Overide switch is $switchState, mobileState is $valueVar, parsing desciption: $description, generating event:"
    
        def presenceEvent = [
            translatable: true,
            name: nameVar,
            value: valueVar,
            unit: null,
            linkText: linkTextVar,
            descriptionText: descriptionTextVar,
            handlerName: handlerNameVar,
            isStateChange: isStateChangePresence,
            displayed: displayedPresence
 			]
    	
        return [presenceEvent, mobileEvent]
        
    } else {
        log.debug "Overide switch is ON, no event generated. Stored description: $description"
        return mobileEvent
    }
}

/* ******************************************************************************************
* ParseName: return presence if description starts with "presence: "					 	*
*********************************************************************************************/
private String parseName(String description) {
	if (description?.startsWith("presence: ")) {
		return "presence"
	}
	null
}

/* ******************************************************************************************
* On: Set override and send send presence event											 	*
*********************************************************************************************/
def on () {
    // Overide state to present
	def descriptionVar = "presence: 1"
    log.debug("Switch ON, overriding $device.displayName to description: $descriptionVar:")
    sendSwitchPresenceEvent(descriptionVar)
    sendEvent(name: "switch", value: "on", unit: null, linkText: "Override Switch", descriptionText: "Override Switch is on")
}

/* ******************************************************************************************
* Off: Remove override, and set state to last commanded from mobiel presence			 	*
*********************************************************************************************/
def off () {
	// Define default description and overide if previous saved, FUTURE: Consider no action if state.results doesn't exist?
    def descriptionVar = "presence: 0"
    if (state.descriptionVar) {
        descriptionVar = state.descriptionVar
        log.debug("Switch OFF, returning $device.displayName to last parsed description: $descriptionVar:")
    } else {
    	log.debug("Switch OFF, no previously saved state, setting $device.displayName to default $descriptionVar:")
    }
    
    sendSwitchPresenceEvent(descriptionVar)
    sendEvent(name: "switch", value: "off", unit: null, linkText: "Override Switch", descriptionText: "Override Switch is off")
}

/* ******************************************************************************************
* SendSwitchPresenceEvent: parse description and send cooresponding event				 	*
*********************************************************************************************/
private sendSwitchPresenceEvent (description) {
	// Parses description and sends corresponding event
    def nameVar = parseName(description)
    def valueVar = parseValue(description)
    def linkTextVar = getLinkText(device)
    def descriptionTextVar = parseDescriptionText(linkTextVar, valueVar, description)
    def handlerNameVar = getState(valueVar)
    def isStateChangePresence = isStateChange(device, nameVar, valueVar)
    def displayedPresence = displayed(description, isStateChangePresence)
    
    log.debug "name=$nameVar, value=$valueVar, handlerName=$handlerNameVar"
    log.debug "descriptionText=$descriptionTextVar, linkText=$linkTextVar"
    log.debug "isStateChangePresence=$isStateChangePresence, displayedPresence=$displayedPresence"
    
    sendEvent(translatable: true, 
    	name: nameVar, 
        value: valueVar, 
        unit: null, 
        linkText: linkTextVar, 
        descriptionText: descriptionTextVar, 
        handlerName: handlerNameVar, 
        isStateChange: isStateChangePresence, 
        displayed: displayedPresence)
}

/* ******************************************************************************************
* ParseValue: Parse description from mobile presence for present or not present			 	*
*********************************************************************************************/
private String parseValue(String description) {
	switch(description) {
		case "presence: 1": return "present"
		case "presence: 0": return "not present"
		default: return description
	}
}

/* ******************************************************************************************
* ParseDescriptionTest: Parse text for arrival or leaving								 	*
*********************************************************************************************/
private parseDescriptionText(String linkText, String value, String description) {
	switch(value) {
		case "present": return "{{ linkText }} has arrived"
		case "not present": return "{{ linkText }} has left"
		default: return value
	}
}

/* ******************************************************************************************
* GetState: parse value for arrived or left												 	*
*********************************************************************************************/
private getState(String value) {
	switch(value) {
		case "present": return "arrived"
		case "not present": return "left"
		default: return value
	}
}