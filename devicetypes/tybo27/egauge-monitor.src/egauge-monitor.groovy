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
    input("usageWindowThresh", "number", title: "Time in minutes for usage alert calculations, reported as temperature to allow dashboard access", required: true,
          displayDuringSetup: true)
    input("resolution", "number", title: "Number of decimals to show for current and daily power", required: true,
          displayDuringSetup: true)
}

metadata {
	definition (name: "Egauge Monitor", namespace: "tybo27", author: "tybo27") {
	capability "Power Meter"
    capability "Refresh"
	capability "Polling"
    capability "Sensor"
    capability "Temperature Measurement"
        
    // Net energy atttibutes
	attribute "netEnergyToday", "number"
    attribute "gridEnergyToday", "number"
    attribute "solarEnergyToday", "number"
    
    // Instantaneous Attributes
    attribute "refreshTime", "number"
    attribute "gridPower", "number"
    attribute "solarPower", "number"
    attribute "frequency", "number"
    attribute "voltage1", "number"
    attribute "voltage2", "number"
}

	simulator {
	}

	tiles(scale:2) {
    		
            // Main Net Energy tile
            standardTile("netEnergyMain", "device.netEnergyToday", width: 1, height: 1, canChangeIcon: true,) {
   	         state("netEnergyToday", label: '${currentValue}', unit:"kWh", icon: "st.Weather.weather14", action: "refresh.refresh", backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ] )
        	}
            
            // Refresh button and time
            standardTile("refresh", "device.net", inactiveLabel: true, decoration: "flat", width: 2, height: 1) {
                state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
            }
            standardTile("refreshTime", "device.refreshTime", inactiveLabel: true, decoration: "flat", width: 4, height: 1) {
                state "refreshTime", label:'${currentValue}'
            }
            
            // Daily Energy Tiles
            standardTile("netEnergy", "device.netEnergyToday", width: 2, height: 2) {
   	         state("netEnergyToday", label: '${currentValue}', unit:"kWh", icon: "st.Home.home1", backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ])
        	}
            standardTile("solarEnergy", "device.solarEnergyToday", width: 2, height: 2) {
   	         state("solarEnergyToday", label: '${currentValue}', unit:"kWh", icon: "st.Weather.weather14", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#d3d3d3"],
    	            ])
        	}    
            standardTile("gridEnergy", "device.gridEnergyToday", width: 2, height: 2) {
   	         state("gridEnergyToday", label: '${currentValue}', unit:"kWh", icon: "st.Appliances.appliances17", backgroundColors: [
                    [value: 0, color: "#d3d3d3"],
                    [value: 1, color: "#FF0000"],
    	            ])
        	}  
            //Labels for Daily Energy
            valueTile("netTodayHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Net Daily kWh')//, icon: "st.Home.home1" )
        	}
            valueTile("solarTodayHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Solar Daily kWh')//, icon: "st.Weather.weather14" )
        	}
            valueTile("gridTodayHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Grid Daily kWh')//, icon: "st.Appliances.appliances17" )
        	}  
            
            //Current Power Tiles
            standardTile("net", "device.power", width: 2, height: 2) {
   	         state("power", label: '${currentValue}', unit:"kW", icon: "st.Home.home1", backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ])
        	}
            standardTile("solar", "device.solarPower", width: 2, height: 2) {
   	         state("solarPower", label: '${currentValue}', unit:"kW", icon: "st.Weather.weather14", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#d3d3d3"],
    	            ])
        	}    
            standardTile("grid", "device.gridPower", width: 2, height: 2) {
   	         state("gridPower", label: '${currentValue}', unit:"kW", icon: "st.Appliances.appliances17", backgroundColors: [
                    [value: 0, color: "#d3d3d3"],
                    [value: 1, color: "#FF0000"],
    	            ])
        	}    
            //Labels for current power
            valueTile("netCurrentHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Net Current kW')//, icon: "st.Home.home1" )
        	}
            valueTile("solarCurrentHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Solar Current kW')//, icon: "st.Weather.weather14" )
        	}
            valueTile("gridCurrentHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1, decoration: "flat") {
   	         state("default", label: 'Grid Current kW')//, icon: "st.Appliances.appliances17" )
        	}  
            
            //Frequency and Voltage
            standardTile("frequency", "device.frequency", width: 2, height: 2) {
   	         state("frequency", label: '${currentValue}', unit:"Hz", icon: "st.Entertainment.entertainment15", backgroundColors: [
                    [value: 50, color: "#FF0000"],
                    [value: 60, color: "#32CD32"],
                    [value: 70, color: "#FF0000"],
    	            ])
        	}    
            valueTile("voltage1", "device.voltage1", width: 2, height: 2) {
   	         state("voltage1", label: '${currentValue}', unit:"V", backgroundColors: [
                    [value: 110, color: "#FF0000"],
                    [value: 120, color: "#32CD32"],
                    [value: 130, color: "#FF0000"],
    	            ])
        	}  
            valueTile("voltage2", "device.voltage2", width: 2, height: 2) {
   	         state("voltage2", label: '${currentValue}', unit:"V", backgroundColors: [
                    [value: 110, color: "#FF0000"],
                    [value: 120, color: "#32CD32"],
                    [value: 130, color: "#FF0000"],
    	            ])
        	}  
            //Labels for Frequency and Voltage
            valueTile("freqHeader", "device.temperature", inactiveLabel: true, width: 2, height: 1) {
   	         state("default", label: 'Frequency(Hz)')//, icon: "st.Home.home1" )
        	}
            valueTile("v1Header", "device.temperature", inactiveLabel: true, width: 2, height: 1) {
   	         state("default", label: 'Voltage 1(V)')//, icon: "st.Weather.weather14" )
        	}
            valueTile("v2Header", "device.temperature", inactiveLabel: true, width: 2, height: 1) {
   	         state("default", label: 'Voltage 2(V)')//, icon: "st.Appliances.appliances17" )
        	}  
            
        main ("netEnergyMain")
        details(["refresh", "refreshTime", 
        	"netEnergy", "solarEnergy", "gridEnergy", "netTodayHeader", "solarTodayHeader", "gridTodayHeader", 
            "net", "solar", "grid","netCurrentHeader", "solarCurrentHeader", "gridCurrentHeader",
            "frequency","voltage1","voltage2", "freqHeader", "v1Header","v2Header"
        ])
	}
}

