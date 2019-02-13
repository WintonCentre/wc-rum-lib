(defproject wc-rum-lib "0.1.7-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/wintoncentre/wc-rum-lib"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]
                 [sablono "0.8.3"]
                 [rum "0.10.8"]
                 [cljs-css-modules "0.2.1"]
                 [figwheel-sidecar "0.5.15"]
                 [devcards "0.2.4"]
                 [binaryage/devtools "0.9.9"]]
  :plugins [;[lein-figwheel "0.5.15"]
            [lein-ancient "0.6.15"]]
  :source-paths ["src" "test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  :cljsbuild {
              :builds [{:id           "devcards"
                        :source-paths ["src"]
                        :figwheel     {:devcards  true      ;; <- note this
                                       ;; :open-urls will pop open your application
                                       ;; in the default browser once Figwheel has
                                       ;; started and complied your application.
                                       ;; Comment this out once it no longer serves you.
                                       :open-urls ["http://localhost:3449/cards.html"]}
                        :compiler     {:main                 "wc-cards.core"
                                       :asset-path           "js/compiled/devcards_out"
                                       :output-to            "resources/public/js/compiled/wc_cards_devcards.js"
                                       :output-dir           "resources/public/js/compiled/devcards_out"
                                       :preloads             [devtools.preload]
                                       :source-map-timestamp true}}
                       {:id           "dev"
                        :source-paths ["src"]
                        :figwheel     true
                        :compiler     {:main                 "wc-cards.core"
                                       :asset-path           "js/compiled/out"
                                       :output-to            "resources/public/js/compiled/wc_cards.js"
                                       :output-dir           "resources/public/js/compiled/out"
                                       :source-map-timestamp true}}
                       ]}
  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.9"]
                                  [figwheel-sidecar "0.5.15"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   :plugins      [[cider/cider-nrepl "0.16.0"]]
                   :repl-options {; for nREPL dev you really need to limit output
                                  :init             (set! *print-length* 50)
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}


  )