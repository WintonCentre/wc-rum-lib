(ns wc-cards.core
  (:require
   [rum.core :as rum]
   [devcards.core :refer-macros [defcard-doc defcard deftest]]
   [sablono.core :as sab :include-macros true]
   [wc-rum-lib.numeric-input :refer [numeric-input]]
   [wc-rum-lib.bs3 :refer [modal simple-header simple-body simple-footer]]
   )
  )

(enable-console-print!)

(rum/defc basic-rum-test []
  [:#placeholder "Placeholder"])

(defcard rum-card-test
  (basic-rum-test))

(def n (atom 20))

(defcard numeric-input
  (numeric-input {:input-ref n
                  :onChange #(reset! n %)
                  :min 20
                  :max 95}))


(def close-handler identity)

(defcard bs3-modal
  (modal "placeholder" simple-header simple-body simple-footer close-handler))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

