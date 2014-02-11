(ns lingo.features
  (:import
   (simplenlg.features
    DiscourseFunction
    Feature
    Form
    Gender
    Inflection
    InterrogativeType
    NumberAgreement
    Person
    Tense)))

(def ^:private forms
  (let [f-type #(Form/valueOf (name %))]
   [(Feature/FORM)
    {:base-infinitive    (f-type :BARE_INFINITIVE)
     :gerund             (f-type :GERUND)
     :imperative         (f-type :IMPERATIVE)
     :infinitive         (f-type :INFINITIVE)
     :normal             (f-type :NORMAL)
     :past-participle    (f-type :PAST_PARTICIPLE)
     :present-participle (f-type :PRESENT_PARTICIPLE)}]))

(def ^:private numbers
  [(Feature/NUMBER)
   {:both     (NumberAgreement/BOTH)
    :plural   (NumberAgreement/PLURAL)
    :singular (NumberAgreement/SINGULAR)}])

(def ^:private persons
  [(Feature/PERSON)
   {:first  (Person/FIRST)
    :second (Person/SECOND)
    :third  (Person/THIRD)}])

(def ^:private interrogatives
  (let [i-type #(InterrogativeType/valueOf (name %))]
   [(Feature/INTERROGATIVE_TYPE)
    {:how                 (i-type :HOW)
     :how-many            (i-type :HOW_MANY)
     :how-predicate       (i-type :HOW_PREDICATE)
     :what-object         (i-type :WHAT_OBJECT)
     :what-subject        (i-type :WHAT_SUBJECT)
     :where               (i-type :WHERE)
     :who-indirect-object (i-type :WHO_INDIRECT_OBJECT)
     :who-object          (i-type :WHO_OBJECT)
     :who-subject         (i-type :WHO_SUBJECT)
     :why                 (i-type :WHY)
     :yes-no              (i-type :YES_NO)}]))

(def ^:private tenses
  [(Feature/TENSE)
   {:past    (Tense/PAST)
    :present (Tense/PRESENT)
    :future  (Tense/FUTURE)}])

(def features
  (let [is+not {:is  true :not false}
        has+no {:has true :no  false}]
   {:adj-ordered      [(Feature/ADJECTIVE_ORDERING)  is+not]
    :appositive       [(Feature/APPOSITIVE)          is+not]
    :comparative      [(Feature/IS_COMPARATIVE)      is+not]
    :elided           [(Feature/ELIDED)              is+not]
    :negated          [(Feature/NEGATED)             is+not]
    :passive          [(Feature/PASSIVE)             is+not]
    :perfect          [(Feature/PERFECT)             is+not]
    :possessive       [(Feature/POSSESSIVE)          is+not]
    :pronominal       [(Feature/PRONOMINAL)          is+not]
    :progressive      [(Feature/PROGRESSIVE)         is+not]
    :superlative      [(Feature/IS_COMPARATIVE)      is+not]
    :raised-specifier [(Feature/RAISE_SPECIFIER)     has+no]
    :auxiliary-verbs  [(Feature/AGGREGATE_AUXILIARY) has+no]
    :suppressed-genitive-in-gerund
    [(Feature/SUPPRESS_GENITIVE_IN_GERUND)           has+no]
    :suppressed-complementiser
    [(Feature/SUPRESSED_COMPLEMENTISER)              has+no]
    :form   forms
    :number numbers
    :person persons
    :tense  tenses
    :?      interrogatives}))

(def feature-fns
  {:complement  (Feature/COMPLEMENTISER)
   :conjunction (Feature/CONJUNCTION)
   :cue-phrase  (Feature/CUE_PHRASE)
   :modal       (Feature/MODAL)
   :particle    (Feature/PARTICLE)})

(defn feature
  "Returns a vector containing a feature
  along with its option."
  [spec kind]
 (let [[feat opt] (features (keyword kind))]
   [feat ((keyword spec) opt)]))

(defn feature-fn [kind element]
  [(feature-fns kind) element])

(feature :first :person)

(feature-fn :complement "in the house")
