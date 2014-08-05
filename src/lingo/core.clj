(ns lingo.core
  (:use [lingo.features :only [feature]]
        [clojure.core.match :only [match]])
  (:import (simplenlg.framework NLGFactory CoordinatedPhraseElement)
           (simplenlg.lexicon Lexicon XMLLexicon)
           (simplenlg.realiser.english Realiser)))

(declare gen mod*)

(def lexicon (Lexicon/getDefaultLexicon))

(defn mod* [p o & [q]]
  (let [obj (or q (atom o)), f (.getFactory o)]
   (match [(:* p)]
    [[:pre   r]] (.addPreModifier   o r)
    [[:front r]] (.addFrontModifier o r)
    [[:post  r]] (.addPostModifier  o r)
    [([& rs] :seq)] (doseq [r rs] (mod* {:* r} o obj))
    [{:complement c}] (.addComplement o c)
    [{:> (:or :verb :noun :subject :object :clause)}]
      (if (instance? CoordinatedPhraseElement @obj)
        (.addCoordinate @obj (gen f (:* p)))
        (reset! obj (.createCoordinatedPhrase f o (gen f (:* p)))))
    [{:feature [a b]}]
      (let [[c d] (feature a b)] (.setFeature @obj c d))
    [:plural]
      (let [[a b] (feature :plural :numbers)]
        (.setFeature @obj a b))
    [r] (.addModifier o r))
   @obj))

(defmulti modify (fn [p o] (p :>)))
(defmethod modify :default [p o] o)
(defmethod modify :noun    [p o] (if (:* p) (mod* p o) o))
(defmethod modify :verb    [p o] (if (:* p) (mod* p o) o))
(defmethod modify :clause  [p o] (if (:* p) (mod* p o) o))

(defn- noun [s f]
  (match [s]
    [[d n]] (doto (.createNounPhrase f n) (.setDeterminer d))
    [n] (.createNounPhrase f n)))

(defmulti gen (fn [f x] (x :>)))
(defmethod gen :default [f ps] ps)
(defmethod gen :noun    [f ps] (modify ps (noun (:+ ps) f)))
(defmethod gen :verb    [f ps] (modify ps (.createVerbPhrase f (:+ ps))))
(defmethod gen :subject [f ps] (gen f (assoc ps :> :noun)))
(defmethod gen :object  [f ps] (gen f (assoc ps :> :noun)))

(defmethod gen :clause [f ps]
  (let [c (.createClause f)]
    (doseq [p (:+ ps)]
      (condp = (:> p)
        :subject (.setSubject c (gen f p))
        :verb    (.setVerb    c (gen f p))
        :object  (.setObject  c (gen f p))
        :complement (.addComplement c (:+ p))))
    (modify ps c)))

(defmethod gen :generator [n ps]
  (let [f (NLGFactory. (:+ ps))
        r (Realiser.   (:+ ps))]
    {:> :generator
     :name n
     :factory f
     :realiser r
     :lexicon (:+ ps)
     :* (partial gen f)
     :! #(.realiseSentence r (gen f %))}))

(defn make-gen [& [lex name]]
  (let [id (or name (str (java.util.UUID/randomUUID)))]
    (gen id {:> :generator :+ (or lex lexicon)})))
