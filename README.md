# lingo

"A Clojure natural language generator built on top of Google's simplenlg library."

## Usage

1. Clone the repository to your local file system.
2. Run `lein deps`
3. Open a REPL and hack away!

core.clj holds most of the answers. You can do cool stuff like this:

```clojure
(realise (gen-clause
  {:subject "Fred"
   :verb "run"
   :object "the race"
   :feature (feature :how :?)}))
;; => "How does Fred run the race?"
```
