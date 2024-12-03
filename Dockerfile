FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
# 의존성 먼저 다운로드하여 캐시 활용
RUN ./gradlew dependencies

# 소스 코드는 마지막에 복사
COPY src src
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar
# 불필요한 파일 제거
RUN rm -rf /var/cache/* && \
    rm -rf /tmp/*
ENTRYPOINT ["java","-jar","app.jar"]