(ns main.commands
  (:require [clojure.string :refer [split]])
  (:require [clojure.core.match :refer [match]])
  (:require [main.services :refer [playerService]])
  (:require [main.players :refer [sendMessage]])
  (:import (org.rs2server.rs2.model World Item)))

(def no-special-commands #(sendMessage % "There are no special commands for players of your permission level."))
(def unrecognized-input #(sendMessage %1 (str "Unrecognized input string \"" %2 "\"")))

(defn player-commands [player *input*]
  (match (first *input*)
         "players" (sendMessage player (str "There are currently " (.size (.getPlayers (World/getWorld))) " players online."))
         _ (unrecognized-input player *input*)))

(def donator-commands player-commands)
(def moderator-commands player-commands)

(defn admin-commands [player args] (match (first args)
                                          "testitem" (.giveItem playerService player (Item. ((comp (partial read-string)
                                                                                                   (partial second)) args)) false)
                                          _ (player-commands player args)))

(let [args *command-line-args* command (first args) player (second args) permissions (last args)
      *input* (split command #" ")]
  (match (str permissions)
         "ADMINISTRATOR" (admin-commands player *input*)
         "MODERATOR" (moderator-commands player *input*)
         "DONATOR" (donator-commands player *input*)
         "PLAYER" (player-commands player *input*)
         _ (no-special-commands player)))