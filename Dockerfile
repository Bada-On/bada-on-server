FROM openjdk:21-slim

ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENTRYPOINT ["java", "-jar", "/app.jar"]
