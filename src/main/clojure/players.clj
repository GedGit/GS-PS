(ns main.players)

(def messageEncoder #(.getActionSender %))

(defn sendMessage [player text] (let [encoder (messageEncoder player)]
                                  (.sendMessage encoder text)))