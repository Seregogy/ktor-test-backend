.PHONY: build docker-build-image

build:
	gradlew.bat shadowjar

docker-build-image: build
	docker build -t reptiloidd/ktor-server .

docker-push: build docker-build-image
	docker push reptiloidd/ktor-server:latest

run: docker-build-image
	docker run -p 8080:8080 reptiloidd/ktor-server

up: build docker-build-image run