/**
 *  Dell 1610
 *
 *  Copyright 2016 T&amp;A
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

	input("url", "text", title: "Dell 1610 URL")
}

metadata {
	definition (name: "Dell 1610", namespace: "tybo27", author: "tybo27") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
		//capability "TV"
        attribute "projectorStatus", "enum", ["Lamp On", "Standby", "Power Saving", "Cooling", "Warming Up"]
        attribute "powerSaving", "enum", ["Off", "30", "60", "90", "120"]
        attribute "alertStatus", "enum", ["Lamp warming", "Low lamp life", "Temperature"]
        attribute "projectorMode", "enum", ["Front Projection-Desktop", "Front Projection-Ceiling Mount", "Rear Projection-Desktop", "Rear Projection-Ceiling Mount"]
        attribute "sourceSelect", "enum", ["VGA-A", "VGA-B", "S-VIDEO", "COMPOSITE VIDEO", "HDMI"]
        attribute "videoMode", "enum", ["Presentation", "Bright", "Movie", "sRGB", "Custom"]
        attribute "blankScreen", "enum", ["On", "Off"]
        attribute "aspectRatio", "enum", ["Original", "4:3", "Wide"]
        attribute "brightness", "number" //0-100
        attribute "contrast", "number" //0-100
        attribute "autoAdjust", "enum", ["On", "Off"]
        attribute "audioInput", "enum", ["Audio-A", "Audio-B", "HDMI", "Microphone"]
        attribute "volume", "number" // volume from capability TV
        attribute "speaker", "enum", ["On", "Off"]
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    tiles {
		// TODO: define your main and details tiles here
        multiAttributeTile(name:"mainControlTile", type:"lighting", width:6, height:4) {
            tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "off", icon: "st.Electronics.electronics7", label:'Off', backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "on", icon: "st.Electronics.electronics7", label:'${name}', backgroundColor:"#79b821", nextState:"turningOff"
                //attributeState "turningOn", icon: "st.Electronics.electronics7", label:'${name}', backgroundColor:"#95f213", nextState:"turningOff"
                //attributeState "turningOff", icon: "st.Electronics.electronics7", label:'${name}', backgroundColor:"#adc8ff", nextState:"turningOn"
    		}
            tileAttribute("device.blankScreen", key: "SECONDARY_CONTROL") {
                attributeState "off", backgroundColor:"#ffffff", icon: 'st.Electronics.electronics7', action:"blankScreen", nextState:"on"
                attributeState "on", backgroundColor:"#000000", icon: 'st.Electronics.electronics7', action:"blankScreen", nextState:"off"
            }
            tileAttribute("device.volume", key: "SLIDER_CONTROL") {
               attributeState "default", action:"setVolume", range:"(0..20)"
            }
		}
        
        standardTile("Power On", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "off", label: "off", icon: "st.switches.switch.off", backgroundColor: "#ffffff", action: "switch.on"
    		state "on", label: "on", icon: "st.switches.switch.on", backgroundColor: "#79b821", action: "switch.off"
        }
        standardTile("refresh", "device.projectorStatus", inactiveLabel: false, decoration: "flat") {
            state "default", action:"refresh", icon:"st.secondary.refresh"
        }
        controlTile("volumeControl", "device.volume", "slider", height: 1,
        	width: 2, inactiveLabel: false, range:"(0..20)") {
    		state "level", action:"setVolume"
		}
        controlTile("brightnessControl", "device.brightness", "slider", height: 1,
        	width: 2, inactiveLabel: false, range:"(0..100)") {
    		state "level", action:"setBrightness"
		}
        controlTile("contrastControl", "device.contrast", "slider", height: 1,
        	width: 2, inactiveLabel: false, range:"(0..100)") {
    		state "level", action:"setContrast"
		}
        main (["Power On"])
        details(["mainControlTile", "Power On", "refresh", "volumeControl", "brightnessControl"])//, "Power On 2"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	// TODO: handle 'volume' attribute
	// TODO: handle 'channel' attribute
	// TODO: handle 'power' attribute
	// TODO: handle 'picture' attribute
	// TODO: handle 'sound' attribute
	// TODO: handle 'movieMode' attribute
	def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	// TODO: handle 'poll' command
    refresh()
}

def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command
    def hubActionHeaders = [
                'HOST': "192.168.2.216",
                //HOST: "C0A802D8"//,
				'Connection': "keep-alive"
            ]
    		hubActionHeaders.put("Content-Type", "application/x-www-form-urlencoded")     
            hubActionHeaders.put("Accept-Encoding", "gzip, deflate")
            hubActionHeaders.put("Accept-Language", "en-US,en;q=0.5")
            hubActionHeaders.put("User-Agent", 'Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0')
            
    def hubActionPath = "/status.htm"
    try {
        def hubActionVar = new physicalgraph.device.HubAction(
            'method': "GET",
            'path': hubActionPath,
            'headers': hubActionHeaders
		)
         hubActionVar
	} catch (e) {
    	log.debug "HubAction Error: $e on hubAction: $hubActionVar"
    }
   
}

def on() {
	log.debug "Executing 'on'"
    def powerOnResponse = hubActionPowerOn()
    log.debug "Hub Action Response: ${powerOnRespone}"
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
    def powerOnResponse = hubActionPowerOn()
    log.debug "Hub Action Response: ${powerOnRespone}"
	// TODO: handle 'off' command
}

def setVolume() {
	log.debug "Executing 'volumeUp'"
	// TODO: handle 'volumeUp' command
    //Max of 20
}

def blankScreen () {
	log.debug "Blanking Screen"

}

def hubActionPowerOn () {
	log.debug "Executing HubAction"
    def hubActionHeaders = [
                'HOST': "192.168.2.216",
                //HOST: "C0A802D8"//,
				'Accept': "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
				'Referer': "http://192.168.2.216/tgi/status.tgi",
				//Cookie: "ATOP=tuA7d",
				'Connection': "keep-alive"
            ]
    		hubActionHeaders.put("Content-Type", "application/x-www-form-urlencoded")     
            hubActionHeaders.put("Accept-Encoding", "gzip, deflate")
            hubActionHeaders.put("Accept-Language", "en-US,en;q=0.5")
            hubActionHeaders.put("User-Agent", 'Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0')
            
    def hubActionPath = "/tgi/status.tgi"
    //def hubActionBody = "PowerOn=Power+ON+"
    //def hubActionBody = "Content-Type: application/x-www-form-urlencoded/nContent-Length: 279/n/nPJSTATE=0&DSP_SOURCE=0&ERRORSTA=85&FREEZE0=&HIDE0=170&inp_objname=&inp_objvalue=0&redio_objname=&radio_objvalue=0&PJSTATE2=Standby+&PowerOn=Power+ON+&PwSave=99&ERRORSTA2=&ecoMode=28&PrjMode=99&PrjSRC=0&VideoMode=99&hide=170&Aspect=1&Bright=0&Contrast=0&PrjSRCA=1&Volume=0&Spk=170"
    def hubActionBody = "PJSTATE=0&DSP_SOURCE=0&ERRORSTA=85&FREEZE0=&HIDE0=170&inp_objname=&inp_objvalue=0&redio_objname=&radio_objvalue=0&PJSTATE2=Standby+&PowerOn=Power+ON+&PwSave=99&ERRORSTA2=&ecoMode=28&PrjMode=99&PrjSRC=0&VideoMode=99&hide=170&Aspect=1&Bright=0&Contrast=0&PrjSRCA=1&Volume=0&Spk=170"
 
    /*def hubActionQuery = [PJSTATE:0,
            		DSP_SOURCE:0,
                    ERRORSTA:85,
                    FREEZE0:"",
                    HIDE0:170,
                    inp_objname:"",
                    inp_objvalue:0,
                    redio_objname:"",
                    radio_objvalue:0,
                    PJSTATE2:"Standby+",
                    PowerOn:"Power+ON+",
                    PwSave:99,
                    ERRORSTA2:"",
                    ecoMode:28,
                    PrjMode:99,
                    PrjSRC:0,
                    VideoMode:99,
                    hide:170,
                    Aspect:1,
                    Bright:0,
                    Contrast:0,
                    PrjSRCA:1,
                    Volume:0,
                    Spk:170
					]*/
                 
    try {
        def hubActionVar = new physicalgraph.device.HubAction(
            'method': "POST",
            'path': hubActionPath,
            'headers': hubActionHeaders,
            'body': hubActionBody
		)
        //log.degbug hubActionVar
        hubActionVar
	} catch (e) {
    	log.debug "HubAction Error: $e on hubAction: $hubActionVar"
    }
    sendHubCommand(hubActionVar)
    //log.degbug hubActionVar
    return hubActionVar
}


