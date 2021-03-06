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
(defglobal ?*agent* = 1)	; our contact with the Java world

; List of all functions/methods we want access to
(deffunction getNodeID ()
    "Acquires the Node's ID.  Necessary for determining whether datagrams are addressed to us."
    (?*agent* getID)
    )

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

(deffunction removePath (?path)
    "Removes a nonfunctioning path from the path table."
    (?*agent* removePath ?path)
    )

(deffunction isNovelRREQID (?rreqID)
    "Determine whether the rreq has been seen before."
    (?*agent* isNovelRREQID ?rreqID)
    )

(deffunction isNextHopInPath (?src ?path)
    "Determine if id is after src in path"
    (= (call Frame getNextHopInPath ?src ?path) (getNodeID))
    )

(deffunction sendDatagram (?datagram ?transmissionDistance)
    "Wrapper to hide implementation of sending datagrams.  Sends the datagram to agent."
    (?*agent* sendDatagram ?datagram ?transmissionDistance)
    )

(deffunction sendSegment (?segment) 
    "Sends the segment up the stack to eventually hit user space"
    (?*agent* sendMessage ?segment)
    )


; --- List of all rules used by processing in JESS ---
; Initialization Rule 1
(defrule setAgent
    "We need access to an Agent and its ID in JESS"
    (Agent (OBJECT ?a))
    =>
    (bind ?*agent* ?a)
    (printout t "Agent & Agent ID are set" crlf))


; --- Responses to Datagrams received from other nodes ---

; --- Universal Rule for Datagrams ---
; Universal Rule 1: Given any Datagram, we want to extract the battery metric, path & transmission cost
(defrule ExtractPathAndBatteryCost
    "Extracts the Battery Metric, Path & Transmission Cost from a datagram."
    (declare (salience 100))
    (Datagram (path ?path) (batteryMetricValues ?metrics) (transmissionValues ?tValues))
    (test (> (?path size) 0))
    =>
    (updatePathTable ?path)
    (updateBatteryMetrics ?path ?metrics)
    (updateTransmissionCosts ?path ?tValues)
    (printout t "Battery Metric, Path & Transmission Costs updated from an Incoming Datagram" crlf)) 


; --- Rules for RREQs ---

; RREQ Rule 1: Given a RREQ (request) for which we are the destination, we want to send a RREP (reply)
(defrule RREQtoRREP
    "A RREQ Datagram arrives at the destination and a RREP Datagram is sent back."
    (Datagram {type == "RREQ"} {destination == (getNodeID)} (OBJECT ?incoming))
    =>
    (?incoming addToPath (getNodeID))
	; Assign the incoming datagram's reversed path to revpath
    (bind ?revpath (call Datagram reverse ?incoming.path))
	; Create a datagram and bind it to the response variable
    (bind ?response (
            new Datagram 
            	"RREP" 
            	(getNodeID) 
            	?incoming.source 
            	?incoming.segment
            	?revpath 
            	(getBatteryMetric)
            	))
	(undefinstance ?incoming)
    (sendDatagram ?response 10)
    (printout t "RREQ-RREP Fired" crlf))  
    
; RREQ Rule 2: Given a RREQ that has a RREQID that we've already seen before.
(defrule NonNovelRREQID
    "We've received RREQ for which we are not the destination.  Its RREQ_ID is non-novel, meaning we've already been a part of this RREQ."
    ?incoming <- (Datagram {type == "RREQ"}{destination != (getNodeID)}(rreqID ?rreqID))
    (not (test(isNovelRREQID ?rreqID)))
    =>
    (retract ?incoming)
    (printout t "Receive a RREQ and have not seen the RREQ-ID before" crlf))

; RREQ Rule 3: Given a RREQ where we already have a path to the destination
(defrule ShortCircuitRREQ
    "Receive a RREQ datagram and we already have a path."
    ?dg <- (Datagram {type == "RREQ"} {destination != (getNodeID)} (destination ?dest) (rreqID ?rreqID) (OBJECT ?incoming))
    (test (<> ?dest (getNodeID)))
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
            	(getNodeID) 
                ?incoming.source 
            	?incoming.segment
            	?revBestPath 
            	(getBatteryMetric)))
    (retract ?dg)
    (sendDatagram ?response 10)
    (printout t "Short circuited RREQ" crlf)
    )

; RREQ Rule 4: Given a RREQ with a unique RREQID, meaning we have to build this one's path and forward it on.
(defrule ForwardRREQ
    "Forward a RREQ datagram for which we have no path and is not for us."
    ?dg <- (Datagram {type == "RREQ"}(destination ?dest)(rreqID ?rreqID) (OBJECT ?incoming))
    (test (<> ?dest (getNodeID)))
    (test (isNovelRREQID ?rreqID))
    (not ( test( hasPath ?dest)))
    =>
    (?*agent* addRREQID ?rreqID)
    
    (?incoming addToPath (getNodeID))
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREQ" 
            	;?incoming.source 
            	(getNodeID)
            	?incoming.destination
            	?incoming.segment 
            	?incoming.path
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (sendDatagram ?response 10)
    (printout t "Forward RREQ" crlf)
    )


; --- Rules for RREPs ---

