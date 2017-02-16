/**
 *  Dell 1610
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
* Preferences																			 	*
*********************************************************************************************/
preferences {

}

/* ******************************************************************************************
* Metadata: definitions, capabilities, attributes, simulator, tiles						 	*
*********************************************************************************************/
metadata {

	definition (name: "Dell 1610", namespace: "tybo27", author: "tybo27") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
		//capability "TV"
        
        command "setVolume"
        command "muteOn"
        command "muteOff"
        command "setBrightness"
        command "blankScreenOn"
        command "blankScreenOff"
        command "setContrast"
        command "autoAdjust"
        
        command "nextPowerSaving"
        command "ecoModeOn"
        command "ecoModeOff"
        
        command "nextProjectorMode"
        command "nextSource"
        command "nextVideoMode"
        command "nextAspectRatio"
        
        
        command "nextAudioInput"
        command "speakerOn"
        command "speakerOff"
        
        
        attribute "projectorStatus", "enum", ["Lamp On", "Standby", "Power Saving", "Cooling", "Warming Up"]
        attribute "powerSaving", "enum", ["Off", "30", "60", "90", "120", "No Statement"]
        attribute "alertStatus", "enum", ["Lamp warming", "Low lamp life", "Temperature"]
        attribute "projectorMode", "enum", ["Front Projection-Desktop", "Front Projection-Ceiling Mount", "Rear Projection-Desktop", "Rear Projection-Ceiling Mount", "No Statement"]
        attribute "sourceSelect", "enum", ["VGA-A", "VGA-B", "S-VIDEO", "COMPOSITE VIDEO", "HDMI", "No Statement"]
        attribute "videoMode", "enum", ["Presentation Mode", "Bright Mode", "Movie Mode", "sRGB Mode", "Custom Mode", "No Statement"]
        attribute "blankScreen", "enum", ["On", "Off"]
        attribute "aspectRatio", "enum", ["Original", "4:3", "Wide"]
        attribute "brightness", "number" //0-100
        attribute "contrast", "number" //0-100
        attribute "ecoMode", "enum", ["Normal Mode", "ECO Mode"]
        attribute "audioInput", "enum", ["Audio-A", "Audio-B", "HDMI", "Microphone", "No Statement"]
        attribute "volume", "string" // volume from capability TV
        attribute "speaker", "enum", ["On", "Off"]
        attribute "mute", "enum", ["On", "Off"]
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    tiles (scale: 2){
		// TODO: define your main and details tiles here
        multiAttributeTile(name:"mainControlTile", type:"generic", width:6, height:2) {
            tileAttribute("device.projectorStatus", key: "PRIMARY_CONTROL") {
                attributeState "Standby", icon: "st.Electronics.electronics7", label:'${currentValue}', action:"switch.on",  backgroundColor:"#ffffff", nextState:"Warming Up"
                attributeState "Warming Up", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.off", backgroundColor:"#ffffe0", nextState:"Cooling"
                attributeState "Lamp On", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.off", backgroundColor:"#98fb98", nextState:"Cooling"
                attributeState "Cooling", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.on", backgroundColor:"#ADD8E6", nextState:"Warming Up"
                attributeState "Power Saving", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.on", backgroundColor:"#4E8975", nextState:"Warming Up"
    		}
             tileAttribute("device.projectorStatus", key: "SECONDARY_CONTROL") {
            	attributeState "default", action:"refresh", icon:"st.secondary.refresh"
        	}
            /*tileAttribute("device.volume", key: "SLIDER_CONTROL") {
               attributeState "volume", action:"setVolume", range:"(0..20)"
            }
            tileAttribute("device.blankScreen", key: "SECONDARY_CONTROL") {
                attributeState "off", backgroundColor:"#ffffff", icon: 'st.Electronics.electronics7', action:"blankScreenOn", nextState:"on"
                attributeState "on", backgroundColor:"#000000", icon: 'st.Electronics.electronics7', action:"blankScreenOff", nextState:"off"
            }
            tileAttribute("device.mute", key: "SECONDARY_CONTROL") {
                attributeState "off", backgroundColor:"#ffffff", icon: 'st.Electronics.electronics17', action:"muteOn", nextState:"on"
                attributeState "on", backgroundColor:"#000000", icon: 'st.Electronics.electronics17', action: "muteOff", nextState:"off"
            }*/
		}
        /// Power, status and refresh
        standardTile("Power On", "device.switch", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state "off", label: "off", icon: "st.Electronics.electronics7", backgroundColor: "#ffffff", action: "switch.on"
    		state "on", label: "on", icon: "st.Electronics.electronics7", backgroundColor: "#79b821", action: "switch.off"
        }
        valueTile("Status", "device.projectorStatus", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state "Standby", icon: "st.Electronics.electronics7", label:'${currentValue}', action:"switch.on",  backgroundColor:"#ffffff", nextState:"Warming Up"
            state "Warming Up", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.off", backgroundColor:"#ffffe0", nextState:"Cooling"
            state "Lamp On", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.off", backgroundColor:"#98fb98", nextState:"Cooling"
            state "Cooling", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.on", backgroundColor:"#ADD8E6", nextState:"Warming Up"
            state "Power Saving", icon: "st.Electronics.electronics7", label:'${name}', action:"switch.on", backgroundColor:"#4E8975", nextState:"Warming Up"
        }
        standardTile("refresh", "device.projectorStatus", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
            state "default", action:"refresh", icon:"st.secondary.refresh"
        }
        
        // Volume, Speaker and Mute
        valueTile("Volume Label", "device.volume", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: "Volume"
        }
        controlTile("Volume Control", "device.volume", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..20)") {
    		state "volume", action: "setVolume"
		}
        standardTile("Speaker Button", "device.speaker", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: "Speaker", icon: "st.Electronics.electronics16", backgroundColor: "#ffffff", action: "speakerOn"
    		state "on", label: "Speaker", icon: "st.Electronics.electronics16", backgroundColor: "#79b821", action: "speakerOff"
        }
        standardTile("Mute Button", "device.mute", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: "Mute", icon: "st.Entertainment.entertainment15", backgroundColor: "#ffffff", action: "muteOn"
    		state "on", label: "Mute", icon: "st.Entertainment.entertainment15", backgroundColor: "#79b821", action: "muteOff"
        }
        
       // Brightness, ECO, and blank
       	valueTile("Brightness Label", "device.brightness", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: "Brightness"
        }
        controlTile("Brightness Control", "device.brightness", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
    		state "level", action:"setBrightness"
		}
        standardTile("ECO Mode Button", "device.ecoMode", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: "ECO", icon: "st.Outdoor.outdoor3", backgroundColor: "#ffffff", action: "ecoModeOn"
    		state "on", label: "ECO", icon: "st.Outdoor.outdoor3", backgroundColor: "#79b821", action: "ecoModeOff"
        }
        standardTile("Blank Button", "device.blankScreen", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: "Blank", icon: "st.Electronics.electronics18", backgroundColor: "#ffffff", action: "blankOn"
    		state "on", label: "Blank", icon: "st.Entertainment.entertainment18", backgroundColor: "#79b821", action: "blankOff"
        }
        
        //Contrast and ECO Mode
        valueTile("Contrast Label", "device.contrast", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: "Contrast"
        }
        controlTile("Contrast Control", "device.contrast", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
    		state "level", action:"setContrast"
		}
        standardTile("Video Mode Label", "device.videoMode", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: 'Video: ${currentValue}', action: "nextVideoMode"
        }
        
        standardTile("Projector Mode Label", "device.projectorMode", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: '${currentValue}', action: "nextProjectorMode"
        }
        standardTile("Power Saving Label", "device.powerSaving", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: 'Power: ${currentValue}', action: "nextPowerSaving"
        }
        standardTile("Aspect Ratio Label", "device.aspectRatio", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: 'Aspect: ${currentValue}', action: "nextAspectRatio"
        }
        
        standardTile("Next Source Label", "device.sourceSelect", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: 'Video: ${currentValue}', action: "nextSource"
        }
        standardTile("Next Audio Input Label", "device.audioInput", height: 1, width: 2, inactiveLabel: false, decoration: "flat") {
        	state "default", label: 'Audio: ${currentValue}', action: "nextAudioInput"
        }
        
         
        /*
        command "autoAdjust"
        StandardTile("Auto Adjust Button", "device.blankScreen", height: 1, width: 1, inactiveLabel: false, decoration: "flat") {
            state "off", label: "Blank", icon: "st.Electronics.electronics18", backgroundColor: "#ffffff", action: "switch.on"
    		state "on", label: "Blank", icon: "st.Entertainment.entertainment18", backgroundColor: "#79b821", action: "switch.off"
        }*/
        
        // Tiles
        main (["Power On"])
        details(["Power On", "Status", "refresh", 
        	"Volume Label", "Volume Control", "Speaker Button", "Mute Button",
            "Brightness Label", "Brightness Control", "ECO Mode Button", "Blank Button",
            "Contrast Label", "Contrast Control", "Video Mode Label",
            "Projector Mode Label", "Power Saving Label", "Aspect Ratio Label",
            "Next Source Label", "Next Audio Input Label",
            
            ])
	}
}

