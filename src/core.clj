(ns core
  (:gen-class)
  (:require
    [clojure.tools.logging :as log])
  (:import (org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer)
           (org.apache.kafka.clients.producer ProducerConfig KafkaProducer ProducerRecord RecordMetadata)
           (org.apache.kafka.common.serialization StringSerializer StringDeserializer)
           (java.time Duration)
           (java.util Properties)))

(defn brokers []
  (or (System/getenv "BOOTSTRAP_SERVERS_CONFIG") "127.0.0.1:9092"))

(defn topic []
  (or (System/getenv "TOPIC") "test"))

(defn kafka-producer [{:keys [bootstrap-servers-config]}]
  (KafkaProducer.
    (doto (Properties.)
      (.put ProducerConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-servers-config)
      (.put ProducerConfig/CLIENT_ID_CONFIG "KafkaExampleProducer")
      (.put ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG (.getName StringSerializer))
      (.put ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG (.getName StringSerializer)))))

(defn kafka-consumer [{:keys [bootstrap-servers-config]}]
  (KafkaConsumer.
    (doto (Properties.)
      (.put ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-servers-config)
      (.put ConsumerConfig/GROUP_ID_CONFIG "my-group-id")
      (.put ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG (.getName StringDeserializer))
      (.put ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG (.getName StringDeserializer))
      (.put ConsumerConfig/MAX_POLL_RECORDS_CONFIG (Integer/parseInt "1"))
      (.put ConsumerConfig/ENABLE_AUTO_COMMIT_CONFIG "false")
      (.put ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "latest"))))

(defn start-producer [opts]
  (let [^KafkaProducer producer (kafka-producer opts)
        topic (:topic opts)
        thread (Thread.
                 ^Runnable
                 (fn []
                   (loop [counter 0]
                     (let [producer-record (ProducerRecord. topic (str counter) (str "test-" counter))
                           ^RecordMetadata record-metadata (.get (.send producer producer-record))]
                       (log/infof "Produced record with offset: %s" (.offset record-metadata)))
                     (Thread/sleep 1000)
                     (recur (inc counter)))))]
    (.start thread)
    thread))

(defn start-consumer [opts]
  (let [^KafkaConsumer consumer (kafka-consumer opts)
        duration (Duration/ofSeconds 2)
        ^String topic (:topic opts)]
    (.subscribe consumer (re-pattern topic))
    (loop [count 0]
      (doseq [r (.poll consumer duration)]
        (log/infof "Iteration=%s record='%s'" count r))
      (.commitAsync consumer)
      (recur (inc count)))))

(defn -main [& args]
  (let [opts {:bootstrap-servers-config (brokers)
              :topic                    (topic)}]
    (log/infof "Starting Clojure Kafka native-image demo with: %s" opts)
    (start-producer opts)
    (start-consumer opts)))
