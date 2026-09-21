FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/patient-service-0.0.1-SNAPSHOT.jar patient-service.jar

ENTRYPOINT ["java", "-jar", "patient-service.jar"]