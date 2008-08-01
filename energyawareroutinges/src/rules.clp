; Declare all objects that are defined by Java
(import energyaware.*)

(deftemplate Agent 			( declare( from-class Agent )))
(deftemplate Battery 		( declare( from-class Battery )))
(deftemplate BatteryMetric 	( declare( from-class BatteryMetric )))
(deftemplate Datagram		( declare( from-class Datagram )))
(deftemplate NodeID 		( declare( from-class NodeID )))
(deftemplate Segment		( declare( from-class Segment )))

; We need to get our JEss code to run a java static method and give us an output object

(defglobal ?*id* = 1)
(defglobal ?*agent* = 1)
(defrule setAgent
    (Agent (OBJECT ?a))
    =>
    (bind ?*agent* ?a)
    (bind ?*id* (?*agent* getID))
    (printout t "SetAgent worked. My id is " ?*id* " bitchz." crlf))


 
(defrule RREQtoRREP
    "A RREQ arrives at the destination and a RREP is sent back."
    ;(Datagram(myid ?pumpkin))
    ;?incoming <- (Datagram {type == "RREQ"}{destination == ?*id*})
    ;(NodeID (ID ?id))
    (Agent (OBJECT ?agent))
    ?incoming <- (Datagram {type == "RREQ"} {destination == ?*id*})
    ;(test (eq (new Integer 789) ?*id*))
    ;?incoming <- (Datagram (or (type ?t&: (= ?t "RREQ"))(destination ?d&: (= ?d ?*id*)))) ;{destination == ?*id*})
    ;?incoming <- (Datagram {type == "RREQ"}(destination ?dest))
    ;(test (= ?dest ?*id*))
    =>
    (printout t "Well- src: " ?incoming.source " dst: " ?incoming.destination crlf)
    (bind ?revpath (call Datagram reverse ?incoming.path))
    (bind ?response (
            new Datagram 
            	"RREP" 
            	?*id* 
            	?incoming.source 
            	?incoming.segment 
            	?revpath 
            	4))
    (?*agent* sendDatagram ?response 10)
    (printout t "RREQ-RREP Fired" crlf))
    
    ;(bind ?response (call Datagram reverse ?path))


(run)
(facts)



    ;(?*id* (call ?*agent*.ID intValue))

; Our listing of protocol rules
;(defrule Test
;    (Datagram (type ?t&: (= ?t "RREQ")))
;    =>
;    (printout t "RREQ Datagrams" crlf))

;(defrule Test2
;    (Datagram (type ?t&: (= ?t "RREP")))
;    =>
;    (printout t "RREP Datagrams" crlf))
