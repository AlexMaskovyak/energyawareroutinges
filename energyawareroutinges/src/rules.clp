; Allow JESS to access Java objects in the following package
(import energyaware.*)
(import java.util.ArrayList)

; List of objects from Java that are modeled in JESS
(deftemplate Agent 			( declare( from-class Agent )))
(deftemplate Battery 		( declare( from-class Battery )))
(deftemplate BatteryMetric 	( declare( from-class BatteryMetric )))
(deftemplate Datagram		( declare( from-class Datagram )))
(deftemplate NodeID 		( declare( from-class NodeID )))
(deftemplate Segment		( declare( from-class Segment )))

(deftemplate DestinationPath (slot Destination)(slot Path))

; List of all global variables available to JESS
(defglobal ?*id* = 1)		; this device's id
(defglobal ?*agent* = 1)	; our contact with the Java world

; List of all functions/methods we want access to
(deffunction getBatteryMetric ()
    "Returns the battery metric"
    (?*agent* getBatteryMetric))

(deffunction updatePathTable (?path)
    "Adds a path to the path table if it does not already exist"
    (?*agent* updatePathTable ?path)
    )

(deffunction updateBatteryMetrics (?path ?metrics)
    "Updates the metrics related with another node"
    (?*agent* updateBatteryMetrics ?path ?metrics)
    )

(deffunction updateTransmissionCosts (?path ?tcosts)
    "Updates the transmission cost to a node"
    (?*agent* updateTransmissionCosts ?path ?tcosts)
    )

(deffunction getPath (?destination)
    "Return the best path to a given destination"
    (?*agent* getBestPath ?destination)
    )

(deffunction hasPath (?destination)
    "Determine if there is a path to a destination"
    "Returns true or false"
    (?*agent* hasPath ?destination)
    )

(deffunction isNovelRREQID (?rreqID)
    "Determine whether the rreq has been seen before."
    (?*agent* isNovelRREQID ?rreqID)
    )

(deffunction isNextHopInPath (?src ?path)
    "Determine if id is after src in path"
    (= (call Frame getNextHopInPath ?src ?path) ?*id*)
    )

; --- List of all rules used by processing in JESS ---
;1: Initialization Rule 1
(defrule setAgent
    "We need access to an Agent and its ID in JESS"
    (Agent (OBJECT ?a))
    =>
    (bind ?*agent* ?a)
    (bind ?*id* (?*agent* getID))
    (printout t "Agent & Agent ID are set" crlf))


; --- Responses to Datagrams received from other nodes ---

; --- Universal Rule for Datagrams ---
;2: Universal Rule 1: Given any Datagram, we want to extract the battery metric, path & transmission cost
(defrule ExtractTransmissionCost
    "Extracts the Battery Metric, Path & Transmission Cost from a datagram."
    (Datagram (path ?path) (batteryMetricValues ?metrics) (transmissionValues ?tValues))
    =>
    (updatePathTable ?path)
    (updateBatteryMetrics ?path ?metrics)
    (updateTransmissionCosts ?path ?tValues)
    (printout t "Battery Metric, Path & Transmission Costs updated from an Incoming Datagram" crlf)) 


; --- Rules for RREQs ---

;3: RREQ Rule 1: Given a RREQ (request) for which we are the destination, we want to send a RREP (reply)
(defrule RREQtoRREP
    "A RREQ Datagram arrives at the destination and a RREP Datagram is sent back."
    (Datagram {type == "RREQ"} {destination == ?*id*} (OBJECT ?incoming))
    =>
    (?incoming addToPath ?*id*)
	; Assign the incoming datagram's reversed path to revpath
    (bind ?revpath (call Datagram reverse ?incoming.path))
	; Create a datagram and bind it to the response variable
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* 
            	?incoming.source 
            	?incoming.segment
            	?revpath 
            	(getBatteryMetric)
            	))
	(undefinstance ?incoming)
    (?*agent* sendDatagram ?response 10)   
    (printout t "RREQ-RREP Fired" crlf))  
    

; RREQ Rule 2:

(defrule NonNovelRREQID
    "We've received RREQ for which we are not the destination.  Its RREQ_ID is non-novel, meaning we've already been a part of this RREQ."
    ?incoming <- (Datagram {type == "RREQ"}{destination != ?*id*}(rreqID ?rreqID))
    (not (test(isNovelRREQID ?rreqID)))
    =>
    (retract ?incoming)
    (printout t "Receive a RREQ and have not seen the RREQ-ID before" crlf))

; RREQ Rule 3:

