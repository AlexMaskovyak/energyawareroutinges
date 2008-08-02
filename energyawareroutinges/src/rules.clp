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
(deftemplate Path			( declare( from-class ArrayList)))

; List of all global variables available to JESS
(defglobal ?*id* = 1)
(defglobal ?*agent* = 1)

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
    "Return the best path to a given destination")

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

; Rule 1: Given a RREQ (request), we want to send a RREP (Replay)
(defrule RREQtoRREP
    "Tested: A RREQ Datagram arrives at the destination and a RREP Datagram is sent back."
    ?incoming <- (Datagram {type == "RREQ"} {destination == ?*id*})
    =>
    (printout t "Well- src: " ?incoming.source " dst: " ?incoming.destination crlf)
    (?incoming addToPath ?*id*)
    (bind ?revpath (call Datagram reverse ?incoming.path))
    (?incoming clearBatteryMetricValues)
    (?incoming addBatteryMetricValue (getBatteryMetric))
    ;(bind ?revBattMetric (call Datagram reverse ?incoming.batteryMetrics))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* 
            	?incoming.source 
            	?incoming.segment 
            	?revpath 
            	(?incoming getBatteryMetricValues))
            	)
    (retract ?incoming)
    (?*agent* sendDatagram ?response 10)
    (printout t "RREQ-RREP Fired" crlf))

; Rule 2: Given any Datagram, we want to extract the battery metric, path & transmission cost
(defrule ExtractTransmissionCost
    "Extracts the Battery Metric, Path & Transmission Cost from a datagram."
    (Datagram(path ?path)(batteryMetricValues ?metrics)(transmissionValues ?tValues))
    =>
    (updatePathTable ?path)
    (updateBatteryMetrics ?metrics)
    (updateTransmissionCost ?path ?tValues)
    (printout t "Battery Metric, Path & Transmission Costs updated from an Incoming Datagram" crlf)) 

; Rule 3:
(defrule NonNovelRREQID
    "We've received non-new RREQ with an ID and we're not the destination."
    ?dg <- (Datagram {type == "RREQ"}{destination != ?*id*}(rreqID ?rreqID))
    (not (test(isNovelRREQID ?rreqID)))
    =>
    (retract ?dg)
    (printout t "Receive a RREQ and have not seen the RREQ-ID before"))

; Rule 4:
(defrule ShortCircuitRREQ
    "Receive a RREQ datagram and we already have a path."
    ?incoming <- (Datagram {type == "RREQ"}(destination ?dest)(rreqID ?rreqID))
    (test (<> ?dest ?*id*))
    (test (isNovelRREQID ?rreqID))
    (test (havePath ?dest))
    =>
 	(bind ?pathSoFar (?incoming getPath))
    (bind ?restOfPath (getPath ?dest))
    (bind ?fullPath (?pathSoFar addAll ?restOfPath))
    (bind ?revBestPath (call Datagram reverse ?fullPath))
    (?incoming addBatteryMetricValue (getBatteryMetric))
    (bind ?revBattMetric (call Datagram reverse ?incoming.batteryMetrics))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* 
            	?incoming.source 
            	?incoming.segment 
            	?revBestPath 
            	?revBattMetric))
    (retract ?incoming)
    (?*agent* sendDatagram ?response 10)
    (printout t "Shut up" crlf)
    )

; Rule 5
(defrule ForwardRREQ
    "Forward a RREQ datagram for which we have no path and is not for us."
    ?incoming <- (Datagram {type == "RREQ"}(destination ?dest)(rreqID ?rreqID))
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
    (retract ?incoming)
    (?*agent* sendDatagram ?response 10)
    (printout t "Shut up" crlf)
    )


;Rule 6 
(defrule RrepAtSource
    "RREP returns to original RREQer"
    ?incoming <- (Datagram {type == "RREP"}{destination == ?*id*})
    =>
    (retract ?incoming)
    )

; Rule 7
(defrule ForwardRREP
    "RREP and we are the next node in the path but not the destination"
    ?incoming <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src))
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
    (retract ?incoming)
    (?*agent* sendDatagram ?response 10)
	)

; Rule 8
(defrule DropRREP
    "RREP and we are not the next node in the path or the destination"
    ?incoming <- (Datagram {type == "RREP"}{destination != ?*id*}(source ?src))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?incoming)
	)

;Rule 9
(defrule SegmentForUs
    "Data type datagram arrived at final destination"
    ?incoming <- (Datagram {type == "DATA"}{destination == ?*id*} (segment ?segment))
    =>
    (retract ?incoming)
    (?*agent* sendMessage ?segment)
    )

; Rule 10
(defrule ForwardDatagram
    "Data type datagram arrived at a midpoint along the path to destination."
    ?incoming <- (Datagram {type == "DATA"} {destination != ?*id*} (source ?src))
    (test (isNextHopInPath ?src (?incoming getPath)))
    =>
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

; Rule 11
(defrule DropDatagram
    "Data type datagram arrived that we overheard, we aren't the next hop in the path, so we drop it."
    ?incoming <- (Datagram {type == "DATA"} {destination != ?*id*} (source ?src))
    (not (test (isNextHopInPath ?src (?incoming getPath))))
    =>
    (retract ?incoming)
    )


; ---- Segment rules ----
; Rul 12
(defrule ReceiveSegmentFromUser
    "Segment received, determine if we have a path."
    =>
    
    )

(facts)