/* ******************************************************************************************
* Parse hubAction returned queries into power and energy data							 	*
*********************************************************************************************/
def parse(String description) {
	//log.debug "Parsing '${description}'"
	def msg = parseLanMessage(description)
    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
	def roundResolution = settings.resolution // handle undefined case
    
    log.debug "HubAction Returned status: $status"

	if (status>=200 && status<300) {
		body = removeBodyHeaders(body)
		
		// Parse XML for data
		def rootNode =  new XmlSlurper().parseText(body)
		switch (rootNode.name()){
			// rootNode of group indicates return from getDailyValuesHub
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
                
                // Loop through data fileds and capture name, time and value
                rootNode.data.eachWithIndex { data, dIdx ->
					
                    String timeStamp = data.@time_stamp
                    def timeUTC = convertHexToLong (timeStamp)
                    String timeDeltaText = data.@time_delta
                    int timeDelta = timeDeltaText.toInteger()
					
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
                    }
                    data.r.eachWithIndex { r, rIdx ->
                        r.c.eachWithIndex { c, cIdx ->
                        	def timeValue = timeUTC - timeDelta*rIdx
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
                
                // Only create/fire event if exactly three data points are returned (only three times requested)
                if (groupData["timeStamp"].size()==3) {
                    def midnightUTC = (timeToday("00:00", location.timeZone).time/1000)//.round(0)
                    if (midnightUTC >= groupData["timeStamp"][0]) {
                        midnightUTC = midnightUTC - 24*60
                    }
					// Priginal data requested in descending order, first is now, figure out which of the next two is midnight and which is x minutes ago
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
                    // Create and return events
                    log.debug "solarToday=${solarToday.round(3)}kWh, gridToday=${gridToday.round(3)}kWh, netToday=${netToday.round(3)}kWh, temp=${temp.round(3)}kWh"
                    def gridEnergyEvent = createEvent(name: 'gridEnergyToday', value: gridToday.round(roundResolution))
                    def solarEnergyEvent = createEvent(name: 'solarEnergyToday', value: solarToday.round(roundResolution))
                    def netEnergyEvent = createEvent(name: 'netEnergyToday', value: netToday.round(roundResolution))
                    def tempEvent = createEvent(name: 'temperature', value: temp.round(roundResolution))
                    return ([gridEnergyEvent, solarEnergyEvent, netEnergyEvent, tempEvent])
                } else {
                	log.debug "Invalid parse, groupData size=${groupData["timeStamp"].size()}"
                }
				break
            // rootNode of measurements indicates return from getInstantValuesHub
			case 'measurements':
                def timeStamp = (rootNode.timestamp).toLong()
                def refreshDate = new Date(timeStamp*1000)
                String refreshTime = refreshDate.format("'Refreshed:' MMM d 'at' h:mm:ss a z", location.timeZone)
                log.debug "Measurements timeStamp=$timeStamp, refreshDate=$refreshDate and refreshTime=$refreshTime"
                def currentNetPower = 0
                def currentSolarPower = 0
                def currentGridPower = 0
                
                def currentFreq = 0
                def currentV1 = 0
                def currentV2 = 0
                
                def toDateNetEnergy = 0
                def toDateSolarEnergy = 0
                def toDateGridEnergy = 0
                
                // Parse Meters for Grid and Solar 
                rootNode.meter.each { meter ->
                    log.debug meter.name() + ":"+ meter.@title + ":" + meter.text()
                    if (meter.@title.equals("Grid")) {
                        currentGridPower = (meter.power.toFloat()/1000).round(roundResolution)
                        toDateGridEnergy = (meter.energy.toFloat())
                    }
                    if (meter.@title.equals("Solar")) {
                        currentSolarPower = (meter.power.toFloat()/1000).round(roundResolution)
                        toDateSolarEnergy = (meter.energy.toFloat())
                    }
                }

                currentNetPower = (currentGridPower - currentSolarPower).round(roundResolution)
                toDateNetEnergy = toDateGridEnergy - toDateSolarEnergy
                log.debug "currentGridPower=${currentGridPower} and currentSolarPower=${currentSolarPower}, currentNetPower=${currentNetPower}"

                currentFreq = rootNode.frequency.toFloat().round(1)
                log.debug "currentFrequency=$currentFreq"

				// Loop through voltages to captrue both channels
                rootNode.voltage.each { v ->
                    log.debug v.name() + ":"+ v.@ch + ":" + v.text()
                    if (v.name().equals("voltage") && v.@ch.equals("0")) {
                        currentV1 = v.toFloat().round(1)
                        log.debug "Ch1 Voltage=$currentV1"
                    }

                    if (v.name().equals("voltage") && v.@ch.equals("1")) {
                        currentV2 = v.toFloat().round(1)
                        log.debug "Ch1 Voltage=$currentV2"
                    }
                }   
                // Create and return events
                def powerEvent = createEvent(name: 'power', value: (currentNetPower))
                def gridPowerEvent = createEvent(name: 'gridPower', value: (currentGridPower))
                def solarPowerEvent = createEvent(name: 'solarPower', value: (currentSolarPower))
                def frequencyEvent = createEvent(name: 'frequency', value: (currentFreq))
                def v1Event = createEvent(name: 'voltage1', value: (currentV1))
                def v2Event = createEvent(name: 'voltage2', value: (currentV2))	
                def refreshTimeEvent = createEvent(name: 'refreshTime', value: (refreshTime))
                return [powerEvent, gridPowerEvent, solarPowerEvent, frequencyEvent, v1Event, v2Event, refreshTimeEvent]
				break
                
			default:
                log.debug "default reached on rootNode.name switch statement"
				break
		}
	}
}

/* ******************************************************************************************
* Poll is run automatically every several minutes, only request dailyValues since those  	*
* are displayed as main, and used as temperature in Hub Dashboard							*
*********************************************************************************************/
def poll() {
	log.debug "Executing 'poll'"
    getDailyValuesHub()
}

/********************************************************************************************
* Refresh is requested when on the device page, update both daily and instantaneous numbers	*
********************************************************************************************/
def refresh() {
  log.debug "Executing 'refresh'"
  getDailyValuesHub()
  getInstantValuesHub()
}

/********************************************************************************************
* getDailyValuesHub calculates current time, x mintues ago, and midnight today to send a	*
* hubAction to the egauge requesting energy data for those times							*
********************************************************************************************/
def getDailyValuesHub () {
	log.debug "Executing 'getDailyValuesHub'"
    def offset=10 // offset for api call in s (ensure data is written to register already)
    def nowUTC = Math.round( now()/1000 - offset )  //.round(0)
    def midnightUTC = (timeToday("00:00", location.timeZone).time/1000)//.round(0)
    def windowUTC = nowUTC - settings.usageWindowThresh*60
    
    def times = [nowUTC, windowUTC, midnightUTC]
    times = times.sort().reverse() // times must be sent in descending order
    log.debug "T=${times[0]},${times[1]},${times[2]}"
	
    // Define and return hubAction
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/cgi-bin/egauge-show",
        headers: [
            HOST: getHostAddress()
        ],
        query: [T: "${times[0]},${times[1]},${times[2]}"]
	)
    result
    return result
}

/********************************************************************************************
* getInstantValuesHub sends a hubAction to the egauge requesting 'noteam' power data		*
********************************************************************************************/
def getInstantValuesHub () {
	
	log.debug "Executing 'getInstantValuesHub'"
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/cgi-bin/egauge?noteam",
        headers: [
            HOST: getHostAddress()
        ],
	)
    result
    return result
}


