FROM openjdk:17-jdk-slim

WORKDIR /romashkako-app

COPY target/Effective-Mobile-RestAPI-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT  ["java", "-jar", "app.jar"]