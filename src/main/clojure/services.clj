(ns main.services
  (import org.rs2server.Server
          (org.rs2server.rs2.domain.service.api PlayerService PermissionService)))


(defn instanceOf [service]
  "Gets the instance of a service from the guice injector."
  (.getInstance (Server/getInjector) service))

(def playerService (instanceOf PlayerService))
(def permissionService (instanceOf PermissionService))