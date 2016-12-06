/**
 *  eGauge Monitor using HubAction for API calls
 * IMPORTANT - eGauge device network ID must be the LAN IP:port in hex!!!!
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
 
preferences {
    input("usageWindowThresh", "number", title: "Time in minutes for usage alert calculations, reported as temperature to allow dashboard access")
}

metadata {
	definition (name: "Egauge Monitor", namespace: "tybo27", author: "tybo27") {
	capability "Power Meter"
    capability "Refresh"
	capability "Polling"
    capability "Sensor"
    capability "Temperature Measurement"
        
    attribute "netEnergyToday", "number"
    attribute "gridEnergyToday", "number"
    attribute "solarEnergyToday", "number"
    
    attribute "gridPower", "number"
    attribute "solarPower", "number"
    attribute "frequency", "number"
    attribute "voltage1", "number"
    attribute "voltage2", "number"
}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    		//st.Home.home1
            valueTile("netEnergy", "device.netEnergyToday") {
   	         state("netEnergyToday", label: '${currentValue}kWh\nNet', unit:"kWh", icon: "st.Home.home1", action:refresh, backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ]
                )
        	}
            //st.Weather.weather14
            valueTile("solarEnergy", "device.solarEnergyToday") {
   	         state("solarEnergyToday", label: '${currentValue}kWh\nSolar', unit:"kWh", icon: "st.Weather.weather14", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#d3d3d3"],
    	            ]
                )
        	}    
            //st.Appliances.appliances17
            valueTile("gridEnergy", "device.gridEnergyToday") {
   	         state("gridEnergyToday", label: '${currentValue}kWh\nGrid', unit:"kWh", icon: "st.Appliances.appliances17", backgroundColors: [
                    [value: 0, color: "#d3d3d3"],
                    [value: 1, color: "#FF0000"],
    	            ]
                )
        	}  
            
            //st.Home.home1
            valueTile("net", "device.power", canChangeIcon: true) {
   	         state("power", label: '${currentValue}kW\nNet', unit:"kW", icon: "st.Home.home1", action:refresh, backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ]
                )
        	}
            //st.Weather.weather14
            valueTile("solar", "device.solarPower") {
   	         state("solarPower", label: '${currentValue}kW\nSolar', unit:"kW", icon: "st.Weather.weather14", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#d3d3d3"],
    	            ]
                )
        	}    
            //st.Appliances.appliances17
            valueTile("grid", "device.gridPower") {
   	         state("gridPower", label: '${currentValue}kW\nGrid', unit:"kW", icon: "st.Appliances.appliances17", backgroundColors: [
                    [value: 0, color: "#d3d3d3"],
                    [value: 1, color: "#FF0000"],
    	            ]
                )
        	}    
            //st.Entertainment.entertainment15
            valueTile("frequency", "device.frequency") {
   	         state("frequency", label: '${currentValue}Hz', unit:"Hz", backgroundColors: [
                    [value: 50, color: "#FF0000"],
                    [value: 60, color: "#32CD32"],
                    [value: 70, color: "#FF0000"],
    	            ]
                )
        	}    

			valueTile("voltage1", "device.voltage1") {
   	         state("voltage1", label: '${currentValue}V', unit:"V", backgroundColors: [
                    [value: 110, color: "#FF0000"],
                    [value: 120, color: "#32CD32"],
                    [value: 130, color: "#FF0000"],
    	            ]
                )
        	}  
            
            valueTile("voltage2", "device.voltage2") {
   	         state("voltage2", label: '${currentValue}V', unit:"V", backgroundColors: [
                    [value: 110, color: "#FF0000"],
                    [value: 120, color: "#32CD32"],
                    [value: 130, color: "#FF0000"],
    	            ]
                )
        	}  
            standardTile("refresh", "device.energy_today", inactiveLabel: false, decoration: "flat") {
                state "default", action:"refresh()", icon:"st.secondary.refresh"
            }

        main ("net")
        details(["netEnergy", "solarEnergy", "gridEnergy", "net", "solar", "grid","frequency","voltage1","voltage2", "refresh"])
	}
}


// parse events into attributes
def parse(String description) {
	//log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response

	//log.debug "headersAsString: $headersAsString"
    //log.debug "headerMap: $headerMap"
    //log.debug "body: $body"
    log.debug "HubAction Returned status: $status"

	if (status>=200 && status<300) {
		body = removeBodyHeaders(body)
		//log.debug "updated body: $body"
		
		//getXML
		def rootNode =  new XmlSlurper().parseText(body)
		//log.debug "XML root node: ${rootNode.name()}"
		switch (rootNode.name()){
			case 'group':
                def groupData = [:]	
				groupData.put("timeStamp", [:])
                def numColumns = rootNode.data[0].@columns
                def epoch = rootNode.data[0].@epoch
				def cumIdx = 0
                def cnameMap = [:]
                
                def netToday
                def gridToday
                def solarToday
                def temp
                
                rootNode.data.eachWithIndex { data, dIdx ->
					
                    String timeStamp = data.@time_stamp
                    long timeUTC = Long.parseLong(timeStamp[2..-1],16)
                    String timeDeltaText = data.@time_delta
                    int timeDelta = timeDeltaText.toInteger()
                    
                    //log.debug "Data for dIdx:$dIdx contains timeStamp=$timeStamp($timeUTC), timeDelta=$timeDelta"
					
                    if (dIdx==0) {
                        data.cname.eachWithIndex { cname, idx ->
                            if (cname.@t == "P") {
                                def powerName = cname.text()
                                powerName = powerName.replace("+","Plus")
                                groupData.put(powerName, [:])
                                cnameMap.put(idx, powerName)
                            } else {
                                log.debug "Did not add cname type=${cname.@t}, cname text=cname"
                            }
                        }
                        //log.debug "groupData map updated with $groupData"
                    }
                    data.r.eachWithIndex { r, rIdx ->
                        r.c.eachWithIndex { c, cIdx ->
                        	def timeValue = timeUTC - timeDelta*rIdx
                        	//log.debug "Value loop at rIdx=$rIdx, cIdx=$cIdx and cumIdx=$cumIdx, timeValue=$timeValue(orig=$timeUTC)"
                            groupData["timeStamp"].put(cumIdx, timeUTC - timeDelta*rIdx)
                            groupData[cnameMap[cIdx]].put(cumIdx, c.toFloat()/3600000)
                            
                        }
                        cumIdx++
                    }
                }
                // Debug output to test data parsing
                groupData.each {key, value ->
					log.debug "$key contains $value"
				}
                def midnightUTC = (timeToday("00:00", location.timeZone).time/1000)//.round(0)
                if (midnightUTC >= groupData["timeStamp"][0]) {
                	midnightUTC = midnightUTC - 24*60
                }
                
                if ((groupData["timeStamp"][1]-midnightUTC).abs() < (groupData["timeStamp"].get(2)-midnightUTC).abs()) {
                	solarToday = groupData["SolarPlus"][0]-groupData["SolarPlus"][1]
                    gridToday = groupData["Grid"][0]-groupData["Grid"][1]
                    netToday =gridToday-solarToday
                    temp = groupData["Grid"][0]-groupData["Grid"][2]
                } else {
                	solarToday = groupData["SolarPlus"][0]-groupData["SolarPlus"][2]
                    gridToday = groupData["Grid"][0]-groupData["Grid"][2]
                    netToday =gridToday-solarToday
                    temp = groupData["Grid"][0]-groupData["Grid"][1]
                }
                log.debug "solarToday=${solarToday.round(3)}kWh, gridToday=${gridToday.round(3)}kWh, netToday=${netToday.round(3)}kWh, temp=${temp.round(3)}kWh"
                def gridEnergyEvent = createEvent(name: 'gridEnergyToday', value: gridToday.round(3))
                def solarEnergyEvent = createEvent(name: 'solarEnergyToday', value: solarToday.round(3))
                def netEnergyEvent = createEvent(name: 'netEnergyToday', value: netToday.round(3))
                def tempEvent = createEvent(name: 'temperature', value: temp.round(3))
                return ([gridEnergyEvent, solarEnergyEvent, netEnergyEvent, tempEvent])
				break
                
			case 'measurements':
				log.debug "Need to test measurements"
                def currentNetPower = 0
                def currentSolarPower = 0
                def currentGridPower = 0
                
                def currentFreq = 0
                def currentV1 = 0
                def currentV2 = 0
                
                def toDateNetEnergy = 0
                def toDateSolarEnergy = 0
                def toDateGridEnergy = 0
                
                def timeStamp = rootNode.data.timeStamp
                rootNode.meter.each { meter ->
                    log.debug meter.name() + ":"+ meter.@title + ":" + meter.text()
                    if (meter.name().equals("meter") && meter.@title.equals("Grid")) {
                        log.debug "Found Grid Power"
                        currentGridPower = (meter.power.toFloat()/1000).round(3)
                        toDateGridEnergy = (meter.energy.toFloat())
                    }

                    if (meter.name().equals("meter") && meter.@title.equals("Solar")) {
                        log.debug "Found Solar Power"
                        currentSolarPower = (meter.power.toFloat()/1000).round(3)
                        toDateSolarEnergy = (meter.energy.toFloat())
                    }

                }
                log.debug "currentGridPower=${currentGridPower}"
                log.debug "currentSolarPower=${currentSolarPower}"

                currentNetPower = (currentGridPower - currentSolarPower).round(3)
                toDateNetEnergy = toDateGridEnergy - toDateSolarEnergy
                log.debug "currentNetPower=${currentNetPower}"

                currentFreq = rootNode.frequency.toFloat().round(1)
                log.debug "currentFrequency=$currentFreq"

                rootNode.voltage.each { v ->
                    log.debug v.name() + ":"+ v.@ch + ":" + v.text()
                    if (v.name().equals("voltage") && v.@ch.equals("0")) {
                        log.debug "Found Ch1 Voltage"
                        currentV1 = v.toFloat().round(1)
                    }

                    if (v.name().equals("voltage") && v.@ch.equals("1")) {
                        log.debug "Found Ch2 Voltage"
                        currentV2 = v.toFloat().round(1)
                    }
                }   
                def powerEvent = createEvent(name: 'power', value: (currentNetPower))
                def gridPowerEvent = createEvent(name: 'gridPower', value: (currentGridPower))
                def solarPowerEvent = createEvent(name: 'solarPower', value: (currentSolarPower))
                def frequencyEvent = createEvent(name: 'frequency', value: (currentFreq))
                def v1Event = createEvent(name: 'voltage1', value: (currentV1))
                def v2Event = createEvent(name: 'voltage2', value: (currentV2))	
                return [powerEvent, gridPowerEvent, solarPowerEvent, frequencyEvent, v1Event, v2Event]
				break
			default:
                log.debug "default"
				break
		}
	}
}

def poll() {
	log.debug "Executing 'poll'"
    getDailyValuesHub()
}

def refresh() {
  log.debug "Executing 'refresh'"
  delaybetween([getDailyValuesHub(),getInstantValuesHub()])
}

def getDailyValuesHub () {
	def offset=10 // offset for api call in s
    def nowUTC = Math.round( now()/1000 - offset )  //.round(0)
    def midnightUTC = (timeToday("00:00", location.timeZone).time/1000)//.round(0)
    def windowUTC = nowUTC - settings.usageWindowThresh*60
    
    def times = [nowUTC, windowUTC, midnightUTC]
    times = times.sort().reverse()
	//def T = "${times[0]},${times[1]},${times[2]}"
    log.debug "T=${times[0]},${times[1]},${times[2]}"
    
	log.debug "Executing 'getDailyValuesHub'"
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/cgi-bin/egauge-show",
        headers: [
            HOST: getHostAddress()
        ],
        query: [T: "${times[0]},${times[1]},${times[2]}"]
		)
    //log.debug "result= $result"
    
    result
    return result
}

def getInstantValuesHub () {
	
	log.debug "Executing 'getInstantValuesHub'"
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/cgi-bin/egauge?noteam",
        headers: [
            HOST: getHostAddress()
        ],
        //query: ["noteam"]
		)
    //log.debug "result= $result"
    
    result
    return result
}

def removeBodyHeaders (body) {
	// Removes first line and second if <!DOCTYPE 
	def bodyList = body.readLines()
	body = bodyList[1..bodyList.size-1].join("")
    
	if (bodyList[1].contains("<!DOCTYPE")) {
		body = bodyList[2..bodyList.size-1].join("")
	} else {
		body = bodyList[1..bodyList.size-1].join("")
	}
	return body
}

private String convertIPToHex(ipAddress) {
	return Long.toHexString(converIntToLong(ipAddress));
}

private Long converIntToLong(ipAddress) {
	log.debug "from Long converIntToLong = $ipAddress"
	long result = 0
	def parts = ipAddress.split("\\.")
    for (int i = 3; i >= 0; i--) {
        result |= (Long.parseLong(parts[3 - i]) << (i * 8));
    }
    return result & 0xFFFFFFFF;
}

private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}