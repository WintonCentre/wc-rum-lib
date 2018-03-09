(ns wc-cards.core
  (:require
   [rum.core :as rum]
   [devcards.core :refer-macros [defcard-doc defcard deftest]]
   [sablono.core :as sab :include-macros true]
   [wc-rum-lib.numeric-input :refer [numeric-input]])
  )

(enable-console-print!)

(rum/defc basic-rum-test []
  [:div "Hi"])

(defcard rum-card-test
  (basic-rum-test))

(def n (atom 0))

(defcard numeric-input-1
  (numeric-input {:input-ref n
                  :onChange #(reset! n %)
                  :min 20
                  :max 95}))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div "This is working"]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

