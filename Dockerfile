FROM amazoncorretto:21
WORKDIR /app
COPY target/security-server-0.0.1-SNAPSHOT.jar security-server.jar
COPY src/main/resources/application.properties application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "security-server.jar"]