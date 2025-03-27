FROM eclipse-temurin:17-jdk-jammy

WORKDIR /effective_mobile

COPY  target/Effective-Mobile-RestAPI-0.0.1-SNAPSHOT.jar effective_mobile.jar

ENTRYPOINT ["java", "-jar", "app.jar"]