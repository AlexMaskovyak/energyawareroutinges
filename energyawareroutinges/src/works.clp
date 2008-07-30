(deftemplate Cat(slot meow)(slot runaway))

(defrule Test
    (Cat (meow ?t))
    =>
    (printout t "Fruit" crlf))

(defrule Test2
    (Cat (meow ?m&: (= ?m 1)))
    =>
    (printout t "Yeah for second rule!" crlf))
    
(defrule Test3
    (Cat (runaway ?r&: (= ?r "hi")))
    =>
    (printout t "Test3 worked shitheads" crlf))

(assert (Cat(meow 1)(runaway "poo")))