/********************************************************************************************
* removeBodyHeaders removes first line (xml tag) and second if !DOCTYPE to clean up for 	*
* XMLslurper																				*
********************************************************************************************/
def removeBodyHeaders (body) {
	
	def bodyList = body.readLines()
	body = bodyList[1..bodyList.size-1].join("")
    
	if (bodyList[1].contains("<!DOCTYPE")) {
		body = bodyList[2..bodyList.size-1].join("")
	} else {
		body = bodyList[1..bodyList.size-1].join("")
	}
	return body
}

/********************************************************************************************
* convertIPToHex converts in ipadress to hex (currently unused)								*																				*
********************************************************************************************/
private String convertIPToHex(ipAddress) {
	return Long.toHexString(converIntToLong(ipAddress));
}

/********************************************************************************************
* converIntToLong converts in ipadress:port from int to long (currently unused)				*																				*
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
* getHostAddress grabs hex device network ID (IpAddress:port in HEX!!!)and parses to 		*
* decimal format (nnn.nnn.nnn.nnn:xxxx) 													*																				*
********************************************************************************************/
private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}

/********************************************************************************************
* convertHexToInt converts hex value to integer												*																				*
********************************************************************************************/
private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

/********************************************************************************************
* convertHexToInt converts hex value to long												*																				*
********************************************************************************************/
private Long convertHexToLong(hex) {
    if (hex[0..1]=="0x") {
    	hex = hex[2..-1]
    }
    Long.parseLong(hex,16)
}

/********************************************************************************************
* convertHexToIP converts hex ip value to string in nnn.nnn.nnn.nnn format												*																				*
********************************************************************************************/
private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}