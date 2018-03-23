(ns wc-rum-lib.bs3
  "A port of bootstrap javascript code which would otherwise cause us to include jQuery, so we can track _all_ state
  in our clojurescript atom."
  (:require [rum.core :as rum]))

(def modal-transition-duration 300)
(def modal-backdrop-duration 150)
(def modal-defaults {:backdrop true
                     :keyboard true
                     :show true})

(def modal-state {:is-shown false})

()

(defn hide
  "hide modal"
  [])

(defn show
  "show modal"
  [])

;---------------------------------------

(rum/defc backdrop []
  "append this to <body>"
  [:.modal-backdrop.animate])

;---------------------------------------

(defn simple-header []  "simple-header")
(rum/defc rum-header [])


(defn simple-body [] "simple body")
(rum/defc rum-body [] [:div "rum body"])


(defn simple-footer [] "simple footer")
(rum/defc rum-footer [] [:div "rum footer"])


(rum/defc modal
  "A bs3 compatible modal"
  [element-id header body footer close-handler]

  [:.modal.fade {:id          element-id
                 :role        "dialog"
                 :aria-hidden "true"}
   [:.modal-dialog
    [:.modal-content
     [:.modal-header
      (when close-handler
        [:button.close {:type                    "button "
                        :on-click                close-handler
                        :aria-hidden             true
                        :dangerouslySetInnerHTML {:__html "&times;"}}])
      [:h4.modal-title (header)]]

     [:.modal-body (body)]

     [:.modal-footer
      (footer)
      (when close-handler
        [:button.btn.btn-default {:type     "button"
                                  :on-click close-handler} "Close"])]]]])



