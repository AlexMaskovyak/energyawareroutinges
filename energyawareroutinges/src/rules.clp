; Allow JESS to access Java objects in the following package
(import energyaware.*)
(import java.util.ArrayList)

; List of objects from Java that are modeled in JESS
(deftemplate Agent 			( declare( from-class Agent )))
(deftemplate Battery 		( declare( from-class Battery )))
(deftemplate BatteryMetric 	( declare( from-class BatteryMetric )))
(deftemplate Datagram		( declare( from-class Datagram )))
(deftemplate NodeID 		( declare( from-class NodeID )))
(deftemplate Path			( declare( from-class ArrayList)))
(deftemplate Segment		( declare( from-class Segment )))

; List of all global variables available to JESS
(defglobal ?*id* = 1)		; this device's id
(defglobal ?*agent* = 1)	; our contact with the Java world

; List of all functions/methods we want access to
(deffunction getBatteryMetric ()
    "Returns the battery metric"
    (?*agent* getBatteryMetric))

(deffunction updatePathTable (?path)
    "Adds a path to the path table if it does not already exist"
    ; Update code later
    )

(deffunction updateBatteryMetrics (?metrics)
    "Updates the metrics related with another node"
    ; Update code later
    )

(deffunction updateTransmissionCost (?path ?tcost)
    "Updates the transmission cost to a node"
    ; Update code later
    )

(deffunction getPath (?destination)
    "Return the best path to a given destination"
    (bind ?path (new ArrayList))
    (?path add 1)
    (?path add 789)
    ?path
    )

(deffunction havePath (?destination)
    "Determine if there is a path to a destination"
    "Returns true or false")

(deffunction isNovelRREQID (?rreqID)
    "Determine whether the rreq has been seen before."
    )

(deffunction isNextHopInPath (?src ?path)
    "Determine if id is after src in path"
    
    )

; --- List of all rules used by processing in JESS ---

(defrule setAgent
    "We need access to an Agent and its ID in JESS"
    (Agent (OBJECT ?a))
    =>
    (bind ?*agent* ?a)
    (bind ?*id* (?*agent* getID))
    (printout t "Agent & Agent ID are set" crlf))


; --- Responses to Datagrams received from other nodes ---
; 
; --- Universal Rule for Datagrams ---
; Rule 1: Given any Datagram, we want to extract the battery metric, path & transmission cost
(defrule ExtractTransmissionCost
    "Extracts the Battery Metric, Path & Transmission Cost from a datagram."
    (Datagram (path ?path) (batteryMetricValues ?metrics) (transmissionValues ?tValues))
    =>
    (updatePathTable ?path)
    (updateBatteryMetrics ?metrics)
    (updateTransmissionCost ?path ?tValues)
    (printout t "Battery Metric, Path & Transmission Costs updated from an Incoming Datagram" crlf)) 


; --- Rules for RREQs ---
; Rule 1: Given a RREQ (request) for which we are the destination, we want to send a RREP (reply)
(defrule RREQtoRREP
    "Tested: A RREQ Datagram arrives at the destination and a RREP Datagram is sent back."
    (Datagram {type == "RREQ"} {destination == ?*id*} (OBJECT ?incoming))
    =>
    (?incoming addToPath ?*id*)

    (bind ?revpath (call Datagram reverse ?incoming.path))

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

    
    

; Rule 2:
(defrule NonNovelRREQID
    "We've received RREQ for which we are not the destination.  Its RREQ_ID is non-novel, meaning we've already been a part of this RREQ."
    ?dg <- (Datagram {type == "RREQ"}{destination != ?*id*}(rreqID ?rreqID))
    (not (test(isNovelRREQID ?rreqID)))
    =>
    (retract ?dg)
    (printout t "Receive a RREQ and have not seen the RREQ-ID before" crlf))

; Rule 3:
(defrule ShortCircuitRREQ
    "Receive a RREQ datagram and we already have a path."
    ?dg <- (Datagram {type == "RREQ"} {destination != ?*id*} (destination ?dest) (rreqID ?rreqID) (OBJECT ?incoming))
    ;(test (<> (?incoming getDestination) ?*id*))
    (test (isNovelRREQID ?rreqID))
    (test (havePath ?dest))
    =>
 	(bind ?pathSoFar (?incoming getPath))
    (bind ?restOfPath (getPath ?dest))
    (?pathSoFar addAll ?restOfPath)

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

; Rule 4
(defrule ForwardRREQ
    "Forward a RREQ datagram for which we have no path and is not for us."
    ?dg <- (Datagram {type == "RREQ"}(destination ?dest)(rreqID ?rreqID) (OBJECT ?incoming))
    (test (<> ?dest ?*id*))
    (test (isNovelRREQID ?rreqID))
    (not (test(havePath ?dest)))
    =>
    (?incoming addToPath ?*id*)
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREQ" 
            	?incoming.source 
            	?incoming.destination
            	?incoming.segment 
            	(?incoming getPath)
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (?*agent* sendDatagram ?response 10)
    (printout t "Forward RREQ" crlf)
    )


;Rule 5 
(defrule RrepAtSource
    "RREP returns to original RREQer"
    ?incoming <- (Datagram {type == "RREP"}{destination == ?*id*})
    =>
    (retract ?incoming)
    (printout t "RREP received at Source" crlf)
    )

; Rule 6
(defrule ForwardRREP
    "RREP and we are the next node in the path but not the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src) (OBJECT ?incoming))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?incoming.source 
            	?incoming.destination
            	?incoming.segment 
            	(?incoming getPath)
            	(?incoming getBatteryMetricValues)))
    (retract ?dg)
    (?*agent* sendDatagram ?response 10)
	)

; Rule 7
(defrule DropRREP
    "RREP and we are not the next node in the path or the destination"
    ?dg <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src) (OBJECT ?incoming))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?dg)
	)

;Rule 8
(defrule SegmentForUs
    "Data type datagram arrived at final destination"
    ?incoming <- (Datagram {type == "DATA"}{destination == ?*id*} (segment ?segment))
    =>
    (retract ?incoming)
    (?*agent* sendMessage ?segment)
    )

; Rule 9
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

; Rule 10
(defrule DropDatagram
    "Data type datagram arrived that we overheard, we aren't the next hop in the path, so we drop it."
    ?incoming <- (Datagram {type == "DATA"} {destination != ?*id*} (source ?src))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?incoming)
    )


; --- Responses to datagrams that we create ---

; Rule 1
(defrule ForwardOurDatagram
    "Data type datagram was created from a segment and must be sent out."
    ?outgoing <- (Datagram {type == "DATA"} {source == ?*id*} (destination ?dest))
    (test (havePath ?dest))
    =>
    (?outgoing addBatteryMetricValue (getBatteryMetric))
    (?outgoing setPath (getPath ?dest))
	(bind ?response ?outgoing)
    (retract ?outgoing)
    (?*agent* sendDatagram ?response 10)    
    )

; Rule 2
(defrule CreateRREQForDatagram
    "Data type datagram was created from a segment, but we need a path first.  We want to keep this datagram until we get the response."
    ?outgoing <- (Datagram {type == "DATA"} {source == ?*id*} (destination ?dest))
	(not (test (havePath ?dest)))
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
; Rule 1
(defrule ReceiveSegmentFromUser
    "Segment received, create a datagram without path information."
    ?outgoing <- (Segment (destination ?dest))
    =>
    (add (new Datagram
            "DATA"
            ?*id*
            ?dest))
    (retract ?outgoing)
    )

(facts)