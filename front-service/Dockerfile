FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/front-service-0.0.1-SNAPSHOT.jar front-service.jar

ENTRYPOINT ["java", "-jar", "front-service.jar"]