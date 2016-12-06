/**
 *  Updated eGauge Energy Monitoring System
 Based on starters from Ronald Gouldner and carloss66
 */
 
preferences {

	input("uri", "text", title: "eGauge Monitor URL")
   // input("usageThresh", "number", title: "Usage Alert Threshold")
    input("usageWindowThresh", "number", title: "Time in minutes for usage alert calculations")
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
    
    attribute "energy_today", "STRING"
        
    fingerprint deviceId: "CASeGauge"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    		//st.Home.home1
            valueTile("net", "device.power") {
   	         state("power", label: '${currentValue}kW\nNet', unit:"kW", action:refresh, backgroundColors: [
                    [value: 1, color: "#FF0000"],
                    [value: -1, color: "#32CD32"],
    	            ]
                )
        	}
            //st.Weather.weather14
            valueTile("solar", "device.solarPower") {
   	         state("solarPower", label: '${currentValue}kW\nSolar', unit:"kW", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#d3d3d3"],
    	            ]
                )
        	}    
            //st.Appliances.appliances17
            valueTile("grid", "device.gridPower") {
   	         state("gridPower", label: '${currentValue}kW\nGrid', unit:"kW", backgroundColors: [
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
                state "default", action:"polling.poll", icon:"st.secondary.refresh"
            }

        
        main ("net")
        details(["net", "solar", "grid","frequency","voltage1","voltage2", "refresh"])

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
				def numColumns = rootNode.data[0].@columns
                def epoch = rootNode.data[0].@epoch
				def cumIdx = 0
                groupData.put("timeStamp", [:])
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

def getDailyValues() {  
      log.debug "Executing 'getDailyValues'"
  
    // Calculate Unix Timestamp
      def UTC = Math.round(now()/1000)  //.round(0)
      def midnight = (timeToday("00:00", location.timeZone).time/1000)//.round(0)
      def offset = (UTC-midnight)//.round(0)
      //log.debug "In UTC: Current time=$UTC and midnight=$midnight, offset=$offset"
  
      //def curTimeDate = new Date()
      //def midnight = curDate.time/1000
      //new Date().format("yyyy-MM-dd")
      //def df = new java.text.SimpleDateFormat("MM/dd/yyyy")
      //df.setTimeZone(location.timeZone)
      //def date = df.format(new Date())
      //log.debug "Method 1 Date is $date"
      //def cmd = "http://egauge20410.egaug.es/cgi-bin/egauge-show"
      def cmd = "${settings.uri}/cgi-bin/egauge-show"
      def arg = "n=1"

      def postParams = [
      		"uri": settings.uri,
            "path": "/cgi-bin/egauge-show",
            //"query": ["n": 1]
            ]

      //def cmd = [uri: "${settings.uri}/cgi-bin/egauge-show?n=1&f=1479801600", headers: "!DOCTYPE"]
      //log.debug "Sending request cmd: [ ${cmd} ]"
	  httpPost(postParams) {resp ->
      //httpPost(cmd,arg) {resp ->
      	log.debug "httpGet sent, resp=$resp"
        //log.debug "response contentType: ${resp.contentType}"
            if (resp.data) {
                log.debug "${resp.data}"
                def gpathString = resp.data.text()
                def netEnergy = 0
                def solarEnergy = 0
                def gridEnergy = 0

                def timeStamp = resp.data.data.@time_stamp
                def timeUTC = Integer.decode(timeStamp)
                def timeDelta = resp.data.data.@time_delta
                def columns =  resp.data.data.@columns
                log.debug "timeStamp=$timeStamp, timeUTC=$timeUTC, timeDelta=$timeDelta, columns=$columns"

                resp.data.data.cname.eachwithIndex {  it, i ->
                    log.debug it.name() + ":"+ it.@t + ":" + it.text() + ": i=" i
                    log.debug "Data1=${resp.data.data.r[0]}, data2=${resp.data.data.r[1]}"
                    switch (it.text()) {
                        case 'grid':
                            gridEnergy = resp.data.data.r[0].c[i]-resp.data.data.r[1].c[i]
                            break
                        case 'solar':
                            solarEnergy = resp.data.data.r[0].c[i]-resp.data.data.r[1].c[i]
                            break
                    }
                }
                log.debug "gridEnergy=${gridEnergy}"
                log.debug "solarEnergy=${solarEnergy}"

                netEnergy = (gridEnergy - solarEnergy).round(3)
                log.debug "netEnergy=${netEnergy}"

                delayBetween([sendEvent(name: 'netEnergy', value: (netEnergy))
                              ,sendEvent(name: 'gridEnergy', value: (gridEnergy))
                              ,sendEvent(name: 'solarEnergy', value: (solarEnergy))
                             ])				
        	}
            
            if(resp.status == 200) {
                log.debug "poll results returned"
            } else {
                log.error "polling children & got http status ${resp.status}"
            }
      }
}

def getInstantValues() {  
      log.debug "Executing 'getInstantValues'"

      def cmd = "${settings.uri}/cgi-bin/egauge?noteam";
      log.debug "Sending request cmd[${cmd}]"

      httpGet(cmd) {resp ->
            if (resp.data) {
                log.debug "${resp.data}"
                def gpathString = resp.data.text()
                def currentNetPower = 0
                def currentSolarPower = 0
                def currentGridPower = 0
                
                def currentFreq = 0
                def currentV1 = 0
                def currentV2 = 0

                resp.data.meter.each { 
                    log.debug it.name() + ":"+ it.@title + ":" + it.text()
                    if (it.name().equals("meter") && it.@title.equals("Grid")) {
                        log.debug "Found Grid Power"
                        currentGridPower = (it.power.toFloat()/1000).round(3)
                    }

                    if (it.name().equals("meter") && it.@title.equals("Solar")) {
                        log.debug "Found Solar Power"
                        currentSolarPower = (it.power.toFloat()/1000).round(3)
                    }

                }
                log.debug "currentGridPower=${currentGridPower}"
                log.debug "currentSolarPower=${currentSolarPower}"

                currentNetPower = (currentGridPower - currentSolarPower).round(3)
                log.debug "currentNetPower=${currentNetPower}"

                currentFreq = resp.data.frequency.toFloat().round(1)
                log.debug "currentFrequency=$currentFreq"

                resp.data.voltage.each { 
                    log.debug it.name() + ":"+ it.@ch + ":" + it.text()
                    if (it.name().equals("voltage") && it.@ch.equals("0")) {
                        log.debug "Found Ch1 Voltage"
                        currentV1 = it.text().toFloat().round(1)
                    }

                    if (it.name().equals("voltage") && it.@ch.equals("1")) {
                        log.debug "Found Ch2 Voltage"
                        currentV2 = it.text().toFloat().round(1)
                    }
                }   
                delayBetween([sendEvent(name: 'power', value: (currentNetPower))
                              ,sendEvent(name: 'gridPower', value: (currentGridPower))
                              ,sendEvent(name: 'solarPower', value: (currentSolarPower))
                              ,sendEvent(name: 'frequency', value: (currentFreq))
                              ,sendEvent(name: 'voltage1', value: (currentV1))
                              ,sendEvent(name: 'voltage2', value: (currentV2)) 
                             ])				

            }
        if(resp.status == 200) {
            	log.debug "poll results returned"
        } else {
            log.error "polling children & got http status ${resp.status}"
        }
	}
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