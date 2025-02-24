FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/security-server-0.0.1-SNAPSHOT.jar security-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "security-server.jar"]