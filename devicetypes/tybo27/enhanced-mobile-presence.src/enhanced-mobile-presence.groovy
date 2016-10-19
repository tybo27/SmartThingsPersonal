/**
 *  Enhanced Mobile Presence
 *    Adds override capability to presence
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
			state("off", label: 'off', action: "switch.on", icon:"st.Home.home2", backgroundColor:"#ffffff", nextState: "on")
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
	log.debug "Parse returned $results.descriptionText"
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
    def old = device.latestValue("presence")
    
    // do nothing if already in that state
	if ( old != "present") {
    	log.debug "Overriding $device.displayName to present"
	    sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "present", descriptionText: "$device.displayName is $present")
	} 

}

def off () {
	sendEvent(name: "switch", value: "off")
}

private String parseValue(String description) {
	switch(description) {
		case "presence: 1": return "present"
		case "presence: 0":
        	// Override presence if switch is on
        	switch (device.switch) {
            	case "on": return "present"
                case "off": return  "not present"
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