// gets the address of the hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

// gets the address of the device
private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    log.debug "Using IP: $ip and port: $port for device: ${device.id}"
    return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

def powerOn2 () {
/*
Parameters:

    String uri - The URI to make the HTTP POST call to

    String body - The body of the request

    Map params - A map of parameters for configuring the request. The valid parameters are:
    Parameter 	Description
    uri 	Either a URI or URL of of the endpoint to make a request from.
    path 	Request path that is merged with the URI.
    query 	Map of URL query parameters.
    headers 	Map of HTTP headers.
    contentType 	Forced response content type and request Accept header.
    requestContentType 	Content type for the request, if it is different from the expected response content-type.
    body 	Request body that will be encoded based on the given contentType.

    Closure closure - The closure that will be called with the response of the request.
*/
	//def cmd = "${settings.uri}/tgi/status.tgi"
    //cmd = "192.168.2.216/tgi/status.tgi"
    
    def cmd = [
		uri: "http://${settings.url}",
        path: "/tgi/status.tgi",
        contentType: "application/x-www-form-urlencoded",
		//body: [ name: "PowerOn", value: "Power+On+" ]
        headers: [
                HOST: "http://192.168.2.216",
                //User-Agent: 'Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0',
				Accept: "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
				//Accept-Language: "en-US,en;q=0.5",
				//Accept-Encoding: "gzip, deflate",
				Referer: "http://192.168.2.216/tgi/status.tgi",
				Cookie: "ATOP=tuA7d",
				Connection: "keep-alive"
        ],
        //query: [name:"PowerOn", value:"Power+ON+", type:"submit"],
        //body: "name=PowerOn&value=Power+On+"//&type=submit"
        body: "PJSTATE=0&DSP_SOURCE=0&ERRORSTA=85&FREEZE0=&HIDE0=170&inp_objname=&inp_objvalue=0&redio_objname=&radio_objvalue=0&PJSTATE2=Standby+&PowerOn=Power+ON+&PwSave=99&ERRORSTA2=&ecoMode=28&PrjMode=99&PrjSRC=0&VideoMode=99&hide=170&Aspect=1&Bright=0&Contrast=0&PrjSRCA=1&Volume=0&Spk=170"
	]
    /*
    log.debug "Sending request cmd [${cmd.url}] with path [${cmd.path}] and body [${cmd.body}]"
    
    try {
        httpPost(cmd) { resp -> //"name=PowerOn&value=Power On &type=submit") { resp ->
            log.debug "response data: ${resp.data}"
            log.debug "response contentType: ${resp.contentType}"
        }
    } catch (e) {
        log.debug "httpPost error: $e"
    }
    */
}
