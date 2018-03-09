# wc-rum-lib

A Clojure library designed to support Winton Centre web stuff which uses
the Rum React wrapper.

## Usage
We have not published this library in Maven, so the first step is to
clone it and install locally with leiningen.

```
git clone <this repo>
lein install
```

Then include it as as a dependency in your project file.
```
...
:dependencies [
  ...
  [wc-rum-lib "0.1.1"]
  ...
  ]
...
```

and require what you need, e.g.
```clj

(ns your-namespace
  (:require [rum.core :as rum]
            [wc-rum-lib/numeric-input :refer [numeric-input]])

(def a-number (atom 0))

(defc my-component []
  ...
  (numeric-input {:input-ref a-number
                  :onChange #(reset! a-number )
                  :min max

```

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
