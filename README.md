# lingo

"A Clojure natural language generator built on top of Google's simplenlg library."

## Usage

1. Clone the repository to your local file system.
2. Open a REPL.
3. Do: `(use 'lingo.core)`
4. And: `(use 'lingo.features)`
5. Hack away!

## Todo

- Test custom lexicons.
- Add support for multiple subjects, verbs, and objects.
- Integrate core.logic in some bizarre and amazing way (meters anyone?)
- Define multi-clause functions.
- Much, much more...

## Walkthrough

We compose our language around basic building blocks called phrases. Here's a simple noun phrase.

```clojure
(noun "Fred")
;;=> #<NPPhraseSpec...>
```

Noun phrases often go along with a reference to their context. For example, "the park" uses what's known as a determiner.

```clojure
(noun "the" "park")
;;=> #<NPPhraseSpec...>
```

We can also add a determiner after the fact.

```clojure
(def park (noun "park"))

(determiner "the" park)

(determiner "that" park)
;;=> #<NPPhraseSpec...>

```

We need more than the ability to define a large set of things. They need to do something. Let's define a verb phrase.

```clojure
(verb "run")
;;=> #<VPPhraseSpec...>
```

Our verbs can be more specific as well. We add modifiers to express other attributes of actions. We can add as many as we want.

```clojure
(modifier "quickly" (verb "run"))
;;=> #<VPPhraseSpec...>

(modifier "for thirty seconds" (modifier "quickly" (verb "run")))
;;=> #<VPPhraseSpec...>
```

The same rule applies to our noun phrases.

```clojure
(modifier "happy" (noun "Fred"))
;;=> #<NPPhraseSpec...>

(modifier "dancing" (modifier "happy" (verb "Fred")))
;;=> #<NPPhraseSpec...>
```

The computer groups our noun and verb phrases into a few different categories. So far, we've seen `#<NPPhraseSpec>` and `#<VPPhraseSpec>`. We can also define a plain sentence.

```clojure
(sentence "my cat might be feral")
;; => [#<Realiser simplenlg.realiser...> #<DocumentElement...>]
```

Doing so returns a vector containing a pair of elements. The first is our realiser, the component that is fed a lexicon in order to reason about our language. The second is the element to be realised. Our computer realises the sentence and outputs a string.

```clojure
(realise (sentence "my cat might be feral"))
;; => "My cat might be feral."
```

Clauses are made up of a subject, verb, and object. We can define them just in that order.

```clojure
(make-clause "Fred" "run" "the race")
;; => [#<Realiser simplenlg.realiser...> #<SPhraseSpec...>]
```

We realise our clauses just as we did with sentences.

```clojure
(realise (make-clause "Fred" "run" "the race"))
;; => "Jack runs the race."
```

We can also realise our nouns and verbs, but we have to be more forceful.

```clojure
(realise! (noun "Fred"))
;; => "Fred."
```

Unlike sentences, we can do a great deal to influence the way our computer realises our clauses. Here we use a feature. Think of a feature as a modifer for clauses. Let's define a function that takes any clause and puts it in the past tense.

```clojure
;; In this example we will call the features directly from Google's library.
(import '(simplenlg.features Feature Tense))

(defn past-tense-clause [[real clause]]
  (.setFeature clause (Feature/TENSE) (Tense/PAST))
  [real clause])

(realise (past-tense-clause (make-clause "Fred" "run" "the race")))
;; => "Fred ran the race."
```

Simple clauses can be constructed directly with strings; however, there's another way to create clauses.

```clojure
(def my-clause
  (make-clause (noun "Fred")
               (modifier "quickly" (verb "run"))
               (modifier "long" (noun "the" "race"))))

(realise my-clause)
;; => "Fred quickly runs the long race."

(realise (past-tense-clause my-clause))
;; => "Fred quickly ran the long race."
```

Thankfully there's a convenient and idiomatic way to call features.

```clojure
(feature :first :person)
;; => ["person" #<Person FIRST>]
```

We can construct clauses by running them together. Anything we've already defined will be replaced.

```clojure
(realise
 (cons-clause
  (make-clause "Jack" "run" "the race")
  [:subject "Fred"]))
;; => "Fred runs the race."
```

We can nest clauses to construct them. Also, our constructor function knows what to do with features.

```clojure
(realise
   (cons-clause
    (cons-clause
     (make-clause "Jack" "run" "the race") [:subject "Fred"])
    [:features [(feature :past :tense) (feature :how :?)]]))
;; => "How did Fred run the race?"
```

We have another function to generate our clauses through construction.

```clojure
(realise (gen-clause
  {:subject "Fred"
   :verb "run"
   :object "the race"
   :features [(feature :past :tense) (feature :how :?)]}))
;; => "How does Fred run the race?"
```

We can also add complements to our clauses. These can come in the form of prepositions, that-clauses, adjective phrases, or adverb phrases.

```clojure
(realise (gen-clause
  {:subject "Fred"
   :verb "run"
   :object "the race"
   :features [(feature :past :tense) (feature :how :?)]
   :complements ["so quickly" "without getting tired"]}))
;; => "How did Fred run the race so quickly without getting tired?"
```
