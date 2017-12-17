(ns html-templating.core
  (:require [selmer.parser :as selmer]
            [selmer.filters :as filters]
            [selmer.middleware :refer [wrap-error-page]]))

(selmer/render "Hello {{name}}" {:name "world"})

(selmer/render-file "hello.html" {:name "World" :items (range 10)})

(selmer/render "<p>Hello {{user.first}} {{user.last}}</p>" {:user {:first "John" :last "Doe"}})

(filters/add-filter! :empty? empty?)
(selmer/render "{% if files|empty? %} no files {% else %} files {% endif %}" {:files []})

(filters/add-filter! :foo
                     (fn [x] [:safe (.toUpperCase x)]))
(selmer/render "{{x|foo}}" {:x "<div>I'm safe</div>"})

(selmer/add-tag! :image
                 (fn [args context-map]
                   (str "<image src ="
                        (first args)
                        "/>")))
(selmer/render "{% image \"http://foo.com/logo.jpg\" %}" {})

(selmer/add-tag! :uppercase
                 (fn [args context-map content]
                   (.toUpperCase (get-in content [:uppercase :content])))
                 :enduppercase)

(selmer/render "{% uppercase %} foo {{bar}} baz {% enduppercase %}" {:bar "injected"})

(.toUpperCase (get-in {:content (str "foo" " " "injected" " " "baz")} [:content]))

(defn renderer []
  (wrap-error-page
   (fn [template]
     {:state 200
      :body (selmer/render-file template {})})))

((renderer) "hello.html")
((renderer) "error.html")
