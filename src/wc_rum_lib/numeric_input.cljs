(ns wc-rum-lib.numeric-input
  (:require [rum.core :as rum]
            [cljs-css-modules.macro :refer-macros [defstyle]]))

(defstyle style
  ["#numeric-input" {:width      "130px"
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

; this can be global as there is only one input under focus at any one time
(def timer (atom nil))

(defn handle-numeric-input [min max color onChange e]

  (let [el (-> e .-target)
        value (js/parseInt (.. e -target -value))
        value (if (error? value) "" value)
        ]
    ; _very_ tricky to force in-range values that are editable here since they are not in-range while
    ; partially entered. Hence the 500ms timeout needed to allow entry of a valid value. We aim to flag
    ; incomplete or bad values in red, and delete them after 500ms.
    #_(if (and (not (error? value)) (<= min value max))
      ; in range
      (when @timer (js/clearTimeout @timer))

      ; out of range
      (let [v ""
            src-element (-> e .-nativeEvent .-srcElement)]
        (reset! timer (js/setTimeout #(do
                                        ; this block must be idempotent as it may be called multiple times
                                        (goog.object.set el "value" "")
                                        (goog.object.set (goog.object.get src-element "style") "color" color)
                                        (onChange "")
                                        ) 500))
        ;(onChange "")
        ))

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

#_(def echo-update {:did-update (fn [state]
                                (let [args (:rum/args state)
                                      comp (:rum/react-component state)
                                      node (js/ReactDOM.findDOMNode comp)]
                                  (assoc state ::node node))
                                )})


(rum/defc numeric-input < rum/static rum/reactive           ;echo-update
  [{:keys [input-ref onChange min max error-color color] :or {error-color "red" color "black"} :as props}]


  (let [value (rum/react input-ref)]

    [:div {:id          "numeric-input"
           :style       {:width      "130px"
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
      {:aria-valuenow value
       :aria-valuemin min
       :aria-valuemax max}
      (inc-dec-button (assoc props :increment -1 :cursor input-ref))
      [:input
       {:type      "text"
        :value     value
        :on-click  (partial handle-numeric-input min max color onChange)
        :on-change (partial handle-numeric-input min max color onChange)
        :style     {:width            "58px" :height "36px" :font-size "14px"
                    :border-top       "2px solid #ddd"
                    :border-left      "2px solid #ddd"
                    :background-color (if (not= value "") "#CCEEF8" "#fff")
                    :color            (if (<= min value max) color error-color)
                    :padding          "0 0 4px 0"
                    :text-align       "center"
                    #_#_:font-weight      "bold"}
        }]
      (inc-dec-button (assoc props :increment 1 :cursor input-ref))
      ]]

    ))
