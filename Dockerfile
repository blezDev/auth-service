FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
