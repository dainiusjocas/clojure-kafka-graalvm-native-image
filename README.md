# clojure-kafka-graavm-native-image

It turns out that it is possible to compile Kafka Producer and Consumer to get compiled with GraalVM native-image.

## TL;DR

```shell script
make build-clj-kafka && ./clj-kafka
```

## Demo

It will start a background thread with a Kafka Producer that every second sends a message to the Kafka.
And in the main thread it'll launch a Kafka Consumer that polls for messages from the same topic as is used by the producer.
Process doesn't stop.

Cool thing is that the binary starts instantly and use ~28 mb of RAM.
```
./clj-kafka   0.01s  user 0.02s system 0% cpu 4.168 total
avg shared (code):         0 KB
avg unshared (data/stack): 0 KB
total (sum):               0 KB
max memory:                28 MB
page faults from disk:     0
other page faults:         1488
```

## Params

Environment variables are used:
- `BOOTSTRAP_SERVERS_CONFIG`: Kafka brokers, default value `"127.0.0.1:9092"`
- `TOPIC`: kafka topic name, default value `"test"`

## Optional

Start Kafka cluster in docker-compose in another terminal:
```shell script
make run-dev-env
```

## Requirements

Linux, Docker, docker-compose, clojure.

For MacOS download GraalVM, install native-image, set `JAVA_HOME`, add native-image to `PATH` and run:
```shell script
clojure -A:native-image
```
Or in one step:
```shell script
(JAVA_HOME=/home/user/graalvm-ce-java11-20.0.0/ && PATH=/home/user/graalvm-ce-java11-20.0.0/bin:$PATH && clojure -A:native-image)
```

## License

Copyright &copy; 2020 [Dainius Jocas](https://www.jocas.lt).

Distributed under the The Apache License, Version 2.0.
