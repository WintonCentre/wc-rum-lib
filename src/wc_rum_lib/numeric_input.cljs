(ns wc-rum-lib.numeric-input
  (:require [rum.core :as rum]
            [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  [".numeric-input" {:width      "130px"
                     :tab-index  1
                     :selectable true}
   [".incdec"
    [".right" {:border-top-right-radius    "10px"
               :border-bottom-right-radius "10px"
               :width                      "36px"}]
    [".left" {:border-top-left-radius    "10px"
              :border-bottom-left-radius "10px"
              :width                     "36px"}]]])


(defn error? [value] (or (nil? value) (= "" value) (js/isNaN value)))

(defn handle-numeric-input [min max onChange e]
  (let [value (js/parseInt (.. e -target -value))
        value (if (error? value) min value)
        ;value (if (<= value max) (if (>= value min) value min) max)
        ]
    ; This is tricky;
    ;
    ; On change, we parse this new value and force our cursor state value back to 100, but his means
    ; that the React value has not changed and so we don't get another component update in which to
    ; reset the visible 1003 back to 100. So we must handle overflows and echo them to the DOM
    ; before dispatching them.
    (when-not (<= min value max)
      (let [v (if (> min value) min max)
            src-element (-> e .-nativeEvent .-srcElement)]
        (.log js/console "value " v)
        (js/setTimeout #(goog.object.set src-element "value" (str v)) 1000)
        (onChange (str v))))

    ; dispatch the clipped value.
    (onChange (str value)))
  )

(defn handle-inc [value onChange min max step]
  (when (not= 0 step)
    (let [val-1 (if (= value "")
                  (if (pos? step)
                    (dec min)
                    (if (neg? step) (inc max) ""))
                  (js/parseInt value))
          val-2 (if (error? val-1) "" (+ step val-1))
          val-3 (if (< val-2 min) min (if (> val-2 max) max val-2))]
      (onChange (str val-3))
      )))

(rum/defcs inc-dec-button < rum/static (rum/local nil ::timer)
  [state
   {:keys [cursor increment onChange min max]
    :as   props}]
  (let [start-timer (fn [e] (js/setInterval #(handle-inc @cursor onChange min max increment) 100))]
    [:span {:class-name "incdec"}
     [:a {:class-name    (str (if (pos? increment) "right" "left") " btn btn-default") ;:style
          #_{:border-top-right-radius    left-r
             :border-bottom-right-radius left-r
             :border-top-left-radius     right-r
             :border-bottom-left-radius  right-r
             :width                      "36px"}
          :aria-hidden   "true"
          :tab-index     -1
          :on-mouse-down #(do (reset! (::timer state) (start-timer %))
                              ;(handle-inc @cursor onChange min max increment)
                              )
          :on-mouse-up   #(do (js/clearInterval @(::timer state))
                              (reset! (::timer state) nil))
          :on-mouse-out  #(do (js/clearInterval @(::timer state))
                              (reset! (::timer state) nil))
          :on-click      #(handle-inc @cursor onChange min max increment)}
      (if (pos? increment) "+" "–")]]))

(def echo-update {:did-update (fn [state]
                                (let [args (:rum/args state)
                                      comp (:rum/react-component state)
                                      node (js/ReactDOM.findDOMNode comp)]
                                  (assoc state ::node node))
                                )})


(rum/defcs numeric-input < rum/static rum/reactive echo-update
  [state {:keys [input-ref onChange min max] :as props}]


  (let [value (rum/react input-ref)]

    [:div {:id "numeric-input"
           :class-name (:numeric-input style)
           ;:style
           #_{:width      "130px"
            :tab-index  1
            :selectable true}
               :on-key-down #(let [key-code (.. % -nativeEvent -code)]
                               (handle-inc value onChange min max
                                           (cond
                                             (= "ArrowRight" key-code) 5
                                             (= "ArrowUp" key-code) 1
                                             (= "ArrowDown" key-code) -1
                                             (= "ArrowLeft" key-code) -5
                                             :else 0)))}
     [:button-group.form-control
      (inc-dec-button (assoc props :increment -1 :cursor input-ref))
      [:input
       {:type      "text"
        :value     value                                    ;(str (if (<= value max) value (if (>= value min) value min)))
        :on-click  (partial handle-numeric-input min max onChange)
        :on-change (partial handle-numeric-input min max onChange)
        :style     {:width            "58px" :height "36px" :font-size "14px"
                    :border-top       "2px solid #ddd"
                    :border-left      "2px solid #ddd"
                    :background-color (if (not= value "") "#CCEEF8" "#fff")
                    :color            (if (<= min value max) "black" "red")
                    :padding          "0 0 4px 0"
                    :text-align       "center"
                    :font-weight      "bold"}
        }]
      (inc-dec-button (assoc props :increment 1 :cursor input-ref))
      ]]

    ))
