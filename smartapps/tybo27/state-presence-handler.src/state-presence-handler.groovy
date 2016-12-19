/**
 *  State Presence Handler
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
definition(
    name: "State Presence Handler",
    namespace: "tybo27",
    author: "tybo27",
    description: "Makes use of presence sensors to change states",
    category: "Family",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Person 1") {
		input "presence1", "capability.presenceSensor", multiple: false, title: "Using whose presence"
        input "dayMode1", "mode", title: "Day mode to transition into when person 1 is present", multiple: false, required: false
        input "eveningMode1", "mode", title: "Evening mode to transition into when person 1 is present", multiple: false, required: false
        input "nightMode1", "mode", title: "Night mode to transition into when person 1 is present", multiple: false, required: false
	}
    section("Person 2") {
        input "presence2", "capability.presenceSensor", multiple: false, title: "Using whose presence"
    	input "dayMode2", "mode", title: "Day mode to transition into when person 2 is present", multiple: false, required: false
        input "eveningMode2", "mode", title: "Evening mode to transition into when person 2 is present", multiple: false, required: false
		input "nightMode2", "mode", title: "Night mode to transition into when person 2 is present", multiple: false, required: false
    }
    section("Joint Modes") {
    	input "dayModeJoint", "mode", title: "Day mode to transition into when both people are present", multiple: false, required: false
        input "eveningModeJoint", "mode", title: "Evening mode to transition into when both people are present", multiple: false, required: false
        input "nightModeJoint", "mode", title: "Night mode to transition into when both people are present", multiple: false, required: false
    }
    section("Away Mode") {
    	input "awayMode", "mode", title: "Mode to transition into when both people are away", multiple: false, required: false
        input "vacationMode", "mode", title: "Mode to transition into when both people are away for over X hours", multiple: false, required: false
        input "vacationModeDelay", "number", title: "Number of hours to transition to vacation mode", multiple: false, required: false
    }
    section("Times") {
    	input "weekdayNightTime", "time", title: "Time to auto transition to night on weekdays", multiple: false, required: false
        input "weekdayDayTime", "time", title: "Time to auto transition to day on weekdays", multiple: false, required: false
        input "weekendNightTime", "time", title: "Time to auto transition to night on weekends", multiple: false, required: false
        input "weekendDayTime", "time", title: "Time to auto transition to day on weekends", multiple: false, required: false
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    //subscribe
    subscribe(location, modeChangeHandler)
    subscribe(location, "position", positionChange)
    subscribe(presence1, "presence", modeHandler)
    subscribe(presence2, "presence", modeHandler)
    //subscribe(location, "sunset", sunsetHandler)
    //subscribe(location, "sunrise", sunriseHandler)
    //schedule(theTime, handler)
    subscribe(location, "sunsetTime", sunsetTimeHandler)
    subscribe(location, "sunriseTime", sunriseTimeHandler)
    subscribe(location, "mode", modeChangeHandler)

}

// TODO: implement event handlers
def modeHandler (evt) {
	if (presence1=="present" && presence2=="present") {
    
    } else if (presence1=="present") {
    
    } else if (presence2=="present") {
    
    } else {
    
    
    }
}

def timeHandler (evt) {
	/*
    timeHandler returns timePeriod which handles transitions from:
    	night to day at weekdayDayTime or weekendDayTime
        day to evening at sunset
        evening to night at weekdayNightTime or weekendNightTime
    */
    
    def df = new java.text.SimpleDateFormat("EEEE")
    // Ensure the new date object is set to local time zone
    df.setTimeZone(location.timeZone)
    def curDay = df.format(new Date())
	def curTime = new Date()
    
    def weekdays = [Monday, Tuesday, Wednesday, Thursday, Friday]
    def isWeekday = weekdays.contains(curDay)
    log.debug "Day is $curDay, weekday=$isWeeday, and Time is $curTime"
    
    // intialize timePediod to day
    def timePeriod = 'day'
    
    // handle override (alarm override transition for night)
    if (isWeekday) {
    	if (timeOfDayIsBetween(weekdayDayTime, sunsetTime, curTime, location.timeZone)) {
        	timePeriod = 'day'
        } else if (timeOfDayIsBetween(sunsetTime, weekdayNightTime, curTime, location.timeZone)) {
    		timePeriod = 'evening'
    	} else if (timeOfDayIsBetween(weekdayNightTime, weekdayDayTime, curTime, location.timeZone)) {
    		timePeriod = 'night'
    	}
	} else {
    	if (timeOfDayIsBetween(weekendDayTime, sunsetTime, curTime, location.timeZone)) {
        	timePeriod = 'day'
        } else if (timeOfDayIsBetween(sunsetTime, weekendNightTime, curTime, location.timeZone)) {
    		timePeriod = 'evening'
    	} else if (timeOfDayIsBetween(weekendNightTime, weekendDayTime, curTime, location.timeZone)) {
    		timePeriod = 'night'
    	}
    }
    return timePeriod
}