(ns lingo.core
  (:use [lingo.features :only [feature feature-fn]])
  (:import (simplenlg.framework NLGFactory)
           (simplenlg.lexicon Lexicon XMLLexicon)
           (simplenlg.realiser.english Realiser)
           (simplenlg.phrasespec)
           (simplenlg.features Feature Tense)))

(def lexicon (atom (Lexicon/getDefaultLexicon)))

(defn set-xml-lexicon! [path]
  (reset! lexicon (XMLLexicon. path)))

(defn reset-lexicon! []
  (reset! lexicon (Lexicon/getDefaultLexicon)))

(defn noun
  ([phrase]
   (let [fact (NLGFactory. @lexicon)]
     (.createNounPhrase fact phrase)))
  ([determiner phrase]
   (let [fact (NLGFactory. @lexicon)]
     (.createNounPhrase fact determiner phrase))))

(defn determiner [det phrase]
  (.setDeterminer phrase det)
  phrase)

(defn verb
  [phrase]
  (let [fact (NLGFactory. @lexicon)]
    (.createVerbPhrase fact phrase)))

(defn modifier
  ([modi phrase]
   (modifier modi phrase :default))
  ([modi phrase kind]
   (condp = (name kind)
     "front" (.addFrontModifier phrase modi)
     "pre"   (.addPreModifier phrase modi)
     "post"  (.addPostModifier phrase modi)
     (.addModifier phrase modi))
   phrase))

(defn realise [[real clause]]
  (.realiseSentence real clause))

(defn realise! [phrase]
  (realise [(Realiser. @lexicon) phrase]))

(defn sentence
  "Create a simple sentence. This is the most
  basic function we can execute with simplenlg.
  We return a vector containing the realiser and
  an DocumentElement object. (We'll be using this
  pattern in further functions.)"
  [string]
  (let [lexi @lexicon
        fact (NLGFactory. lexi)
        real (Realiser. lexi)]
    [real (.createSentence fact string)]))

(defn make-clause
  "We add a subject verb and object to
  create a clause. Unlike our sentence from
  before, our clause can be mutated (yuck!)
  with additional subjects and objects."
  [subj verb obj]
  (let [lexi @lexicon
        fact (NLGFactory. lexi)
        real (Realiser. lexi)]
    [real
     (doto (.createClause fact)
       (.setSubject subj)
       (.setVerb verb)
       (.setObject obj))]))

(defn cons-clause
  "Let's have some fun. Here we are able to
  add or replace different parts of our clause."
  [[real clause] [k v]]
  (condp = k
    :subject (.setSubject clause v)
    :verb    (.setVerb clause v)
    :object  (.setObject clause v)
    :features
    (doseq [[a b] v]
      (.setFeature clause a b))
    :complements
    (doseq [compl v]
      (.addComplement clause compl)))
  [real clause])

(defn gen-clause
  "Since Clojure is the bees knees, we can continue
  to build upon our functions in order to generate
  clauses out of persistent data structures."
  [table]
  (let [lexi @lexicon
        fact (NLGFactory. lexi)
        real (Realiser. lexi)
        clause (.createClause fact)]
    (doseq [t (seq table)]
      (cons-clause [real clause] t))
    [real clause]))
