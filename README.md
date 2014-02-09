# lingo

"A Clojure natural language generator built on top of Google's simplenlg library."

## Usage

1. Clone the repository to your local file system.
2. Open a REPL.
3. Do: `(use 'lingo.core)`
4. And: `(use 'lingo.features)`
5. Hack away!

core.clj holds most of the answers. You can do cool stuff like this:

```clojure
(realise (gen-clause
  {:subject "Fred"
   :verb "run"
   :object "the race"
   :feature (feature :how :?)}))
;; => "How does Fred run the race?"
```
