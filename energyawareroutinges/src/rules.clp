; Declare all objects that are defined by Java
(import energyaware.*)

(deftemplate Agent 			( declare( from-class Agent )))
(deftemplate Battery 		( declare( from-class Battery )))
(deftemplate BatteryMetric 	( declare( from-class BatteryMetric )))
(deftemplate Datagram		( declare( from-class Datagram )))
(deftemplate Segment		( declare( from-class Segment )))

; We need to get our JEss code to run a java static method and give us an output object

(deftemplate ourid(slot id))

; Our listing of protocol rules
(defrule Test
    (Datagram (type ?t&: (= ?t "RREQ")))
    =>
    (printout t "RREQ Datagrams" crlf))

(defrule Test2
    (Datagram (type ?t&: (= ?t "RREP")))
    =>
    (printout t "RREP Datagrams" crlf))
 
(defrule RREQtoRREP
    "A RREQ arrives at the destination and a RREP is sent back."
    ?id <- (ourid)
    ?incoming <- (Datagram {type == "RREQ"}{destination == id.id})
    =>
    (printout t "Rule 1 Works" crlf))

;	(add (new Datagram "RREP" id.id incoming.source (call Datagram.reverse( incoming.path ) ) )))

(defglobal ?*arrayList* = (new java.util.ArrayList))
(call ?*arrayList* add 1)
(call ?*arrayList* add 2)
(call ?*arrayList* add 3)
(call ?*arrayList* add 4)

(printout t (call ?*arrayList* get 0) crlf)

(bind ?*arrayList* (call Datagram reverse ?*arrayList*))

;(printout t (call Datagram reverse ?*arrayList* ) crlf)

(printout t (call ?*arrayList* get 0) crlf)

;(defrule Checkdest
;    (Datagram (type ?t:(= ?t 10)))
;    =>
;    (printout t "Go F Yourself!" crlf))

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