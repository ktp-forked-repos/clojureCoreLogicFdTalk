(ns clojureCoreLogicFdTalk.core
  (:use [clojure.core.logic])
  (:require [clojure.core.logic.fd :as fd]))

;; Remember run*
(run* [q]
      (== q :james))

;; Membero
(run* [q]
      (membero q [:apples :pears :oranges]))

;; Remember fresh
(run* [q]
      (fresh [a b]
             (membero a [:y :x])
             (membero b [:1 :2])
             (== q [a b])))

;; Remember logic is declarative so order is irrelevant
(run* [q]
      (fresh [a b]
             (== q [a b])
             (membero a [:y :x])
             (membero b [:1 :2])))

;; Remember conde
(run* [q]
      (conde 
           [(== q 1)]
           [(== q 2)]))

(run* [q]
      (fresh [a b]
             (== q [a b])
             (conde 
               [(membero a [:y :x])]
               [(membero b [:1 :2])])))

;; Remember conso the magnificant
(run* [q]
      (conde 
        [(conso 1 [2 3] q)]
        [(conso q [4 5] [:first 4 5])]
        [(conso 4 q [4 5 6 7 8 9])]))

;; And into finite domains
;; ------------------------

;; Creating a domain
(run* [q]
      (fd/in q (fd/domain 99 101 103 105)))
                 
(run* [q]
      (fd/in q (apply fd/domain (take 10 (iterate #(* % 2) 1)))))
             
;; Building a domain using interval
(run* [q]
      (fd/in q (fd/interval 9)))

(run* [q]
      (fd/in q (fd/interval 99 107)))

;; Performing maths
(run* [q]
     (fd/+ 3 q 12))

(run* [q]
      (fd/+ 5 6 q))

(run* [q]
      (fd/* q 3 99))

(run* [q]
      (fd/- 999 q 333))

;; Comparisons
(run* [q]
      (fd/in q (fd/interval 10))
      (fd/< q 4))

(run* [q]
      (fd/in q (fd/interval 10))
      (fd/>= q 6))

(run* [q]
      (fresh [a b]
	      (fd/in a b (fd/interval 3))
	      (fd/!= a b)
	      (== q [a b])))

;; All factors of 24
(run* [q]
      (fresh [a b]
             (fd/in a b (fd/interval 24))
             (fd/* a b 24)
             (fd/< a b)
             (== q [a b])))

;; Lists of lvars
;; All possible ways of ordering 1 to 5
(let [
      lvars (repeatedly 5 lvar)
      domain (fd/domain 1 2 3 4 5)
      ]
  (run* [q]
        (== q lvars)
        (fd/distinct lvars)
        (everyg #(fd/in % domain) lvars)))

;; function that gets element (elem) at index i from col.
(defn geto 
  ([i col elem] (geto i col elem 0))
  ([i col elem cur-i]
    (fresh [first rest next-cur-i]
           (conso first rest col)
           (conde
		          [(fd/== i cur-i) (== elem first)]
	            [(fd/< cur-i i) (fd/+ cur-i 1 next-cur-i) (geto i rest elem next-cur-i)]))))

;; (run* [q] (geto 2 [:a :b :c :d :e] q))
;; (run* [q] (fd/in q (fd/interval 5)) (geto q [:a :b :c :d :e] :d))

(defn inco [i i-plus-1]
  (fd/+ i 1 i-plus-1))

(defn my-constrainto [index col]
  (fresh [index-b index-c elem elem-b elem-c]
               (inco index index-b)
               (inco index-b index-c)
               (geto index lvars elem)
               (geto index-b lvars elem-b)
               (geto index-c lvars elem-c)
               (fd/< elem elem-b)
               (fd/< elem-b elem-c)
               (fd/+ elem elem-b elem-c)))

(let [
      lvars (repeatedly 5 lvar)
      domain (fd/interval 1000)
      ]
  (run* [q]
        (== q lvars)
       (everyg #(fd/in % domain) lvars)
       (my-constrainto 0 lvars)
       (my-constrainto 1 lvars)
       (my-constrainto 2 lvars)))
               
               
               
               
               
        