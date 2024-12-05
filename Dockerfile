FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies

COPY src src
# Firebase 서비스 계정 파일 복사
COPY src/main/resources/firebase-service-account.json src/main/resources/
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar
# Firebase 서비스 계정 파일을 최종 이미지로 복사
COPY --from=build /workspace/app/src/main/resources/firebase-service-account.json /app/src/main/resources/
RUN rm -rf /var/cache/* && \
    rm -rf /tmp/*
ENTRYPOINT ["java","-jar","app.jar"]