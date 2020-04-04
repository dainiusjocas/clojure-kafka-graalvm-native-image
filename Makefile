run-dev-env:
	@docker-compose \
		-p clj-kafka \
		-f dockerfiles/docker-compose.dev.yml \
		down
	@docker-compose \
		-p clj-kafka \
		-f dockerfiles/docker-compose.dev.yml \
		up

build-clj-kafka:
	docker build -f dockerfiles/Dockerfile.clj-kafka-builder -t clj-kafka-native-image .
	docker rm clj-kafka-native-image-build || true
	docker create --name clj-kafka-native-image-build clj-kafka-native-image
	docker cp clj-kafka-native-image-build:/usr/src/app/clj-kafka clj-kafka
