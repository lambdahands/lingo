(ns lingo.core
  (:use [lingo.features :only [feature]]
        [clojure.core.match :only [match]])
  (:import (simplenlg.framework NLGFactory CoordinatedPhraseElement)
           (simplenlg.lexicon Lexicon XMLLexicon)
           (simplenlg.realiser.english Realiser)))

(declare gen modify!)

(def lexicon (Lexicon/getDefaultLexicon))

(defn modify!
  ([phrase object]
   (modify! phrase object (atom object)))
  ([phrase object object-ref]
   (let [factory (.getFactory object)]
     (match [(:* phrase)]
       [[:pre modifier]]   (.addPreModifier   object modifier)
       [[:front modifier]] (.addFrontModifier object modifier)
       [[:post  modifier]] (.addPostModifier  object modifier)
       [([& modifiers] :seq)]
         (doseq [modifier modifiers]
           (modify! {:* modifier} object object-ref))
       [{:complement complement}] (.addComplement object complement)
       [{:> (:or :verb :noun :subject :object :clause)}]
         (if (instance? CoordinatedPhraseElement @object-ref)
           (.addCoordinate @object-ref (gen factory (:* phrase)))
           (let [cont (gen factory (:* phrase))
                 phrase (.createCoordinatedPhrase factory object cont)]
             (reset! object-ref phrase)))
       [{:feature [kind ident]}]
         (let [[feature spec] (feature kind ident)]
           (.setFeature @object-ref feature spec))
       [:plural]
         (let [[feature spec] (feature :plural :numbers)]
           (.setFeature @object-ref feature spec))
       [modifier] (.addModifier object modifier))
     @object-ref)))

(defmulti modify (fn [phrase object] (:> phrase)))
(defmethod modify :default [phrase object] object)
(defmethod modify :noun [phrase object]
  (if (:* phrase) (modify! phrase object) object))
(defmethod modify :verb [phrase object]
  (if (:* phrase) (modify! phrase object) object))
(defmethod modify :clause [phrase object]
  (if (:* phrase) (modify! phrase object) object))

(defn- noun [phrase factory]
  (match [phrase]
    [[determiner noun]]
      (doto (.createNounPhrase factory noun)
        (.setDeterminer determiner))
    [noun] (.createNounPhrase factory noun)))

(defmulti gen (fn [factory phrase] (:> phrase)))
(defmethod gen :default [factory phrase] phrase)
(defmethod gen :noun    [factory phrase]
  (modify phrase (noun (:+ phrase) factory)))
(defmethod gen :verb    [factory phrase]
  (modify phrase (.createVerbPhrase factory (:+ phrase))))
(defmethod gen :subject [factory phrase]
  (gen factory (assoc phrase :> :noun)))
(defmethod gen :object  [factory phrase]
  (gen factory (assoc phrase :> :noun)))

(defmethod gen :clause [factory phrases]
  (let [clause (.createClause factory)]
    (doseq [phrase (:+ phrases)]
      (condp = (:> phrase)
        :subject (.setSubject clause (gen factory phrase))
        :verb    (.setVerb    clause (gen factory phrase))
        :object  (.setObject  clause (gen factory phrase))
        :complement (.addComplement clause (:+ phrase))))
    (modify phrases clause)))

(defmethod gen :generator [name lexicon]
  (let [factory (NLGFactory. (:+ lexicon))
        realiser (Realiser.   (:+ lexicon))]
    {:> :generator
     :name name
     :factory factory
     :realiser realiser
     :lexicon (:+ lexicon)
     :* (partial gen factory)
     :! #(.realiseSentence realiser (gen factory %))}))

(defn make-gen [& [lexicon- name]]
  (let [id (or name (str (java.util.UUID/randomUUID)))]
    (gen id {:> :generator :+ (or lexicon- lexicon)})))
