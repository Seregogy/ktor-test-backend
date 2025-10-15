FROM openjdk:24

ENV audience="onewave.duckdns.org/help" \
    host="onewave.duckdns.org" \
    localHost="0.0.0.0" \
    port=8080 \
    issuer="onewave.duckdns.org"\
    realm="Full API access" \
    secret="secret" \
    JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED" \
    dbPath="database2.db" \
    baseUrl="https://onewave.duckdns.org/"

WORKDIR /app

RUN ./gradlew shadowjar

COPY build/libs/*-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

#sudo docker run -d -p 8080:8080 -v /home/seregogy/audio:/app/src/files/audio/ -v /home/seregogy/docs/documentation.yaml:/app/src/docs/documentation.yaml -v /home/seregogy/database2.db:/app/src/files/database.db --name music-server reptiloidd/ktor-server:latest