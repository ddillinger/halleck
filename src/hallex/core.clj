(ns hallex.core
  (:require [clojure.java.io :as io]
            [hallex.delegate :refer [delegate]]))

(delegate {org.apache.log4j.FileAppender (.state this)}
          #{doAppend}
          (gen-class :name hallex.Appender
                     :implements [org.apache.log4j.Appender
                                  org.apache.log4j.spi.OptionHandler]
                     :constructors {[] []
                                    [org.apache.log4j.Layout String] []
                                    [org.apache.log4j.Layout String
                                     boolean] []
                                     [org.apache.log4j.Layout String boolean
                                      boolean int] []}
                     :init init
                     :state state))

(defn -activateOptions [this]
  (.activateOptions (.state this)))

(defn -init
  ([] [[] (org.apache.log4j.FileAppender.)])
  ([layout filename]
     [[] (org.apache.log4j.FileAppender.
          layout
          filename)])
  ([layout filename append]
     [[] (org.apache.log4j.FileAppender.
          layout
          filename
          append)])
  ([layout filename append buffered-io buf-size]
     [[] (org.apache.log4j.FileAppender.
          layout
          filename
          append
          buffered-io
          buf-size)]))

(defn clone-event [event & {:keys [message level throw-info]}]
  (org.apache.log4j.spi.LoggingEvent.
   (.getFQNOfLoggerClass event)
   (.getLogger event)
   (.getTimeStamp event)
   (or level (.getLevel event))
   (or message (.getMessage event))
   (.getThreadName event)
   (or throw-info (.getThrowableInformation event))
   (.getNDC event)
   (.getLocationInformation event)
   (.getProperties event)))


(defn index [f]
  (with-open [fins (io/input-stream f)]
    (let [index (java.util.ArrayList. 1000)]
      (loop [n (.read fins)
             n-1 nil
             n-2 nil
             start 0
             place 0]
        (cond
         (= -1 n) index
         (and (= (char n) \newline)
              (= (char n-1) \%)
              (= (char n) \newline))
         (do
           (.add index [f start (- (- place 2) start)])
           (recur (.read fins)
                  n
                  n-1
                  (inc place)
                  (inc place)))
         :else
         (recur (.read fins)
                n
                n-1
                start
                (inc place)))))))

(defn random-wub []
  (apply str (interpose " "
                        (map #(if (= 0 (rand-int 3))
                                (.toUpperCase %)
                                %)
                             (take (inc (rand-int 50)) (repeat "wub"))))))

(defn -doAppend [this event]
  (.doAppend (.state this) event)
  (when (= org.apache.log4j.Level/WARN
           (.getLevel event))
    (try
      (.doAppend (.state this)
                 (clone-event
                  event
                  :throw-info nil
                  :message
                  (random-wub)))
      (catch Exception _))))
