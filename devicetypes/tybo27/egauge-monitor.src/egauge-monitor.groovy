/**
 *  Updated eGauge Energy Monitoring System
 Based on starters from Ronald Gouldner and carloss66
 */
 
preferences {

	input("uri", "text", title: "eGauge Monitor URL")
}

metadata {
	definition (name: "Egauge Monitor", namespace: "tybo27", author: "tybo27") {
	capability "Power Meter"
    capability "Refresh"
	capability "Polling"
        
    attribute "energy_today", "STRING"
        
    fingerprint deviceId: "CASeGauge"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
            valueTile("solar", "device.power") {
   	         state("solarPower", label: '${currentValue}W\nSolar', unit:"W", backgroundColors: [
                    [value: 1, color: "#32CD32"],
                    [value: 0, color: "#000000"],
    	            ]
                )
        	}
            valueTile("grid", "device.gridpower") {
   	         state("gridPower", label: '${currentValue}W\nGrid', unit:"W", backgroundColors: [
                    [value: -1, color: "#32CD32"],
                    [value: 1, color: "#FF0000"],
    	            ]
                )
        	}    

            standardTile("refresh", "device.energy_today", inactiveLabel: false, decoration: "flat") {
                state "default", action:"polling.poll", icon:"st.secondary.refresh"
            }

        
        main (["solar","grid","energy_today"])
        details(["solar", "grid", "energy_today", "refresh"])

	}
}


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"

}

def poll() {
	refresh()
}

def refresh() {
  log.debug "Executing 'refresh'"
  energyRefresh()
}


def energyRefresh() {  
  log.debug "Executing 'energyToday'"
  
  def cmd = "${settings.uri}/cgi-bin/egauge?noteam";
  log.debug "Sending request cmd[${cmd}]"
  
  httpGet(cmd) {resp ->
        if (resp.data) {
        	log.debug "${resp.data}"
            def gpathString = resp.data.text()
            def currentsolarPower = 0
            def currentgridPower = 0
            def energyToday = 0

            resp.data.meter.each { 
                log.debug it.name() + ":"+ it.@title + ":" + it.text()
                if (it.name().equals("meter") && it.@title.equals("Grid")) {
                    log.debug "Found Grid Power"
                    currentgridPower = it.power
                    energyToday = it.energy
				}

                if (it.name().equals("meter") && it.@title.equals("Solar")) {
                    log.debug "Found Solar Power"
                    currentsolarPower = it.power
                    energyToday = it.energy
                }

            }
			log.debug "currentgridPower=${currentgridPower}"
			log.debug "currentsolarPower=${currentsolarPower}"

                         
             // String.format("%5.2f", energyToday)
             	delayBetween([sendEvent(name: 'gridpower', value: (currentgridPower))
                				,sendEvent(name: 'power', value: (currentsolarPower))
             		])				

        }
        if(resp.status == 200) {
            	log.debug "poll results returned"
        }
         else {
            log.error "polling children & got http status ${resp.status}"
        }
    }
}