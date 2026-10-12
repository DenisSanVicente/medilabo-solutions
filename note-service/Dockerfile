FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/note-service-0.0.1-SNAPSHOT.jar note-service.jar

ENTRYPOINT ["java", "-jar", "note-service.jar"]