;RREP Rule 1: RREP arrives in response to a RREQ that this node sense.
(defrule RrepAtSource
    "RREP returns to original RREQer"
    ?dg <- (Datagram {type == "RREP"}{destination == (getNodeID)})
    =>
    (retract ?dg)
    (printout t "RREP received at Source" crlf)
    )

; RREP Rule 2: RREP arrives and we are not the destination but are in the route path, forward it to next hop.
(defrule ForwardRREP
    "RREP and we are the next node in the path but not the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != (getNodeID)}(source ?src) (OBJECT ?incoming))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	(getNodeID) ;?incoming.source 
            	?incoming.destination
            	?incoming.segment 
            	?incoming.path
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (sendDatagram ?response 10)
    (printout t "Forward received RREP" crlf)
	)

; RREP Rule 3: RREP arrives where we are not along the route path, drop it.
(defrule DropRREP
    "RREP and we are not the next node in the path or the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != (getNodeID)}(source ?src) (OBJECT ?incoming))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?dg)
    (printout t "Drop RREP" crlf)
	)


; --- Rules for Receiving Data Types ---

; Data Rule 1: Data arrives for us.
(defrule SegmentForUs
    "Data type datagram arrived at final destination"
    ?incoming <- (Datagram {type == "DATA"}{destination == (getNodeID)} (segment ?segment))
    =>
    (retract ?incoming)
    (sendSegment ?segment)
    )

; Data Rule 2: Data arrives for someone where we are the next hop along the path
(defrule ForwardReceivedDatagram
    "Data type datagram arrived at a midpoint along the path to destination."
    ?dg <- (Datagram {type == "DATA"} {destination != (getNodeID)} (source ?src) (OBJECT ?incoming))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram
            	"DATA"
            	(getNodeID)
            	?incoming.destination
            	?incoming.segment
            	?incoming.path
            	(?incoming getBatteryMetricValues)))
    (undefinstance ?incoming)
    (sendDatagram ?response 10)
    (printout t "Forwarding Received Datagram" crlf)
    )

; Data Rule 3: Data arrives that is neither for us, nor for someone along a path involving us.
(defrule DropDatagram
    "Data type datagram arrived that we overheard, we aren't the next hop in the path, so we drop it."
    ?dg <- (Datagram {type == "DATA"} {destination != (getNodeID)} {source != (getNodeID)}(source ?src) (OBJECT ?incoming))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (undefinstance ?incoming)
    (printout t "Drop datagram" crlf)
    )


; --- Rules for Receiving RERR Types ---

; RERR Rule 1: RERR indicates the path we attempted to use does not work.  We should remove this path.
;(defrule RERRatDestination
;    "Remove path when a RERR is received for that path."
;    (Datagram {type == "RERR"} {destination == getID}(path ?path)(OBJECT ?incoming))
;    =>
;    (removePath ?path)
;    (undefinstance ?incoming)
;    (printout t "RERR received at destination and path removed." crlf)
;    )

; RERR Rule 2: RERR at a node other than the destination means we must remove the path and forward the datagram.
;(defrule RERRalongPath
;    "Remove path from our system and forward the RERR to destination."
;    (Datagram {type == "RERR"} {destination != getID} (OBJECT ?incoming))
;    =>
;    (undefinstance ?incoming)
;    (printout t "RERR received and forwarde" crlf)
;    )

; --- Responses to datagrams that we create ---

; Originate Rule 1: Forward our datagram.
(defrule ForwardOurDatagram
    "Data type datagram was created from a segment and must be sent out."
    ?dg <- (Datagram {type == "DATA"} {source == (getNodeID)} (destination ?dest) (OBJECT ?outgoing))
    (test (hasPath ?dest))
    =>
    (?outgoing addBatteryMetricValue (getBatteryMetric))
    (?outgoing setPath (getPath ?dest))
	(bind ?response ?outgoing)
    (undefinstance ?outgoing)
    (sendDatagram ?response 10)
    (printout t "Forward our datagram" crlf)
    )

; Originate Rule 2: Create a RREQ to get a path for our datagram
(defrule CreateRREQForDatagram
    "Data type datagram was created from a segment, but we need a path first.  We want to keep this datagram until we get the response."
    ?outgoing <- (Datagram {type == "DATA"} {source == (getNodeID)} (destination ?dest))
	(not (test (hasPath ?dest)))
    =>
    (bind ?response (
            new Datagram
            	"RREQ"
            	(getNodeID)
            	?dest
            	))
    (?response addToPath (getNodeID))
    (?response addBatteryMetricValue (getBatteryMetric))
    (sendDatagram ?response 10)
    (printout t "Create RREQ for our datagram to send" crlf)
    )


; --- Responses to segments received from User/Node ---

; Segment Rule 1: Segment received from transport layer
(defrule ReceiveSegmentFromUser
    "Segment received, create a datagram without path information."
    ?seg <- (Segment (destination ?dest) (OBJECT ?outgoing))
    =>
    (add (new Datagram
            "DATA"					; Type of Datagram is "DATA"
            (getNodeID)					; Our address
            ?dest					; Destination address
            ?outgoing ))			; Append our Battery Metric?
    
    (retract ?seg)				; Remove the segment from our facts
    (printout t "Received segment" crlf)
    )
	; Do we have to do an append or update in order for the next rule to catch
	; this Datagram without a pat

;(facts)