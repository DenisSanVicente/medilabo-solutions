FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/assessment-service-0.0.1-SNAPSHOT.jar assessment-service.jar

ENTRYPOINT ["java", "-jar", "assessment-service.jar"]