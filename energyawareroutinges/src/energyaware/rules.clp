; Declare all objects that are defined by Java
(deftemplate Segment(declare(from-class Segment)))
(deftemplate Network(declare(from-class Network)))
(deftemplate Node(declare(from-class Node)))
(deftemplate Battery(declare(from-class Battery)))
(deftemplate BatteryMetric( declare(from-class BatteryMetric)))

(defrule checkdest
    (destination {address == 123})
    =>
    (printout t "Go F Yourself!" crlf))

; ------------- ALL CODE BENEATH THIS LINE MAY NOT APPLY TO OUR PROJECT -----------

; Define all "objects" in Jess
;(deftemplate destination (slot address))

;(deftemplate datagram
;    (slot type)
;    (slot source)
;    (slot destination)
;    (slot segment)
;    (multislot path)
;    (multislot powerMetric))

;(deftemplate segment
;    (slot destination)
;    (slot message))

;(deftemplate battery
;    (slot energy))

; This links a java Datagram object to JESS
;(deftemplate Datagram
;    (declare (from-class Datagram)))

;(deffunction createRREP (?dgin)
;    ?dgout <- JAVA.GetOutgoing( ?dgin ) )
;	=>
;	JAVA.Broadcast( ?dgout )
;   )


;Public static Datagram ConvertRREQ2RREP( Datagram rreq ){
;
;	Datagram rrep = new RREP( "RREP", rreq.destination, rreq.source, rreq.paths, rreq. );
;}

;(defglobal ?*ThisAddress* = "a")
;(defglobal ?*network* = )

;(defrule destinationReceivesRREQ
;    ?dg <-(Datagram {type == "RREQ" && destination == ?*ThisAddress*})
;    =>
;    ( call  (createRREP ?dg )))

;"Look up and Identify the type of an object"

;(defrule checkdest
;    (destination {address == 123})
;    =>
;    (printout t "Go F Yourself!" crlf))

; Assert rules for our Intelligent Agent
;(assert (destination(address 123)))

(run)
(facts)