; Declare all objects that are defined by Java
(import energyaware.*)

(deftemplate Agent 			( declare( from-class Agent )))
(deftemplate Battery 		( declare( from-class Battery )))
(deftemplate BatteryMetric 	( declare( from-class BatteryMetric )))
(deftemplate Datagram		( declare( from-class Datagram )))
(deftemplate Segment		( declare( from-class Segment )))


; Everything under between this line &
(bind ?meth Datagram.getInstance)
; This line is for testing...

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
    ;(bind ?mmw ?incoming.path)
    (bind ?revpath (call Datagram reverse ?incoming.path))
    ;(new Datagram "RREP" 1 4 ?incoming.segment ?revpath 4)
    (bind ?response(new Datagram "RREP" 1 4 ?incoming.segment ?revpath 4))
    (call )
    (printout t "RREQ-RREP Fired" crlf))
    
    ;(bind ?response (call Datagram reverse ?path))

(defrule )

(run)
(facts)