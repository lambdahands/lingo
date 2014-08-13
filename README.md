# lingo

"A Clojure natural language generator built on top of Google's simplenlg library."

## Usage

1. Clone the repository to your local file system.
2. Run `lein repl` in the lingo directory.
3. Do: `(use 'lingo.core)`
4. And: `(use 'lingo.features)`
5. Hack away!

## Todo

- Test custom lexicons.
- Integrate core.logic in some bizarre and amazing way (meters anyone?)
- Much, much more...

## Walkthrough

**This walkthrough is available as a Clojure file in the examples directory. It's encouraged that you go through it interactively.**


Lingo's pieces comprise Clojure data structures. Maps are the primary structure used to express each part of speech we're interested in.

For example, an empty clause looks like this:

```clojure
{:> :clause}
```

The key `:>` is the identifier key. It tells us what kind of part we plan on generating. Here are other parts we're able to generate:

```clojure
{:> :noun}
{:> :verb}
{:> :subject}
{:> :object}
```

Generating any of these parts will create empty phrases. To do something more useful, we're able to "add" specific pieces to our parts.

A noun can simply pass a string or a vector of two strings, the first being what's known as the determiner ("the", "a", or "that") and the second being the actual phrase.

```clojure
{:> :noun :+ "dog"}
```

```clojure
{:> :noun :+ ["the" "dog"]}
```

However verbs accept only one string.

```clojure
{:> :verb :+ "run"}
```

Subjects and objects are only nouns, but they're used to specifically create clauses. Clauses take a vector of one of each a subject, object and verb as well as many complements as you wish.  We'll cover complements soon.

```clojure
(def dog-and-rabbit
  {:> :clause
   :+ [{:> :subject :+ ["the" "dog"]}
       {:> :verb    :+ "chase"}
       {:> :object  :+ ["the" "rabbit"]}]})
```

In order to generate our pieces into speech, we need to create a generator.

```clojure
(def generator (make-gen))
```

The generator contains two primary functions, accessed as keys to
a Clojure map.

The first creates SimpleNLG objects. Once created, you can use
Java methods to access fields and (not recommended!) mutate them.

```clojure
(:* generator)
```

The second realises- or "renders"- our speech into a sentence.  It's recommended to do as many operations on Clojure data structures before passing them to the generator.

```clojure
(:! generator)
```

We pass the realisation function to our dog and rabbit clause to see the generated result.

```clojure
((:! generator) dog-and-rabbit)
;; => "The dog chases the rabbit."
```

We can manipulate our clauses just as maps, and we can even make philosphical inqueries.

```clojure
((:! generator)
 (assoc dog-and-rabbit :* {:feature [:why :?]}))
;; => "Why does the dog chase the rabbit?"
```

By assoc-ing the `:*` key with a feature, we were able to turn our statement into a question. The `:*` key is known as the modifier key.

Modifiers can be a great deal of different things. Here are several examples of what you can pass as the `:*` key:

```clojure
{:> :verb :+ "run" :* "quickly"} ;; Add an adverb
```

```clojure
{:> :noun :+ "dog" :* "confused"} ;; Add an adjective
```

```clojure
{:> :noun
 :+ "management"
 :* [:pre "time"]} ;; A pre-modifier resides before the phrase.
```

```clojure
{:> :noun
 :+ "management"
 :* [:post "time"]} ;; A post-modifier resides after the phrase.
```

Complements are similar to modifiers, but they can represent phrases in a general sense, from simple adverbs to prepositional phrases. In linguistics, a complement is [loosely defined as anything that comes after the verb](http://en.wikipedia.org/wiki/Complement_(linguistics)).

```clojure
(def park-chase
  (assoc dog-and-rabbit :* {:complement "around the park"}))
```

```clojure
((:! generator) park-chase)
;; => "The dog chases the rabbit around the park."
```

We can add as many features and complements, in any order, as we like.

```clojure
((:! generator)
 (merge
   dog-and-rabbit
   {:* [{:feature [:how :?]}
        {:feature [:is :perfect]}
        {:feature [:future :tense]}
        {:complement "around the park"}]}))
;; => "How will the dog have chased the rabbit around the park?"
```

Modifiers can even be other parts to create coordinated phrases.

```clojure
((:! generator)
 {:> :noun :+ "Jack"
  :* [{:> :noun :+ "Wendy"}
      {:> :noun :+ "Danny"}]})
;; => "Jack, Wendy and Danny."
```

We can combine all of these pieces to create well known phrases.

```clojure
(def redrum
  {:> :clause
   :+ [{:> :subject
        :+ "work"
        :* [{:> :noun :+ "play" :* [:pre "no"]}
            [:pre "all"]]}
       {:> :verb :+ "make"}
       {:> :object :+ "Jack"}
       {:> :complement :+ "a dull boy"}]
   :* {:feature [:base-infinitive :form]}})

((:! generator) redrum)
;; => "All work and no play makes Jack a dull boy."
```