(defrule ShortCircuitRREQ
    "Receive a RREQ datagram and we already have a path."
    ?dg <- (Datagram {type == "RREQ"} {destination != ?*id*} (destination ?dest) (rreqID ?rreqID) (OBJECT ?incoming))
    (test (<> ?dest ?*id*))
    (test (isNovelRREQID ?rreqID))
    (test (hasPath ?dest))
    =>
    (?*agent* addRREQID ?rreqID)
    
 	(bind ?pathSoFar (?incoming getPath))
    (bind ?restOfPath (getPath ?dest))
    (?*agent* mergePathsInMiddle ?pathSoFar ?restOfPath)

    (bind ?fullPath ?pathSoFar)
    (bind ?revBestPath (call Datagram reverse ?fullPath))
    ;(?incoming addBatteryMetricValue (getBatteryMetric))
    ;(bind ?revBattMetric (call Datagram reverse (?incoming getBatteryMetricValues)))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* 
                ?incoming.source 
            	?incoming.segment
            	?revBestPath 
            	(getBatteryMetric)))
    (retract ?dg)
    (?*agent* sendDatagram ?response 10)
    (printout t "Short circuited RREQ" crlf)
    )

; RREQ Rule 4
(defrule ForwardRREQ
    "Forward a RREQ datagram for which we have no path and is not for us."
    ?dg <- (Datagram {type == "RREQ"}(destination ?dest)(rreqID ?rreqID) (OBJECT ?incoming))
    (test (<> ?dest ?*id*))
    (test (isNovelRREQID ?rreqID))
    (not ( test( hasPath ?dest)))
    =>
    (?*agent* addRREQID ?rreqID)
    
    (?incoming addToPath ?*id*)
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREQ" 
            	;?incoming.source 
            	?*id*
            	?incoming.destination
            	?incoming.segment 
            	?incoming.path
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (?*agent* sendDatagram ?response 10)
    (printout t "Forward RREQ" crlf)
    )

; --- Rules for RREPs ---


;RREP Rule 1

(defrule RrepAtSource
    "RREP returns to original RREQer"
    ?incoming <- (Datagram {type == "RREP"}{destination == ?*id*})
    =>
    (retract ?incoming)
    (printout t "RREP received at Source" crlf)
    )

; RREP Rule 2

(defrule ForwardRREP
    "RREP and we are the next node in the path but not the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src) (OBJECT ?incoming))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* ;?incoming.source 
            	?incoming.destination
            	?incoming.segment 
            	?incoming.path
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (?*agent* sendDatagram ?response 10)
	)

; RREP Rule 3

(defrule DropRREP
    "RREP and we are not the next node in the path or the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src) (OBJECT ?incoming))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?dg)
	)


; --- Rules for Receiving Data Types ---

; Data Rule 1

(defrule SegmentForUs
    "Data type datagram arrived at final destination"
    ?incoming <- (Datagram {type == "DATA"}{destination == ?*id*} (segment ?segment))
    =>
    (retract ?incoming)
    (?*agent* sendMessage ?segment)
    )

; Data Rule 2

(defrule ForwardReceivedDatagram
    "Data type datagram arrived at a midpoint along the path to destination."
    ?incoming <- (Datagram {type == "DATA"} {destination != ?*id*} (source ?src))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram
            	"DATA"
            	?incoming.source
            	?incoming.destination
            	(?incoming getPath)
            	(?incoming getBatteryMetricValues)))
    (retract ?incoming)
    (?*agent* sendDatagram ?response 10)
    )


; Data Rule 3

(defrule DropDatagram
    "Data type datagram arrived that we overheard, we aren't the next hop in the path, so we drop it."
    ?incoming <- (Datagram {type == "DATA"} {destination != ?*id*} (source ?src))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?incoming)
    )


; --- Responses to datagrams that we create ---

; Creating Rule 1
(defrule ForwardOurDatagram
    "Data type datagram was created from a segment and must be sent out."
    ?outgoing <- (Datagram {type == "DATA"} {source == ?*id*} (destination ?dest))
    (test (hasPath ?dest))
    =>
    (?outgoing addBatteryMetricValue (getBatteryMetric))
    (?outgoing setPath (getPath ?dest))
	(bind ?response ?outgoing)
    (retract ?outgoing)
    (?*agent* sendDatagram ?response 10)    
    )

; Creating Rule 2
(defrule CreateRREQForDatagram
    "Data type datagram was created from a segment, but we need a path first.  We want to keep this datagram until we get the response."
    ?outgoing <- (Datagram {type == "DATA"} {source == ?*id*} (destination ?dest))
	(not (test (hasPath ?dest)))
    =>
    (bind ?response (
            new Datagram
            	"RREQ"
            	?*id*
            	?dest
            	))
    (?response addToPath ?*id*)
    (?response addBatteryMetricValue (getBatteryMetric))
    (?*agent* sendDatagram ?response 10)    
    )

; --- Responses to segments received from User/Node ---

; New Segment Rule 1

(defrule ReceiveSegmentFromUser
    "Segment received, create a datagram without path information."
    ?outgoing <- (Segment (destination ?dest))
    =>
    (add (new Datagram
            "DATA"					; Type of Datagram is "DATA"
            ?*id*					; Our address
            ?dest					; Destination address
            ?outgoing ))			; Append our Battery Metric?
    (retract ?outgoing)				; Remove the segment from our facts
    )
	; Do we have to do an append or update in order for the next rule to catch
	; this Datagram without a path

(facts)