(ns icals.tools)

(defn apply-safe [fn & args]
  "Like apply, but returns a map with an :ok or an :error key, containing the
  result of the function execution or the raised exception."
  (try {:ok (apply fn args)}
       (catch Exception e {:error e})))