/* ******************************************************************************************
* Parse: Parse return from hubAction													 	*
*********************************************************************************************/
def parse(String description) {

	// log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response

	log.debug "parse: headersAsString=$headersAsString"
    parseResponseHeaders(headersAsString)
    
    if (status>=200 && status<300) {
		
        def projStatus = parseResponseBody(body)
        log.debug "status = $projStatus"
		if (projStatus) {
        	state.status = projStatus
            def statusEvents = ['ERRORSTA2', 'PwSave', 'PrjMode', 'PrjSRC', 'VideoMode', 'hide', 'Aspect', 'Bright', 'Contrast', 'ecoMode', 'PrjSRCA', 'Volume', 'Spk']
            def projectorEvents = []
            def attributeName = getAttributeName('PJSTATE')
            def valueText = getValueText('PJSTATE', projStatus['PJSTATE'])
            projectorEvents.add(createEvent(
        		name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        		value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        		descriptionText: "Projector is currently in ${valueText}", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        		displayed: true,
                linkText: "Current Projector Status", //String - Name of the event to show in the mobile application activity feed, if specified
                //isStateChange: isStateChange((),
                unit: null
            ))
            
            statusEvents.each {
            	attributeName = getAttributeName(it)
            	valueText = getValueText(it, projStatus[it])
                if (attributeName && valueText) {
                    projectorEvents.add(createEvent(
                        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
                        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
                        descriptionText: "$attributeName is set to ${valueText}", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
                        displayed: false, //displayable?
                        linkText: "$attributeName Status", //String - Name of the event to show in the mobile application activity feed, if specified
                        //isStateChange: isStateChange(),
                        unit: null
                    ))
                }
            }
            //log.debug "Parse: Sending events:$projectorEvents"
            return projectorEvents
        } else {
        	runIn(10,refresh)
    	}
    } else {
    	log.debug "Parse: Returned status: $status"
    }
}

