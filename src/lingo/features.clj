(ns lingo.features
  (:import
   (simplenlg.features Feature Tense InterrogativeType)))

(def features
  (let [i-type #(InterrogativeType/valueOf (name %))]
   {:tense [(Feature/TENSE)
           {:past (Tense/PAST)
            :present (Tense/PRESENT)
            :future (Tense/FUTURE)}]
   :negated [(Feature/NEGATED)
             {:is true
              :not false}]
   :? [(Feature/INTERROGATIVE_TYPE)
         {:how (i-type :HOW)
          :how-many (i-type :HOW_MANY)
          :how-predicate (i-type :HOW_PREDICATE)
          :what-object (i-type :WHAT_OBJECT)
          :what-subject (i-type :WHAT_SUBJECT)
          :where (i-type :WHERE)
          :who-indirect-object (i-type :WHO_INDIRECT_OBJECT)
          :who-object (i-type :WHO_OBJECT)
          :who-subject (i-type :WHO_SUBJECT)
          :why (i-type :WHY)
          :yes-no (i-type :YES_NO)}]}))

(defn feature
  "Returns a vector containing a feature
  along with its option."
  [spec kind]
 (let [[feat opt] (features (keyword kind))]
   [feat ((keyword spec) opt)]))

