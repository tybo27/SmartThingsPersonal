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
 
metadata {
	definition (name: "Enhanced Mobile Presence", namespace: "tybo27", author: "tybo27") {
		capability "Presence Sensor"
		capability "Sensor"
        capability "Switch"
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
		main "presence"
		details (["presence", "override"])
	}
}

def parse(String description) {
	def name = parseName(description)
	def value = parseValue(description)
	def linkText = getLinkText(device)
	def descriptionText = parseDescriptionText(linkText, value, description)
	def handlerName = getState(value)
	def isStateChange = isStateChange(device, name, value)

	def results = [
    	translatable: true,
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
    state.descriptionVar = description
    state.valueVar = results.value
	log.debug "Parse returned:"
    log.debug " name: $name"
    log.debug " value: $value"
    log.debug " linkText: $linkText"
    log.debug " descriptionText: $descriptionText"
    log.debug " handlerName: $handlerName"
    log.debug " isStateChange: $isStateChange"
    log.debug " displayed: $displayed"
	return results

}

private String parseName(String description) {
	if (description?.startsWith("presence: ")) {
		return "presence"
	}
	null
}

def on () {
	sendEvent(name: "switch", value: "on")
	def descriptionVar = "presence: 1"
    def nameVar = "presence"
    def valueVar = "present"
	def linkTextVar = getLinkText(device)
	def descriptionTextVar = parseDescriptionText(linkTextVar, valueVar, '')
	def handlerNameVar = getState(valueVar)
	def isStateChange = isStateChange(device, nameVar, valueVar)
    
    log.debug "Overriding $device.displayName to present"
    log.debug "Switch ON: Overide returned:"
    log.debug " name: $nameVar"
    log.debug " value: $valueVar"
    log.debug " linkText: $linkTextVar"
    log.debug " descriptionText: $descriptionTextVar"
    log.debug " handlerName: $handlerNameVar"
    log.debug " isStateChange: $isStateChange"
    log.debug " displayed: ${displayed(descriptionVar, isStateChange)}"
    sendEvent(translatable: true, name: nameVar, value: valueVar, unit: null, linkText: linkTextVar, descriptionText: descriptionTextVar, handlerName: handlerNameVar, isStateChange: isStateChange, displayed: displayed(descriptionVar, isStateChange))
    
}

def off () {
	// Define defaults
    def descriptionVar = "presence: 0"
    def nameVar = "presence"
    def valueVar = "not present"
    
	sendEvent(name: "switch", value: "off")
    // Override if previous state saved
    if (state.valueVar) {
    	log.debug("Switch OFF, state.results returned:")
        descriptionVar = state.descriptionVar
    	valueVar = state.valueVar
    } else {
    	log.debug("Switch OFF, state.results does not exist, default returned:")
        // Consider no action if state.results doesn't exist?
        
    }
    def linkTextVar = getLinkText(device)
    def descriptionTextVar = parseDescriptionText(linkTextVar, valueVar, '')
    def handlerNameVar = getState(valueVar)
    def isStateChange = isStateChange(device, nameVar, valueVar)
    
        
    log.debug " name: $nameVar"
    log.debug " value: $valueVar"
    log.debug " linkText: $linkTextVar"
    log.debug " descriptionText: $descriptionTextVar"
    log.debug " handlerName: $handlerNameVar"
    log.debug " isStateChange: $isStateChange"
    log.debug " displayed: ${displayed(descriptionVar, isStateChange)}"
    sendEvent(translatable: true, name: nameVar, value: valueVar, unit: null, linkText: linkTextVar, descriptionText: descriptionTextVar, handlerName: handlerNameVar, isStateChange: isStateChange, displayed: displayed(descriptionVar, isStateChange))
	
}

private String parseValue(String description) {
	switch(description) {
		case "presence: 1": return "present"
		case "presence: 0":
        	// Override presence if switch is on
            log.debug "$device.displayName is not present and override state is ${device.currentValue("switch")}"
        	switch (device.currentValue("switch")) {
            	case "on": return "present"
                case "off": return  "not present"
                default: return "not present"
            }
		default: return description
	}
}

private parseDescriptionText(String linkText, String value, String description) {
	switch(value) {
		case "present": return "{{ linkText }} has arrived"
		case "not present": return "{{ linkText }} has left"
		default: return value
	}
}

private getState(String value) {
	switch(value) {
		case "present": return "arrived"
		case "not present": return "left"
		default: return value
	}
}