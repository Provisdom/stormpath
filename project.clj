(defproject provisdom/stormpath "0.1.3"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.apache.httpcomponents/httpclient "4.3.5"]
                 [clj-http "2.1.0" :exclusions [commons-logging
                                                commons-codec
                                                org.apache.httpcomponents/httpclient
                                                org.apache.httpcomponents/httpcore]]
                 [cheshire "5.5.0"]
                 [com.cemerick/url "0.1.1"]
                 [com.stormpath.sdk/stormpath-sdk-api "1.0.RC8.5"]
                 [com.stormpath.sdk/stormpath-sdk-httpclient "1.0.RC8.5"]
                 [com.stormpath.sdk/stormpath-sdk-oauth "1.0.RC8.5" :exclusions [commons-codec]]
                 [com.stormpath.sdk/stormpath-sdk-impl "1.0.RC8.5"]])
