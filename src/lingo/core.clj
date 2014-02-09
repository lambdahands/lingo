(ns lingo.core
  (:require [lingo.features :refer [feature]])
  (:import (simplenlg.framework NLGFactory)
           (simplenlg.lexicon Lexicon)
           (simplenlg.realiser.english Realiser)
           (simplenlg.phrasespec)
           (simplenlg.features Feature Tense)))

(defn sentence
  "Create a simple sentence. This is the most
  basic function we can execute with simplenlg.
  We return a vector containing the realiser and
  an DocumentElement object. (We'll be using this
  pattern in further functions.)"
  [string]
  (let [lexi (Lexicon/getDefaultLexicon)
        fact (NLGFactory. lexi)
        real (Realiser. lexi)]
    [real (.createSentence fact string)]))

#_(sentence "my cat might be feral")
;; => [#<Realiser simplenlg.realiser...> #<DocumentElement...>]

(defn realise [[real sentence]]
  (.realiseSentence real sentence))

#_(realise (sentence "my cat might be feral"))
;; => "My cat might be feral."

;; I'm a LISP newbie (so by consequence a CS newbie).
;; Forgive my horrible function naming.

(defn make-clause
  "We add a subject verb and object to
  create a clause. Unlike our sentence from
  before, our clause can be mutated (yuck!)
  with additional subjects and objects."
  [subj verb obj]
  (let [lexi (Lexicon/getDefaultLexicon)
        fact (NLGFactory. lexi)
        real (Realiser. lexi)]
    [real
     (doto (.createClause fact)
       (.setSubject subj)
       (.setVerb verb)
       (.setObject obj))]))

#_(make-clause "Jack" "run" "race")
;; => [#<Realiser simplenlg.realiser...> #<SPhraseSpec...>]

;; We realise our clauses just as we did with setences.
#_(realise (make-clause "Jack" "run" "the race"))
;; => "Jack runs the race."

(defn past-tense-clause [[real sentence]]
  "Let's add features to our clause.
  This is covered in Section IV, 'Verbs'
  of the simplenlg tutorial."
  (.setFeature sentence (Feature/TENSE) (Tense/PAST))
  [real sentence])

#_(realise (past-tense-clause (make-clause "Jack" "run" "the race")))
;; => "Jack ran the race."

(defn cons-clause
  "Let's have some fun. Here we are able to
  add or replace different parts of our clause."
  [[real sentence] [k v]]
  (cond
   (= k :subject)
   (.setSubject sentence v)
   (= k :verb)
   (.setVerb sentence v)
   (= k :object)
   (.setObject sentence v)
   (= k :feature)
   (let [[a b] v]
     (.setFeature sentence a b)))
  [real sentence])

#_(realise
 (cons-clause (make-clause "Jack" "run" "the race")
              [:subject "Fred"]))
;; => "Fred runs the race."

(realise
   (cons-clause
    (cons-clause
     (make-clause "Jack" "run" "the race")
     [:subject "Fred"]) [:feature (feature :past :tense)]))
;; => "Fred ran the race."

#_(defn gen-clause
  "Since Clojure is the bees knees, we can continue
  to build upon our functions in order to generate
  clauses out of persistent data structures."
  [table]
  (let [lexi (Lexicon/getDefaultLexicon)
        fact (NLGFactory. lexi)
        real (Realiser. lexi)
        clause (.createClause fact)]
    (doseq [t (seq table)]
      (cons-fact [real clause] t))
    [real clause]))

;; The `feature` function is required from another
;; namespace. There lots of work to be done there, but
;; the function is much more idiomatic than the Java way.

#_(realise (gen-clause
  {:subject "Fred"
   :verb "run"
   :object "the race"
   :feature (feature :how :?)}))
;; => "How does Fred run the race?"

;; We need to allow for a :features key that
;; adds more than one feature to our clause!
