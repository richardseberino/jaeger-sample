FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY app.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
