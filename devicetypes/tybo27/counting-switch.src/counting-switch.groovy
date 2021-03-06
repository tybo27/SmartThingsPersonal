/**
 *  Counting Switch
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
	definition (name: "Counting Switch", namespace: "tybo27", author: "tybo27") {
		capability "Refresh"
		capability "Switch"
        capability "Sensor"
        capability "Actuator"
        
        attribute "count", "number"
        attribute "resetState", "enum", ["reset", "counting"]
        
        command "getCount"
		command "reset"
	}

	simulator {
		// Unused, no external inputs
	}

	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.Home.home30", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', action: "switch.off", icon: "st.Home.home30", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}   
        valueTile("counter", "device.count", width: 1, height: 1) { 
        	state "val", label:'${currentValue}', action:reset, icon: "st.Health & Wellness.health7"
            backgroundColors:[ 
            	[value: 0, color: "#ffffff"],
                [value: 1, color: "#153591"], 
                [value: 3, color: "#1e9cbb"], 
                [value: 5, color: "#90d2a7"], 
                [value: 7, color: "#44b621"], 
                [value: 9, color: "#f1d801"], 
                [value: 11, color: "#d04e00"], 
                [value: 13, color: "#bc2323"]
            ]
        }
        valueTile("resetState", "device.resetState", width: 2, height: 1) { 
        	state "reset", label:'${currentValue}', action:reset, backgroundColor: "#ffffff"
            state "counting", label:'${currentValue}', action:reset, backgroundColor: "#79b821"
        }
		main "counter"
		details(["button", "counter", "refresh", "resetState"])
	}
}


/* ******************************************************************************************
* Parse: Parse events into attibutes													 	*
*********************************************************************************************/
def parse(String description) {
	log.debug "Parsing '${description}'"
	// Unused
}

/* ******************************************************************************************
* Refresh: send event to update current count attribute									 	*
*********************************************************************************************/
def refresh() {
	log.debug "Executing 'refresh'"
    sendEvent(name:"count", value: (state.counter))
}

/* ******************************************************************************************
* On: Set switch to counting, turn switch on, and increment counter						 	*
*********************************************************************************************/
def on() {
	
    log.debug "Executing 'on'"
    sendEvent(name:"resetState", value: "counting")
	sendEvent(name: "switch", value: "on")

	// Handle state.counter not being created yet
    if (state.counter) {
    	state.counter = state.counter + 1
    } else {
    	state.counter = 1
    }
        
    log.debug "count=${state.counter}"
    sendEvent(name:"count", value: (state.counter))
    
}

/* ******************************************************************************************
* Off: Turn switch off and set switch to counting										 	*
*********************************************************************************************/
def off() {
	log.debug "Executing 'off'"
	sendEvent(name: "switch", value: "off")
    sendEvent(name:"resetState", value: "counting")
}

/* ******************************************************************************************
* Reset: Set state.counter to 0, refresh count attribute, and set state to reset					 	*
*********************************************************************************************/
def reset() {
	state.counter = 0
    sendEvent(name:"count", value: (state.counter))
    sendEvent(name:"resetState", value: "reset")
    log.debug "reset: ${state.counter}"
}

/* ******************************************************************************************
* GetCount: return current state.counter												 	*
*********************************************************************************************/
def getCount() {
	log.debug "getCount: ${state.counter}"
    return state.counter
}