/* ******************************************************************************************
* On: Turn projector power on																*
*********************************************************************************************/
def on() {
	log.debug "Executing 'on'"
    
    def commandName = 'PowerOn'
    def command = 'Power On ' 
    //def attributeName = getAttributeName(commandName)
    //def valueText = getValueText(commandName)
	
    def commandResponse = sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: 'switch', // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: 'On', //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Turning projector on", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Projector On", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* Off: Turn projector power off															 	*
*********************************************************************************************/
def off() {

	log.debug "Executing 'off'"
    def commandName = 'PowerOn'
    def command = 'Power Off ' 
    //def attributeName = getAttributeName(commandName)
    //def valueText = getValueText(commandName)
	
    def commandResponse = sendCommand(structureCommand(commandName, command))
    //log.debug "Command Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: 'switch', // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: 'Off', //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Turning projector off", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Projector Off", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )

}

/* ******************************************************************************************
* SetVolume: Change volume of the projector												 	*
*********************************************************************************************/
def setVolume(value) {
	
    def commandName = 'Volume'
    def attributeName = getAttributeName(commandName)
    def valueText = getValueText(commandName, value.toString())
	
    log.debug "Executing 'setVolume' to $value"
    def command = value
    
    //Constrain to allowed range of 0 to 20
    if (command<0) {
    	command=0
    } else if (command>20) {
    	command=20
    }
    state.volume = command
    def commandResponse = sendCommand(structureCommand(commandName, command.toString()))
    //log.debug "Command Response: ${commandResponse}"
    return commandResponse

    /*
	sendEvent(
        name: 'volume', // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: 'Off', //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )*/
}

/* ******************************************************************************************
* mute: Mute audio																		 	*
*********************************************************************************************/
def mute () {

	log.debug "Mute on"
    def commandName = 'Volume'
    def command = 0
    def attributeName = getAttributeName(commandName)
    //def valueText = getValueText(commandName)
    
	state.volume = device.currentValue("volume") // store current volume for unmute
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: command, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Mute on", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Mute On", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
    
}

/* ******************************************************************************************
* unmute: Unmute audio																	 	*
*********************************************************************************************/
def unmute () {

	log.debug "Mute off"
    def commandName = 'Volume'
    def command = state.volume
    def attributeName = getAttributeName(commandName)
    //def valueText = getValueText(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: command, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Mute off", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Mute Off", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
    
}

/* ******************************************************************************************
* blankScreenOn: Blank the screen														 	*
*********************************************************************************************/
def blankScreenOn () {

	log.debug "Blanking Screen"
    
    def commandName = 'hide'
    def valueText = 'On'
    def command = getValueFromText(commandName, valueText) // 85
    def attributeName = getAttributeName(commandName)
	
    def commandResponse = sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Blanking Screen", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Blank On", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* blankScreenOff: Unblank the screen													 	*
*********************************************************************************************/
def blankScreenOff () {

	log.debug "Unblanking Screen"
    
    def commandName = 'hide'
    def valueText = 'Off'
    def command = getValueFromText(commandName, valueText)// 170
    def attributeName = getAttributeName(commandName)
	
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Unblanking Screen", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "Blank Off", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )

}

/* ******************************************************************************************
* setBrightness: Change brightness of the projector										 	*
*********************************************************************************************/
def setBrightness(value) {
	
    def commandName = 'Bright'
    def attributeName = getAttributeName(commandName)
    def valueText = getValueText(commandName, value)
	
    log.debug "Executing 'setBrightness' to $value"
    def command = value
    
    //Constrain to allowed range of 0 to 100
    if (command<0) {
    	command=0
    } else if (command>100) {
    	command=100
    }

    def commandResponse = sendCommand(structureCommand(commandName, command))
    //log.debug "Command Response: ${commandResponse}"
    return commandResponse
    
    /*
	sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "$attributeName is set to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )*/
}

/* ******************************************************************************************
* setContrast: Change brightness of the projector										 	*
*********************************************************************************************/
def setContrast(value) {
	
    def commandName = 'Contrast'
    def attributeName = getAttributeName(commandName)
    def valueText = getValueText(commandName, value)
	
    log.debug "Executing 'setContrast' to $value"
    def command = value
    
    //Constrain to allowed range of 0 to 100
    if (command<0) {
    	command=0
    } else if (command>100) {
    	command=100
    }
    
    def commandResponse = sendCommand(structureCommand(commandName, command))
    //log.debug "Command Response: ${commandResponse}"
    return commandResponse

    /*
	sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )*/
}

/* ******************************************************************************************
* ecoModeOn: Turn on ECO Mode the screen												 	*
*********************************************************************************************/
def ecoModeOn () {

	log.debug "ecoModeOn"
    
    def commandName = 'ecoMode'
    def valueText = 'On'
    def command = getValueFromText(commandName, valueText) //27
    def attributeName = getAttributeName(commandName)
	
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* ecoModeOff: Turn off ECO Mode the screen												 	*
*********************************************************************************************/
def ecoModeOff () {

	log.debug "ecoModeOn"
    
    def commandName = 'ecoMode'
    def valueText = 'Off'
    def command = getValueFromText(commandName, valueText) //28
    def attributeName = getAttributeName(commandName)
	
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* speakerOn: Turn on Speakers On														 	*
*********************************************************************************************/
def speakerOn () {

	log.debug "speakerOn"
    
    def commandName = 'Spk'
    def valueText = 'On'
    def command = getValueFromText(commandName, valueText) //85
    def attributeName = getAttributeName(commandName)
	
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* speakerOff: Turn off Speakers															 	*
*********************************************************************************************/
def speakerOff () {

	log.debug "speakerOff"
    
    def commandName = 'Spk'
    def valueText = 'Off'
    def command = getValueFromText(commandName, valueText) //170
    def attributeName = getAttributeName(commandName)
	
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextPowerSaving: Cycle power saving modes												 	*
*********************************************************************************************/
def nextPowerSaving () {

	log.debug "nextPowerSaving"
    def commandName = 'PwSave'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {
    	case 'Off':
        	nextMode = '30 min'
        	break
        case '30 min':
        	nextMode = '60 min'
        	break
        case '60 min':
        	nextMode = '90 min'
        	break
        case '90 min':
        	nextMode = '120 min'
        	break
        case '120 min':
        	nextMode = 'Off'
        	break
        case 'No Statement':
        	nextMode = 'No Statement'
        	break
        default:
            nextMode = 'No Statement'
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextProjectorMode: Cycle projector modes												 	*
*********************************************************************************************/
def nextProjectorMode () {

	log.debug "nextProjectorMode"
    def commandName = 'PrjMode'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {
        case 'Front Projection-Desktop':
        	nextMode = 'Front Projection-Ceiling Mount'
        	break
        case 'Front Projection-Ceiling Mount':
        	nextMode = 'Rear Projection-Desktop'
        	break
        case 'Rear Projection-Desktop':
        	nextMode = 'Rear Projection-Ceiling Mount'
        	break
        case 'Rear Projection-Ceiling Mount':
        	nextMode = 'Front Projection-Desktop'
        	break
        case 'No Statement':
        	nextMode = 'No Statement'
        	break
        default:
            nextMode = 'No Statement'
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextSource: Cycle projector modes														 	*
*********************************************************************************************/
def nextSource () {

	log.debug "nextSource"
    def commandName = 'PrjSRC'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {

        case 'VGA-A':
            nextMode = 'VGA-B'
            break
        case 'VGA-B':
            nextMode = 'S-VIDEO'
            break
        case 'S-VIDEO':
            nextMode = 'COMPOSITE VIDEO'
            break
        case 'COMPOSITE VIDEO':
            nextMode = 'HDMI'
            break
        case 'HDMI':
            nextMode = 'VGA-A'
            break
        case 'No Statement':
        	nextMode = 'No Statement'
        	break
        default:
            nextMode = 'No Statement'
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextVideoMode: Cycle projector modes													 	*
*********************************************************************************************/
def nextVideoMode () {

	log.debug "nextVideoMode"
    def commandName = 'VideoMode'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {
        case 'Presentation Mode':
        	nextMode = 'Bright Mode'
        	break
        case 'Bright Mode':
        	nextMode = 'Movie Mode'
        	break
        case 'Movie Mode':
        	nextMode = 'sRGB Mode'
        	break
        case 'sRGB Mode':
        	nextMode = 'Custom Mode'
        	break
        case 'Custom Mode':
        	nextMode = 'Presentation Mode'
        	break
        case 'No Statement':
        	nextMode = 'No Statement'
        	break
        default:
            nextMode = 'No Statement'
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextAspectRatio: Cycle projector modes												 	*
*********************************************************************************************/
def nextAspectRatio () {

	log.debug "nextAspectRatio"
    def commandName = 'Aspect'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {
        case 'Original':
        	nextMode = '4:3'
        	break
        case '4:3':
        	nextMode = 'Wide'
        	break
        case 'Wide':
        	nextMode = 'Original'
        	break
        default:
            nextMode = ''
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* nextAudioInput: Cycle projector modes													 	*
*********************************************************************************************/
def nextAudioInput () {

	log.debug "nextAudioInput"
    def commandName = 'PrjSRCA'
    def curMode = device.currentValue(commandName)
    def nextMode = curMode
    
    switch (curMode) {
        case 'Audio-A':
        	nextMode ='Audio-B'
        	break
        case 'Audio-B':
        	nextMode = 'HDMI'
        	break
        case 'HDMI':
        	nextMode = 'Microphone'
        	break
        case 'Microphone':
        	nextMode = 'Audio-A'
        	break
        case 'No Statement':
        	nextMode = 'No Statement'
        	break
        default:
            nextMode = 'No Statement'
        	break
    }
    
    def command = getValueFromText(nextMode)
    def attributeName = getAttributeName(commandName)
    
    def commandResponse=sendCommand(structureCommand(commandName, command))
    //log.debug "Hub Action Response: ${commandResponse}"
    return commandResponse
    
    sendEvent(
        name: attributeName, // String - The name of the event. Typically corresponds to an attribute name of the device-handler’s capabilities.
        value: valueText, //The value of the event. The value is stored as a String, but you can pass in numbers or other objects. SmartApps will be responsible for parsing the event’s value into back to its desired form (e.g., parsing a number from a string)
        descriptionText: "Setting $attributeName to $valueText", //String - The description of this event. This appears in the mobile application activity feed for the device. If not specified, this will be created using the event name and value.
        displayed: true, //displayable?
        linkText: "$attributeName $valueText", //String - Name of the event to show in the mobile application activity feed, if specified
        //isStateChange: isStateChange(),
        unit: null
    )
}

/* ******************************************************************************************
* getValueText: Returns enumerated value for field/value pair						 		*
*********************************************************************************************/
def getValueText (String fieldName, String value) {
	def valueText = ''
    switch (fieldName) {
    	case 'PJSTATE':  //Double check mapping
        	switch (value) {
            	case '0':
                	valueText = 'Standby'
                    break
                case '1':
                	valueText = 'Lamp On'
                    break
                case '2':
                	valueText = 'Power Saving'
                    break
                case '3':
                	valueText = 'Cooling'
                    break
                case '4':
                	valueText = 'Warming Up'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'PJSTATE2': 
        	valueText = value
            break
        case 'ERRORSTA':  //Double check mapping
        	switch (value) {
            	case '0':
                	valueText = 'Lamp warming'
                    break
                case '1':
                	valueText = 'Low lamp life'
                    break
                case '2':
                	valueText = 'Temperature'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'ERRORSTA2': 
        	valueText = value
            break
        case 'PwSave':
        	switch (value) {
            	case '0':
                	valueText = 'Off'
                    break
                case '1':
                	valueText = '30 min'
                    break
                case '2':
                	valueText = '60 min'
                    break
                case '3':
                	valueText = '90 min'
                    break
                case '4':
                	valueText = '120 min'
                    break
                case '99':
                	valueText = 'No Statement'
                    break
                default:
                	valueText = ''
                    break
            }
            breka
        case 'ecoMode':
        	switch (value) {
            	case '28':
                	valueText = 'Normal Mode'
                    break
                case '27':
                	valueText = 'ECO Mode'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'PrjMode':
        	switch (value) {
            	case '0':
                	valueText = 'Front Projection-Desktop'
                    break
                case '1':
                	valueText = 'Front Projection-Ceiling Mount'
                    break
                case '2':
                	valueText = 'Rear Projection-Desktop'
                    break
                case '3':
                	valueText = 'Rear Projection-Ceiling Mount'
                    break
                case '99':
                	valueText = 'No Statement'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'PrjSRC':
        	switch (value) {
            	case '113':
                	valueText = 'VGA-A'
                	break
                case '114':
                	valueText = 'VGA-B'
                    break
                case '115':
                	valueText = 'S-VIDEO'
                    break
                case '116':
                	valueText = 'COMPOSITE VIDEO'
                    break
                case '117':
                	valueText = 'HDMI'
                    break
                case '99':
                	valueText = 'No Statement'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'VideoMode':
        	switch (value) {
            	case '0':
                	valueText = 'Presentation Mode'
                    break
                case '1':
                	valueText = 'Bright Mode'
                    break
                case '2':
                	valueText = 'Movie Mode'
                    break
                case '3':
                	valueText = 'sRGB Mode'
                    break
                case '4':
                	valueText = 'Custom Mode'
                    break
                case '99':
                	valueText = 'No Statement'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'hide':
        	switch (value) {
            	case '85':
                	valueText = 'On'
                    break
                case '170':
                	valueText = 'Off'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'HIDE0': 
        	switch (value) {
            	case '85':
                	valueText = 'On'
                    break
                case '170':
                	valueText = 'Off'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'Aspect': 
        	switch (value) {
            	case '1':
                	valueText = 'Original'
                    break
                case '2':
                	valueText = '4:3'
                    break
                case '3':
                	valueText = 'Wide'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'Bright': 
        	valueText = value
            break
        case 'Contrast': 
        	valueText = value
            break
        case 'PrjSRCA':
        	switch (value) {
            	case '1':
                	valueText = 'Audio-A'
                    break
                case '2':
                	valueText = 'Audio-B'
                    break
                case '4':
                	valueText = 'HDMI'
                    break
                case '6':
                	valueText = 'Microphone'
                    break
                case '99':
                	valueText = 'No Statement'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        case 'Volume': 
        	valueText = value
            break
        case 'Spk':
        	switch (value) {
            	case '85':
                	valueText = 'On'
                    break
                case '170':
                	valueText = 'Off'
                    break
                default:
                	valueText = ''
                    break
            }
            break
        default: 
        	valueText = 'Attribute Name not found'
            break
    }
    //log.debug "getValueText: Input=$fieldName=$value,  Output=$valueText"
    return valueText
}

/* ******************************************************************************************
* getValueFromText: Returns enumerated value for field/value pair						 	*
*********************************************************************************************/
def getValueFromText (String fieldName, String valueText) {
	def value = ''
    switch (fieldName) {
    	case 'PJSTATE':  //Double check mapping
        	switch (valueText) {
            	case 'Standby':
                	value = '0'
                    break
                case 'Lamp On':
                	value = '1'
                    break
                case 'Power Saving':
                	value = '2'
                    break
                case 'Cooling':
                	value = '3'
                    break
                case 'Warming Up':
                	value = '4'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'PJSTATE2': 
        	value = valueText
            break
        case 'ERRORSTA':  //Double check mapping
        	switch (valueText) {
            	case 'Lamp warming':
                	value = '0'
                    break
                case 'Low lamp life':
                	value = '1'
                    break
                case 'Temperature':
                	value = '2'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'ERRORSTA2': 
        	value = valueText
            break
        case 'PwSave':
        	switch (valueText) {
            	case 'Off':
                	value = '0'
                    break
                case '30 min':
                	value = '1'
                    break
                case '60 min':
                	value = '2'
                    break
                case '90 min':
                	value = '3'
                    break
                case '120 min':
                	value = '4'
                    break
                case 'No Statement':
                	value = '99'
                    break
                default:
                	value = ''
                    break
            }
            breka
        case 'ecoMode':
        	switch (valueText) {
            	case  'Normal Mode':
                	value ='28'
                    break
                case 'ECO Mode':
                	value = '27'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'PrjMode':
        	switch (valueText) {
            	case 'Front Projection-Desktop':
                	value = '0'
                    break
                case 'Front Projection-Ceiling Mount':
                	value = '1'
                    break
                case 'Rear Projection-Desktop':
                	value = '2'
                    break
                case 'Rear Projection-Ceiling Mount':
                	value = '3'
                    break
                case 'No Statement':
                	value = '99'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'PrjSRC':
        	switch (valueText) {
            	case 'VGA-A':
                	value = '113'
                	break
                case 'VGA-B':
                	value = '114'
                    break
                case 'S-VIDEO':
                	value = '115'
                    break
                case 'COMPOSITE VIDEO':
                	value = '116'
                    break
                case 'HDMI':
                	value = '117'
                    break
                case 'No Statement':
                	value = '99'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'VideoMode':
        	switch (valueText) {
            	case 'Presentation Mode':
                	value = '0'
                    break
                case 'Bright Mode':
                	value = '1' 
                    break
                case 'Movie Mode':
                	value = '2'
                    break
                case 'sRGB Mode':
                	value = '3'
                    break
                case 'Custom Mode':
                	value = '4'
                    break
                case 'No Statement':
                	value = '99'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'hide':
        	switch (valueText) {
            	case 'On':
                	value = '85'
                    break
                case 'Off':
                	value = '170'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'HIDE0': 
        	switch (valueText) {
            	case 'On':
                	value = '85'
                    break
                case 'Off':
                	value = '170'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'Aspect': 
        	switch (valueText) {
            	case 'Original':
                	value = '1'
                    break
                case '4:3':
                	value = '2'
                    break
                case 'Wide':
                	value = '3'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'Bright': 
        	value = valueText
            break
        case 'Contrast': 
        	value = valueText
            break
        case 'PrjSRCA':
        	switch (valueText) {
            	case 'Audio-A':
                	value = '1'
                    break
                case 'Audio-B':
                	value = '2'
                    break
                case 'HDMI':
                	value = '4'
                    break
                case 'Microphone':
                	value = '6'
                    break
                case 'No Statement':
                	value = '99'
                    break
                default:
                	value = ''
                    break
            }
            break
        case 'Volume': 
        	value = valueText
            break
        case 'Spk':
        	switch (valueText) {
            	case 'On':
                	value = '85'
                    break
                case 'Off':
                	value = '170'
                    break
                default:
                	value = ''
                    break
            }
            break
        default: 
        	value = 'Attribute Name not found'
            break
    }
    //log.debug "getValueFromText: Input=$fieldName=$valueText,  Output=$value"
    return valueText
}

/* ******************************************************************************************
* getAttributeName: Returns enumerated value for field/value pair				 		*
*********************************************************************************************/
def getAttributeName (String fieldName) {

    def attributeName = ''
    switch (fieldName) {
    	case 'PJSTATE':
        	attributeName = 'projectorStatus'
        	break
        case 'PJSTATE2':
        	attributeName = 'projectorStatus2'
            break
        case 'ERRORSTA':
        	attributeName = 'alertStatus'
            break
        case 'ERRORSTA2':
        	attributeName = 'alertStatus2'
            break
        case 'PwSave':
        	attributeName = 'powerSaving'
            break
        case 'ecoMode':
        	attributeName = 'ecoMode'
            break
        case 'PrjMode':
        	attributeName = 'projectorMode'
            break
        case 'PrjSRC':
        	attributeName = 'sourceSelect'
            break
        case 'VideoMode':
        	attributeName = 'videoMode'
            break
        case 'hide':
        	attributeName = 'blankScreen'
            break
        case 'HIDE0':
        	attributeName = 'blankScreen1'
            break
        case 'Aspect':
        	attributeName = 'aspectRatio'
            break
        case 'Bright':
        	attributeName = 'brightness'
            break
        case 'Contrast': 
        	attributeName = 'contrast'
            break
        case 'PrjSRCA':
        	attributeName = 'audioInput'
            break
        case 'Volume':
        	attributeName = 'volume'
            break
        case 'Spk':
        	attributeName = 'speaker'
            break
        default:
        	attributeName = 'Attribute Name not found'
            break
    }
    //log.debug "getAttributeName: Input=$fieldName, Output=$attributeName"
    return attributeName
    /*
    attribute btnAutoAdj "autoAdjust", "enum", ["On", "Off"]
    */
}

/* ******************************************************************************************
* parseResponseBody: Looks for Set-Cookie command and sets state.cookie				 		*
*********************************************************************************************/
def parseResponseBody (body) {
	def paragraph = body.readLines().join("") 
    log.debug "parseResponseBody: running"
    
    def matches = paragraph.findAll(/<(.+?)>/)
    def fieldName = ''
	def fieldValue = ''
	def fieldType=''
    def status = [:]
	/* TODO troubleshoot:
 		&radio_objvalue=0 (input)
	*/
    // For each HTML element within <>, look for input types and extract settings
    matches.each {
    	
        if (it =~ /<input /) {
        	fieldType='input'
            fieldName = it.find(/NAME="(.+?)"/)
			if (fieldName =~ /radio_objvalue/) {
            	fieldValue = it.find(/VALUE=\s*?"(.*?)"/)
                log.debug "parseResponseBody: $it \r\n FOUND type=$fieldType, field=$fieldName, value=$fieldValue"
            }
            if (it =~ /TYPE="radio"/) {
                if (it =~ /CHECKED/) {
                    fieldValue = it.find(/VALUE="(.*?)"/)
                }
                if (fieldName && fieldValue) {
                	fieldName = fieldName - /NAME="/ - /"/
                    fieldValue = fieldValue - /VALUE="/ - /"/
                    status.put(fieldName, fieldValue)
                }
            } else {
                fieldValue = it.find(/VALUE=\s*?"(.*?)"/)
                if (fieldName && fieldValue) {
                	fieldName = fieldName - /NAME="/ - /"/
                    fieldValue = (fieldValue - /VALUE=/ - /"/ - /"/).trim()
                    status.put(fieldName, fieldValue)
                }
            }
            //log.debug "parseResponseBody: type=$fieldType, field=$fieldName, value=$fieldValue"           
        } else if (it =~ /<select /) {
            fieldType='select'
            fieldName = it.find(/NAME="(.+?)"/)
            //log.debug "parseResponseBody: type=$fieldType, field=$fieldName"
        } else if (it =~/<option / && it =~ /SELECTED/ && fieldType == 'select') {
            fieldValue = it.find(/VALUE="(.*?)"/)
            if (fieldName =~ /PwSave/) {
                log.debug "parseResponseBody: $it \r\n FOUND type=$fieldType, field=$fieldName, value=$fieldValue"
            }
            
            if (fieldName && fieldValue) {
            	fieldName = fieldName - /NAME="/ - /"/
                fieldValue = fieldValue - /VALUE="/ - /"/
                status.put(fieldName, fieldValue)
            }
            //log.debug "parseResponseBody: type=$fieldType, field=$fieldName value=$fieldValue"   
        } 
    }
    return status
}

/* ******************************************************************************************
* parseResponseHeaders: Looks for Set-Cookie command and sets state.cookie				 	*
*********************************************************************************************/
def parseResponseHeaders (response) {
    
    response.readLines().each {
    	def splitVals = it.split(': ')
        switch (splitVals[0]) {
            case "Set-Cookie":
            	def splitVals2 = splitVals[1].split(';')
            	state.cookie = splitVals2[0]
                log.debug "parseResponseHeaders: cookie set to ${splitVals2[0]}"
           //case "Connection": log.debug "parseResponseHeaders: responseList.${splitVals[0]}=${splitVals[1]}"
           //default: log.debug "parseResponseHeaders: default case: responseList.${field}=$val"
        }
    }
}

/* ******************************************************************************************
* structureCommand: Creates body for POST in following format:							 	*
    Content-Type: application/x-www-form-urlencoded\r\n
    Content-Length: 279\r\n
    \r\n
    PJSTATE=0&DSP_SOURCE=0&ERRORSTA=85&FREEZE0=&HIDE0=170&inp_objname=&inp_objvalue=0&redio_objname=&radio_objvalue=0&PJSTATE2=Standby+&PowerOn=Power+ON+&PwSave=99&ERRORSTA2=&ecoMode=28&PrjMode=99&PrjSRC=0&VideoMode=99&hide=170&Aspect=1&Bright=0&Contrast=0&PrjSRCA=1&Volume=0&Spk=170
*********************************************************************************************/
def structureCommand (commandName, commandValue) {
    
    log.debug "structureCommand: Setting $commandName to $commandValue"
    def defaults = ['PJSTATE': '0',
				'DSP_SOURCE': '0',
				'ERRORSTA': '85',
				'FREEZE0': '',
				'HIDE0': '170',
				'inp_objname': '',
				'inp_objvalue': '0',
				'redio_objname': '',
				'radio_objvalue': '0',
				'PJSTATE2': 'Standby ',
				'PowerOn': 'Power ON ',
				'PwSave': '99',
				'ERRORSTA2': '',
				'ecoMode': '28',
				'PrjMode': '99',
				'PrjSRC': '0',
				'VideoMode': '99',
				'hide': '170',
				'Aspect': '1',
				'Bright': '0',
				'Contrast': '0',
				'PrjSRCA': '1',
				'Volume': '0',
				'Spk': '170'  
    ]
    def projStatus = [:]
    if (state.status) {
    	projStatus = state.status
	}
    
    def command=''
    defaults.each { key, value ->
    	if (key==commandName) {
        	//log.debug "structureCommand: Adding commanded input: &${commandName}=${commandValue}"
        	command = command + "&" + commandName + "=" + commandValue
        } else if (projStatus.containsKey(key)) {
        	//log.debug "structureCommand: Adding state status: &${key}=${projStatus[key]}"
        	command = command + "&" + key + "=" + projStatus[key]
        } else {
        	//log.debug "structureCommand: Adding default status: &${key}=${value}"
        	command = command + "&" + key + "=" + value
        }
    }
    command = command -"&"
    def body = "Content-Type: application/x-www-form-urlencoded\r\nContent-Length: ${command.length()}\r\n\r\n${command}"
    log.debug "structureCommand: body=\r\n$body"
    return body
}

/********************************************************************************************
* removeFirstLine: Removes first line from inputText f to support later parsing				*
********************************************************************************************/
def removeIpToHTMLTag(inputText) {
	
	def inputList = inputText.readLines()
  	def startPoint = 0
    0.upto(3) {
    	if (inputList[it].contains("<html>")) {
    		startPoint=it
            log.debug "removeToHTMLTag: Searched '{inputList[it]}', found <html> at line $startPoint"
    	} else {
        	log.debug "removeToHTMLTag: Searched '{inputList[it]}', did not find <html>"
        }
    }
	inputText = inputList[startPoint..inputList.size-1].join("")
	return inputText
}

/* ******************************************************************************************
* Poll: Poll Refresh status																 	*
*********************************************************************************************/
def poll() {

	log.debug "Executing 'poll'"
    //refresh()
}

/* ******************************************************************************************
* Refresh: Send hubAction Get to refresh status											 	*
*********************************************************************************************/
def refresh() {

	log.debug "Executing 'refresh'"
	getStatus()
}

/* ******************************************************************************************
* getStatus: Send hubAction Get to get projector status											 	*
*********************************************************************************************/
def getStatus() {
	log.debug "Executing 'getStatus'"
    
    def hubActionCookie = ''
    if (state.cookie) {
    	hubActionCookie = state.cookie
    } 
    
    def hubActionPath = "/status.htm"
    try {
		def hubActionVar = new physicalgraph.device.HubAction(
			method: "GET",
			path: "/status.htm",
			headers: [
				HOST: getHostAddress(),
                Cookie: hubActionCookie
                //Referer: "http://192.168.2.216/links.htm"
			]
		)
		hubActionVar
		return hubActionVar
	} catch (e) {
    	log.debug "HubAction Error: $e on hubAction: $hubActionVar"
    }
   
}

/* ******************************************************************************************
* sendCommand: Send HubAction POST for input command									 	*
*********************************************************************************************/
def sendCommand (command) {
	
	log.debug "Executing HubAction on command=$command"
    def hubActionCookie = 'ATOP=ryPKd'
    if (state.cookie) {
    	hubActionCookie = state.cookie
    }
    
    def hostAddress = getHostAddress()
    def splitVals = hostAddress.split(':')
    
    def hubActionHeaders = [
        'HOST': hostAddress,
        'Referer': "${splitVals[0]}/status.htm",
        'Cookie': hubActionCookie,
        'Connection': "keep-alive"
    ]
    hubActionHeaders.put("Upgrade-Insecure-Requests", "1")
    //hubActionHeaders.put("Content-Type", "application/x-www-form-urlencoded")     
            
    def hubActionPath = "/tgi/status.tgi"
    
    try {
        def hubActionVar = new physicalgraph.device.HubAction(
            'method': "POST",
            'path': hubActionPath,
            'headers': hubActionHeaders,
            'body': command
		)
        log.debug hubActionVar
        hubActionVar
        return hubActionVar
    } catch (e) {
    	log.debug "HubAction Error: $e on hubAction: $hubActionVar"
    }

}

/********************************************************************************************
* convertIPToHex: Converts in ipadress to hex (currently unused)							*																				*
********************************************************************************************/
private String convertIPToHex(ipAddress) {

	return Long.toHexString(converIntToLong(ipAddress));

}

/********************************************************************************************
* converIntToLong: Converts in ipadress:port from int to long (currently unused)			*																				*
********************************************************************************************/
private Long converIntToLong(ipAddress) {

	log.debug "from Long converIntToLong = $ipAddress"
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }
    return result & 0xFFFFFFFF;

}

/********************************************************************************************
* getHostAddress: Grabs hex device network ID (IpAddress:port in HEX!!!)and parses to 		*
* decimal format (nnn.nnn.nnn.nnn:xxxx) 													*																				*
********************************************************************************************/
private getHostAddress() {

	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port

}

/********************************************************************************************
* convertHexToInt: Converts hex value to integer											*																				*
********************************************************************************************/
private Integer convertHexToInt(hex) {
	
	if (hex[0..1]=="0x") {
    	hex = hex[2..-1]
    }
	Integer.parseInt(hex,16)

}

/********************************************************************************************
* convertHexToInt: Converts hex value to long												*																				*
********************************************************************************************/
private Long convertHexToLong(hex) {

    if (hex[0..1]=="0x") {
    	hex = hex[2..-1]
    }
    Long.parseLong(hex,16)

}

/********************************************************************************************
* convertHexToIP: Converts hex ip value to string in nnn.nnn.nnn.nnn format					*																				*
********************************************************************************************/
private String convertHexToIP(hex) {

	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")

}
