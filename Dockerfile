FROM adoptopenjdk/openjdk8:slim
VOLUME /tmp
COPY ./target/simple-crawler-1.0-SNAPSHOT.jar app.jar